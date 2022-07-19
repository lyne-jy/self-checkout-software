package ca.ucalgary.seng300.selfcheckout.product;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.BarcodeScannerObserver;
import ca.ucalgary.seng300.selfcheckout.utility.ComplexAbstractSoftware;
import ca.ucalgary.seng300.selfcheckout.utility.Database;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;

/**
 * Handles the barcodeScanned() event
 */
public class Scan extends ComplexAbstractSoftware<ScanObserver> implements BarcodeScannerObserver{
	
    /**
     * The scanner adds the scanned item into waiting area
     * Customer should be unable to scan another item while an item is in waiting area
     * After this, the touch screen will say something like "please place the item in bagging area"
     * Once the ElectronicScale detects the item has been added, the machine enters it's normal state
     * Then, the item is added to the REAL inventory, updating the virtual cart and subtotal. And the touchscreen becomes enabled again
     * 
     * @param barcodeScanner
     *          The hardware representation of the barcode scanner device that scanned the item
     * @param barcode
     *          The barcode of the item which was scanned
     */
	@Override
    public void barcodeScanned(BarcodeScanner barcodeScanner, Barcode barcode) {
		
    	// Software is not in the right phase for an item to be scanned
        if (curPhase != SoftwarePhase.READY) {
        	notifyBarcodeScannedInWrongPhase();
        	return;
        }
        
        // Retrive the barcoded product associated to the scanned barcode, if it does not exist scanning process cannot continue
        try {
        	Database.database().readProductWithBarcode(barcode);
        }catch(IllegalArgumentException e) {
        	notifyBarcodeScannedNotInDatabase();
        	return;
        }
          
       notifyProductScanned(barcode);     // phase should be changed to holding
    }
	
    /* OBSERVERS */
	private void notifyBarcodeScannedInWrongPhase() {
		for(ScanObserver obs : observers) {
			obs.barcodeScannedInWrongPhase();
		}
	}
	
	private void notifyBarcodeScannedNotInDatabase() {
		for(ScanObserver obs : observers) {
			obs.barcodeScannedNotInDatabase();
		}
	}
	
	private void notifyProductScanned(Barcode barcode) {
		for(ScanObserver obs : observers) {
			obs.productScanned(barcode);
		}
	}
	
    /* NOT IMPORTANT */
	
    @Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// ignore
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// ignore
	}
}
