package dev.saasstarter.app.infra;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;

@ApplicationScoped
public class HealthChecks {

    @Liveness
    @ApplicationScoped
    public static class Live implements HealthCheck {
        @Override
        public HealthCheckResponse call() {
            return HealthCheckResponse.up("liveness");
        }
    }

    @Readiness
    @ApplicationScoped
    public static class Ready implements HealthCheck {
        @Override
        public HealthCheckResponse call() {
            return HealthCheckResponse.up("readiness");
        }
    }
}
