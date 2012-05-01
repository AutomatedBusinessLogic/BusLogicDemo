package buslogicdemo.data.manual;

import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

import buslogicdemo.data.*;
import buslogicdemo.util.HibernateFactory;

/**
 * Business logic for the Product class. This class actually has no business logic per se,
 * but it was felt that it needed a service object anyway for uniformity.
 */
public class SProduct {
	private Product product;
	
	protected SProduct(Product product) {
		this.product = product;
	}
	
	/**
	 * Get a Product based on its primay key.
	 */
	public static SProduct getById(long id) {
		Product product = (Product)HibernateFactory.session.load(Product.class, id);
		SProduct result = new SProduct(product);
		return result;
	}
	
	/**
	 * Get all Products, sorted by name.
	 */
	public static List<SProduct> getAll() {
		@SuppressWarnings("unchecked")
		List<Product> products = HibernateFactory.session.createQuery("from Product order by name").list();
		List<SProduct> result = new Vector<SProduct>();
		for (Product p : products)
			result.add(new SProduct(p));
		return result;
	}
	
	/**
	 * Internal method: get the underlying Hibernate bean.
	 * @return
	 */
	protected Product getBean() { return product; }
	
	/**
	 * Get this Product's primary key.
	 */
	public long getProductNumber() { return product.getProductNumber(); }
	
	/**
	 * Get this Product's name.
	 */
	public String getName() { return product.getName(); }
	
	/**
	 * Get this Product's price.
	 */
	public BigDecimal getPrice() { return product.getPrice(); }
}
