package buslogicdemo.util;

import java.math.BigDecimal;

import org.hibernate.Session;

import buslogicdemo.data.*;

public class DataLoader {
	
	public static boolean initialDataLoaded = false;
	
	public static void reloadData(Session session) {
		dropData(session);
		loadData(session);
	}
	
	public static void dropData(Session session) {
		session.createQuery("delete from Lineitem").executeUpdate();
		session.createQuery("delete from Purchaseorder").executeUpdate();
		session.createQuery("delete from Product").executeUpdate();
		session.createQuery("delete from Customer").executeUpdate();
		
		session.createSQLQuery("ALTER TABLE PURCHASEORDER ALTER COLUMN order_number RESTART WITH 1").executeUpdate();
		session.createSQLQuery("ALTER TABLE LINEITEM ALTER COLUMN lineitem_id RESTART WITH 1").executeUpdate();
	}

	public static void loadData(Session session) {
		
		initialDataLoaded = true;
		
		System.out.println("Loading sample data...");
		// Create customers
		Customer alpha = new Customer();
		alpha.setName("Alpha and Sons");
		alpha.setCreditLimit(new BigDecimal(1900));
		session.save(alpha);
		Customer bravo = new Customer();
		bravo.setName("Bravo Hardware");
		bravo.setCreditLimit(new BigDecimal(5000));
		session.save(bravo);
		Customer charlie = new Customer();
		charlie.setName("Charlie's Construction");
		charlie.setCreditLimit(new BigDecimal(1500));
		session.save(charlie);
		Customer delta = new Customer();
		delta.setName("Delta Engineering");
		delta.setCreditLimit(new BigDecimal(500));
		session.save(delta);
		
		// Create products
		Product drill = new Product();
		drill.setProductNumber(1);
		drill.setName("Drill");
		drill.setPrice(new BigDecimal(315));
		session.save(drill);
		Product hammer = new Product();
		hammer.setProductNumber(2);
		hammer.setName("Hammer");
		hammer.setPrice(new BigDecimal(10));
		session.save(hammer);
		Product shovel = new Product();
		shovel.setProductNumber(3);
		shovel.setName("Shovel");
		shovel.setPrice(new BigDecimal(25));
		session.save(shovel);

		// Create PurchaseOrders
		Purchaseorder po1 = new Purchaseorder();
		po1.setOrderNumber(1L);
		po1.setPaid(false);
		po1.setNotes("This is a small order");
		po1.setCustomer(alpha);
		session.save(po1);
		Purchaseorder po2 = new Purchaseorder();
		po2.setOrderNumber(2L);
		po2.setPaid(true);
		po2.setNotes("");
		po2.setCustomer(bravo);
		session.save(po2);
		Purchaseorder po3 = new Purchaseorder();
		po3.setOrderNumber(3L);
		po3.setPaid(false);
		po3.setNotes("Please rush this order");
		po3.setCustomer(bravo);
		session.save(po3);
		Purchaseorder po4 = new Purchaseorder();
		po4.setOrderNumber(4L);
		po4.setPaid(false);
		po4.setNotes("Deliver by overnight with signature required");
		po4.setCustomer(charlie);
		session.save(po4);
		Purchaseorder po5 = new Purchaseorder();
		po5.setOrderNumber(5L);
		po5.setPaid(false);
		po5.setNotes("");
		po5.setCustomer(charlie);
		session.save(po5);
		Purchaseorder po6 = new Purchaseorder();
		po6.setOrderNumber(6L);
		po6.setPaid(false);
		po6.setNotes("Pack with care - fragile merchandise");
		po6.setCustomer(alpha);
		session.save(po6);
		Purchaseorder po7 = new Purchaseorder();
		po7.setOrderNumber(7L);
		po7.setPaid(false);
		po7.setNotes("No Saturday delivery");
		po7.setCustomer(delta);
		session.save(po7);

		// Create LineItems
		Lineitem li = new Lineitem();
		li.setLineitemId(1L);
		li.setQtyOrdered(1);
		li.setProduct(hammer);
		li.setPurchaseorder(po1);
		session.save(li);
		li = new Lineitem();
		li.setLineitemId(2L);
		li.setQtyOrdered(2);
		li.setProduct(hammer);
		li.setPurchaseorder(po2);
		session.save(li);
		li = new Lineitem();
		li.setLineitemId(3L);
		li.setQtyOrdered(1);
		li.setProduct(shovel);
		li.setPurchaseorder(po2);
		session.save(li);
		li = new Lineitem();
		li.setLineitemId(4L);
		li.setQtyOrdered(3);
		li.setProduct(drill);
		li.setPurchaseorder(po2);
		session.save(li);
		li = new Lineitem();
		li.setLineitemId(5L);
		li.setQtyOrdered(1);
		li.setProduct(hammer);
		li.setPurchaseorder(po3);
		session.save(li);
		li = new Lineitem();
		li.setLineitemId(6L);
		li.setQtyOrdered(2);
		li.setProduct(shovel);
		li.setPurchaseorder(po3);
		session.save(li);
		li = new Lineitem();
		li.setLineitemId(7L);
		li.setQtyOrdered(1);
		li.setProduct(hammer);
		li.setPurchaseorder(po4);
		session.save(li);
		li = new Lineitem();
		li.setLineitemId(8L);
		li.setQtyOrdered(3);
		li.setProduct(shovel);
		li.setPurchaseorder(po4);
		session.save(li);
		li = new Lineitem();
		li.setLineitemId(9L);
		li.setQtyOrdered(1);
		li.setProduct(hammer);
		li.setPurchaseorder(po5);
		session.save(li);
		li = new Lineitem();
		li.setLineitemId(10L);
		li.setQtyOrdered(5);
		li.setProduct(shovel);
		li.setPurchaseorder(po5);
		session.save(li);
		li = new Lineitem();
		li.setLineitemId(11L);
		li.setQtyOrdered(2);
		li.setProduct(shovel);
		li.setPurchaseorder(po6);
		session.save(li);
		li = new Lineitem();
		li.setLineitemId(12L);
		li.setQtyOrdered(1);
		li.setProduct(shovel);
		li.setPurchaseorder(po1);
		session.save(li);
		li = new Lineitem();
		li.setLineitemId(13L);
		li.setQtyOrdered(3);
		li.setProduct(hammer);
		li.setPurchaseorder(po6);
		session.save(li);
		li = new Lineitem();
		li.setLineitemId(14L);
		li.setQtyOrdered(7);
		li.setProduct(hammer);
		li.setPurchaseorder(po7);
		session.save(li);
	}
}
