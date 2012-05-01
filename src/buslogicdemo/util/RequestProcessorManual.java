package buslogicdemo.util;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import buslogicdemo.data.manual.*;

/**
 * Process requests for the BusLogicDemo app (the version with manually-implemented
 * business logic).
 * 
 * Requests are organized as follows:
 * <ul>
 * <li>custName is the name of the customer to display
 * <li>action is one of insert, update or delete
 * <li>type is the object type on which the action should be performed: Customer, Order or Lineitem
 * <li>id is the primary key for that object
 * <li>att is the name of the attribute being updated (if relevant)
 * <li>value is the new value for that attribute (if relevant)
 * </ul>
 */
public class RequestProcessorManual {

	public static void processRequest(HttpServletRequest request) {
		
		String action = request.getParameter("action");
		if (action == null || action.trim().length() == 0)
			return;
		
		HibernateFactory.beginTransaction();

		try {
			if ("update".equals(action))
				processUpdate(request);
			else if ("insert".equals(action))
				processInsert(request);
			else if ("delete".equals(action))
				processDelete(request);
			
			HibernateFactory.commitTransaction();
		}
		catch(Exception ex) {
			HibernateFactory.rollbackTransaction();
			request.setAttribute("errors", ex.getMessage());
		}
		
		request.setAttribute("showChanges", "true");
	}
	
	private static void processUpdate(HttpServletRequest request) {
		String type = request.getParameter("type");
		if (type == null || type.trim().length() == 0)
			return;
		String id = request.getParameter("id");
		String att = request.getParameter("att");
		String value = request.getParameter("value");
		if ("Order".equals(type)) {
			SPurchaseorder order = SPurchaseorder.getById(Long.valueOf(id));
			if ("paid".equals(att)) {
				Boolean oldValue = order.getPaid();
				if (oldValue == null)
					oldValue = Boolean.FALSE;
				order.setPaid( ! oldValue);
			}
			else if ("customer".equals(att)) {
				if (value == null || value.startsWith("- ")) // Do nothing if somehow the "- select a customer -" item was selected
					return;
				SCustomer customer = SCustomer.getByName(value);
				order.setCustomer(customer);
			}
			else if ("notes".equals(att)) {
				order.setNotes(value);
			}
			order.save();
		}
		else if ("Customer".equals(type)) {
			SCustomer customer = SCustomer.getByName(id);
			if ("creditLimit".equals(att)) {
				BigDecimal val = FormatUtil.parseMoney(value);
				customer.setCreditLimit(val);
			}
			customer.save();
		}
		else if ("Lineitem".equals(type)) {
			SLineitem lineitem = SLineitem.getById(new Long(id));
			if ("quantity".equals(att)) {
				Integer val = FormatUtil.parseNumber(value);
				lineitem.setQtyOrdered(val);
			}
			else if ("unitPrice".equals(att)) {
				BigDecimal val = FormatUtil.parseMoney(value);
				lineitem.setProductPrice(val);
			}
			else if ("product".equals(att)) {
				SProduct product = SProduct.getById(new Long(value));
				lineitem.setProduct(product);
			}
			lineitem.save();
		}
	}
	
	private static void processInsert(HttpServletRequest request) {
		String type = request.getParameter("type");
		if (type == null || type.trim().length() == 0)
			return;
		String id = request.getParameter("id");
		if ("Lineitem".equals(type)) {
			SPurchaseorder order = SPurchaseorder.getById(Long.valueOf(id));
			SProduct product = SProduct.getById(new Long(1));
			SLineitem newItem = SLineitem.create(product, order);
			newItem.setQtyOrdered(1);
			newItem.save();
		}
		else if ("Order".equals(type)) {
			String custName = request.getParameter("custName");
			SCustomer customer = SCustomer.getByName(custName);
			SPurchaseorder newOrder = SPurchaseorder.create(customer);
			newOrder.setPaid(Boolean.FALSE);
			newOrder.setNotes("");
			newOrder.save();
		}
	}

	private static void processDelete(HttpServletRequest request) {
		String type = request.getParameter("type");
		if (type == null || type.trim().length() == 0)
			return;
		String id = request.getParameter("id");
		if ("Lineitem".equals(type)) {
			SLineitem lineitem = SLineitem.getById(new Long(id));
			//lineitem.getPurchaseorder().removeLineitem(lineitem);
			lineitem.delete();
		}
		else if ("Order".equals(type)) {
			SPurchaseorder order = SPurchaseorder.getById(Long.valueOf(id));
			//order.getCustomer().removePurchaseorder(order);
			order.delete();
		}
	}
}
