package ca.ucalgary.seng300.selfcheckout.coordination;

import ca.ucalgary.seng300.selfcheckout.utility.Cart;

/*
 * Interface used to notify observer of the notifier about events
 */
public interface SCCoordinatorObserver {

	/*
	 * Informs that the selfcheckout has been shut down
	 */
	public void shutDownStation();
	
	/*
	 * Informs that the selfcheckout station has been started
	 */
	public void startStation();

	/*
	 * Informs that the selfcheckout station has been blocked
	 */
	public void blockStation();

	/*
	 * Informs that the selfcheckout station has been blocked
	 */
	public void unblockStation();
	
	/*
	 * Notifies that a product has been removed from the cart
	 */
	public void productRemovedFromPurchase(Cart cart, boolean showMsg);
	
	/*
	 * Notifies that a product has been added to the cart
	 */
	public void productAddedToPurchase(Cart cart);
	
	/*
	 * Notifies that a bag(s) have been added to the cart
	 */
	public void bagsAddedToPurchase(Cart cart);
}