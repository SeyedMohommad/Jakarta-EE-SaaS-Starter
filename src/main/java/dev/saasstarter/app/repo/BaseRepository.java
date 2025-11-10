package dev.saasstarter.app.repo;

import dev.saasstarter.app.tenant.TenantContext;
import dev.saasstarter.app.model.BaseEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.function.Consumer;

@RequestScoped
public class BaseRepository {

    @Inject EntityManager em;
    @Inject TenantContext tenant;

    public <T extends BaseEntity> T create(T entity){
        entity.setTenantId(tenant.getTenantId());
        em.persist(entity);
        return entity;
    }

    public <T extends BaseEntity> T find(Class<T> clazz, Long id){
        T e = em.find(clazz, id);
        if (e == null) return null;
        if (!tenant.getTenantId().equals(e.getTenantId())) return null;
        return e;
    }

    public <T extends BaseEntity> T update(Class<T> clazz, Long id, Consumer<T> updater){
        T e = find(clazz, id);
        if (e == null) return null;
        updater.accept(e);
        return e;
    }

    public <T extends BaseEntity> boolean delete(Class<T> clazz, Long id){
        T e = find(clazz, id);
        if (e == null) return false;
        em.remove(e);
        return true;
    }

    public <T extends BaseEntity> List<T> listPage(Class<T> clazz, int page, int size, String sort, boolean asc){
        String s = (sort == null || sort.isBlank()) ? "id" : sort;
        String dir = asc ? "ASC" : "DESC";
        String jpql = "SELECT e FROM " + clazz.getSimpleName() + " e WHERE e.tenantId = :tid ORDER BY e." + s + " " + dir;
        TypedQuery<T> q = em.createQuery(jpql, clazz);
        q.setParameter("tid", tenant.getTenantId());
        q.setFirstResult(Math.max(0, page) * Math.max(1, size));
        q.setMaxResults(Math.max(1, size));
        return q.getResultList();
    }
}
