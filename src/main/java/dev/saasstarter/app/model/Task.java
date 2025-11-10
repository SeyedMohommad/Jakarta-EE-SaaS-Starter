package dev.saasstarter.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="tasks", indexes = @Index(name="idx_tasks_project", columnList="project_id"))
public class Task extends BaseEntity {

    @NotBlank
    @Column(nullable=false, length=300)
    private String title;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="project_id")
    private Project project;

    @Column(name="is_done", nullable=false)
    private boolean done;

    public String getTitle(){ return title; }
    public void setTitle(String title){ this.title = title; }
    public Project getProject(){ return project; }
    public void setProject(Project project){ this.project = project; }
    public boolean isDone(){ return done; }
    public void setDone(boolean done){ this.done = done; }
}
