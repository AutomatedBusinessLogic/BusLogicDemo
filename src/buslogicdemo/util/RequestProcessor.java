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
 */
public class RequestProcessor {

	public static void processRequest(HttpServletRequest request) {
		
		String action = request.getParameter("action");
		if (action == null || action.trim().length() == 0)
			return;
		
		HibernateFactory.beginTransaction();

		if ("update".equals(action))
			processUpdate(request);
		else if ("insert".equals(action))
			processInsert(request);
		else if ("delete".equals(action))
			processDelete(request);
		else if ("reloadData".equals(action)) {
			DataLoader.reloadData(HibernateFactory.session);
		}
		
		try {
			HibernateFactory.commitTransaction();
		}
		catch(ConstraintException cex) {
			request.setAttribute("errors", cex.getMessage());
			HibernateFactory.rollbackTransaction();
		}
		catch(Exception ex) {
			request.setAttribute("errors", ex.toString());
			HibernateFactory.rollbackTransaction();
		}
		
		if ("reloadData".equals(action))
			if (DemoEventListener.getInstance() != null)
				DemoEventListener.getInstance().resetEvents();
	}
	
	private static void processUpdate(HttpServletRequest request) {
		String type = request.getParameter("type");
		if (type == null || type.trim().length() == 0)
			return;
		String id = request.getParameter("id");
		String att = request.getParameter("att");
		String value = request.getParameter("value");
		if ("Order".equals(type)) {
			Purchaseorder order = (Purchaseorder)HibernateFactory.session.load(Purchaseorder.class, new Long(id));
			if ("paid".equals(att)) {
				Boolean oldValue = order.getPaid();
				if (oldValue == null)
					oldValue = Boolean.FALSE;
				order.setPaid( ! oldValue);
				if (oldValue)
					HibernateFactory.setCurrentUseCaseName("Order paid");
				else
					HibernateFactory.setCurrentUseCaseName("Order unpaid");
			}
			else if ("customer".equals(att)) {
				if (value == null || value.startsWith("- ")) // Do nothing if somehow the "- select a customer -" item was selected
					return;
				Customer customer = (Customer)HibernateFactory.session.load(Customer.class, value);
				order.setCustomer(customer);
				String msg = "The order has been reassigned to customer " + customer.getName();
				request.setAttribute("message", msg);
				HibernateFactory.setCurrentUseCaseName("Order reassigned");
			}
			else if ("notes".equals(att)) {
				order.setNotes(value);
				HibernateFactory.setCurrentUseCaseName("Order notes updated");
			}
			HibernateFactory.session.saveOrUpdate(order);
		}
		else if ("Customer".equals(type)) {
			Customer customer = (Customer)HibernateFactory.session.load(Customer.class, id);
			if ("creditLimit".equals(att)) {
				BigDecimal val = FormatUtil.parseMoney(value);
				customer.setCreditLimit(val);
				HibernateFactory.setCurrentUseCaseName("Customer credit limit updated");
			}
			HibernateFactory.session.saveOrUpdate(customer);
		}
		else if ("Lineitem".equals(type)) {
			Lineitem lineitem = (Lineitem)HibernateFactory.session.load(Lineitem.class, new Long(id));
			if ("quantity".equals(att)) {
				Integer val = FormatUtil.parseNumber(value);
				lineitem.setQtyOrdered(val);
				HibernateFactory.setCurrentUseCaseName("Line Item quantity updated");
			}
			else if ("unitPrice".equals(att)) {
				BigDecimal val = FormatUtil.parseMoney(value);
				lineitem.setProductPrice(val);
				HibernateFactory.setCurrentUseCaseName("Line Item unit price updated");
			}
			else if ("product".equals(att)) {
				Product product = (Product)HibernateFactory.session.load(Product.class, new Long(value));
				lineitem.setProduct(product);
				HibernateFactory.setCurrentUseCaseName("Line Item product changed");
			}
			HibernateFactory.session.saveOrUpdate(lineitem);
		}
	}
	
	private static void processInsert(HttpServletRequest request) {
		String type = request.getParameter("type");
		if (type == null || type.trim().length() == 0)
			return;
		String id = request.getParameter("id");
		if ("Lineitem".equals(type)) {
			Purchaseorder order = (Purchaseorder)HibernateFactory.session.load(Purchaseorder.class, new Long(id));
			Product product = (Product)HibernateFactory.session.load(Product.class, new Long(1));
			Lineitem newItem = new Lineitem();
			newItem.setPurchaseorder(order);
			newItem.setProduct(product);
			newItem.setQtyOrdered(1);
			HibernateFactory.setCurrentUseCaseName("New Line Item created");
			HibernateFactory.session.saveOrUpdate(newItem);
		}
		else if ("Order".equals(type)) {
			String custName = request.getParameter("custName");
			Customer customer = (Customer)HibernateFactory.session.load(Customer.class, custName);
			Purchaseorder newOrder = new Purchaseorder();
			newOrder.setCustomer(customer);
			newOrder.setPaid(Boolean.FALSE);
			newOrder.setNotes("");
			HibernateFactory.setCurrentUseCaseName("New Order created");
			HibernateFactory.session.saveOrUpdate(newOrder);
		}
	}

	private static void processDelete(HttpServletRequest request) {
		String type = request.getParameter("type");
		if (type == null || type.trim().length() == 0)
			return;
		String id = request.getParameter("id");
		if ("Lineitem".equals(type)) {
			Lineitem lineitem = (Lineitem)HibernateFactory.session.load(Lineitem.class, new Long(id));
			HibernateFactory.session.delete(lineitem);
			HibernateFactory.setCurrentUseCaseName("Line Item deleted");
		}
		else if ("Order".equals(type)) {
			Purchaseorder order = (Purchaseorder)HibernateFactory.session.load(Purchaseorder.class, new Long(id));
			HibernateFactory.session.delete(order);
			HibernateFactory.setCurrentUseCaseName("Order deleted");
		}
	}
}
