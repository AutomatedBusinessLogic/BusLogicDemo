package buslogicdemo.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.autobizlogic.abl.logic.LogicContext;

/**
 * This is a VERY simplistic class that is emphatically NOT meant as an example
 * of how to do this in the real world. The main goal here was maximum simplicity.
 */
public class HibernateFactory {
	public static Transaction tx = null;
	public static Session session = null;
	public static Configuration config;
	public static SessionFactory sessionFactory = null;
	private static boolean manual = false;

	/**
	 * Set up Hibernate. This can be called any number of times, but only the first time
	 * will actually do anything.
	 * @param realPath The path to the application's root directory, used to find the database files
	 * @param useManual Whether to use ABL or not. True means that we don't use ABL.
	 */
	public static void setup(boolean useManual) {
		
		// Do we need to re-initialize Hibernate?
		if (config != null && (useManual != manual)) {
			config = null;
		}
		
		if (config != null)
			return;
		
		manual = useManual;
		config = new Configuration().configure("/buslogicdemo/data/buslogicdemo.cfg.xml");
		
		// Which current session context class should we use? The normal one, or ABL's ?
		if (useManual)
			config.setProperty("hibernate.current_session_context_class", "org.hibernate.context.ThreadLocalSessionContext");
		else
			config.setProperty("hibernate.current_session_context_class", "com.autobizlogic.abl.session.LogicThreadLocalSessionContext");
		
		// If using HSQL, we use a local database, otherwise we assume the database is specified in the config.
		if (config.getProperty("hibernate.dialect").equals("org.hibernate.dialect.HSQLDialect")) {

			config.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:BusLogicDemo");
		}

		sessionFactory = config.buildSessionFactory();
		
		if (session == null && !DataLoader.initialDataLoaded) { // We're just started: load the sample data
			beginTransaction();
			DataLoader.loadData(session);
			commitTransaction();
			if (DemoEventListener.getInstance() != null)
				DemoEventListener.getInstance().resetEvents();
		}
	}

	public static void beginTransaction() {
		session = sessionFactory.getCurrentSession();
		tx = session.beginTransaction();
	}
	
	public static void commitTransaction() {
		tx.commit();
	}

	public static void rollbackTransaction() {
		tx.rollback();
	}
	
	public static void setCurrentUseCaseName(String s) {
		LogicContext.setCurrentUseCaseName(session, tx, s);
	}
}
