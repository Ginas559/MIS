package vn.iotstar.coolenglish.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JPAUtil {

	private static final JPAUtil INSTANCE = new JPAUtil();
	private static volatile EntityManagerFactory factory = null;

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
				}
			}
		}
		return factory.createEntityManager();
	}

	public static void shutDown() {
		if (factory != null && factory.isOpen()) {
			factory.close();
		}
	}
}

