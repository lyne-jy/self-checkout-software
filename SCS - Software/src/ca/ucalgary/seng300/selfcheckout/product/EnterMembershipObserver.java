package ca.ucalgary.seng300.selfcheckout.product;

import org.lsmr.selfcheckout.Barcode;

/*
* Simple interface for observers of the Scan membership class
*/
public interface EnterMembershipObserver {
	
	/*
	 * Notifies observer that the membership card has been scanned succesfully
	 */
	public void membershipCardVerifiedSuccesfully(Barcode barcode);
	
	/*
	 * Notifies observer that the member card could not be found in the database
	 */
	public void membershipCardNotInDatabase();
	
	/*
	 * Notifies observer that the member card could not be found in the database
	 */
	public void scannedMemberCardInWrongPhase();
}
