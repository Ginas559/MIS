package vn.iotstar.coolenglish.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JPAUtil {

	private static final JPAUtil INSTANCE = new JPAUtil();
	private static volatile EntityManagerFactory factory = null;
	private static volatile boolean schemaCompatibilityChecked = false;

	private JPAUtil() {
	}

	public static JPAUtil getInstance() {
		return INSTANCE;
	}

	public static EntityManager getEntityManager() {
		if (factory == null || !factory.isOpen()) {
			synchronized (JPAUtil.class) {
				if (factory == null || !factory.isOpen()) {
					factory = Persistence.createEntityManagerFactory("CoolEnglishPU");
					schemaCompatibilityChecked = false;
				}
			}
		}
		ensureSchemaCompatibility();
		return factory.createEntityManager();
	}

	private static void ensureSchemaCompatibility() {
		if (schemaCompatibilityChecked) {
			return;
		}

		synchronized (JPAUtil.class) {
			if (schemaCompatibilityChecked) {
				return;
			}

			EntityManager em = factory.createEntityManager();
			try {
				if (!hasColumn(em, "is_active")) {
					em.getTransaction().begin();
					em.createNativeQuery("ALTER TABLE user_account ADD is_active BIT NOT NULL DEFAULT (1)").executeUpdate();

					if (hasColumn(em, "active")) {
						em.createNativeQuery("UPDATE user_account SET is_active = CAST(active AS BIT)").executeUpdate();
					}

					em.getTransaction().commit();
				}
				schemaCompatibilityChecked = true;
			} catch (RuntimeException ex) {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
				if (hasColumn(em, "is_active")) {
					schemaCompatibilityChecked = true;
					return;
				}
				throw ex;
			} finally {
				em.close();
			}
		}
	}

	private static boolean hasColumn(EntityManager em, String columnName) {
		Number count = (Number) em.createNativeQuery(
				"SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'user_account' AND COLUMN_NAME = ?")
				.setParameter(1, columnName)
				.getSingleResult();
		return count != null && count.intValue() > 0;
	}

	public static void shutDown() {
		if (factory != null && factory.isOpen()) {
			factory.close();
		}
	}
}

