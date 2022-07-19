package ca.ucalgary.seng300.selfcheckout.product;

import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;
import ca.ucalgary.seng300.selfcheckout.utility.ComplexAbstractSoftware;
import ca.ucalgary.seng300.selfcheckout.utility.Database;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;

/**
 * 
 * Class deals with the logic of adding a new PLUItem
 *  
 */
public class PLULogic extends ComplexAbstractSoftware<PLULogicObserver> implements ElectronicScaleObserver{
	
	private double prevWeight;
	private PriceLookupCode pluEntered;
	
	public PLULogic(SelfCheckoutStation hw) {
		hw.scanningArea.attach(this);
		prevWeight = 0.0;
		pluEntered = null;
	}
	
	public void PLUCodeEntered(PriceLookupCode code){
		
    	// Software is not in the right phase for a plu code to be entered
        if (curPhase != SoftwarePhase.READY) {
        	notifyPLUEnteredInWrongPhase();
        	return;
        }
        
        try {
        	Database.database().readProductWithPLU(code);
        	pluEntered = code;
        	notifyProductAdded(code);
        }catch(IllegalArgumentException e) {
        	notifyPLUEnteredNotInDatabase();
        }
    }
	
	@Override
	public void weightChanged(ElectronicScale scale, double weightInGrams) {
		
		if(this.curPhase != SoftwarePhase.WEIGHING) return;
		
		double expectedWeight = Database.database().pluToWeight(pluEntered);
				
		//if phase not weighing return, otherwise notify 
		
		if(weightInGrams == 0.0 && expectedWeight == prevWeight) {
			notifyProductPlacedInScanningArea(prevWeight);
			prevWeight = 0.0;
			pluEntered = null;
		}else {
			prevWeight = weightInGrams;
		}
	}
	
	private void notifyProductPlacedInScanningArea(double expectedWeight) {
		for(PLULogicObserver obs : observers) {
			obs.productPlacedInScanningArea(expectedWeight);
		}
	}
	
	private void notifyPLUEnteredInWrongPhase() {
		for(PLULogicObserver obs : observers) {
			obs.PLUEnteredInWrongPhase();
		}
	}

	private void notifyPLUEnteredNotInDatabase() {
		for(PLULogicObserver obs : observers) {
			obs.PLUEnteredNotInDatabase();
		}
	}

	private void notifyProductAdded(PriceLookupCode c) {
		for(PLULogicObserver obs : observers) {
			obs.productAdded(c);
		}
	}
	
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// ignore
	}
	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// ignore
		
	}

	@Override
	public void overload(ElectronicScale scale) {
		// ignore
	}
	
	@Override
	public void outOfOverload(ElectronicScale scale) {
		// ignore
	}
}
