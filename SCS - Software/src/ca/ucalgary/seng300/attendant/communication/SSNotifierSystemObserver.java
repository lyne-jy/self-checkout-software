package ca.ucalgary.seng300.attendant.communication;

import org.lsmr.selfcheckout.products.Product;

import ca.ucalgary.seng300.selfcheckout.utility.Cart;

/*
 * Interface used for communication among software systems [it will simulate a wireless communication]
 * This will be used only by the SelfCheckoutControlSoftware
 */
public interface SSNotifierSystemObserver extends SSNotifierObserver {

	/*
	 * Notify Self-Checkout station that attendant decided to unblock a self-checkout station
	 */
	public void approveWeightDiscrepancy(); 
	
	/*
	 * Notify Self-Checkout station that attendant decided to block a self-checkout station
	 */
	public void blockStation();

	/*
	 * Notify Self-Checkout station that attendant decided to unblock a self-checkout station
	 */
	public void unblockStation();
		
	/*
	 * Notify Self-Checkout station that the attendant decided to shut down a self-checkout station
	 */
	public void shutDownStation();		
	
	/*
	 * Notify Self-Checkout station that the attendant decided to start the given self-checkout station
	 */
	public void startStation();
	
	/*
	 * Notify Self-Checkout station that the attedant removed a product from the cart
	 */
	public void productRemovedFromPurchase(Cart newCart);
	
	
	/*
	 * Notify self-checkout station that the attendant allows the product to not be bagged
	 */
	public void approveProductNotBagged();
	
	/*
	 * Notify self-checkout station that the attendant will allow the person to proceed even though the verification of adding 
	 * customer's own bags to the bagging area failed.
	 */
	public void approveBagWeightVerificationFailure(boolean addOwnBag);
	
	/*
	 * Notify self-checkout station that a new product(s) is to be added to the cart
	 */
	public void productAddedToPurchase(Product product);
}
