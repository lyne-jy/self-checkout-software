package ca.ucalgary.seng300.selfcheckout.product;

import org.lsmr.selfcheckout.Barcode;

import ca.ucalgary.seng300.selfcheckout.utility.ComplexAbstractSoftware;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.BarcodeScannerObserver;
import ca.ucalgary.seng300.selfcheckout.utility.Database;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;


/* 
 * Class that corresponds to the scanning of a membership card 
 **/
public class EnterMembership extends ComplexAbstractSoftware<EnterMembershipObserver> implements BarcodeScannerObserver{
		
	/*
	 * For membership entered with the scanner
	 */
	@Override
	public void barcodeScanned(BarcodeScanner barcodeScanner, Barcode barcode) {
				
		// Only during the entering membership phase should this work
		if(curPhase != SoftwarePhase.ENTERING_MEMBERSHIP) {
			notifyEnteredMembershipCardInWrongPhase();
			return;
		}
		
        if(Database.database().readMembership(barcode)) {
	        notifyMembershipCardVerifiedSuccesfully(barcode);
        }else{
        	notifyMembershipCardNotInDatabase();
        }  
	}
	
	/*
	 * For membership entered on touch screen
	 */
	public void barcodeEntered(Barcode barcode) {
		// Only during the entering membership phase should this work
		if(curPhase != SoftwarePhase.ENTERING_MEMBERSHIP) {
			notifyEnteredMembershipCardInWrongPhase();
			return;
		}

        if(Database.database().readMembership(barcode)) {
	        notifyMembershipCardVerifiedSuccesfully(barcode);     
        }else{
        	notifyMembershipCardNotInDatabase();
        }  
	}

	/* OBSERVERS */

	private void notifyMembershipCardVerifiedSuccesfully(Barcode barcode){
		for (EnterMembershipObserver obs : observers) {
			obs.membershipCardVerifiedSuccesfully(barcode);
		}
	}
		
	private void notifyMembershipCardNotInDatabase(){
		for (EnterMembershipObserver obs : observers) {
			obs.membershipCardNotInDatabase();
		}
	}
	
	private void notifyEnteredMembershipCardInWrongPhase() {
		for (EnterMembershipObserver obs : observers) {
			obs.scannedMemberCardInWrongPhase();
		}
	}
	
	/* IGNORE */
	
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// ignore
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		//ignore
	}
}
