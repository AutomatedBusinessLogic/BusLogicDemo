package buslogicdemo.data;

import java.math.BigDecimal;
import java.util.*;
import javax.persistence.*;


/**
 * Data Object for Purchaseorder
 * (each row represents a purchase by a Customer for a set of Products, not due until set <em>ready</em>).
 */
@Entity
@Table(name="purchaseorder")
public class Purchaseorder implements java.io.Serializable {

	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)  // Use this for HSQL and MySQL
//	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="purchaseorder_seq")  // Use these two lines for Oracle
//	@SequenceGenerator(name="purchaseorder_seq", sequenceName="purchaseorder_seq")
	@Column(name="order_number")
	public Long getOrderNumber() { return orderNumber; }
	public void setOrderNumber(Long orderNumber) { this.orderNumber = orderNumber; }
	private Long orderNumber;

	@Column(name="amount_total")
	public BigDecimal getAmountTotal() { return amountTotal; }
	public void setAmountTotal(BigDecimal amountTotal) { this.amountTotal = amountTotal; }
	private BigDecimal amountTotal;

	@Column
	public Boolean getPaid() { return paid; }
	public void setPaid(Boolean paid) { this.paid = paid; }
	private Boolean paid;

	@Column
	public String getNotes() { return notes; }
	public void setNotes(String notes) { this.notes = notes; }
	private String notes;

	// Relationships
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="customer_name", nullable=false)
	public Customer getCustomer() { return customer; }
	public void setCustomer(Customer customer) { this.customer = customer; }
	private Customer customer;

	@OneToMany(mappedBy="purchaseorder", cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
	@OrderBy("lineitemId desc")
	public List<Lineitem> getLineitems() { return lineitems; }
	public void setLineitems(List<Lineitem> lineitems) { this.lineitems = lineitems; }
	private List<Lineitem> lineitems = new Vector<Lineitem>();

	private static final long serialVersionUID = 1L;
}
