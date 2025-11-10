package dev.saasstarter.app.resources;

import dev.saasstarter.app.model.Project;
import dev.saasstarter.app.repo.BaseRepository;
import dev.saasstarter.app.security.Roles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@RequestScoped
@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({Roles.USER, Roles.ADMIN})
public class ProjectResource {

    @Inject BaseRepository repo;

    @GET
    public List<Project> list(@QueryParam("page") String page, @QueryParam("size") String size,
                              @QueryParam("sort") @DefaultValue("id") String sort,
                              @QueryParam("asc") @DefaultValue("true") String asc) {
        int p = CrudUtil.parseIntOr(page, 0);
        int s = CrudUtil.parseIntOr(size, 20);
        boolean a = CrudUtil.parseBoolOr(asc, true);
        return repo.listPage(Project.class, p, s, sort, a);
    }

    @POST @Transactional
    public Project create(Project p){ return repo.create(p); }

    @GET @Path("{id}")
    public Project one(@PathParam("id") Long id){
        Project p = repo.find(Project.class, id);
        if (p == null) throw new NotFoundException();
        return p;
    }

    @PUT @Path("{id}") @Transactional
    public Project update(@PathParam("id") Long id, Project in){
        Project p = repo.update(Project.class, id, e -> {
            e.setName(in.getName());
            e.setDescription(in.getDescription());
        });
        if (p == null) throw new NotFoundException();
        return p;
    }

    @DELETE @Path("{id}") @Transactional
    public void delete(@PathParam("id") Long id){
        if (!repo.delete(Project.class, id)) throw new NotFoundException();
    }
}
