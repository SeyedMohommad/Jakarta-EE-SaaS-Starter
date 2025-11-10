package dev.saasstarter.app.model;

import jakarta.persistence.*;
import java.time.Instant;

@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="tenant_id", nullable = false, length = 64)
    private String tenantId;

    @Column(name="created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name="updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void preUpdate(){ this.updatedAt = Instant.now(); }

    public Long getId(){ return id; }
    public String getTenantId(){ return tenantId; }
    public void setTenantId(String tenantId){ this.tenantId = tenantId; }
    public Instant getCreatedAt(){ return createdAt; }
    public Instant getUpdatedAt(){ return updatedAt; }
}
