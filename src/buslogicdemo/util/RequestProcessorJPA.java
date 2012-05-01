package buslogicdemo.util;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import com.autobizlogic.abl.engine.ConstraintException;

import buslogicdemo.data.*;

/**
 * Process requests for the BusLogicDemo app. Requests are organized as follows:
 * <ul>
 * <li>custName is the name of the customer to display
 * <li>action is one of insert, update or delete
 * <li>type is the object type on which the action should be performed: Customer, Order or Lineitem
 * <li>id is the primary key for that object
 * <li>att is the name of the attribute being updated (if relevant)
 * <li>value is the new value for that attribute (if relevant)
 * </ul>
 * 
 * This version uses the JPAFactory.
 */
public class RequestProcessorJPA {

	public static void processRequest(HttpServletRequest request) {
		
		String action = request.getParameter("action");
		if (action == null || action.trim().length() == 0)
			return;
		
		JPAFactory.getInstance().beginTransaction();

		if ("update".equals(action))
			processUpdate(request);
		else if ("insert".equals(action))
			processInsert(request);
		else if ("delete".equals(action))
			processDelete(request);
		
		try {
			JPAFactory.getInstance().commitTransaction();
		}
		catch(Exception ex) {
			// JPA tends to wrap constraint exceptions into several layers, so we have to go fishing
			// for the "real" exception.
			Throwable t = ex;
			while (true) {
				Throwable t2 = t.getCause();
				if (t2 == null || t2 == t) {
					t = null;
					break;
				}
				t = t2;
				if (t instanceof ConstraintException)
					break;
			}
			if (t != null) {
				request.setAttribute("errors", t.toString());
			}
			JPAFactory.getInstance().rollbackTransaction();
			ex.printStackTrace();
		}
	}
	
	private static void processUpdate(HttpServletRequest request) {
		String type = request.getParameter("type");
		if (type == null || type.trim().length() == 0)
			return;
		String id = request.getParameter("id");
		String att = request.getParameter("att");
		String value = request.getParameter("value");
		if ("Order".equals(type)) {
			Purchaseorder order = JPAFactory.getInstance().getObjectById(Purchaseorder.class, new Long(id));
			if ("paid".equals(att)) {
				Boolean oldValue = order.getPaid();
				if (oldValue == null)
					oldValue = Boolean.FALSE;
				order.setPaid( ! oldValue);
				if (oldValue)
					JPAFactory.getInstance().setCurrentUseCaseName("Order paid");
				else
					JPAFactory.getInstance().setCurrentUseCaseName("Order unpaid");
			}
			else if ("customer".equals(att)) {
				if (value == null || value.startsWith("- ")) // Do nothing if somehow the "- select a customer -" item was selected
					return;
				Customer customer = JPAFactory.getInstance().getObjectById(Customer.class, value);
				order.setCustomer(customer);
				request.setAttribute("message", "The order has been reassigned to customer " + customer.getName());
				JPAFactory.getInstance().setCurrentUseCaseName("Order reassigned");
			}
			else if ("notes".equals(att)) {
				order.setNotes(value);
				JPAFactory.getInstance().setCurrentUseCaseName("Order notes updated");
			}
			JPAFactory.getInstance().saveObject(order);
		}
		else if ("Customer".equals(type)) {
			Customer customer = JPAFactory.getInstance().getObjectById(Customer.class, id);
			if ("creditLimit".equals(att)) {
				BigDecimal val = FormatUtil.parseMoney(value);
				customer.setCreditLimit(val);
				JPAFactory.getInstance().setCurrentUseCaseName("Customer credit limit updated");
			}
			JPAFactory.getInstance().saveObject(customer);
		}
		else if ("Lineitem".equals(type)) {
			Lineitem lineitem = JPAFactory.getInstance().getObjectById(Lineitem.class, new Long(id));
			if ("quantity".equals(att)) {
				Integer val = FormatUtil.parseNumber(value);
				lineitem.setQtyOrdered(val);
				JPAFactory.getInstance().setCurrentUseCaseName("Line Item quantity updated");
			}
			else if ("unitPrice".equals(att)) {
				BigDecimal val = FormatUtil.parseMoney(value);
				lineitem.setProductPrice(val);
				JPAFactory.getInstance().setCurrentUseCaseName("Line Item unit price updated");
			}
			else if ("product".equals(att)) {
				Product product = JPAFactory.getInstance().getObjectById(Product.class, new Long(value));
				lineitem.setProduct(product);
				JPAFactory.getInstance().setCurrentUseCaseName("Line Item product changed");
			}
			JPAFactory.getInstance().saveObject(lineitem);
		}
	}
	
	private static void processInsert(HttpServletRequest request) {
		String type = request.getParameter("type");
		if (type == null || type.trim().length() == 0)
			return;
		String id = request.getParameter("id");
		if ("Lineitem".equals(type)) {
			Purchaseorder order = JPAFactory.getInstance().getObjectById(Purchaseorder.class, new Long(id));
			Product product = JPAFactory.getInstance().getObjectById(Product.class, new Long(1));
			Lineitem newItem = new Lineitem();
			newItem.setPurchaseorder(order);
			newItem.setProduct(product);
			newItem.setQtyOrdered(1);
			JPAFactory.getInstance().setCurrentUseCaseName("New Line Item created");
			JPAFactory.getInstance().saveObject(newItem);
		}
		else if ("Order".equals(type)) {
			String custName = request.getParameter("custName");
			Customer customer = JPAFactory.getInstance().getObjectById(Customer.class, custName);
			Purchaseorder newOrder = new Purchaseorder();
			newOrder.setCustomer(customer);
			newOrder.setPaid(Boolean.FALSE);
			newOrder.setNotes("");
			JPAFactory.getInstance().setCurrentUseCaseName("New Order created");
			JPAFactory.getInstance().saveObject(newOrder);
		}
	}

	private static void processDelete(HttpServletRequest request) {
		String type = request.getParameter("type");
		if (type == null || type.trim().length() == 0)
			return;
		String id = request.getParameter("id");
		if ("Lineitem".equals(type)) {
			Lineitem lineitem = JPAFactory.getInstance().getObjectById(Lineitem.class, new Long(id));
			JPAFactory.getInstance().deleteObject(lineitem);
			JPAFactory.getInstance().setCurrentUseCaseName("Line Item deleted");
		}
		else if ("Order".equals(type)) {
			Purchaseorder order = JPAFactory.getInstance().getObjectById(Purchaseorder.class, new Long(id));
			JPAFactory.getInstance().deleteObject(order);
			JPAFactory.getInstance().setCurrentUseCaseName("Order deleted");
		}
	}
}
