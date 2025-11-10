package dev.saasstarter.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="projects")
public class Project extends BaseEntity {

    @NotBlank
    @Column(nullable = false, length = 200)
    private String name;

    @Column(length=1000)
    private String description;

    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }
    public String getDescription(){ return description; }
    public void setDescription(String description){ this.description = description; }
}
