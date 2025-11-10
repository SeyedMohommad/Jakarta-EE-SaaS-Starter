package dev.saasstarter.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="comments", indexes = {@Index(name="idx_comments_task", columnList="task_id")})
public class Comment extends BaseEntity {

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="task_id")
    private Task task;

    @NotBlank
    @Column(nullable=false, length=2000)
    private String content;

    @Column(name="author_email", length=200, nullable=false)
    private String authorEmail;

    public Task getTask(){ return task; }
    public void setTask(Task task){ this.task = task; }

    public String getContent(){ return content; }
    public void setContent(String content){ this.content = content; }

    public String getAuthorEmail(){ return authorEmail; }
    public void setAuthorEmail(String authorEmail){ this.authorEmail = authorEmail; }
}
