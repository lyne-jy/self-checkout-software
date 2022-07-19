package ca.ucalgary.seng300.selfcheckout.product;

/*
 * Simple observer interface for bagging area
 */
public interface BaggingAreaObserver {
	
	/*
	 * Notifies observer that the weight verification for the item added to the bagging area
	 * was successful
	 */
	public void productWeightVerificationSuccesful();
	
	/*
	 * Notifies observer that the weight verification for the item added to the bagging area
	 * failed due to a discrepancy in weight expected vs the weight of the item actually added
	 */
	public void productWeightVerificationFailure();
	
	/*
	 * Notifies observer that weight was added to the electronic scale on the wrong software phase
	 */
	public void addedProductInWrongPhase();
	
	/*
	 * Notifies observer that all products in the bagging area have been removed
	 */
	public void allProductsRemoved();
		
	/*
	 * Notifies observer of an attempt to add bags in the wrong software phase
	 */
	public void addingBagsInWrongPhase();
	
	/*
	 * Notifies that bags must be placed in the bagging area
	 */
	public void placeBagsInBaggingArea();
	
	/*
	 * Notifes that the verification for adding bags into the bagging area was succesful
	 */
	public void addBagVerificationSuccessful(boolean ownBag);
	
	/*
	 * Notifies that the verification for adding bags into the bagging area failed
	 */
	public void addBagVerificationFailure(boolean ownBag);
	
	/*
	 * Notifiers observer that an overload of the scale has occurred
	 */
	public void overloadOfScale();
}