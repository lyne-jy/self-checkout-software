package ca.ucalgary.seng300.simulations;

/*
 * Interface that notifies observers of the StoreAttendant class
 */
public interface StoreAttendantObserver {

	/*
	 * Notify observer that paper has been added to the selfcheckout station 
	 */
	public void paperAddedToPrinter(int units);
	
	/*
	 * Notify observer that ink has been added to the selfcheckout station
	 */
	public void inkAddedToPrinter(int quantity);
}
