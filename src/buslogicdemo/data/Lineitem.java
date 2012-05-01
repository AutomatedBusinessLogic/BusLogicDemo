package buslogicdemo.data;

import java.math.BigDecimal;
import javax.persistence.*;

/**
 * Data Object for LineItem.
 * Each row represents a Product purchased as part of a PurchaseOrder.
 */
@Entity
@Table(name="lineitem")
public class Lineitem implements java.io.Serializable {
	
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)  // Use this for HSQL and MySQL
//	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="lineitem_seq") // Use these two lines for Oracle
//	@SequenceGenerator(name="lineitem_seq", sequenceName="lineitem_seq")
	@Column(name="lineitem_id")
	public long getLineitemId() { return lineitemId;}
	public void setLineitemId(long lineitemId) { this.lineitemId = lineitemId;}
	private long lineitemId;
	
	@Column(name="qty_ordered")
	public Integer getQtyOrdered() { return qtyOrdered; }
	public void setQtyOrdered(Integer qtyOrdered) { this.qtyOrdered = qtyOrdered; }
	private Integer qtyOrdered;
	
	@Column(name="product_price")
	public BigDecimal getProductPrice() { return productPrice; }
	public void setProductPrice(BigDecimal productPrice) { this.productPrice = productPrice; }
	private BigDecimal productPrice;
	
	@Column
	public BigDecimal getAmount() { return amount; }
	public void setAmount(BigDecimal amount) { this.amount = amount; }
	private BigDecimal amount;
	
	// Relationships
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="product_number")
	public Product getProduct() { return product;}
	public void setProduct(Product product) { this.product = product; }
	private Product product;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="order_number")
	public Purchaseorder getPurchaseorder() { return purchaseorder; }
	public void setPurchaseorder(Purchaseorder purchaseorder) { this.purchaseorder = purchaseorder; }
	private Purchaseorder purchaseorder;


	private static final long serialVersionUID = 1L;
}
