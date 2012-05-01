package buslogicdemo.util;

import javax.naming.InitialContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.hibernate.Session;

/**
 * A very simplistic class to handle sessions and transactions. 
 * 
 * This is the JPA + JTA version.
 */
public class JPAJTAFactory extends JPAFactory {

	private UserTransaction jtaTx;

	public static JPAJTAFactory setup() {
		JPAJTAFactory instance = new JPAJTAFactory();
		try {
			InitialContext ctxt = new InitialContext();
			instance.jtaTx = (UserTransaction)ctxt.lookup("java:comp/UserTransaction");
			instance.jtaTx.begin();
		}
		catch(Exception ex) {
			throw new RuntimeException("Error during JTA setup", ex);
		}
		
		if ( !DataLoader.initialDataLoaded) {
			instance.beginTransaction();
			Session session = (Session)instance.em.getDelegate();
			DataLoader.loadData(session);
			instance.commitTransaction();
		}
		
		System.out.println("You are now using JTA for transaction management");
		return instance;
	}
	
	@Override
	public void beginTransaction() {
		try {
			if (jtaTx.getStatus() != Status.STATUS_ACTIVE) {
				jtaTx.begin();
			}
		}
		catch(Exception ex) {
			throw new RuntimeException("Error during JTA beginTransaction", ex);
		}

		em = emf.createEntityManager();
		//em.joinTransaction();
	}
	
	@Override
	public void commitTransaction() {
		try {
			jtaTx.commit();
			em.close();
		}
		catch(Exception ex) {
			throw new RuntimeException("Error during JTA commit", ex);
		}
	}
	
	@Override
	public void rollbackTransaction() {
		try {
			jtaTx.rollback();
			em.close();
		}
		catch(Exception ex) {
			throw new RuntimeException("Error during JTA rollback", ex);
		}
	}
}
