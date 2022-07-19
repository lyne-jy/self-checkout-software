package ca.ucalgary.seng300.selfcheckout.product;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import ca.ucalgary.seng300.selfcheckout.utility.ComplexAbstractSoftware;
import ca.ucalgary.seng300.selfcheckout.utility.Database;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;

/**
 * This Class is the control software for the BaggingArea of the self checkout station.
 */
public class BaggingArea extends ComplexAbstractSoftware<BaggingAreaObserver> implements ElectronicScaleObserver{
	
	private double expectedPLUWeight;
	private PriceLookupCode pluToBeWeighed;
	private Barcode itemToBeWeighed;
	public final double ALLOWABLE_ERROR;
	private double curWeight;
	
	private double prevWeightRegistered;
	
	private int expectedNumBags;
	private double expectedBagWeight;
	public final double BAG_WEIGHT;
	
	private boolean addOwnBag;
	
	/**
	 * Constructor for BaggingArea class. Simulates the role of a real bagging area to communicate with other parts of the
	 * system. Utilizes Cart, SelfCheckoutStation and ElectronicScale.
	 * NullPointerException expected when either parameter == null (i.e., not passed)
	 * Sets the allowable error to 5 grams and initializes the current weight to 0.0.
	 * Calls attaches the new instance of BaggingArea to hw (formal parameter; instance of SelfCheckoutStation)
	 * 
	 * @param hw is a formal parameter that is passed in and attached to that instance of BaggingArea
	 * @param cart is formal parameter is passed in and associated to that instance of BaggingArea.
	 */
	public BaggingArea(){
		this.ALLOWABLE_ERROR = 0.5; // 0.5 grams
		this.curWeight = 0.0;
		BAG_WEIGHT = 2;
		reset();
	}
	
	public double getPrevWeightRegistered() {
		return prevWeightRegistered;
	}

	public int getExpectedNumBags() {
		return expectedNumBags;
	}
	
	public void addToCurWeight(double weight) {
		this.curWeight += weight;
	}

	public void setCurWeight(double weight) {
		this.curWeight = weight;
	}
	
	/**
	 * Method for getting the current weight registered (ON SCALE OR TOTAL WEIGHT OF BAGGING AREA THUS FAR?)
	 * @return current weight
	 */
	public double getCurWeightRegistered() {
		return this.curWeight;
	}
	
	/*
	 * Update the current weight that the bagging area should be expecting 
	 */
	public void subtractWeight(double weightToSubract) {
		
		if(weightToSubract > curWeight) throw new IllegalArgumentException();
		
		curWeight -= weightToSubract;
	}
	
	/*
	 * Getter for the weight that is expected in the bagging area scale in regards to bags
	 */
	public double getEpectedBagWeight() {
		return this.expectedBagWeight;
	}
		
	/**
	 * Method to get the item that needs to be weighed
	 * @return itemToBeWeighed the item to be weighed
	 */
	public Barcode getBarcodedItemToBeWeighed() {
		return this.itemToBeWeighed;
	}
	
	/*
	 * Method to get the item that needs to be weighed
	 * @return plyToBeWeiged
	 */
	public PriceLookupCode getPLUItemToBeWeighed(){
		return this.pluToBeWeighed;
	}
	
	/*
	 * Returns the expected weight of the PLU item
	 */
	public double getExpectedPLUWeight() {
		return this.expectedPLUWeight;
	}
	
	/**
	 * Method to assign the next item to be weighed
	 * @param newItemToBeWeighed
	 */
	public void setBarcodedItemToBeWeiged(Barcode newItemToBeWeighed) {
		itemToBeWeighed = newItemToBeWeighed;
	}
	
	/*
	 * Method to assign the next item to be weighed
	 * @param newItemToBeWeighed
	 */
	public void setPLUItemToBeWeighed(PriceLookupCode item) {
		pluToBeWeighed = item;
	}
	
	/*
	 * Method to assing the expected weight of the PLU item to be weighed
	 * @param weight
	 */
	public void setPLUExpectedWeight(double weight){
		expectedPLUWeight = weight;
	}
	
	/*
	 * Resets all private fields for the next use 
	 */
	public void reset() {
		pluToBeWeighed = null;
		itemToBeWeighed = null;
		expectedBagWeight = 0.0;
		expectedNumBags = 0;
		addOwnBag = false;
		prevWeightRegistered = 0.0;
	}
		
	/*
	 * Used for repesenting the use in which the customer decides to add his/her own bags
	 * or decides to buy bags
	 */
	public void addBags(int numOfBags, boolean ownBag) {
		// Software is in the paying phase
		if(curPhase == SoftwarePhase.ADDING_OWN_BAG){
			this.addOwnBag = ownBag;
			expectedNumBags = numOfBags;
			expectedBagWeight  = numOfBags * BAG_WEIGHT;
			notifyPlaceBagsInBaggingArea();
		}else {
			notifyAddingBagsInWrongPhase();
		}
	}
			
	/* COMMUNICATION WITH ELECTRONIC SCALE */
	/**
	 * This function deals with notifying observers whether or not their weight verification passed after being placed 
	 * in the bagging area
	 * 
	 * Should the weight passed exceed the weight limit of the scale, return without doing anything. 
	 * If the current phase is the checkout phase then don't do anything
	 * If the system is NOT in the holding phase then notify the observers that the product was added in the wrong phase
	 * If all the conditions above fail, i.e., the current phase IS holding and there is an item in the holding area, 
	 * verify the weight of that particular product and if this passes notify the observers that it has (phase should change to ready)
	 * otherwise, if the verification fails, phase stays the same and observers are notified of the failure.
	 */
	@Override
	public void weightChanged(ElectronicScale scale, double weightInGrams) {
		
		if(curPhase == SoftwarePhase.OFF || curPhase == SoftwarePhase.WEIGHING) return;
		
		if(weightInGrams > scale.getWeightLimit()) return; // In case of overload
		
		if(this.curPhase == SoftwarePhase.ADDING_OWN_BAG) {
			double diff = Math.abs(curWeight - weightInGrams);
			if (Math.abs(diff - expectedBagWeight) <= ALLOWABLE_ERROR) {  
				curWeight = weightInGrams;
				notifyAddBagVerificationSuccesful(this.addOwnBag);
			}else {
				notifyAddBagVerificationFailure(this.addOwnBag);
			}
			prevWeightRegistered = weightInGrams;
			return;
		}else if(this.curPhase == SoftwarePhase.CHECKOUT) { 
			if(weightInGrams == 0) {
				curWeight = 0;
				notifyAllProductsRemoved();
			}
			prevWeightRegistered = weightInGrams;
			return;	
		}else if(curPhase != SoftwarePhase.HOLDING && weightInGrams != curWeight){
			notifyProductWeightVerificationFailure();
			return;
		}else if(curPhase != SoftwarePhase.HOLDING && weightInGrams == curWeight) {
			notifyProductWeightVerificationSuccesful();
			return;
		}
		
		// An invalid item must have been removed
		if(weightInGrams == curWeight) return;
		
		/* Current phase is HOLDING and there is an item in the holding area  */
		if(itemToBeWeighed != null){
			
			BarcodedProduct product = Database.database().readProductWithBarcode(itemToBeWeighed);
			double diff = Math.abs(curWeight - weightInGrams);
			
			if (Math.abs(diff - product.getExpectedWeight())  <= ALLOWABLE_ERROR) {  
				curWeight = weightInGrams;
				notifyProductWeightVerificationSuccesful();  	// phase should change to READY
			}else {
				notifyProductWeightVerificationFailure();		// phase should not change until the right product is added
			}
			prevWeightRegistered = weightInGrams;

		}else if(pluToBeWeighed != null){
						
			double diff = Math.abs(curWeight - weightInGrams);
			if (Math.abs(diff - expectedPLUWeight)  <= ALLOWABLE_ERROR) {  
				curWeight = weightInGrams;
				notifyProductWeightVerificationSuccesful();  	// phase should change to READY
			}else {
				notifyProductWeightVerificationFailure();		// phase should not change until the right product is added
			}
			prevWeightRegistered = weightInGrams;
		}
	}

	@Override
	public void overload(ElectronicScale scale) {
		
		if(curPhase == SoftwarePhase.HOLDING) {
			notifyOverloadOfScale();
		}else {
			notifyOverloadOfScale();
		}	
	}
	
	/* OBSERVERS */

	/*
	 * Notifies observer that an overload in the scale occurred
	 */
	private void notifyOverloadOfScale() {
		for(BaggingAreaObserver obs : observers) {
			obs.overloadOfScale();
		}
	}

	/**
	 * method for notifying all registered observers that weight verification was successful
	 */
	
	private void notifyProductWeightVerificationSuccesful(){
		for(BaggingAreaObserver obs: observers) {
			obs.productWeightVerificationSuccesful();
		}
	}
	/**
	 * method for notifying all registered observers that weight verification was a failure
	 */
	private void notifyProductWeightVerificationFailure() {
		for(BaggingAreaObserver obs: observers) {
			obs.productWeightVerificationFailure();
		}
	}
	/**
	 * method for notifying all registered observers that the addition of the product occurred in the wrong phase
	 */
	@SuppressWarnings("unused")
	private void notifyAddedProductInWrongPhase() {
		for(BaggingAreaObserver obs: observers) {
			obs.addedProductInWrongPhase();
		}
	}
	/**
	 * Method to notify observers that all items on the scale have been removed
	 */
	private void notifyAllProductsRemoved() {
		for(BaggingAreaObserver obs: observers) {
			obs.allProductsRemoved();
		}
	}
	
	/**
	 * Method to notify observers that bags must be added to the bagging area
	 */
	private void notifyPlaceBagsInBaggingArea() {
		for(BaggingAreaObserver obs: observers) {
			obs.placeBagsInBaggingArea();
		}
	}
	
	/*
	 * Notify that the bags have been added succesfully
	 */
	private void notifyAddingBagsInWrongPhase(){
		for(BaggingAreaObserver obs: observers) {
			obs.addingBagsInWrongPhase();
		}
	}
	
	/*
	 * Notify that adding bag was succesfully verified
	 */
	private void notifyAddBagVerificationSuccesful(boolean ownBag) {
		for(BaggingAreaObserver obs: observers) {
			obs.addBagVerificationSuccessful(ownBag);
		}
	}
	
	/*
	 * Notify that adding bag failed
	 */
	private void notifyAddBagVerificationFailure(boolean ownBag) {
		for(BaggingAreaObserver obs: observers) {
			obs.addBagVerificationFailure(ownBag);
		}
	}

	/* NOT IMPORTANT */
	
	@Override
	public void outOfOverload(ElectronicScale scale) {
		// ignore
	}
	
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// ignore
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// ignore
	}
}
