package vn.iotstar.coolenglish.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.ExamResult;

public class ExamResultDAO extends AbstractDAO<ExamResult> {

    @Override
    protected void validateEntity(ExamResult entity) {
        if (entity == null) {
            throw new IllegalArgumentException("ExamResult entity is required.");
        }
    }

    public void insertBatch(List<ExamResult> examResults) {
        if (examResults == null || examResults.isEmpty()) {
            return;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            for (ExamResult result : examResults) {
                validateEntity(result);
                if (!existsDuplicate(em, result)) {
                    em.persist(result);
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    private boolean existsDuplicate(EntityManager em, ExamResult candidate) {
        Long count = em.createQuery(
                "SELECT COUNT(er) FROM ExamResult er "
                        + "WHERE er.partnerCode = :partnerCode "
                        + "AND er.studentEmail = :studentEmail "
                        + "AND er.examCode = :examCode "
                        + "AND er.takenAt = :takenAt",
                Long.class)
                .setParameter("partnerCode", candidate.getPartnerCode())
                .setParameter("studentEmail", candidate.getStudentEmail())
                .setParameter("examCode", candidate.getExamCode())
                .setParameter("takenAt", candidate.getTakenAt())
                .getSingleResult();

        return count != null && count > 0;
    }
}

