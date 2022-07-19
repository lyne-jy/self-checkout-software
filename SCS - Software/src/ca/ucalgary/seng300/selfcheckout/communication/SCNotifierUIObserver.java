package ca.ucalgary.seng300.selfcheckout.communication;

import java.math.BigDecimal;

import org.lsmr.selfcheckout.Barcode;
import ca.ucalgary.seng300.selfcheckout.utility.Cart;

/*
 * Interface for observer of the Notifier class [In practice this interface will be implemented by the UI Only]
 */

public interface SCNotifierUIObserver extends SCNotifierObserver{
	
	/*
	 * Informs the UI that it should display a thank you message as payment have been successful
	 */
	public void showThankYouMessage(boolean partialChange);
	
	/*
	 * The card payment has been rejected by the institution (possibly show something) if not shown in the display of the card reader
	 */
	public void cardPaymentError();
	
	/*
	 * Inform the UI that cash funds have been entered so it can update what it is displaying
	 * [THIS SAME IDEA will be used in It3 to display the products in the cart]
	 */
	public void cashFundsRegistered(BigDecimal totalAmount);
	
	
	/*
	 * There was an error with the printer (so the receipt could not be printed) or is partially printed 
	 * [An oops message would work!]
	 */
	public void printerError();
	
	/*
	 * UI show display an error message information about this to the customer
	 */
	public void barcodeScannedNotInDatabase();
	
	/*
	 * Tell's the UI to go back to the start screen 
	 */
	public void allProductsRemoved();
	
	/*
	 * Verification of product by weight failed, a message can be displayed until the item with right weight is added
	 */
	public void productWeightVerificationFailure();
	
	/*
	 * Inform the customer that some change was returned but not fully due to some hardware failure
	 */
	public void couldNotReturnChange();

	/*
	 * Inform the customer to place scanned item to bagging area
	 */
	public void placeItemBaggingArea();

	/*
	 * Inform the customer that the membership card does not exist
	 */
	public void membershipCardNotInDatabase();
	
	/*
	 * Notify UI that product was scanned successfully
	 */
	public void productScanned(Barcode barcode);
	
	/*
	 * Notifies UI that the membership was scanned successfully
	 */
	public void membershipCardScannedSuccesfully(Barcode barcode);

	/*
	 * UI show display an error message information about this to the customer
	 */
	public void PLUEnteredNotInDatabase();

	/*
	 * Notifies UI that the station is to be shutdown
	 */
	public void shutDownStation();
	
	/*
	 * Notifies UI that the station is to be started
	 */
	public void startStation();

	/*
	 * Notifies UI that the station is to be blocked
	 */
	public void blockStation();

	/*
	 * Notifies UI that the station is to be blocked
	 */
	public void unblockStation();
	
	/*
	 * Notifies UI that an item has been removed from the the cart (and bagging area)
	 */
	public void productRemovedFromPurchase(Cart cart);
	
	/*
	 * Notifies UI that a new item has been added into the cart
	 */
	public void productAddedToPurchase(Cart cart);
	
	/*
	 * Notify UI that bags must be placed in the bagging area
	 */
	public void placeBagsInBaggingArea();
	
	/*
	 * Notify UI that addition of bags was succesful
	 */
	public void addBagVerficationSuccesful(boolean ownBag);
	
	/*
	 * Notify UI about the addition of bags into the cart
	 */
	public void bagsAddedToPurchase(Cart cart);
	
	/*
	 * Notify UI that verification of adding a bag failed
	 */
	public void  addBagVerificationFailure(boolean ownBag);
	
	/*
	 *  Notify UI that the card payment was succesful
	 */
	public void cardPaymentSuccesful();
}
