package buslogicdemo.businesslogic;

import com.autobizlogic.abl.annotations.*;

public class LineitemLogic {
	
	@Formula("productPrice * qtyOrdered")
	public void deriveAmount() { }

	@ParentCopy("product.price")
	public void deriveProductPrice() { }
}
