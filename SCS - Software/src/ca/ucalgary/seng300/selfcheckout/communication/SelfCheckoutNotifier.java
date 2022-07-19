package ca.ucalgary.seng300.selfcheckout.communication;

import java.math.BigDecimal;
import java.util.ArrayList;
import ca.ucalgary.seng300.selfcheckout.utility.TimerObserver;
import ca.ucalgary.seng300.simulations.StoreAttendantObserver;
import ca.ucalgary.seng300.ui.UserInterfaceControlSoftware;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.products.Product;

import ca.ucalgary.seng300.attendant.SupervisorStationControlSoftware;
import ca.ucalgary.seng300.attendant.communication.SSNotifierSystemObserver;
import ca.ucalgary.seng300.selfcheckout.coordination.SCCoordinatorObserver;
import ca.ucalgary.seng300.selfcheckout.funds.PaymentObserver;
import ca.ucalgary.seng300.selfcheckout.product.BaggingAreaObserver;
import ca.ucalgary.seng300.selfcheckout.product.CheckoutObserver;
import ca.ucalgary.seng300.selfcheckout.product.EnterMembershipObserver;
import ca.ucalgary.seng300.selfcheckout.product.PLULogicObserver;
import ca.ucalgary.seng300.selfcheckout.product.ScanObserver;
import ca.ucalgary.seng300.selfcheckout.utility.Cart;
import ca.ucalgary.seng300.selfcheckout.utility.ComplexAbstractSoftware;

/*
 * Class used for communication among the Coordinator, Software Components (ex: Payment, Scan, etc), and the future UI
 */
public class SelfCheckoutNotifier extends ComplexAbstractSoftware<SCNotifierObserver> implements PaymentObserver, ScanObserver, BaggingAreaObserver, CheckoutObserver, EnterMembershipObserver, TimerObserver, SCCoordinatorObserver, PLULogicObserver, StoreAttendantObserver{

	/* FOR CONNECTING ALL COMPONENTS IN TERMS OF COMMUNICATION */	
	
	private ArrayList<SSNotifierSystemObserver> communicationWithSupervisor;
	public final int id;
	
	public SelfCheckoutNotifier(int id) {
		communicationWithSupervisor = new ArrayList<>();
		this.id = id;
	}
	
	// Connects this overall system with the supervisor-station system
	public void connectToSupervisorSystem(SupervisorStationControlSoftware sscs) {
		
		if(sscs == null) throw new NullPointerException();
			
		SSNSO newObserver = new SSNSO();
		communicationWithSupervisor.add(newObserver);
		
		sscs.attachToSystem(newObserver);
	}
		
	/*
	 * Checks whether a notifier is equal to the notifier in this system
	 */
	public boolean checkEquals(SSNotifierSystemObserver obs) {
		return communicationWithSupervisor.contains(obs);
	}
	
	/* MESSAGES FROM PRODUCT FACADE */

	@Override
	public void PLUEnteredNotInDatabase() {
		notifyPLUEnteredNotInDatabase(); // To UI
	}

	@Override
	public void productAdded(PriceLookupCode c) {
		notifyProductAdded(c); // To Coordinator 
	}
	
	@Override
	public void productPlacedInScanningArea(double expectedWeight) {
		notifyProductPlacedInScanningArea(expectedWeight);
	}

	@Override
	public void barcodeScannedNotInDatabase() {
		notifyBarcodeScannedNotInDatabase(); // To UI
	}

	@Override
	public void productScanned(Barcode barcode) {
		notifyProductScanned(barcode);			// To Coordinator and UI 
	}
	
	@Override
	public void productWeightVerificationSuccesful() {
		notifyProductVerificationSuccesful(); // To Coordinator
	}

	@Override
	public void overloadOfScale() {
		notifyOverloadOfScale(); // To UI
	}
	
	@Override
	public void productWeightVerificationFailure() {
		notifyProductWeightVerificationFailure(); // To UI and attendant's station
	}
	
	@Override
	public void allProductsRemoved() {
		notifyAllProductsRemoved(); // To Coordinator	
	}
	
	@Override
	public void addBagVerificationSuccessful(boolean ownBag) {
		notifyAddBagVerificationSuccesful(ownBag); // To coordinator and UI
	}

	@Override
	public void addBagVerificationFailure(boolean ownBag) {
		 notifyAddBagVerificationFailure(ownBag);  // TO UI and attendant's station
	}
	
	/* MESSAGES FROM FUNDS FACADE */

	@Override
	public void cardPaymentSuccesful() {
		notifyCardPaymentSuccesful(); 	 // To Coordinator
		notifyShowThankYouMessage(false); 		 		// To UI
	}

	@Override
	public void cardPaymentUnssesful() {
		notifyCreditCardPaymentError();	  // To UI
	}

	@Override
	public void fundsRegistered(BigDecimal totalAmount) {	
		notifyCashFundsRegistered(totalAmount); // To UI
	}

	@Override
	public void enoughCashInserted() {
		notifyEnoughCashInserted(); // To Coordinator
	}
	@Override
	public void partialPayment() {
		notifyPartialPayment();
		
	}
	@Override
	public void allChangeReturned() {
		notifyShowThankYouMessage(false); // To UI
	}

	@Override
	public void partialChangeReturned(BigDecimal credit) {
		// Clearly state credit remains
		notifyShowThankYouMessage(true); // To UI
		notifyPartialChangeReturned(credit); // To Attendant Stations
	}
	
	@Override
	public void hardwareError() {
		notifyCouldNotReturnChange();
	}
	
	/* MESSAGES FROM CHECKOUT */
	
	@Override
	public void printerError() {
		notifyPrinterError(); // To UI
	}

	@Override
	public void printerLowOnInk() {
		notifyPrinterLowOnInk();
	}

	@Override
	public void printerLowOnPaper() {
		notifyPrinterLowOnPaper();
	}
	
	/* MESSAGES FROM SCAN MEMBERHSIP */
	
	@Override
	public void membershipCardVerifiedSuccesfully(Barcode barcode) {
		notifyMembershipCardVerifiedSuccesfully(barcode); // To Coordinator to UI
	}
	
	@Override
	public void membershipCardNotInDatabase() {
		notifyMembershipCardNotInDatabase(); // To Coordinator and UI
	}
	
	@Override
	public void placeBagsInBaggingArea() {
		notifyPlaceBagsInBaggingArea(); // To UI
	}
	
	/* MESSAGES FROM UI FACADE */ // For now they can be directly called as a simulation of the interaction with the UI
	
	public void addBagsButtonPressed(int numOfBags, boolean ownBags) {
		notifyAddBagsButtonPressed(numOfBags, ownBags);      
	}
	
	public void checkoutButtonPressed() {
		notifyCheckoutButtonPressed(); // To coordinator
	}

	public void payWithCashButtonPressed() {
		notifyPayWithCashButtonPressed(); // To coordinator
	}
	
	public void payWithCardButtonPressed() {
		notifyPayWithCardButtonPressed(); // To coordinator
	}
	
	public void payWithGiftCardButtonPressed(String cardNum) {
		notifyPayWithGiftCardButtonPressed(cardNum); // To coordinator
	}
	
	public void addMoreItemsButtonPresses() {
		notifyAddMoreItemsButtonPresses(); 
	}
	
	public void scanMembershipButtonPressed() {
		notifyEnterMembershipButtonPressed(); // To Coordinator
	}
	
	public void membershipCardEntered(Numeral[] listOfDigitsInMembership) { 
		notifyMembershipEntered(new Barcode(listOfDigitsInMembership)); // To Coordinator
	}
	
	public void enterPLUButtonPressed(PriceLookupCode code) {
		notifyenterPLUButtonPressed(code); // to coordinator
	}
	
	/* MESSAGES FROM TIMER */

	@Override
	public void failedToPlaceItemInBaggingArea() {
		notifyUserFailedToPlaceItemInBaggingArea(); // To UI
	}
	
	@Override
	public void failedToPlaceItemInScanningArea() {
		notifyUserFailedToPlaceItemInScanningArea();
	}
	
	/* MESSAGES FROM ATTENDANT's STATION */
	
	private class SSNSO implements SSNotifierSystemObserver{
		
		@Override
		public void approveWeightDiscrepancy() {
			notifyApproveWeightDiscrepancy(); // To coodinator	
		}

		@Override
		public void blockStation() {
			notifyBlockStation();	// To coordinator
		}

		@Override
		public void unblockStation() {
			notifyUnblockStation();
		}

		@Override
		public void shutDownStation() {
			notifyShutDownStationToCoordinator();
		}

		@Override
		public void startStation() {
			notifyStartStationToCoordinator();
		}	

		@Override
		public void approveProductNotBagged() {
			notifyApproveProductNotBagged(); // To coodinator	
		}

		@Override
		public void productRemovedFromPurchase(Cart newCart) {
			notifyProductRemovedFromPurchaseToCoordinator(newCart); // To coodinator
		}

		@Override
		public void approveBagWeightVerificationFailure(boolean addOwnBag) {
			notifyApproveBagWeightVerificationFailure(addOwnBag); // To coodinator
		}

		@Override
		public void productAddedToPurchase(Product product) {
			notifyProductAddedToPurchaseToCoordinator(product);
		}
	}

	/* MESSAGES FROM COORDINATOR */
	
	@Override
	public void shutDownStation() {
		notifyShutDownStationToUI();
	}

	@Override
	public void startStation() {
		notifyStartStationToUI();
	}

	@Override
	public void blockStation() {
		notifyBlockStationToUI();
	}

	@Override
	public void unblockStation() {
		notifyUnblockStationToUI();
	}

	@Override
	public void productRemovedFromPurchase(Cart cart, boolean showMsg) {
		notifyProductRemovedFromPurchaseToUI(cart, showMsg); // To UI
	}

	@Override
	public void productAddedToPurchase(Cart cart) {
		notifyProductAddedToPurchaseToUI(cart); // To UI
	}
	
	@Override
	public void bagsAddedToPurchase(Cart cart) {
		notifyBagsAddedToPurchase(cart);// To UI
	}
	
	/* MESSAGES FROM STORE ATTENDANT SIMULATION */
	
	@Override
	public void paperAddedToPrinter(int units) {
		notifyPaperAddedToPrinter(units); // To coodinator
	}

	@Override
	public void inkAddedToPrinter(int quantity) {
		notifyInkAddedToPrinter(quantity); // To coordinator
	}
	
	/* HELPER METHODS */
 	
	// To Store Attendant Stations

	private void notifyPartialChangeReturned(BigDecimal credit) {
		for(SCNotifierObserver obs : observers) {
						
			// To all supervisor stations
			if(obs instanceof SCNotifierSystemObserver)
				((SCNotifierSystemObserver)obs).partialChangeReturned(id, credit); 
		}
	}
	
	private void notifyPrinterLowOnInk() {
		for (SCNotifierObserver obs : observers) {
			
			// To any other external device 
			if(obs instanceof SCNotifierExternalObserver)
				((SCNotifierExternalObserver)obs).printerLowOnInk(id);
			
			// Notify only the attendant stations (This does not concern the customer)
			if (obs instanceof SCNotifierSystemObserver) {
				((SCNotifierSystemObserver) obs).printerLowOnInk(id);
			}
		}
	}

	private void notifyPrinterLowOnPaper() {
		for (SCNotifierObserver obs : observers) {
						
			// Notify only the attendant stations (This does not concern the customer)
			if (obs instanceof SCNotifierSystemObserver) {
				((SCNotifierSystemObserver) obs).printerLowOnPaper(id);
			}
		}
	}
	
	// To Coordinator
	
	private void notifyPayWithGiftCardButtonPressed(String cardNum) {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).payWithGiftCardButtonPressed(cardNum);
		}
	}
	
	
	private void notifyProductAddedToPurchaseToCoordinator(Product product) {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).productAddedToPurchase(product);
		}
	}
	
	private void notifyApproveBagWeightVerificationFailure(boolean addOwnBag) {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).approveBagWeightVerificationFailure(addOwnBag);
		}
	}
	
	
	private void notifyAddBagVerificationSuccesful(boolean ownBag) {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver) {
				((SCNotifierCoordinatorObserver) obs).addBagVerficationSuccesful(ownBag);
			}
			
			if(obs instanceof SCNotifierUIObserver) {
				((SCNotifierUIObserver) obs).addBagVerficationSuccesful(ownBag);
			}
		}
	}
		
	private void notifyPaperAddedToPrinter(int units) {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).paperAddedToPrinter(units);
		}
	}
	
	private void notifyInkAddedToPrinter(int quantity) {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).inkAddedToPrinter(quantity);
		}
	}
	
	private void notifyProductAdded(PriceLookupCode c){
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).productAdded(c);
		}
		
		try {
			UserInterfaceControlSoftware.productAdded(c);
		}catch(Exception e) {}
	}
	
	private void notifyShutDownStationToCoordinator() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).shutDownStation();
		}
	}
	
	private void notifyStartStationToCoordinator() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).startStation();
		}
	}
	
	private void notifyApproveWeightDiscrepancy() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).approveWeightDiscrepancy();
		}
	}
	
	private void notifyApproveProductNotBagged() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).approveProductNotBagged();
		}
	}
	
	private void notifyBlockStation() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).blockStation();
		}
	}

	private void notifyUnblockStation() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).unblockStation();
		}
	}
	
	private void notifyProductRemovedFromPurchaseToCoordinator(Cart cart) {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).productRemovedFromPurchase(cart);
		}
	}
	
	private void notifyAddBagsButtonPressed(int numOfBags, boolean ownBags) {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).addBagsButtonPressed(numOfBags, ownBags);
		}
	}

	private void notifyAddMoreItemsButtonPresses() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).addMoreItemsButtonPresses();
		}
	}
	private void notifyenterPLUButtonPressed(PriceLookupCode code) {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).enterPLUButtonPressed(code);
		}
	}
	
	private void notifyPayWithCashButtonPressed() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).payWithCashButtonPressed();
		}
	}
		
	private void notifyEnoughCashInserted() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).enoughCashInserted();
		}
	}
	private void notifyPartialPayment() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).partialPayment();
		}
	}
	private void notifyCardPaymentSuccesful() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).cardPaymentSuccesful();
			
			// To UI
			if(obs instanceof SCNotifierUIObserver)
				((SCNotifierUIObserver)obs).cardPaymentSuccesful();
			
		}
	}
	
	private void notifyProductScanned(Barcode barcode) {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).productScanned(barcode);
			
			if(obs instanceof SCNotifierUIObserver)
				((SCNotifierUIObserver)obs).productScanned(barcode);
		}
		
		try {
			UserInterfaceControlSoftware.productScanned(barcode);
		} catch(Exception e) {}
	}
	
	private void notifyProductVerificationSuccesful() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).productVerificationSuccesful();
		}
		
	}
	
	private void notifyCheckoutButtonPressed() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).checkoutButtonPressed();
		}
	}
	
	private void notifyAllProductsRemoved() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).allProductsRemoved();
			
			if(obs instanceof SCNotifierUIObserver)
				((SCNotifierUIObserver)obs).allProductsRemoved();
		}
		
		try {
			UserInterfaceControlSoftware.allProductsRemoved();
		}catch(Exception e){}
	}
		
	private void notifyPayWithCardButtonPressed() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).payWithCardButtonPressed();
		}
	}
	
	private void notifyMembershipCardVerifiedSuccesfully(Barcode barcode) {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).membershipCardVerifiedSuccesfully(barcode);
			
			if(obs instanceof SCNotifierUIObserver)
				((SCNotifierUIObserver)obs).membershipCardScannedSuccesfully(barcode);
		}
		
		try {
			UserInterfaceControlSoftware.membershipCardScannedSuccesfully(barcode);	
		} catch(Exception e) {}
	}
	
	private void notifyEnterMembershipButtonPressed() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).enterMembershipButtonPressed();
		}
	}
	
	private void notifyMembershipEntered(Barcode barcode) {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).membershipEntered(barcode);
		}
	}
	
	private void notifyProductPlacedInScanningArea(double expectedWeight) {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).productPlacedInScanningArea(expectedWeight);;
		}
		
		try {
			UserInterfaceControlSoftware.producPlacedInScanningArea();
		}catch(Exception e) {}
		
	}
	
	// To UI
	
	private void notifyOverloadOfScale() {
		try {
			UserInterfaceControlSoftware.overloadOfScaleInBagging();
		} catch(Exception e) {}
	}
	
	private void notifyBagsAddedToPurchase(Cart cart) {
		
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierUIObserver)
				((SCNotifierUIObserver)obs).bagsAddedToPurchase(cart);
		}
	}
	
	private void notifyPlaceBagsInBaggingArea() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierUIObserver)
				((SCNotifierUIObserver)obs).placeBagsInBaggingArea();
		}
	}
	
	private void notifyPLUEnteredNotInDatabase(){
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierUIObserver)
				((SCNotifierUIObserver)obs).PLUEnteredNotInDatabase();
		}
		
		try {
			UserInterfaceControlSoftware.PLUEnteredNotInDatabase();
		}catch(Exception e) {}
	}
	
	private void notifyShutDownStationToUI() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierUIObserver)
				((SCNotifierUIObserver)obs).shutDownStation();
		}
		
		try {
			UserInterfaceControlSoftware.shutDownStation();
		} catch(Exception e) {}
	}
	
	private void notifyStartStationToUI() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierUIObserver) {
				((SCNotifierUIObserver) obs).startStation();
			}
		}
		
		try {
			UserInterfaceControlSoftware.startStation();
		} catch(Exception e) {}
	}

	private void notifyBlockStationToUI() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierUIObserver) {
				((SCNotifierUIObserver) obs).blockStation();
			}
		}
		
		try {
			UserInterfaceControlSoftware.blockStation();
		} catch(Exception e) {}
	}

	private void notifyUnblockStationToUI() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierUIObserver) {
				((SCNotifierUIObserver) obs).unblockStation();
			}
		}
		
		try {
			UserInterfaceControlSoftware.unblockStation();
		} catch(Exception e) {}
	}
	
	public void notifyShowThankYouMessage(boolean partialChange) {
		
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierUIObserver)
				((SCNotifierUIObserver)obs).showThankYouMessage(partialChange);
		}
		
		try {
			UserInterfaceControlSoftware.showThankYouMessage(partialChange);
		} catch(Exception e) {}
	}
	
	private void notifyCreditCardPaymentError() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierUIObserver)
				((SCNotifierUIObserver)obs).cardPaymentError();
		}
		
		try {
			UserInterfaceControlSoftware.cardPaymentError();
		}catch(Exception e) {}
	}
	
	private void notifyCashFundsRegistered(BigDecimal totalAmount) {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierUIObserver)
				((SCNotifierUIObserver)obs).cashFundsRegistered(totalAmount);
		}
		
		try {
			UserInterfaceControlSoftware.cashFundsRegistered(totalAmount);
		} catch(Exception e) {}
	}
	
	private void notifyPrinterError() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierUIObserver)
				((SCNotifierUIObserver)obs).printerError();
		}
		
		try {
			UserInterfaceControlSoftware.printerError();
		} catch(Exception e) {}
	}
	
	private void notifyBarcodeScannedNotInDatabase() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierUIObserver)
				((SCNotifierUIObserver)obs).barcodeScannedNotInDatabase();
		}
		
		try {
			UserInterfaceControlSoftware.barcodeScannedNotInDatabase();
		} catch(Exception e) {}
	}
	
	private void notifyProductWeightVerificationFailure() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierUIObserver)
				((SCNotifierUIObserver)obs).productWeightVerificationFailure();
									
			// To all supervisor stations
			if(obs instanceof SCNotifierSystemObserver)
				((SCNotifierSystemObserver)obs).productWeightVerificationFailure(id); 
		}
		
		try {
			UserInterfaceControlSoftware.productWeightVerificationFailure();
		} catch(Exception e) {}
	}
	
	private void notifyCouldNotReturnChange() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierUIObserver)
				((SCNotifierUIObserver)obs).couldNotReturnChange();
		}
		
		try {
			UserInterfaceControlSoftware.couldNotReturnChange();
		} catch(Exception e) {}
	}

	private void notifyUserFailedToPlaceItemInBaggingArea() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierUIObserver)
				((SCNotifierUIObserver)obs).placeItemBaggingArea();
			
			// To all supervisor stations
			if(obs instanceof SCNotifierSystemObserver)
					((SCNotifierSystemObserver)obs).failedToPlaceItem(id); 
		}
		
		try {
			UserInterfaceControlSoftware.placeItemBaggingArea();
		} catch(Exception e) {}
	}
	
	private void notifyUserFailedToPlaceItemInScanningArea() {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierUIObserver)
				((SCNotifierUIObserver)obs).placeItemBaggingArea();
		}
		
		try {
			UserInterfaceControlSoftware.placeItemScanningArea();
		}catch(Exception e){}
	}
	
	private void  notifyAddBagVerificationFailure(boolean ownBag) {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierUIObserver)
				((SCNotifierUIObserver)obs).addBagVerificationFailure(ownBag);
						
			// To all supervisor stations
			if(obs instanceof SCNotifierSystemObserver)
					((SCNotifierSystemObserver)obs).addBagVerificationFailure(id, ownBag); 
		}
		
		try {
			UserInterfaceControlSoftware.addBagVerificationFailure(ownBag);	
		} catch(Exception e) {}
	}
	
	private void notifyMembershipCardNotInDatabase() {
		
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierCoordinatorObserver)
				((SCNotifierCoordinatorObserver)obs).membershipCardNotInDatabase();
			
			if(obs instanceof SCNotifierUIObserver)
				((SCNotifierUIObserver)obs).membershipCardNotInDatabase();
		}
		
		try {
			UserInterfaceControlSoftware.membershipCardNotInDatabase();		
		} catch(Exception e) {}	
	}
	
	private void notifyProductRemovedFromPurchaseToUI(Cart cart, boolean showMsg) {
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierUIObserver)
				((SCNotifierUIObserver)obs).productRemovedFromPurchase(cart);
		}
		
		try {
			UserInterfaceControlSoftware.productRemovedFromPurchase(cart, showMsg);	
		} catch(Exception e) {}		
	}
	
	private void notifyProductAddedToPurchaseToUI(Cart cart) {
				
		for(SCNotifierObserver obs : observers) {
			if(obs instanceof SCNotifierUIObserver)
				((SCNotifierUIObserver)obs).productAddedToPurchase(cart);
		}
		
		try {
			UserInterfaceControlSoftware.productAddedToPurchase(cart);	
		} catch(Exception e) {}		
	}
	
	/* IGNORE */
	
	@Override
	public void cardPaymentInWrongPhase() {
		// ignore
	}
	
	@Override
	public void barcodeScannedInWrongPhase() {
		// ignore
	}

	@Override
	public void addedProductInWrongPhase() {
		// ignore
	}
	
	@Override
	public void InWrongPhase() {
		// ignore
	}
	
	@Override
	public void totalCalculatedSuccesfully() {
		// ignore
	}
	
	@Override
	public void receiptPrintedSuccesfully() {
		// ignore
	}
	
	@Override
	public void scannedMemberCardInWrongPhase() {
		// ignore
	}

	@Override
	public void addingBagsInWrongPhase() {
		// ignore
	}
		
	@Override
	public void PLUEnteredInWrongPhase() {
		// ignore
	}
}
