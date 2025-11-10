package dev.saasstarter.app.repo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class EntityManagerProducer {
    @PersistenceContext(unitName="appPU")
    private EntityManager em;

    @Produces
    public EntityManager em(){ return em; }
}
