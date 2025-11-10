package dmit2015.restclient;

import dmit2015.entity.TodoItemDto;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * This class contains starter code for testing REST API endpoints for CRUD operations using REST-assured.
 *
 * <a href="https://github.com/rest-assured/rest-assured">REST Assured GitHub repo</a>
 * <a href="https://github.com/rest-assured/rest-assured/wiki/Usage">REST Assured Usage</a>
 * <a href="http://www.mastertheboss.com/jboss-frameworks/resteasy/restassured-tutorial">REST Assured Tutorial</a>
 * <a href="https://hamcrest.org/JavaHamcrest/tutorial">Hamcrest Tutorial</a>
 * <a href="https://github.com/FasterXML/jackson-databind">Jackson Data-Binding</a>
 *
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TodoItemDtoResourceRestAssuredFakerIT {

    static Faker faker = new Faker();

    final String todoItemDtoResourceUrl = "http://localhost:8090/restapi/TodoItemsDto";

    @BeforeAll
    static void beforeAllTests() {
        // code to execute before all tests in the current test class
    }

    @AfterAll
    static void afterAllTests() {
        // code to execute after all tests in the current test class
    }

    @BeforeEach
    void beforeEachTestMethod() {
        // Code to execute before each test such as creating the test data
    }

    @AfterEach
    void afterEachTestMethod() {
        // code to execute after each test such as deleting the test data
    }

    @Order(1)
    @Test
    void givenTodoItemDtoData_whenCreateTodoItemDto_thenTodoItemDtoIsCreated() throws Exception {
        // Arrange: Set up the initial state
        var currentTodoItemDto = new TodoItemDto();
        // TODO: Set the properties of currentTodoItemDto
        currentTodoItemDto.setProperty1(faker.providerName().methodName());
        currentTodoItemDto.setProperty2(faker.providerName().methodName());
        currentTodoItemDto.setProperty3(faker.providerName().methodName());

        // Act & Assert
        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonBody = jsonb.toJson(currentTodoItemDto);
            given()
                    .contentType(ContentType.JSON)
                    .body(jsonBody)
                    .when()
                    .post(todoItemDtoResourceUrl)
                    .then()
                    .statusCode(201)
                    .header("location", containsString(todoItemDtoResourceUrl))
            ;
        }
    }

    @Order(2)
    @Test
    void givenExistingTodoItemDtoId_whenFindTodoItemDtoById_thenReturnTodoItemDto() throws Exception {
        // Arrange: Set up the initial state
        var currentTodoItemDto = new TodoItemDto();
        // TODO: Set the properties of currentTodoItemDto
        currentTodoItemDto.setProperty1(faker.providerName().methodName());
        currentTodoItemDto.setProperty2(faker.providerName().methodName());
        currentTodoItemDto.setProperty3(faker.providerName().methodName());

        // Act & Assert
        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonBody = jsonb.toJson(currentTodoItemDto);

            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(jsonBody)
                    .when()
                    .post(todoItemDtoResourceUrl);
            var postedDataLocation = response.getHeader("location");

            // Act & Assert
            // TODO: Change property1, property2, property3
            given()
                    .when()
                    .get(postedDataLocation)
                    .then()
                    .statusCode(200)
                    .body("id", notNullValue())
                    .body("property1", equalTo(currentTodoItemDto.getProperty1()))
                    .body("property2", equalTo(currentTodoItemDto.getProperty2()))
                    .body("property3", equalTo(currentTodoItemDto.getProperty3()))
            ;
        }

    }

    @Order(3)
    @Test
    void givenTodoItemDtoExist_whenFindAllTodoItemDtos_thenReturnTodoItemDtoList() throws Exception {
        // Arrange: Set up the initial state
        try (Jsonb jsonb = JsonbBuilder.create()) {
            // Post 3 records and verify the 3 records are added
            var todoItemDtos = new ArrayList<TodoItemDto>();
            for (int index = 0; index < 3; index++) {
                var currentTodoItemDto = new TodoItemDto();
                // TODO: Set the properties of currentTodoItemDto
                currentTodoItemDto.setProperty1(faker.providerName().methodName());
                currentTodoItemDto.setProperty2(faker.providerName().methodName());
                currentTodoItemDto.setProperty3(faker.providerName().methodName());

                todoItemDtos.add(currentTodoItemDto);

                given()
                        .contentType(ContentType.JSON)
                        .body(jsonb.toJson(todoItemDtos.get(index)))
                        .when()
                        .post(todoItemDtoResourceUrl)
                        .then()
                        .statusCode(201);
            }

            // Act & Assert: Perform the action and verify the expected outcome
            // TODO: Change property1, property2, property3
            given()
                    .when()
                    .get(todoItemDtoResourceUrl)
                    .then()
                    .statusCode(200)
                    .body("size()", greaterThan(0))
                    .body("property1", hasItems(todoItemDtos.getFirst().getProperty1(), todoItemDtos.getLast().getProperty1()))
                    .body("property2", hasItems(todoItemDtos.getFirst().getProperty2(), todoItemDtos.getLast().getProperty2()))
                    .body("property3", hasItems(todoItemDtos.getFirst().getProperty3(), todoItemDtos.getLast().getProperty3()))
            ;

        }

    }

    @Order(4)
    @Test
    void givenUpdatedTodoItemDtoData_whenUpdatedTodoItemDto_thenTodoItemDtoIsUpdated() throws Exception {
        // Arrange: Set up the initial state
        var createTodoItemDto = new TodoItemDto();
        createTodoItemDto.setProperty1(faker.providerName().methodName());
        createTodoItemDto.setProperty2(faker.providerName().methodName());
        createTodoItemDto.setProperty3(faker.providerName().methodName());

        var updateTodoItemDto = new TodoItemDto();
        updateTodoItemDto.setProperty1(faker.providerName().methodName());
        updateTodoItemDto.setProperty2(faker.providerName().methodName());
        updateTodoItemDto.setProperty2(faker.providerName().methodName());

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String createJsonBody = jsonb.toJson(createTodoItemDto);

            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(createJsonBody)
                    .when()
                    .post(todoItemDtoResourceUrl);
            var postedDataLocation = response.getHeader("location");
            Long entityId = Long.parseLong(postedDataLocation.substring(postedDataLocation.lastIndexOf("/") + 1));
            updateTodoItemDto.setId(entityId);
            // Act & Assert
            // TODO: Change property1, property2, property3
            String updateJsonBody = jsonb.toJson(updateTodoItemDto);
            given()
                    .contentType(ContentType.JSON)
                    .body(updateJsonBody)
                    .when()
//                .pathParam("id", entityId)
                    .put(postedDataLocation)
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(entityId.intValue()))
                    .body("property1", equalTo(updateTodoItemDto.getProperty1()))
                    .body("property2", equalTo(updateTodoItemDto.getProperty2()))
                    .body("property3", equalTo(updateTodoItemDto.getProperty3()))
            ;
        }

    }

    @Order(5)
    @Test
    void givenExistingTodoItemDtoId_whenDeleteTodoItemDto_thenTodoItemDtoIsDeleted() throws Exception {
        // Arrange: Set up the initial state
        var currentTodoItemDto = new TodoItemDto();
        // TODO: Set the properties of currentTodoItemDto
        currentTodoItemDto.setProperty1(faker.providerName().methodName());
        currentTodoItemDto.setProperty2(faker.providerName().methodName());
        currentTodoItemDto.setProperty3(faker.providerName().methodName());

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonBody = jsonb.toJson(currentTodoItemDto);

            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(jsonBody)
                    .when()
                    .post(todoItemDtoResourceUrl);
            var postedDataLocation = response.getHeader("location");
            int entityId = Integer.parseInt(postedDataLocation.substring(postedDataLocation.lastIndexOf("/") + 1));

            // Act & Assert
            given()
//                .pathParam("id", entityId)
                    .when()
                    .delete(postedDataLocation)
                    .then()
                    .statusCode(204);

            // Verify deletion
            given()
                    .when()
                    .delete(postedDataLocation)
                    .then()
                    .statusCode(404);
        }

    }

}