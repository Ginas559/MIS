package vn.iotstar.coolenglish.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Modules")
@DiscriminatorValue("MODULE")
public class Module extends AcademicContentEntity {

    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "parentModule", targetEntity = AcademicContentEntity.class,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<AcademicContent> children = new ArrayList<>();

    public Module() {
    }

    public Module(String title, String description) {
        super(title, description);
    }

    @Override
    public void add(AcademicContent content) {
        if (content == null || children.contains(content)) {
            return;
        }
        children.add(content);
        if (content instanceof AcademicContentEntity contentEntity) {
            contentEntity.setParentModule(this);
        }
    }

    @Override
    public void remove(AcademicContent content) {
        if (content == null) {
            return;
        }
        if (children.remove(content) && content instanceof AcademicContentEntity contentEntity) {
            contentEntity.setParentModule(null);
        }
    }

    @Override
    public List<AcademicContent> getChildren() {
        return children;
    }

    public void setChildren(List<AcademicContent> children) {
        this.children = children != null ? children : new ArrayList<>();
    }
}
