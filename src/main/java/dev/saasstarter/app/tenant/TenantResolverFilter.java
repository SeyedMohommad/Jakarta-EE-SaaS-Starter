package dev.saasstarter.app.tenant;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import java.net.URI;

@Provider
@Priority(Priorities.AUTHENTICATION)
@ApplicationScoped
public class TenantResolverFilter implements ContainerRequestFilter {

    public static final String TENANT_HEADER = "X-Tenant-ID";

    @Inject
    TenantContext tenantContext;

    @Override
    public void filter(ContainerRequestContext ctx) {
        String tid = ctx.getHeaderString(TENANT_HEADER);
        if (tid == null || tid.isBlank()) {
            // try from subdomain of Host
            String host = ctx.getHeaderString("Host");
            if (host != null && host.contains(".")) {
                String sub = host.split("\\.")[0];
                if (!sub.equalsIgnoreCase("www")) {
                    tid = sub;
                }
            }
        }
        if (tid == null || tid.isBlank()) {
            tid = "public"; // default tenant fallback for demo
        }
        tenantContext.setTenantId(tid);
    }
}
