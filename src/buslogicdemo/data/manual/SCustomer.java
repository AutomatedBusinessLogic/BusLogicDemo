package buslogicdemo.data.manual;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import buslogicdemo.util.HibernateFactory;
import buslogicdemo.data.*;

/**
 * Business logic layer for the Customer class.
 * This class is intended as a demonstration of implementing business "by hand", as opposed 
 * to using ABL's declarative business logic. As such, it is not a complete class -- it only contains
 * those methods that are actually used by the demo application.
 */
public class SCustomer {
	private Customer customer;
	
	protected SCustomer(Customer cust) {
		customer = cust;
	}
	
	/**
	 * Get the underlying Hibernate bean.
	 */
	protected Customer getBean() { return customer; }
	
	/**
	 * Retrieve an instance by name.
	 * @param name The customer's primary key
	 * @return The customer service object
	 */
	public static SCustomer getByName(String name) {
		Customer cust = (Customer)HibernateFactory.session.load(Customer.class, name);
		SCustomer result = new SCustomer(cust);
		return result;
	}
	
	/**
	 * Get all customers, ordered by name.
	 * @return An unmodifiable list of customer service objects.
	 */
	public static List<SCustomer> getAll() {
		@SuppressWarnings("unchecked")
		List<Customer> customers = HibernateFactory.session.createQuery("from Customer order by name").list();
		List<SCustomer> result = new Vector<SCustomer>();
		for (Customer c : customers) {
			result.add(new SCustomer(c));
		}
		return Collections.unmodifiableList(result);
	}
	
	/**
	 * Persist any changes to this customer.
	 */
	public void save() {
		HibernateFactory.session.saveOrUpdate(customer);
	}

	/**
	 * Get the customer's name
	 */
	public String getName() { return customer.getName(); }
	
	/**
	 * Get the customer's credit limit.
	 */
	public BigDecimal getCreditLimit() { return customer.getCreditLimit(); }
	
	/**
	 * Update the customer's credit limit. If the customer's balance is greater than
	 * this new credit limit, an exception will be thrown.
	 */
	public void setCreditLimit(BigDecimal cred) {
		customer.setCreditLimit(cred);
		checkCredit();
	}
	
	/**
	 * Get the customer's balance, which is calculated as the sum of the customer's
	 * unpaid Purchaseorders.
	 */
	public BigDecimal getBalance() { return customer.getBalance(); }
	
	/**
	 * Internal method to change the value of the customer's balance and also check
	 * the credit limit. If the customer's new balance is greater than the credit limit,
	 * an exception will be thrown.
	 * @param delta The amount by which to adjust the balance - can be positive or negative.
	 */
	protected void updateBalance(BigDecimal delta) {
		customer.setBalance(customer.getBalance().add(delta));
		checkCredit();
	}
	
	/**
	 * Get the customer's purchase orders as an unmodifiable set of service objects.
	 */
	public Set<SPurchaseorder> getPurchaseorders() {
		Set<SPurchaseorder> result = new HashSet<SPurchaseorder>();
		for (Purchaseorder po : customer.getPurchaseorders())
			result.add(new SPurchaseorder(po));
		return Collections.unmodifiableSet(result);
	}
	
	/**
	 * Add a Purchaseorder to this customer. If the Purchaseorder is unpaid, this will
	 * adjust the customer's balance. If the new balance exceeds the credit limit, an
	 * exception will be thrown.
	 */
	public void addPurchaseorder(SPurchaseorder po) {
		if ( ! po.getPaid())
			updateBalance(po.getAmountTotal());
		customer.getPurchaseorders().add(po.getBean());
	}
	
	/**
	 * Remove the given Purchaseorder from this customer. If the Purchaseorder is unpaid, 
	 * this will adjust the customer's balance.
	 */
	public void removePurchaseorder(SPurchaseorder po) {
		if ( ! po.getPaid())
			updateBalance(po.getAmountTotal().negate());
		customer.getPurchaseorders().remove(po.getBean());
	}
	
	/**
	 * Compare the customer's balance and credit limit; if the former is greater than
	 * the latter, throw an exception.
	 */
	private void checkCredit() {
		if (customer.getBalance().doubleValue() > customer.getCreditLimit().doubleValue())
			throw new RuntimeException("Constraint failed - balance > credit limit");
	}

}
