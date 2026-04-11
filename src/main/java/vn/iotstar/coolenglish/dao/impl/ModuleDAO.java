package vn.iotstar.coolenglish.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.entity.AcademicContent;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.Module;

public class ModuleDAO extends AbstractDAO<Module> {

    @Override
    protected void validateEntity(Module entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Module entity is required.");
        }
        if (entity.getTitle() == null || entity.getTitle().isBlank()) {
            throw new IllegalArgumentException("Module title is required.");
        }
    }

    public Module findByIdWithChildren(Long moduleId) {
        if (moduleId == null) {
            return null;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Module> query = em.createQuery(
                    "SELECT DISTINCT m FROM Module m LEFT JOIN FETCH m.children WHERE m.id = :moduleId",
                    Module.class);
            query.setParameter("moduleId", moduleId);
            List<Module> modules = query.getResultList();
            if (modules.isEmpty()) {
                return null;
            }

            Module root = modules.get(0);
            initializeTree(root);
            return root;
        } finally {
            em.close();
        }
    }

    private void initializeTree(Module module) {
        if (module == null) {
            return;
        }

        List<AcademicContent> children = module.getChildren();
        children.size();
        for (AcademicContent child : children) {
            if (child instanceof Module childModule) {
                initializeTree(childModule);
            }
        }
    }

    public List<Module> findRootModules() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Module> query = em.createQuery(
                    "SELECT m FROM Module m WHERE m.parentModule IS NULL ORDER BY m.id",
                    Module.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

