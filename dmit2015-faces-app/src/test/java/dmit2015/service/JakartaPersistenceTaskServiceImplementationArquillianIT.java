package dmit2015.service;

import dmit2015.config.ApplicationConfig;
import dmit2015.model.Task;
import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import net.datafaker.Faker;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.jupiter.api.*;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ArquillianTest
public class JakartaPersistenceTaskServiceImplementationArquillianIT { // The class must be declared as public

    static Faker faker = new Faker();

    static String mavenArtifactIdId;

    @Deployment
    public static WebArchive createDeployment() throws IOException, XmlPullParserException {
        PomEquippedResolveStage pomFile = Maven.resolver().loadPomFromFile("pom.xml");
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader("pom.xml"));
        mavenArtifactIdId = model.getArtifactId();
        final String archiveName = model.getArtifactId() + ".war";
        return ShrinkWrap.create(WebArchive.class, archiveName)
                .addAsLibraries(pomFile.resolve("org.codehaus.plexus:plexus-utils:3.4.2").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.hamcrest:hamcrest:3.0").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.assertj:assertj-core:3.27.6").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("net.datafaker:datafaker:2.5.1").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("com.h2database:h2:2.3.232").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("com.microsoft.sqlserver:mssql-jdbc:13.2.0.jre11").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("com.oracle.database.jdbc:ojdbc11:23.9.0.25.07").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.postgresql:postgresql:42.7.7").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("com.mysql:mysql-connector-j:9.2.0").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("org.mariadb.jdbc:mariadb-java-client:3.5.3").withTransitivity().asFile())
                // .addAsLibraries(pomFile.resolve("org.hibernate.orm:hibernate-spatial:6.6.28.Final").withTransitivity().asFile())
                // .addAsLibraries(pomFile.resolve("org.eclipse:yasson:3.0.4").withTransitivity().asFile())
                .addClass(ApplicationConfig.class)
                .addClasses(Task.class, TaskService.class, JakartaPersistenceTaskService.class)
                // TODO Add any additional libraries, packages, classes or resource files required
//                .addAsLibraries(pomFile.resolve("jakarta.platform:jakarta.jakartaee-api:10.0.0").withTransitivity().asFile())
                // .addPackage("dmit2015.entity")
                .addAsResource("META-INF/persistence.xml")
                // .addAsResource(new File("src/test/resources/META-INF/persistence-entity.xml"),"META-INF/persistence.xml")
                .addAsResource("META-INF/beans.xml");
    }

    @Inject
    @Named("jakartaPersistenceTaskService")
    private JakartaPersistenceTaskService taskService;

    @Resource
    private UserTransaction beanManagedTransaction;

    @BeforeAll
    static void beforeAllTests() {
        // code to execute before all tests in the current test class
    }

    @AfterAll
    static void afterAllTests() {
        // code to execute after all tests in the current test class
    }

    @BeforeEach
    void beforeEachTestMethod() throws SystemException, NotSupportedException {
        // Start a new transaction
        beanManagedTransaction.begin();
    }

    @AfterEach
    void afterEachTestMethod() throws SystemException {
        // Rollback the transaction
        beanManagedTransaction.rollback();
    }

    @Order(1)
    @Test
    void givenNewEntity_whenAddEntity_thenEntityIsAdded() {
        // Arrange
        Task newTask = Task.of(faker);

        // Act
        taskService.createTask(newTask);

        // Assert
        assertThat(newTask.getId())
                .isNotNull();

    }

    @Order(2)
    @Test
    void givenExistingId_whenFindById_thenReturnEntity() {
        // Arrange
        Task newTask = Task.of(faker);

        // Act
        newTask = taskService.createTask(newTask);

        // Assert
        Optional<Task> optionalTask = taskService.getTaskById(newTask.getId());
        assertThat(optionalTask.isPresent())
                .isTrue();
        // Assert
        var existingTask = optionalTask.orElseThrow();
        assertThat(existingTask)
                .usingRecursiveComparison()
                // .ignoringFields("field1", "field2")
                .isEqualTo(newTask);

    }

    @Order(3)
    @Test
    void givenExistingEntity_whenUpdatedEntity_thenEntityIsUpdated() {
        // Arrange
        Task newTask = Task.of(faker);

        newTask = taskService.createTask(newTask);
        // TODO: change the values of all properties
        //newTask.setProperty1(faker.providerName().methodName());
        //newTask.setProperty2(faker.providerName().methodName());
        //newTask.setProperty3(faker.providerName().methodName());

        // Act
        Task updatedTask = taskService.updateTask(newTask);

        // Assert
        Optional<Task> optionalTask = taskService.getTaskById(updatedTask.getId());
        assertThat(optionalTask.isPresent())
                .isTrue();
        var existingTask = optionalTask.orElseThrow();
        assertThat(existingTask)
                .usingRecursiveComparison()
                // .ignoringFields("field1", "field2")
                .isEqualTo(newTask);

    }

    @Order(4)
    @Test
    void givenExistingId_whenDeleteEntity_thenEntityIsDeleted() {
        // Arrange
        Task newTask = Task.of(faker);
        newTask = taskService.createTask(newTask);
        // Act
        taskService.deleteTaskById(newTask.getId());
        // Assert
        Optional<Task> optionalTask = taskService.getTaskById(newTask.getId());
        assertThat(optionalTask.isPresent())
                .isFalse();

    }
/*
    @Order(5)
    @ParameterizedTest
    @CsvSource({"10"})
    void givenMultipleEntity_whenFindAll_thenReturnEntityList(int expectedRecordCount) {
        // Arrange: Set up the initial state

        // Delete all existing data
        assertThat(taskService).isNotNull();
        taskService.deleteAllTasks();
        // Generate expectedRecordCount number of fake data
        Task firstExpectedTask = null;
        Task lastExpectedTask = null;
        for (int counter = 1; counter <= expectedRecordCount; counter++) {
            Task currentTask = Task.of(faker);
            if (counter == 1) {
                firstExpectedTask = currentTask;
            } else if (counter == expectedRecordCount) {
                lastExpectedTask = currentTask;
            }

            newTask = taskService.createTask(currentTask);
        }

        // Act: Perform the action to be tested
        List<Task> taskList = taskService.getAllTasks();

        // Assert: Verify the expected outcome
        assertThat(taskList.size())
                .isEqualTo(expectedRecordCount);

        // Get the first entity and compare with expected results
        var firstActualTask = taskList.getFirst();
        assertThat(firstActualTask)
                .usingRecursiveComparison()
                // .ignoringFields("field1", "field2")
                .isEqualTo(firstExpectedTask);
        // Get the last entity and compare with expected results
        var lastActualTask = taskList.getLast();
        assertThat(lastActualTask)
                .usingRecursiveComparison()
                // .ignoringFields("field1", "field2")
                .isEqualTo(lastExpectedTask);

    }

    @Order(6)
    @ParameterizedTest
    // TODO Change the value below
    @CsvSource(value = {
            "Invalid Property1Value, Property2Value, Property3Value, ExpectedExceptionMessage",
            "Property1Value, Invalid Property2Value, Property3Value, ExpectedExceptionMessage",
    }, nullValues = {"null"})
    void givenEntityWithValidationErrors_whenAddEntity_thenThrowException(
            String property1,
            String property2,
            String property3,
            String expectedExceptionMessage
    ) {
        // Arrange
        Task newTask = new Task();
        // TODO uncomment below and set each property of Task using parameter values
        // newTask.setProperty1(property1);
        // newTask.setProperty2(property2);
        // newTask.setProperty3(property3);

        try {
            // Act
            taskService.createTask(newTask);
            fail("An bean validation constraint should have been thrown");
        } catch (Exception ex) {
            // Assert
            assertThat(ex)
                    .hasMessageContaining(expectedExceptionMessage);
        }
    }
*/
}