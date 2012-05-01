package buslogicdemo.businesslogic;

import com.autobizlogic.abl.annotations.*;

public class PurchaseorderLogic {

	@Sum("lineitems.amount")
	public void deriveAmountTotal() { }
}
