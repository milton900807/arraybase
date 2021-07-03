package com.arraybase.db;

import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class HBConnect {

	private static SessionFactory sessionFactory = null;
	private static GBLogger log = GBLogger.getLogger(HBConnect.class);

	static class MyConfiguration extends org.hibernate.cfg.Configuration {

		public void configureIt(String xml) {
			try {
				InputStream stream = new ByteArrayInputStream(
						xml.getBytes("UTF-8"));
				super.doConfigure(stream, "myconfiguration__909807");
			} catch (HibernateException exc) {
				exc.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Load hibernate with a properties object. This is not a hibenrate
	 * properties object; this is proprietary and follows the following format:
	 * 1) classes=$commadelim_fully_qualified_class_libs 2)
	 * cfg=fully_qualified_path_to_hibernate_configuration_file
	 * 
	 * @param properties
	 * @return
	 * @throws IOException
	 */
	private static SessionFactory createSessionFactory(Properties properties)
			throws IOException {
		MyConfiguration config = new MyConfiguration();
		Class[] c = HBType.All.getClasses();
		int index_a = 0;
		for (Class cc : c) {
			config.addAnnotatedClass(c[index_a++]);
		}
		String config_file = properties.getProperty("hibernate");
		// log.config("Configuring file\t" + config_file );
		if (config_file != null) {
			HBType type = HBType.All;
			String xml = type.getConfigFile();

			// File temp = File.createTempFile("hibernate", "cfg.xml");
			// log.config("Reading file: " + temp.getAbsolutePath() );
			// FileWriter write = new FileWriter(temp);
			// write.write(xml);
			// write.close();
			config.configureIt(xml);
			// config.configure(temp);
		}
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
				.applySettings(config.getProperties()).build();
		// log.println("Building session factory. ");

		SessionFactory ses = null;
		try {
			ses = config.buildSessionFactory(serviceRegistry);
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		// log.println("Session factory complete. ");
		return ses;
	}

	public static Map<String, String> convertToNonLazyObject(
			Map<String, String> paf) {
		Map<String, String> values = new LinkedHashMap<String, String>();
		Set keys = paf.keySet();
		for (Object k : keys) {
			values.put(k.toString(), paf.get(k.toString()));
		}
		return values;
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static Session getSession() {
		try {
			SessionFactory factory = getSessionFactory();
			if (factory == null) {
				createSessionFactory();
			}
			return getSessionFactory().getCurrentSession();
		} catch (Exception _e) {
			_e.printStackTrace();
			ABProperties.getProperties();

		}
		return null;
	}

	public static void close() {
		if (sessionFactory != null) {
			Session s = getSessionFactory().getCurrentSession();
			if (s != null && s.isOpen())
				s.close();
		}
	}

	public static void close(Session session) {
		try {
			if (session != null && session.isOpen())
				session.close();
		} catch (Exception _e) {
			_e.printStackTrace();
		}
	}

	public static void createSessionFactory() {
		try {
			sessionFactory = createSessionFactory(ABProperties.getProperties());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
