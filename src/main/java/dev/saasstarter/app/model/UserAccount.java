package dev.saasstarter.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

@Entity
@Table(name="users", uniqueConstraints = @UniqueConstraint(name="uk_users_email", columnNames = {"tenant_id","email"}))
public class UserAccount extends BaseEntity {

    @Email @NotBlank
    @Column(nullable = false, length = 200)
    private String email;

    @NotBlank
    @Column(name="password_hash", nullable = false, length = 100)
    private String passwordHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="user_roles", joinColumns = @JoinColumn(name="user_id"))
    @Column(name="role", length = 32)
    private Set<String> roles;

    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email = email; }

    public String getPasswordHash(){ return passwordHash; }
    public void setPasswordHash(String passwordHash){ this.passwordHash = passwordHash; }

    public Set<String> getRoles(){ return roles; }
    public void setRoles(Set<String> roles){ this.roles = roles; }
}
