package ca.ucalgary.seng300.selfcheckout.communication;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.products.Product;

import ca.ucalgary.seng300.selfcheckout.utility.Cart;

/*
 * Interface for observer of the Notifier class [In practice this interface will be implemented by the Coordinator Only]
 */
public interface SCNotifierCoordinatorObserver extends SCNotifierObserver {
	
	/*
	 * The card payment was successful, receipt should be printed and a thank you message should be displayed
	 */
	public void cardPaymentSuccesful();
	
	/*
	 * Enough funds have been registered, so the Coordinator can return change (as best as it can) and print he receipt
	 */
	public void enoughCashInserted();
	
	/**
	 * partial payment is done so receipt can be printed now
	 */
	public void partialPayment();
	/**
	 * adding more items after partial payments
	 */
	public void addMoreItemsButtonPresses();
	
	/*
	 * Update the totalDue for cash payment
	 */
	public void payWithCashButtonPressed();
	
	/*
	 * Update the product to be weighed in the bagging area (for verification purposes), start the timer
	 * and set the software phase to HOLDING
	 */
	public void productScanned(Barcode barcode);
	/**
	 * Notify UI that PLU coded product was added successfully
	 */
	public void productAdded(PriceLookupCode code);
	/*
	 * Update the phase back to READY
	 */
	public void productVerificationSuccesful();
	
	/*
	 * Update the phase of software to PAYING
	 */
	public void checkoutButtonPressed();
	
	/*
	 * Update the total due for card payment
	 */
	public void payWithCardButtonPressed(); 
	
	/*
	 * Re-start the system by going to the READY phase
	 */
	public void allProductsRemoved();
	
	/*
	 * Update the phase of software to Locked
	 */
	public void addBagsButtonPressed(int numOfBags, boolean ownBags);
	
	/*
	 * Save the scanned barcode to be printed in the receipt or get more info about the client
	 */
	public void membershipCardVerifiedSuccesfully(Barcode barcode);
	
	/*
	 * Update the phase to ready 
	 */
	public void membershipCardNotInDatabase();
	
	/*
	 * Update the phase of software to ENTERING_MEMBERHSIP
	 */
	public void enterMembershipButtonPressed();
	
	/*
	 * Send the inputed barcode to get verified 
	 */
	public void membershipEntered(Barcode barcode);
	
	/*
	 * Unblock the station
	 */
	public void approveWeightDiscrepancy();
	
	/*
	 * Attendant decided to block the station
	 */
	public void blockStation();

	/*
	 * Attendant decided to block the station
	 */
	public void unblockStation();
	
	/*
	 * Update the cart by removing the product
	 */
	public void productRemovedFromPurchase(Cart cart);
	
	/*
	 * Coordinator should make system enter OFF phase
	 * and then notify the UI about it
	 */
	public void shutDownStation();
	
	/*
	 * Coordinator should make the system enter READY phase
	 * and then notify UI about it
	 */
	public void startStation();
	
	/*
	 * Coordinator should update the amount of paper available in Checkout
	 */
	public void paperAddedToPrinter(int units);
	
	/*
	 * Coordinator should update the amount of ink available in Checkout
	 */
	public void inkAddedToPrinter(int quantity);
	
	/*
	 * Button for adding a plu has been pressed
	 */
	public void enterPLUButtonPressed(PriceLookupCode code);

	/*
	 * Notify coordinator that product can not be bagged
	 */
	public void approveProductNotBagged();
	
	/*
	 * Notify coordinator that the verification of the adding bags was successful 
	 */
	public void addBagVerficationSuccesful(boolean ownBag);
	
	/*
	 * Notify coordinator that the attendant approves the bag verification even though it failed
	 */
	public void approveBagWeightVerificationFailure(boolean addOwnBag);
	
	/*
	 * Notify coordinator that product(s) have been added to the cart.
	 */
	public void productAddedToPurchase(Product product);
	
	/*
	 * Notify coordinator that the pay with gift card button has been pressed; 
	 */
	public void payWithGiftCardButtonPressed(String cardNum);
	
	/*
	 * Notify coordinator that an item has been placed in the scanning area
	 */
	public void productPlacedInScanningArea(double expectedWeight) ;
}
