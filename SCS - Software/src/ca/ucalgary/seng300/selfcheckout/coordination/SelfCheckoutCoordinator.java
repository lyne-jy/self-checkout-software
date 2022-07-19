package ca.ucalgary.seng300.selfcheckout.coordination;

import java.math.BigDecimal;

import ca.ucalgary.seng300.ui.UserInterfaceControlSoftware;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;
import ca.ucalgary.seng300.selfcheckout.communication.SCNotifierCoordinatorObserver;
import ca.ucalgary.seng300.selfcheckout.funds.Payment;
import ca.ucalgary.seng300.selfcheckout.product.BaggingArea;
import ca.ucalgary.seng300.selfcheckout.product.Checkout;
import ca.ucalgary.seng300.selfcheckout.product.CheckoutObserver;
import ca.ucalgary.seng300.selfcheckout.product.EnterMembership;
import ca.ucalgary.seng300.selfcheckout.product.PLULogic;
import ca.ucalgary.seng300.selfcheckout.product.Scan;
import ca.ucalgary.seng300.selfcheckout.utility.Cart;
import ca.ucalgary.seng300.selfcheckout.utility.ComplexAbstractSoftware;
import ca.ucalgary.seng300.selfcheckout.utility.Database;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;
import ca.ucalgary.seng300.selfcheckout.utility.PlaceItemTimer;

/*
 * Class in charge of all the logic in the system [this class is supposed to not have any direct interaction with hardware]
 */
public class SelfCheckoutCoordinator extends ComplexAbstractSoftware<SCCoordinatorObserver> implements SCNotifierCoordinatorObserver, CheckoutObserver {

	// Fields
	private PLULogic plu;
	private Scan scan;
	private Payment payment;	
	private BaggingArea bagging;
	private Checkout checkout;
	private PlaceItemTimer timer;
	private EnterMembership enterMembership;
	
	private Cart cart;
	private final BigDecimal TAX_RATE;
	private Barcode membership;
	private SoftwarePhase previousPhase;
	
	private PriceLookupCode enteredPLU;
	
	// Constructor 
	public SelfCheckoutCoordinator(PLULogic l, Scan s, Payment p, BaggingArea b, Checkout c, PlaceItemTimer t, EnterMembership m, int id) {
	
		if(s == null || p == null || b == null || c == null) throw new NullPointerException();
		
		this.plu = l;
		this.scan = s;
		this.payment = p;
		this.bagging = b;
		this.checkout = c;
		this.timer = t;
		this.enterMembership = m;
		
		cart = new Cart(id);
		TAX_RATE = BigDecimal.ZERO; // TODO: Change this once the UI is implemented
		cart.setTaxRate(TAX_RATE);
		membership = null;
		previousPhase = null;
		enteredPLU = null;
		updatePhase(SoftwarePhase.OFF);
	}
	
	/* ADDING AN ITEM LOGIC */
	
	/**
	 * method for scanning an item with a barcode
	 */
	@Override
	public void productScanned(Barcode barcode) {
		
		updatePhase(SoftwarePhase.HOLDING);
		bagging.setBarcodedItemToBeWeiged(barcode);
	
		// Start the timer
		timer.scheduleTimer(true);
	}
	
	@Override
	public void enterPLUButtonPressed(PriceLookupCode code) {
		plu.PLUCodeEntered(code);
	}
	
	@Override
	public void productAdded(PriceLookupCode code) {
		updatePhase(SoftwarePhase.WEIGHING);	
		enteredPLU = code;
		
		// timer for scanning area 
		timer.scheduleTimer(false);
	}
	
	@Override
	public void productPlacedInScanningArea(double expectedWeight) {
		
		// stop timer for scanning area
		try {
			timer.cancelTimer();
		}catch(Exception e) {}
	
		updatePhase(SoftwarePhase.HOLDING);	
		bagging.setPLUExpectedWeight(expectedWeight);
		bagging.setPLUItemToBeWeighed(enteredPLU);
		// Start the timer
		timer.scheduleTimer(true);
		
		enteredPLU = null;
	}
	
	/* ADDING OWN BAGS LOGIC */
	
	@Override
	public void addBagsButtonPressed(int numOfBags, boolean ownBags) {
		updatePhase(SoftwarePhase.ADDING_OWN_BAG);
		bagging.addBags(numOfBags, ownBags);
	}
	
	@Override
	public void addBagVerficationSuccesful(boolean addOwnBag) {
		updatePhase(SoftwarePhase.READY);
		
		cart.addBags(bagging.getExpectedNumBags(), addOwnBag);
		bagging.reset(); // Reseting all the private fields in bagging area
		
		notifyBagsAddedToPurchase(cart);
	}
		
	/* WEIGHT VERIFICATION OF AN ITEM LOGIC */
	
	@Override
	public void productVerificationSuccesful() {
		
		// stop the timer [done by the timer]
		try {
			timer.cancelTimer();
		}catch(NullPointerException npe) {
			// The timer was already stopped
		}
		
		// Add item to the virtual cart
		Barcode barcode = bagging.getBarcodedItemToBeWeighed();
		PriceLookupCode plu = bagging.getPLUItemToBeWeighed();
		
		if(barcode != null) {
			
			BigDecimal price = Database.database().readProductWithBarcode(barcode).getPrice(); 
			cart.addBarcodedItemToCart(barcode, price);
			
			// Go to ready Phase to continue with scanning
			bagging.reset();	
			
			updatePhase(SoftwarePhase.READY);
			
			notifyProductAddedToPurchase(cart);
			
		}else if(plu != null) {
			
			BigDecimal price = Database.database().readProductWithPLU(plu).getPrice();
			cart.addPLUItemToCart(plu, price, bagging.getExpectedPLUWeight());
			
			// Go to ready phase to continue with scanning
			bagging.reset();
			
			updatePhase(SoftwarePhase.READY);
						
			notifyProductAddedToPurchase(cart);
		}else {
			notifyProductAddedToPurchase(cart);
		}
	}
	
	/* PAYMENT LOGIC */
	
	@Override
	public void checkoutButtonPressed() {	
		updatePhase(SoftwarePhase.PAYING);
	}
	
	@Override
	public void payWithCashButtonPressed() {
		payment.cash.setTotalDue(cart.getTotalRemaining());
	}

	@Override
	public void payWithCardButtonPressed() {
		payment.card.setTotalDue(cart.getTotalRemaining());	
	}
	
	@Override
	public void payWithGiftCardButtonPressed(String cardNum) {
		payment.card.setTotalDue(cart.getTotalRemaining());
		payment.card.payWithGiftcard(cardNum);
	}
	
	@Override
	public void cardPaymentSuccesful() {
		checkout.printReceipt(cart, membership);
		updatePhase(SoftwarePhase.CHECKOUT);
	}
	
	@Override
	public void enoughCashInserted() {
		payment.cash.returnChange(payment.cash.getTotalInsertedCash().subtract(payment.cash.getTotalDue()));
		checkout.printReceipt(cart, membership);
		updatePhase(SoftwarePhase.CHECKOUT);
	}
	
	@Override
	public void partialPayment() {
		cart.partialPayment(payment.cash.getTotalInsertedCash());
		payment.cash.resetTotalInsertedCash();
    	payment.cash.setTotalDue(cart.getTotalRemaining());
    	payment.card.setTotalDue(cart.getTotalRemaining());	
	}
	
	@Override
	public void addMoreItemsButtonPresses() {
		updatePhase(SoftwarePhase.READY);
	}

	/* CHECKOUT LOGIC*/
	
	@Override
	public void allProductsRemoved() {
		updatePhase(SoftwarePhase.READY);
		reset();
	}	
	
	@Override
	public void printerError() {
		updatePhase(SoftwarePhase.CHECKOUT);
	}
	
	@Override
	public void inkAddedToPrinter(int quantity) {
		checkout.loadedInkInPrinter(quantity);
	}

	@Override
	public void paperAddedToPrinter(int units) {
		checkout.loadedPaperInPrinter(units);
	}
	
	/* MEMBERSHIP CARD LOGIC */
	
	@Override
	public void enterMembershipButtonPressed() {
		updatePhase(SoftwarePhase.ENTERING_MEMBERSHIP);
	}
	
	@Override
	public void membershipEntered(Barcode barcode) {
		enterMembership.barcodeEntered(barcode);
	}
	
	@Override
	public void membershipCardVerifiedSuccesfully(Barcode barcode) {
		membership = barcode;
		updatePhase(SoftwarePhase.READY);
	}
	
	@Override
	public void membershipCardNotInDatabase() {
		updatePhase(SoftwarePhase.READY);
	}
		
	/* ATTENDANT STATION LOGIC */ 
	
	@Override
	public void approveWeightDiscrepancy() {
		if(bagging.getPLUItemToBeWeighed() == null && bagging.getBarcodedItemToBeWeighed()==null) {
			return;
		}
		// Update bagging area 
		bagging.addToCurWeight(bagging.getPrevWeightRegistered());
		productVerificationSuccesful();
	}

	@Override
	public void approveProductNotBagged() {
		if(curPhase == SoftwarePhase.HOLDING)
			productVerificationSuccesful();
	}
	
	@Override
	public void approveBagWeightVerificationFailure(boolean addOwnBag) {
		if(curPhase == SoftwarePhase.ADDING_OWN_BAG)
			addBagVerficationSuccesful(addOwnBag); 
	}
	
	@Override
	public void blockStation() {
		// Station must already be blocked
		if(curPhase == SoftwarePhase.BLOCKED) return;
		previousPhase = curPhase;
		updatePhase(SoftwarePhase.BLOCKED);
		notifyBlockStation();
	}

	@Override
	public void unblockStation() {
		if(curPhase != SoftwarePhase.BLOCKED) return;
		updatePhase(previousPhase);
		notifyUnblockStation();
	}

	@Override
	public void productRemovedFromPurchase(Cart newCart) {
		this.cart = newCart;
		
		double newWeight = 0.0;
		
		// Iterate through each BarcodedProduct
		for(Barcode b : cart.getBarcodedProducts().keySet()) {
			newWeight += Database.database().readProductWithBarcode(b).getExpectedWeight();
		}
		
		// Iterate through each PLUCoded Product
		for(PriceLookupCode p : cart.getPLUProducts().keySet()) {
			newWeight += cart.getWeightOfPLUProduct(p);
		}
		
		// update the bagging area
		bagging.setCurWeight(newWeight);
		
		notifyProductRemovedFromPurchase(this.cart, true);
	}
	
	@Override
	public void productAddedToPurchase(Product product) {
				
		if(product instanceof BarcodedProduct) {
			productScanned(((BarcodedProduct)product).getBarcode());
		}else {
			enterPLUButtonPressed(((PLUCodedProduct)product).getPLUCode());
		}
	}
	
	@Override
	public void shutDownStation() {
		if(curPhase == SoftwarePhase.OFF) return;
		
		updatePhase(SoftwarePhase.OFF);
		reset();
		notifyShutDownStation();
	}

	@Override
	public void startStation() {
		if(curPhase != SoftwarePhase.OFF) return;
		
		updatePhase(SoftwarePhase.READY);
		notifyStartStation();
	}
		
	/* OTHER */
	
	@Override
	public void updatePhase(SoftwarePhase newPhase){
		this.curPhase = newPhase;
		plu.updatePhase(newPhase);
		scan.updatePhase(newPhase);
		checkout.updatePhase(newPhase);
		bagging.updatePhase(newPhase);
		payment.updatePhase(newPhase);
		enterMembership.updatePhase(newPhase);
	}
	
	private void reset() {
		cart.reset();
		membership = null;
		previousPhase = null;
	}
	
	/* OBSERVERS */
	
	private void notifyBagsAddedToPurchase(Cart cart) {
		for(SCCoordinatorObserver obs : observers) {
			obs.bagsAddedToPurchase(cart);
		}
		
		try {
			UserInterfaceControlSoftware.bagsAddedToPurchase(cart);
		}catch(Exception e) {}
	}
	
	private void notifyShutDownStation() {
		for(SCCoordinatorObserver obs : observers) {
			obs.shutDownStation();
		}
	}
	
	private void notifyStartStation() {
		for(SCCoordinatorObserver obs : observers) {
			obs.startStation();
		}
	}

	private void notifyBlockStation() {
		for (SCCoordinatorObserver obs: observers) {
			obs.blockStation();
		}
	}

	private void notifyUnblockStation() {
		for (SCCoordinatorObserver obs: observers) {
			obs.unblockStation();
		}
	}
	
	private void notifyProductRemovedFromPurchase(Cart cart, boolean showMsg) {
		for(SCCoordinatorObserver obs : observers) {
			obs.productRemovedFromPurchase(cart, showMsg);
		}
	}
	
	private void notifyProductAddedToPurchase(Cart cart) {
		for(SCCoordinatorObserver obs : observers) {
			obs.productAddedToPurchase(cart);
		}
	}
	
	/* IGNORE */
	
	@Override
	public void totalCalculatedSuccesfully() {
		// ignore
	}

	@Override
	public void InWrongPhase() {
		// ignore	
	}
	
	@Override
	public void receiptPrintedSuccesfully() {
		// ignore
	}

	@Override
	public void printerLowOnInk() {
		// ignore
	}

	@Override
	public void printerLowOnPaper() {
		// ignore
	}
}
