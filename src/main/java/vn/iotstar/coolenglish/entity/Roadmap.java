package vn.iotstar.coolenglish.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Roadmaps")
public class Roadmap implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "roadmap_code", nullable = false, unique = true, length = 50)
    private String roadmapCode;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_module_id", nullable = false)
    private Module rootModule;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public Roadmap() {
    }

    public Roadmap(String roadmapCode, String title, String description, Module rootModule, boolean active) {
        this.roadmapCode = roadmapCode;
        this.title = title;
        this.description = description;
        this.rootModule = rootModule;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoadmapCode() {
        return roadmapCode;
    }

    public void setRoadmapCode(String roadmapCode) {
        this.roadmapCode = roadmapCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Module getRootModule() {
        return rootModule;
    }

    public void setRootModule(Module rootModule) {
        this.rootModule = rootModule;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

