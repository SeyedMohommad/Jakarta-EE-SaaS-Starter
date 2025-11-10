package dev.saasstarter.app.websocket;

import dev.saasstarter.app.model.Comment;
import dev.saasstarter.app.tenant.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@ServerEndpoint("/ws/comments/{projectId}")
public class CommentSocket {

    private static final ConcurrentHashMap<String, Set<Session>> sessionsByTenantProject = new ConcurrentHashMap<>();

    @Inject TenantContext tenant; // resolved per-connection in most runtimes

    private static String key(String tenant, String projectId){ return tenant + ":" + projectId; }

    @OnOpen
    public void onOpen(Session session, @PathParam("projectId") String projectId){
        String t = tenant.getTenantId() == null ? "public" : tenant.getTenantId();
        sessionsByTenantProject.computeIfAbsent(key(t, projectId), k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("projectId") String projectId){
        String t = tenant.getTenantId() == null ? "public" : tenant.getTenantId();
        Set<Session> set = sessionsByTenantProject.get(key(t, projectId));
        if (set != null) set.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable thr){}

    public void broadcastNewComment(Comment c){
        try (Jsonb jsonb = JsonbBuilder.create()) {
            String payload = jsonb.toJson(c);
            String k = key(c.getTenantId(), String.valueOf(c.getTask().getProject().getId()));
            Set<Session> set = sessionsByTenantProject.get(k);
            if (set != null) {
                for (Session s : set) {
                    if (s.isOpen()) {
                        s.getAsyncRemote().sendText(payload);
                    }
                }
            }
        } catch (Exception e) { /* ignore */ }
    }
}
