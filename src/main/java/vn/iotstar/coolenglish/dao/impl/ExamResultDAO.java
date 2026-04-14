package vn.iotstar.coolenglish.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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

    public void saveBatch(List<ExamResult> examResults) {
        if (examResults == null || examResults.isEmpty()) {
            return;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            int index = 0;
            for (ExamResult result : examResults) {
                validateEntity(result);
                if (result.getId() == null) {
                    em.persist(result);
                } else {
                    em.merge(result);
                }

                index++;
                if (index % 25 == 0) {
                    em.flush();
                    em.clear();
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

    public List<ExamResult> findByClassID(String classID) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<ExamResult> query = em.createQuery(
                    "SELECT er FROM ExamResult er "
                            + "WHERE er.classID = :classID "
                            + "ORDER BY er.takenAt DESC, er.examCode ASC, er.studentEmail ASC",
                    ExamResult.class);
            query.setParameter("classID", classID);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<ExamResult> findByClassIDAndExamCode(String classID, String examCode) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<ExamResult> query = em.createQuery(
                    "SELECT er FROM ExamResult er "
                            + "WHERE er.classID = :classID AND er.examCode = :examCode "
                            + "ORDER BY er.studentEmail ASC",
                    ExamResult.class);
            query.setParameter("classID", classID);
            query.setParameter("examCode", examCode);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public ExamResult findByClassIDAndStudentEmailAndExamCode(String classID, String studentEmail, String examCode) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<ExamResult> query = em.createQuery(
                    "SELECT er FROM ExamResult er "
                            + "WHERE er.classID = :classID "
                            + "AND er.studentEmail = :studentEmail "
                            + "AND er.examCode = :examCode",
                    ExamResult.class);
            query.setParameter("classID", classID);
            query.setParameter("studentEmail", studentEmail);
            query.setParameter("examCode", examCode);
            List<ExamResult> results = query.setMaxResults(1).getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    public boolean existsByClassIDAndExamCode(String classID, String examCode) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery(
                    "SELECT COUNT(er) FROM ExamResult er WHERE er.classID = :classID AND er.examCode = :examCode",
                    Long.class)
                    .setParameter("classID", classID)
                    .setParameter("examCode", examCode)
                    .getSingleResult();
            return count != null && count > 0;
        } finally {
            em.close();
        }
    }

    public List<ExamResult> findByStudentEmail(String studentEmail) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<ExamResult> query = em.createQuery(
                    "SELECT er FROM ExamResult er "
                            + "WHERE er.studentEmail = :studentEmail "
                            + "ORDER BY er.takenAt DESC, er.classID ASC, er.examCode ASC",
                    ExamResult.class);
            query.setParameter("studentEmail", studentEmail);
            return query.getResultList();
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

