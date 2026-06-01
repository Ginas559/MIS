package vn.iotstar.coolenglish.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.Payment;
import vn.iotstar.coolenglish.enums.PaymentStatus;

public class PaymentDAO extends AbstractDAO<Payment> {

    @Override
    protected void validateEntity(Payment entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Payment entity is required.");
        }
    }

    public Payment findByTransactionRef(String transactionRef) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            TypedQuery<Payment> query = em.createQuery(
                    "SELECT p FROM Payment p WHERE p.transactionRef = :transactionRef",
                    Payment.class);
            query.setParameter("transactionRef", transactionRef);
            java.util.List<Payment> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        }
    }

    public Payment findByTransactionRefWithInvoice(String transactionRef) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            TypedQuery<Payment> query = em.createQuery(
                    "SELECT p FROM Payment p LEFT JOIN FETCH p.invoice WHERE p.transactionRef = :transactionRef",
                    Payment.class);
            query.setParameter("transactionRef", transactionRef);
            java.util.List<Payment> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        }
    }

    /**
     * Tải Payment kèm hóa đơn, ghi danh, lớp, khóa, phòng, lịch, học viên (phục vụ InvoiceBuilder).
     */
    public Payment findByTransactionRefWithInvoiceContext(String transactionRef) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            TypedQuery<Payment> query = em.createQuery(
                    "SELECT DISTINCT p FROM Payment p "
                            + "LEFT JOIN FETCH p.invoice i "
                            + "LEFT JOIN FETCH i.enrollment e "
                            + "LEFT JOIN FETCH e.englishClass c "
                            + "LEFT JOIN FETCH c.course "
                            + "LEFT JOIN FETCH c.room "
                            + "LEFT JOIN FETCH c.schedule "
                            + "LEFT JOIN FETCH e.student s "
                            + "WHERE p.transactionRef = :transactionRef",
                    Payment.class);
            query.setParameter("transactionRef", transactionRef);
            java.util.List<Payment> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        }
    }

    public List<Payment> findPendingCashPayments() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            TypedQuery<Payment> query = em.createQuery(
                    "SELECT p FROM Payment p LEFT JOIN FETCH p.invoice "
                            + "WHERE p.method = :method AND p.status = :status ORDER BY p.paymentDate DESC",
                    Payment.class);
            query.setParameter("method", "CASH");
            query.setParameter("status", PaymentStatus.PENDING);
            return query.getResultList();
        }
    }
}
