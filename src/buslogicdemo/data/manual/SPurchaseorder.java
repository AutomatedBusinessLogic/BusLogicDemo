package buslogicdemo.data.manual;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import buslogicdemo.util.HibernateFactory;
import buslogicdemo.data.*;

/**
 * Business logic layer for the Purchaseorder class.
 */
public class SPurchaseorder {
	private Purchaseorder po;
	
	protected SPurchaseorder(Purchaseorder po) {
		this.po = po;
	}
	
	/**
	 * Get a Purchaseorder based on its primary key.
	 */
	public static SPurchaseorder getById(long id) {
		Purchaseorder po = (Purchaseorder)HibernateFactory.session.load(Purchaseorder.class, id);
		SPurchaseorder result = new SPurchaseorder(po);
		return result;
	}

	/**
	 * Create a new Purchaseorder for the given Customer.
	 */
	public static SPurchaseorder create(SCustomer cust) {
		Purchaseorder po = new Purchaseorder();
		po.setCustomer(cust.getBean());
		po.setPaid(false);
		po.setAmountTotal(BigDecimal.ZERO);
		SPurchaseorder result = new SPurchaseorder(po);
		return result;
	}

	/**
	 * Persist any changes to this Purchaseorder.
	 */
	public void save() {
		HibernateFactory.session.saveOrUpdate(po);
	}
	
	/**
	 * Delete this Purchaseorder. If it is not paid, this will reduce the customer's balance.
	 */
	public void delete() {
		if ( ! po.getPaid())
			getCustomer().updateBalance(getAmountTotal().negate());
		po.getCustomer().getPurchaseorders().remove(po);
		HibernateFactory.session.delete(po);
	}

	/**
	 * Internal method: get the underlying Hibernate bean.
	 */
	protected Purchaseorder getBean() { return po; }
	
	/**
	 * Get this Purchaseorder's primary key.
	 */
	public Long getOrderNumber() { return po.getOrderNumber(); }
	
	/**
	 * Get the total for this Purchaseorder, which is defined as the sum
	 * of all the Lineitems.
	 */
	public BigDecimal getAmountTotal() { return po.getAmountTotal(); }

	/**
	 * Internal method: adjust the amountTotal by the given amount. If this Purchaseorder
	 * is not paid, this will affect the Customer's balance, with all expected ramifications.
	 * @param delta
	 */
	protected void updateAmountTotal(BigDecimal delta) {
		po.setAmountTotal(po.getAmountTotal().add(delta));
		if ( ! getPaid())
			getCustomer().updateBalance(delta);
	}
	
	/**
	 * Whether this Purchaseorder is paid or not.
	 */
	public Boolean getPaid() { return po.getPaid(); }
	
	/**
	 * Set the Paid attribute on this Purchaseorder. This will affect the Customer's balance,
	 * with the usual ramifications.
	 */
	public void setPaid(Boolean b) {
		if (po.getPaid() && !b)
			getCustomer().updateBalance(this.getAmountTotal());
		else if (!getPaid() && b)
			getCustomer().updateBalance(this.getAmountTotal().negate());
		po.setPaid(b);
	}
	
	/**
	 * Notes for this order
	 */
	public String getNotes() { return po.getNotes(); }
	public void setNotes(String notes) { po.setNotes(notes); }
	
	/**
	 * Get the Customer who owns this Purchaseorder.
	 */
	public SCustomer getCustomer() { return new SCustomer(po.getCustomer()); }
	
	/**
	 * Change the Customer who owns this Purchaseorder. If this Purchaseorder is not paid,
	 * this will decrease the old Customer's balance, and increase the new Customer's balance.
	 */
	public void setCustomer(SCustomer customer) {
		// If it's the same customer, we're done
		if (customer.getName().equals(this.getCustomer().getName()))
			return;
		
		if (!getPaid()) {
			getCustomer().updateBalance(this.getAmountTotal().negate());
			customer.updateBalance(this.getAmountTotal());
		}
		po.setCustomer(customer.getBean());
	}
	
	/**
	 * Get the Lineitems for this Purchaseorder as an unmodifiable collection.
	 */
	public Set<SLineitem> getLineitems() {
		Set<SLineitem> result = new HashSet<SLineitem>();
		for (Lineitem li : po.getLineitems())
			result.add(new SLineitem(li));
		return Collections.unmodifiableSet(result);
	}
	
	/**
	 * Add a Lineitem to this Purchaseorder. This will increase the totalAmount,
	 * which will in turn increase the Customer's balance if this Purchaseorder
	 * is not paid.
	 */
	public void addLineitem(SLineitem item) {
		if ( ! po.getPaid())
			getCustomer().updateBalance(this.getAmountTotal());
		updateAmountTotal(item.getAmount());
		po.getLineitems().add(item.getBean());
	}

	/**
	 * Remove a Lineitem from this Purchaseorder. This will decrease the totalAmount,
	 * which will in turn decrease the Customer's balance if this Purchaseorder
	 * is not paid.
	 */
	public void removeLineitem(SLineitem item) {
		if ( ! po.getPaid())
			getCustomer().updateBalance(this.getAmountTotal().negate());
		updateAmountTotal(item.getAmount().negate());
		po.getLineitems().remove(item.getBean());
	}

}
