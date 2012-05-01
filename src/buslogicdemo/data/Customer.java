package buslogicdemo.data;

import java.math.BigDecimal;
import java.util.*;

import javax.persistence.*;

/**
 * Data Object (pojo) for Customer (each row represents a Customer Account). 
*/
@Entity
@Table(name="customer")
public class Customer implements java.io.Serializable {

	@Id
	@Column
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	private String name;

	@Column
	public BigDecimal getBalance() { return balance; }
	public void setBalance(BigDecimal balance) { this.balance = balance; }
	private BigDecimal balance;

	@Column(name="credit_limit")
	public BigDecimal getCreditLimit() { return this.creditLimit; }
	public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }
	private BigDecimal creditLimit;

	/* Relationships */
	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.LAZY, mappedBy="customer")
	@OrderBy("orderNumber desc")
	public List<Purchaseorder> getPurchaseorders() { return this.purchaseorders; }
	public void setPurchaseorders(List<Purchaseorder> purchaseorders) { this.purchaseorders = purchaseorders; }
	private List<Purchaseorder> purchaseorders = new Vector<Purchaseorder>();

	private static final long serialVersionUID = 1L;
}
