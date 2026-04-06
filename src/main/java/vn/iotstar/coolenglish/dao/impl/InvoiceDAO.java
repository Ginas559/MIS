package vn.iotstar.coolenglish.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.Invoice;

public class InvoiceDAO extends AbstractDAO<Invoice> {

    @Override
    protected void validateEntity(Invoice entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Invoice entity is required.");
        }
    }

    public Invoice findByInvoiceNumber(String invoiceNumber) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Invoice> query = em.createQuery(
                    "SELECT i FROM Invoice i WHERE i.invoiceNumber = :invoiceNumber", Invoice.class);
            query.setParameter("invoiceNumber", invoiceNumber);
            java.util.List<Invoice> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    public java.util.List<Invoice> findByEnrollmentId(Long enrollmentId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Invoice> query = em.createQuery(
                    "SELECT i FROM Invoice i WHERE i.enrollment.id = :enrollmentId ORDER BY i.createdAt DESC",
                    Invoice.class);
            query.setParameter("enrollmentId", enrollmentId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

