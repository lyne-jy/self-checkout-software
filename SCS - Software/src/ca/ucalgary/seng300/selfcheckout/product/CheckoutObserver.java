package ca.ucalgary.seng300.selfcheckout.product;

/*
 * Simple interface that communicates events happening during checkout
 */
public interface CheckoutObserver {

	/*
	 * Notifies the observers about the total being calculated 
	 */
	public void totalCalculatedSuccesfully();
	
	/*
	 * Notifies the observers that the entire receipt was printed
	 * and cut successfully
	 */
	public void receiptPrintedSuccesfully();

	/*
	 * Notifies the observers that system is in wrong phase to checkout
	 */
	public void InWrongPhase();

	/**
	 * Notifies the observers that the printer is running low on ink
	 */
	public void printerLowOnInk();
	
	/**
	 * Notifies the observers that the printer is running low on paper
	 */
	public void printerLowOnPaper();
	
	/*
	 * Notifies observers about isses with the printer (either not having ink, paper, or some hardware issues)
	 */
	public void printerError(); // To UI
}
