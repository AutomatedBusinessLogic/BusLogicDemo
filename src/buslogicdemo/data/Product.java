package buslogicdemo.data;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

/**
 * Data Object for Product.
 * Each row represents a Product sold on PurchaseOrders to Customers.
 */
@Entity
@Table(name="product")
public class Product implements java.io.Serializable {

	@Id
	@Column(name="product_number")
	public long getProductNumber() { return productNumber; }
	public void setProductNumber(long prodNum) { this.productNumber = prodNum; }
	private long productNumber;

	@Column
	public String getName() { return this.name; }
	public void setName(String name) { this.name = name; }
	private String name;

	@Column
	public BigDecimal getPrice() { return price; }
	public void setPrice(BigDecimal price) { this.price = price; }
	private BigDecimal price;

	// Relationships
	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.LAZY, mappedBy="product")
	public Set<Lineitem> getLineitems() { return lineitems; }
	public void setLineitems(Set<Lineitem> lineitems) { this.lineitems = lineitems; }
	private Set<Lineitem> lineitems = new HashSet<Lineitem>();

	private static final long serialVersionUID = 1L;
}
