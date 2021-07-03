package com.arraybase.modules;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import com.arraybase.db.HBType;

public class InstallHBConnection {
	private static SessionFactory sessionFactory = null;

	public static Session getSession() {
		try {
			SessionFactory factory = getSessionFactory();
			if (factory == null)
				return null;
			Session fac = getSessionFactory().getCurrentSession();
			return fac;
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return null;
	}

	private static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static void init(File _config_file) {
		sessionFactory = initSessionFactory(HBType.All, _config_file);
	}

	private static SessionFactory initSessionFactory(HBType hbType,
			File config_file) {
		try {
			AnnotationConfiguration config = new AnnotationConfiguration();
			
			Class[] c = hbType.getClasses();
			int index_a = 0;
			for (Class cc : c) {
				config.addAnnotatedClass(c[index_a++]);
			}

			config.configure(config_file);

			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
					.applySettings(config.getProperties())
					.build();
			SessionFactory ses = config.buildSessionFactory(serviceRegistry);
			return ses;
		} catch (Exception _e) {
			_e.printStackTrace();

		}
		return null;
	}

	public static void close(HBType _key) {
		close();
	}

	public static void close() {
		Session s = sessionFactory.getCurrentSession();
		if (s.isOpen())
			s.close();
	}

}
