package dev.saasstarter.app.resources;

import dev.saasstarter.app.model.Comment;
import dev.saasstarter.app.model.Task;
import dev.saasstarter.app.repo.BaseRepository;
import dev.saasstarter.app.security.Roles;
import dev.saasstarter.app.websocket.CommentSocket;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@RequestScoped
@Path("/comments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({Roles.USER, Roles.ADMIN})
public class CommentResource {

    @Inject BaseRepository repo;
    @Inject CommentSocket socket;

    @GET
    public List<Comment> list(@QueryParam("page") String page, @QueryParam("size") String size,
                              @QueryParam("sort") @DefaultValue("id") String sort,
                              @QueryParam("asc") @DefaultValue("true") String asc) {
        int p = CrudUtil.parseIntOr(page, 0);
        int s = CrudUtil.parseIntOr(size, 20);
        boolean a = CrudUtil.parseBoolOr(asc, true);
        return repo.listPage(Comment.class, p, s, sort, a);
    }

    @POST @Transactional
    public Comment create(Comment c){
        Task t = repo.find(Task.class, c.getTask().getId());
        if (t == null) throw new NotFoundException("task not found");
        c.setTask(t);
        Comment created = repo.create(c);
        // broadcast
        socket.broadcastNewComment(created);
        return created;
    }
}
