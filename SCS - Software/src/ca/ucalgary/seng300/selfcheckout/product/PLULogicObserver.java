package ca.ucalgary.seng300.selfcheckout.product;

import org.lsmr.selfcheckout.PriceLookupCode;

/**
 * Simple interface used for objects that will listen/observe PLULogic
 */
public interface PLULogicObserver {
	/**
	 * Notifies observer that an PLU code was entered in the wrong phase
	 */
	public void PLUEnteredInWrongPhase();
	/**
	 * Notifies observer that the PLU coded item entered does not exist in the database
	 */
	public void PLUEnteredNotInDatabase();
	/**
	 * Notifies observer that PLU coded product is to be placed in bagging area
	 */
	public void productAdded(PriceLookupCode c);
	
	/*
	 * Notifies observer that the PLU item has been placed in the scanning area
	 */
	public void productPlacedInScanningArea(double expectedWeight);
}
