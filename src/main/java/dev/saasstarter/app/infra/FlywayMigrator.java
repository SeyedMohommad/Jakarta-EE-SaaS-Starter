package dev.saasstarter.app.infra;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;

@Startup
@Singleton
public class FlywayMigrator {

    @Inject
    @jakarta.annotation.Resource(lookup = "jdbc/PostgresDS")
    private DataSource ds;

    @PostConstruct
    public void migrate(){
        Flyway flyway = Flyway.configure()
                .dataSource(ds)
                .baselineOnMigrate(true)
                .locations("classpath:db/migration")
                .load();
        flyway.migrate();
    }
}
