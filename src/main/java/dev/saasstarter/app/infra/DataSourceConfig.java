package dev.saasstarter.app.infra;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.annotation.sql.DataSourceDefinitions;
import jakarta.enterprise.context.ApplicationScoped;

@DataSourceDefinitions({
        @DataSourceDefinition(
                name = "java:global/jdbc/H2DS",
                className = "org.h2.jdbcx.JdbcDataSource",
                url = "jdbc:h2:mem:appdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false",
                user = "sa",
                password = "",
                minPoolSize = 1,
                maxPoolSize = 10
        ),
        @DataSourceDefinition(
                name = "java:global/jdbc/H2DS__pm",
                className = "org.h2.jdbcx.JdbcDataSource",
                url = "jdbc:h2:mem:appdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false",
                user = "sa",
                password = "",
                minPoolSize = 1,
                maxPoolSize = 10
        )
})
@ApplicationScoped
public class DataSourceConfig { }
