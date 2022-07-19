package ca.ucalgary.seng300.selfcheckout.product;

import org.lsmr.selfcheckout.Barcode;

/*
 * Simple interface used for objects that will listen/observe Scan
 */
public interface ScanObserver {

	/*
	 *  Notifies observer than an item was tried to be scanned in the wrong phase
	 */
	public void barcodeScannedInWrongPhase();
	
	/*
	 * Notifies observer that the scanned item does not exist in the database
	 */
	public void barcodeScannedNotInDatabase();
	
	/*
	 * Notifies observer that product is to be placed in bagging area area
	 */
	public void productScanned(Barcode barcode);
}
