package dmit2015.config;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.enterprise.context.ApplicationScoped;

@DataSourceDefinition(
        name="java:app/datasources/MSSQLServerDWPubsSalesDS",
        className="com.microsoft.sqlserver.jdbc.SQLServerDataSource",
        url="jdbc:sqlserver://localhost;databaseName=DWPubsSales;TrustServerCertificate=true",
        user="user2015",
        password="Password2015")

@ApplicationScoped
public class ApplicationConfig {
}
