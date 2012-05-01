package buslogicdemo.test;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * Sample superclass for test case classes.
 */
public abstract class LogicTest {

	protected Transaction hibTx = null;
	protected Session hibSession = null;
	protected Configuration hibConfig;
	protected SessionFactory hibSessionFactory = null;

	public void setup() {
		if (hibConfig == null)
			hibConfig = new Configuration();

		if (hibSessionFactory == null) {
			
			hibConfig.configure("/buslogicdemo/data/buslogicdemo.cfg.xml");
			hibSessionFactory = hibConfig.buildSessionFactory();
		}
	}
	
	public void cleanup() {
		// Do nothing
	}
	
	public void beginTransaction() {
		hibSession = hibSessionFactory.getCurrentSession();
		hibTx = hibSession.beginTransaction();
	}
	
	public void commitTransaction() {
		hibTx.commit();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getObjectById(Class<T> cls, Serializable id) {
		return (T)hibSession.get(cls, id);
	}
	
	public void saveObject(Object bean) {
		hibSession.save(bean);
	}
	
	public void deleteObject(Object bean) {
		hibSession.delete(bean);
	}
}
