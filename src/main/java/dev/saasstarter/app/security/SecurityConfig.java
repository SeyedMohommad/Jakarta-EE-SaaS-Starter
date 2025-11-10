package dev.saasstarter.app.security;

import jakarta.annotation.security.DeclareRoles;
import jakarta.enterprise.context.ApplicationScoped;

@DeclareRoles({Roles.USER, Roles.ADMIN})
@ApplicationScoped
public class SecurityConfig { }
