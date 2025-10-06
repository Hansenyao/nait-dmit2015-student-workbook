# Instruction

## How to implement In-Memory database service


## How to implement Firebase database service


## How to implement JPA (postgreSQL) database service

 ### Start postgreSQL container

 - List all podman containers

 ```
    $ podman ps -a
 ```
 - Check postgresSQL server container's name, such as 'dmit2015-postgis'

 - Start postgreSQL server container

 ```
    $ podman start dmit2015-postgis
 ```

 - Check it is started or not

 ```
    $ podman ps
 ```
 - Make sure it's status is 'Up'

 - Run pgAdmin4 and try to connect postgreSQL server!


 ### Add dependencies and plugins for database ORM

 - Add below dependencies to file pom.xml

 ```
    <dependency>
        <groupId>jakarta.platform</groupId>
        <artifactId>jakarta.jakartaee-web-api</artifactId>
        <version>10.0.0</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.hibernate.orm</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>6.6.28.Final</version>
    </dependency>
    <dependency>
        <groupId>org.hibernate.orm</groupId>
        <artifactId>hibernate-spatial</artifactId>
        <version>6.6.28.Final</version>
    </dependency>
    <dependency>
        <groupId>com.oracle.database.jdbc</groupId>
        <artifactId>ojdbc11</artifactId>
        <version>23.9.0.25.07</version>
    </dependency>
    <dependency>
        <groupId>com.microsoft.sqlserver</groupId>
        <artifactId>mssql-jdbc</artifactId>
        <version>13.2.0.jre11</version>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.7.7</version>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>2.3.232</version>
    </dependency>
 ```

 - Add below plugins to file pom.xml

 ```
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.14.1</version>
        <configuration>
            <annotationProcessorPaths>
                <path>
                    <groupId>org.projectlombok</groupId>
                    <artifactId>lombok</artifactId>
                    <version>1.18.40</version>
                </path>
                <path>
                    <groupId>org.hibernate.orm</groupId>
                    <artifactId>hibernate-jpamodelgen</artifactId>
                    <version>6.6.28.Final</version>
                </path>
            </annotationProcessorPaths>
        </configuration>
    </plugin>

    <plugin>
        <groupId>org.wildfly.plugins</groupId>
        <artifactId>wildfly-maven-plugin</artifactId>
        <version>5.1.4.Final</version>
        <configuration>
            <feature-packs>
                <feature-pack>
                    <location>wildfly@maven(org.jboss.universe:community-universe)#37.0.1.Final</location>
                </feature-pack>
            </feature-packs>
            <!-- deploys the app on root web context -->
            <name>ROOT.war</name>
            <!-- Enable debugging -->
            <!-- https://www.jetbrains.com/help/idea/attaching-to-local-process.html#attach-to-local -->
            <!-- To attach a debugger to the running server from IntelliJ IDEA
                1. From the main menu, choose `Run | Attach to Process`
                2. IntelliJ IDEA will show the list of running local processes.
                    Select the process with the `jboss-module.jar (8787)` name to attach to.
            -->
            <debug>true</debug>
            <debugPort>8787</debugPort>
            <debugSuspend>false</debugSuspend>
        </configuration>
    </plugin>

    <!-- Plugin to run unit tests-->
    <!-- mvn test -->
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.5.3</version>
    </plugin>

    <!-- Plugin to run functional tests -->
    <!--  mvn failsafe:integration-test -->
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-failsafe-plugin</artifactId>
        <version>3.5.3</version>
    </plugin>
 ```

 - Comment below code in file pom.xml, because it causes a error for wildfly:dev

```
<!--        <dependency>-->
<!--            <groupId>org.jboss.weld.servlet</groupId>-->
<!--            <artifactId>weld-servlet-shaded</artifactId>-->
<!--            <version>6.0.3.Final</version>-->
<!--        </dependency>-->
```

- Refresh mvn

### Add ApplicationConfig

- Create a folder with name 'config' under ./src/main/java/dmit2015/

- Under 'config' folder, import file template: 'DMIT2015 Jakarta Persistence ApplicationConfig'

- Modify file ApplicationConfig.java content to below:

```
    package dmit2015.config;

    import jakarta.annotation.sql.DataSourceDefinition;
    import jakarta.annotation.sql.DataSourceDefinitions;
    import jakarta.enterprise.context.ApplicationScoped;

    /**
    * Define Jakarta Transaction API (JTA) data source definitions for usage in a
    * development environment that can reference the `name` attribute of the `@DataSourceDefinition`
    * in `persistence.xml` using the `<jta-data-source>` element.
    * <p>
    * In a production environment where the data source definition are defined in operating system environment variables
    * the <a href="https://github.com/wildfly-extras/wildfly-datasources-galleon-pack">WildFly Datasources Galleon Feature-Pack</a>
    * are used as an alternative to these data source definitions.
    *
    */
    @DataSourceDefinitions({

        @DataSourceDefinition(
            name="java:app/datasources/PostgreSQLDS",
            className="org.postgresql.xa.PGXADataSource",
            url="jdbc:postgresql://localhost/DMIT2015CourseDB",
            user="user2015",
            password="Password2015"),
    })

    @ApplicationScoped
    public class ApplicationConfig {

    }
```

### Add persistence.xml

- Under folder ./src/main/resources/META-INF, import file template: 'DMIT2015 Jakarta Persistence persistence.xml'

- Modify file persistence.xml content to below:

```
    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <persistence xmlns="https://jakarta.ee/xml/ns/persistence"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"
                version="3.0">

            <persistence-unit name="postgresql-jpa-pu" transaction-type="JTA">
                <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
                <jta-data-source>java:app/datasources/PostgreSQLDS</jta-data-source>

                <!-- Data source to use with wildfly-datasources-galleon-pack
                    https://github.com/wildfly-extras/wildfly-datasources-galleon-pack -->
        <!--            <jta-data-source>java:jboss/datasources/PostgreSQLDS</jta-data-source>-->

                <properties>
                    <!-- jakarta.persistence.schema-generation.database.action:
                    none, create, drop-and-create, drop, validate -->
                    <property name="jakarta.persistence.schema-generation.database.action" value="drop-and-create"/>
                </properties>
            </persistence-unit>
    </persistence>
```

### Update Models

- Add @Entity and @Id annotions to model files

- Task.java

```
    ......

    @Entity
    public class Task {
        @Id
        @Column(name="taskid", unique = true, nullable = false)
        private String id;
        ......
    }
```

- WeatherForcast.java

```
    ......

    @Entity
    public class WeatherForecast {
        @Id
        @Column(name="weatherforecastid",  unique = true, nullable = false)
        private String id;
        ......
    }
```

### Add JakartaPersistence service for database

- Add service for model Task

 - New => File from templates => DMIT2015 Model Service Interface Jakarta Persistence Implementation

 - Input parameters, such as ModelClass=Task, PrimaryKeyDataType=String, and primaryKey=id.

 - Add service for model WeatherForecast

 - New => File from templates => DMIT2015 Model Service Interface Jakarta Persistence Implementation

 - Input parameters, such as ModelClass=WeatherForecast, PrimaryKeyDataType=String, and primaryKey=id.

- Realize TODO sections in these two services


### Switch service to JakartaPersistence in faces

- In file TaskCrudView.java

```
    @Inject
    //@Named("memoryTaskService")
    @Named("jakartaPersistenceTaskService")
    private TaskService taskService;
```

- In file WeatherForecastCrudView.Java

```
    @Inject
    //@Named("memoryWeatherForecastService") // For In-Memory db service
    //@Named("firebaseHttpClientWeatherForecastService")  // For Firebase db service
    @Named("jakartaPersistenceWeatherForecastService")
    private WeatherForecastService weatherForecastService;
```

