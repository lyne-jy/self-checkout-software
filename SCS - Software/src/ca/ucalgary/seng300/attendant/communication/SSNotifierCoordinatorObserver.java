package ca.ucalgary.seng300.attendant.communication;

/*
 * Interface used to inform coordinator about events from the UI
 */
public interface SSNotifierCoordinatorObserver extends SSNotifierObserver {
	
	/*
	 * Notify observer that the attendant attempted to log in 
	 */
	public void login(Integer pin);
	
	/*
	 * Notify observer that the attendant attempted to log out
	 */
	public void logout();
	
	/*
	 * Notify observer that the attendant approved the weight descrepancy of the given station
	 */
	public void approveWeightDiscrepancy(int id);
	
	/*
	 * Notify observer the the attendant station detected a product verification failure
	 */
	public void productVerficationFailure(int id);

	/*
	 * Notify observer that attendant approved that a product should not be placed in the bagging area
	 */
	public void approveProductNotBagged(int id );
	
	/*
	 * Notify observer that the attendant approve that the discrepancy with bags added to the bagging area
	 */
	public void approveBagWeightVerificationFailure(int id);
	
	/*
	 * The store attendant decided to block a station
	 */
	public void blockStation(int id);

	/*
	 * The store attendant decided to unblock a station
	 */
	public void unblockStation(int id);
	
	/*
	 * The store attendant station gets information that respective self-checkout station is in HOLDING [similar to BLOCKED]
	 */
	public void failedToPlaceItem(int id);
}
