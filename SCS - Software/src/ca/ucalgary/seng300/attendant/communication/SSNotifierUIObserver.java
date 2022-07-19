package ca.ucalgary.seng300.attendant.communication;

import java.math.BigDecimal;
import java.util.ArrayList;

/*
 * Interface used to inform the UI of the attendant station about events
 */
public interface SSNotifierUIObserver extends SSNotifierObserver {

	/*
	 * Notifies observer that the credential passed were incorrect
	 */
	public void wrongCredentials();
	
	/*
	 * Notifies observer that the credentials passed were successful
	 */
	public void validCredentials(String employeeName);
	
	/*
	 * Notifier observer that the log out attempt was succesful
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

	/**
	 * Receipt printer of the self-checkout station is low on ink
	 * @param id
	 * 		The identification number of the self-checkout station.
	 */
	public void printerLowOnInk(int id);

	/**
	 * Receipt printer of the self-checkout station is low on paper
	 * @param id
	 * 		The identification number of the self-checkout station.
	 */
	public void printerLowOnPaper(int id);
	
	/*
	 * Notify observer that partial change has been returned by the selfcheckout station with the 
	 * given id.
	 */
	public void partialChangeReturned(int id, BigDecimal credit);

	/*
	 * Notify observer that an item has failed to be placed on the baging area
	 */
	public void failedToPlaceItem(ArrayList<Integer> stations);
	
	/*
	 * Notify observer that approval for not placing an item has been granted
	 */
	public void approveProductNotBagged(ArrayList<Integer> stations);
	
	/*
	 * Notify observer (UI) that item was not placed in the bagging area
	 */
	public void failedToPlaceItem(int id);
	
	/*
	 * Notify observer (UI) that adding a bag verification failed
	 */
	public void addBagVerificationFailure(int id, boolean ownBag);
}
