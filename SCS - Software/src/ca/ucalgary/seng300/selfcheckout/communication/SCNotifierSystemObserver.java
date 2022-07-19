package ca.ucalgary.seng300.selfcheckout.communication;

import java.math.BigDecimal;

/*
 * Interface used for communication among software systems [it will simulate a wireless communication]
 * This will be used only by the SupervisorStationControlSoftware
 */
public interface SCNotifierSystemObserver extends SCNotifierObserver {
	
	/*
	 * Verification of product by weight failed, notify attendant station about it
	 */
	public void productWeightVerificationFailure(int id);
	
	/**
	 * Receipt printer of the self-checkout station is low on ink, notify attendant station
	 * @param id
	 * 		The identification number of the self-checkout station.
	 */
	public void printerLowOnInk(int id);

	/**
	 * Receipt printer of the self-checkout station is low on paper, notify attendant station
	 * @param id
	 * 		The identification number of the self-checkout station.
	 */
	public void printerLowOnPaper(int id);
	
	/*
	 * Notify observer of the selfcheckout system about partial change being returned to the customer, so that the 
	 * store attedeant can either give a giftcard or cash to the person. 
	 */
	public void partialChangeReturned(int id, BigDecimal credit);

	/*
	 * Notify supervisor station about customer scanning/adding an item but not placing it in the bagging area
	 */
	public void failedToPlaceItem(int id);
	
	/*
	 * Notify supervisor station about the customer's weight verification of bags added to the  bagging area failed
	 */
	public void  addBagVerificationFailure(int id, boolean ownBag);
}
