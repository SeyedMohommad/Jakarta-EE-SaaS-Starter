package dev.saasstarter.app.resources;

import dev.saasstarter.app.model.UserAccount;
import dev.saasstarter.app.repo.BaseRepository;
import dev.saasstarter.app.tenant.TenantContext;
import dev.saasstarter.app.dto.AuthDtos.*;
import dev.saasstarter.app.security.Roles;
import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RequestScoped
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject EntityManager em;
    @Inject BaseRepository repo;
    @Inject TenantContext tenant;

    @POST
    @Path("/register")
    @Transactional
    @PermitAll
    public Response register(RegisterRequest req){
        if (req == null || req.email == null || req.password == null) {
            throw new BadRequestException("email/password required");
        }
        // uniqueness check
        long count = em.createQuery("SELECT COUNT(u) FROM UserAccount u WHERE u.tenantId=:tid AND LOWER(u.email)=:e", Long.class)
                .setParameter("tid", tenant.getTenantId())
                .setParameter("e", req.email.toLowerCase())
                .getSingleResult();
        if (count > 0) {
            return Response.status(Response.Status.CONFLICT).entity(Map.of("message","email exists")).build();
        }
        UserAccount u = new UserAccount();
        u.setEmail(req.email.toLowerCase());
        u.setPasswordHash(BCrypt.withDefaults().hashToString(12, req.password.toCharArray()));
        u.setRoles(Set.of(Roles.USER));
        repo.create(u);
        return Response.status(Response.Status.CREATED).entity(Map.of("id", u.getId())).build();
    }

    @POST
    @Path("/login")
    @PermitAll
    public TokenResponse login(LoginRequest req){
        UserAccount u = em.createQuery("SELECT u FROM UserAccount u WHERE u.tenantId=:tid AND LOWER(u.email)=:e", UserAccount.class)
                .setParameter("tid", tenant.getTenantId())
                .setParameter("e", req.email.toLowerCase())
                .getResultStream().findFirst().orElseThrow(() -> new NotAuthorizedException("invalid credentials"));
        var res = BCrypt.verifyer().verify(req.password.toCharArray(), u.getPasswordHash());
        if (!res.verified) throw new NotAuthorizedException("invalid credentials");

        String token = Jwt.issuer("saas-starter")
                .upn(u.getEmail())
                .subject(u.getEmail())
                .groups(u.getRoles())
                .claim("tenant", tenant.getTenantId())
                .expiresIn(Duration.ofHours(12))
                .sign();
        return new TokenResponse(token);
    }
}
