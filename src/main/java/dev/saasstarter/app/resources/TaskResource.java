package dev.saasstarter.app.resources;

import dev.saasstarter.app.model.Project;
import dev.saasstarter.app.model.Task;
import dev.saasstarter.app.repo.BaseRepository;
import dev.saasstarter.app.security.Roles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@RequestScoped
@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({Roles.USER, Roles.ADMIN})
public class TaskResource {

    @Inject BaseRepository repo;
    @Inject EntityManager em;

    @GET
    public List<Task> list(@QueryParam("page") String page, @QueryParam("size") String size,
                           @QueryParam("sort") @DefaultValue("id") String sort,
                           @QueryParam("asc") @DefaultValue("true") String asc) {
        int p = CrudUtil.parseIntOr(page, 0);
        int s = CrudUtil.parseIntOr(size, 20);
        boolean a = CrudUtil.parseBoolOr(asc, true);
        return repo.listPage(Task.class, p, s, sort, a);
    }

    @POST @Transactional
    public Task create(Task t){
        // ensure project exists & in same tenant
        Project proj = repo.find(Project.class, t.getProject().getId());
        if (proj == null) throw new NotFoundException("project not found");
        t.setProject(proj);
        return repo.create(t);
    }

    @PUT @Path("{id}") @Transactional
    public Task update(@PathParam("id") Long id, Task in){
        Task t = repo.update(Task.class, id, e -> {
            e.setTitle(in.getTitle());
            e.setDone(in.isDone());
            if (in.getProject() != null && in.getProject().getId() != null) {
                Project p = repo.find(Project.class, in.getProject().getId());
                if (p == null) throw new NotFoundException("project not found");
                e.setProject(p);
            }
        });
        if (t == null) throw new NotFoundException();
        return t;
    }

    @DELETE @Path("{id}") @Transactional
    public void delete(@PathParam("id") Long id){
        if (!repo.delete(Task.class, id)) throw new NotFoundException();
    }
}
