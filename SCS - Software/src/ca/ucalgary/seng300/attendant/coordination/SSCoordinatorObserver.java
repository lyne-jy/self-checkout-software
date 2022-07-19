package ca.ucalgary.seng300.attendant.coordination;

import java.util.ArrayList;

/*
 * Interface is used to notify observer of the coordinator about events
 */
public interface SSCoordinatorObserver {

	/*
	 * Notifies observer that the credential passed were incorrect
	 */
	public void wrongCredentials();
	
	/*
	 * Notifies observer that the credentials passed were successful
	 */
	public void validCredentials(String employeeName);
	
	/*
	 * Notifier observer that attempted logout was successful
	 */
	public void successfulLogout();
	
	/*
	 * Notify observer about the new list of stations where a weight verification has failed 
	 */
	public void productVerficationFailure(ArrayList<Integer> stations);
	
	/*
	 * Notify observer about the new list of stations where a weight verification has failed
	 */
	public void approveWeightDiscrepancy(ArrayList<Integer> stations);

	/*
	 * Notify observer that an item has failed to be placed on the the bagging area on time
	 */
	public void failedToPlaceItem(ArrayList<Integer> stations);
	
	/*
	 * Notify observer that there has been an approval for not bagging a product
	 */
	public void approveProductNotBagged(ArrayList<Integer> stations);
}

