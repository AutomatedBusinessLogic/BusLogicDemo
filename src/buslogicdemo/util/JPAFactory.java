package buslogicdemo.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.context.ManagedSessionContext;

import com.autobizlogic.abl.logic.LogicContext;

/**
 * A very simplistic class to handle sessions and transactions. In the real world, you will
 * most definitely NOT use static variables, but the goal here is to keep it simple and
 * easy to read.
 * 
 * This is the pure JPA version. If USE_JTA is made true, then JTA will be used.
 */
public class JPAFactory implements DemoFactory {

	/**
	 * Make this variable true if you want to use JTA. Please be aware that a numbers of things
	 * have to be set up properly for JTA to work properly: you must have a transaction manager
	 * available, and you must change persistence.xml to reflect that. In addition, you may need
	 * to update web.xml 
	 */
	private final static boolean USE_JTA = false;
	
	/**
	 * The sole instance of this class. Don't do this in a real app.
	 */
	private static JPAFactory instance;
	
	protected static EntityManagerFactory emf;
	protected EntityManager em;

	private EntityTransaction etx;

	/**
	 * Set up the
	 * @param realPath
	 * @return
	 */
	public static JPAFactory setup() {
		if (instance == null) {
			if (USE_JTA) {
				instance = JPAJTAFactory.setup();
			}
			else {
				instance = new JPAFactory();
			}
			
			if (emf == null) {
				Map<String, String> props = new HashMap<String, String>();
				props.put("hibernate.connection.url", "jdbc:hsqldb:mem:BusLogicDemo");
				emf = Persistence.createEntityManagerFactory("BusLogicDemo", props);
				
				if ( !DataLoader.initialDataLoaded) {
					instance.beginTransaction();
					Session session = (Session)instance.em.getDelegate();
					DataLoader.loadData(session);
					instance.commitTransaction();
				}
			}
		}
		
		return instance;
	}
	
	public static JPAFactory getInstance() {
		return instance;
	}
	
	@Override
	public void beginTransaction() {
		em = emf.createEntityManager();
		
		// Are we using ManagedSessionContext?
		if ("com.autobizlogic.abl.session.LogicManagedSessionContext".equals(emf.getProperties().get("hibernate.current_session_context_class")))
			ManagedSessionContext.bind((org.hibernate.classic.Session)em.getDelegate());
		etx = em.getTransaction();
		etx.begin();
	}
	
	@Override
	public void commitTransaction() {
		etx.commit();
		if ("com.autobizlogic.abl.session.LogicManagedSessionContext".equals(emf.getProperties().get("hibernate.current_session_context_class")))
			ManagedSessionContext.unbind(((Session)em.getDelegate()).getSessionFactory());
		em.close();
	}
	
	@Override
	public void rollbackTransaction() {
		if (etx.isActive())
			etx.rollback();
		if ("com.autobizlogic.abl.session.LogicManagedSessionContext".equals(emf.getProperties().get("hibernate.current_session_context_class")))
			ManagedSessionContext.unbind(((Session)em.getDelegate()).getSessionFactory());
		em.close();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getObjectById(Class<?> T, Serializable id) {
		return (T)em.find(T, id);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List query(String query) {
		return em.createQuery(query).getResultList();
	}
	
	@Override
	public void saveObject(Object bean) {
		em.persist(bean);
	}
	
	@Override
	public void deleteObject(Object bean) {
		em.remove(bean);
	}

	@Override
	public void setCurrentUseCaseName(String s) {
		if ( ! (em.getDelegate() instanceof Session))
			return;
		Session session = (Session)em.getDelegate();
		Transaction tx = session.getTransaction();
		LogicContext.setCurrentUseCaseName(session, tx, s);
	}
}
