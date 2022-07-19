package ca.ucalgary.seng300.selfcheckout.funds;

/*
 * Simple interface for observers of card payment
 */
public interface CardPaymentObserver {
	
	/*
	 * Notifies observer that the card payment was successful
	 */
	public void cardPaymentSuccesful();
	
	/*
	 * Notifies observer that the card payment was not successful
	 */
	public void cardPaymentUnssesful();
	
	/*
	 * Notify card inserted in the wrong phase
	 */
	public void cardPaymentInWrongPhase();
}
