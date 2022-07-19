package ca.ucalgary.seng300.selfcheckout.utility;

public enum SoftwarePhase {
	
	/*
	 * The selfcheckout station is turned off, the UI should show a dark screen
	 */
	OFF,
	
	/**
	 * The device is in normal operation.
	 * The Initial state of the device
	 * Waiting for an item to be scanned
	 * Items should not be added / removed from bagging area
	 * Items can be scanned in this phase
	 * Item scanned -> enter HOLDING
	 * User can enter PAYING phase (if cart is not empty)
	 */
	READY,
	
	/*
	 * Customer desires to scan/enter their membership card, the system will go into another state
	 */
	ENTERING_MEMBERSHIP,
	
	/**
	 * The system is locked out and waiting for Attendant station to accept/reject the addition of customer's own bags
	 */
	ADDING_OWN_BAG,
	
	/**
	 * An item has been scanned and needs to be placed in the bagging area
	 * If wrong weight is placed in the bagging area or weight decreases (item removed), throw error
	 * If the scanned item is placed in the bagging area, add it to cart, then enter phase READY
	 */
	HOLDING,
	
	/**
	 * Initiated by user event only
	 * Items cannot be scanned, added, or removed from bagging area
	 * Only now can coins/banknotes be inserted
	 * Enter CHECKOUT after payment is successfully processed
	 */
	PAYING,
	
	/**
	 * Items can be remove from bagging area without consequence
	 * Items shouldn't be placed in bagging area
	 * Items cannot be scanned
	 */
	CHECKOUT, 
	/**
	 * User has added an item with a PLU Code and system is waiting for user to weigh item
	 */
	WEIGHING,

	/*
	 * The self checkout station can be blocked by the attendants station 
	 * Especially when weight verfication has failed
	 */
	BLOCKED
}