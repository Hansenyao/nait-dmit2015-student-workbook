package common.config;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApplicationConfig {
}

@DataSourceDefinition(
        name="java:app/datasources/MSSQLServerDWPubsSalesDS",
        className="com.microsoft.sqlserver.jdbc.SQLServerDataSource",
        url="jdbc:sqlserver://localhost;databaseName=DWPubsSales;TrustServerCertificate=true",
        user="user2015",
        password="Password2015"),