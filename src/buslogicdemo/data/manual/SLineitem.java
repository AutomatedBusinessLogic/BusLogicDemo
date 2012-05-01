package buslogicdemo.data.manual;

import java.math.BigDecimal;

import buslogicdemo.util.HibernateFactory;
import buslogicdemo.data.*;

/**
 * Business logic layer for the Lineitem class.
 */
public class SLineitem {
	private Lineitem lineitem;
	
	/**
	 * Instances are created internally.
	 */
	protected SLineitem(Lineitem li) {
		lineitem = li;
	}

	/**
	 * Persist any changes to this Lineitem.
	 */
	public void save() {
		HibernateFactory.session.saveOrUpdate(lineitem);
	}

	/**
	 * Internal method: get the underlying Hibernate bean.
	 */
	protected Lineitem getBean() { return lineitem; }
	
	public static SLineitem getById(long id) {
		Lineitem li = (Lineitem)HibernateFactory.session.load(Lineitem.class, id);
		SLineitem result = new SLineitem(li);
		return result;
	}
	
	/**
	 * Create a new Lineitem for the given product and order. The amount and qtyOrdered
	 * are set to zero by default. The product's price is copied into productPrice.
	 * @param product The product for the Lineitem
	 * @param order The parent order
	 */
	public static SLineitem create(SProduct product, SPurchaseorder order) {
		Lineitem li = new Lineitem();
		li.setProduct(product.getBean());
		li.setPurchaseorder(order.getBean());
		li.setProductPrice(product.getPrice());
		li.setAmount(BigDecimal.ZERO);
		li.setQtyOrdered(0);
		SLineitem result = new SLineitem(li);
		return result;
	}
	
	/**
	 * Delete this Lineitem. The Purchaseorder's amountTotal will be updated,
	 * with all the consequences that entails.
	 */
	public void delete() {
		getPurchaseorder().updateAmountTotal(getAmount().negate());
		getPurchaseorder().getBean().getLineitems().remove(getBean());
		HibernateFactory.session.delete(lineitem);
	}

	/**
	 * Get the primary key for this Lineitem.
	 */
	public long getLineitemId() { return lineitem.getLineitemId(); }
	
	/**
	 * Get the number of items for this Lineitem.
	 */
	public Integer getQtyOrdered() { return lineitem.getQtyOrdered(); }
	
	/**
	 * Get the product price for this Lineitem.
	 */
	public BigDecimal getProductPrice() { return lineitem.getProductPrice(); }
	
	/**
	 * Get the amount for this Lineitem, which is defined as the productPrice times
	 * the qtyOrdered.
	 */
	public BigDecimal getAmount() { return lineitem.getAmount(); }
	
	/**
	 * Set the number of items for this Lineitem. This will affect the Amount, which
	 * will in turn affect the Purchaseorder's amountTotal, and so on.
	 */
	public void setQtyOrdered(Integer qtyOrdered) {
		updateAmount(qtyOrdered, getProductPrice());
		lineitem.setQtyOrdered(qtyOrdered);
	}
	
	/**
	 * Set the product price for this Lineitem. This will affect the Amount, which
	 * will in turn affect the Purchaseorder's amountTotal, and so on.
	 */
	public void setProductPrice(BigDecimal productPrice) {
		updateAmount(getQtyOrdered(), productPrice);
		lineitem.setProductPrice(productPrice);
	}

	/**
	 * This is called internally whenever either the productPrice or the qtyOrdered
	 * needs to change.
	 * 
	 * Manual How/When - how 5 rules turn into 500 lines of code:
	 *   ** Analyze what changed
	 *   ** Recompute self
	 *   ** Notify all dependent code, in a proper order
	 *   ** Optimize: aggregate query?  read data into mem??  prune?
	 */
	private void updateAmount(Integer qtyOrdered, BigDecimal productPrice) {
		if (qtyOrdered == null || productPrice == null)
			return;
		BigDecimal newValue = productPrice.multiply(new BigDecimal(qtyOrdered));
		BigDecimal delta = newValue.subtract(lineitem.getAmount());
		getPurchaseorder().updateAmountTotal(delta);
		lineitem.setAmount(newValue);
	}

	/**
	 * Get the Product for this Lineitem.
	 */
	public SProduct getProduct() { return new SProduct(lineitem.getProduct()); }
	
	/**
	 * Set the Product for this Lineitem. The Product's price will be copied into
	 * productPrice, which will of course affect the Lineitem's amount, with all
	 * the usual repercussions.
	 */
	public void setProduct(SProduct product) {
		setProductPrice(product.getPrice());
		lineitem.setProduct(product.getBean());
	}
	
	/**
	 * Get this Lineitem's Purchaseorder.
	 */
	public SPurchaseorder getPurchaseorder() { return new SPurchaseorder(lineitem.getPurchaseorder()); }
	
	/**
	 * Change this Lineitem's Purchaseorder. The current Purchaseorder will be updated
	 * so that its totalAmount is decreased by this Lineitem's amount, and the new
	 * Purchaseorder's totalAmount will be increased by the same amount.
	 * @param purchaseorder
	 */
	public void setPurchaseorder(SPurchaseorder purchaseorder) {
		// Are the two orders equal?
		if (purchaseorder.getOrderNumber().equals(getPurchaseorder().getOrderNumber()))
			return;
		
		// Bona fide change from one order to another
		getPurchaseorder().updateAmountTotal(this.getAmount().negate());
		purchaseorder.updateAmountTotal(this.getAmount());
		lineitem.setPurchaseorder(purchaseorder.getBean());
	}
}
