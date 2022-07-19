 package ca.ucalgary.seng300.attendant.communication;

import java.math.BigDecimal;
import java.util.ArrayList;

import ca.ucalgary.seng300.ui.UserInterfaceControlSoftware;
import org.lsmr.selfcheckout.products.Product;

import ca.ucalgary.seng300.attendant.coordination.SSCoordinatorObserver;
import ca.ucalgary.seng300.attendant.coordination.SupervisorStationCoordinator;
import ca.ucalgary.seng300.selfcheckout.SelfCheckoutControlSoftware;
import ca.ucalgary.seng300.selfcheckout.communication.SCNotifierSystemObserver;
import ca.ucalgary.seng300.selfcheckout.utility.Cart;
import ca.ucalgary.seng300.selfcheckout.utility.ComplexAbstractSoftware;

public class SupervisorStationNotifier extends ComplexAbstractSoftware<SSNotifierObserver> {
	
	/* FOR CONNECTING ALL COMPONENTS IN TERMS OF COMMUNICATION */	
	private ArrayList<SCNotifierSystemObserver> communicatonWithSelfCheckout;
	
	public SupervisorStationNotifier() {
		communicatonWithSelfCheckout = new ArrayList<>();
	}
	
	// Connects this overall system with the self-checkout system
	public void connectToSelfCheckoutSystem(SelfCheckoutControlSoftware sccs) {
		if(sccs == null) throw new NullPointerException();
		
		SCNSO newObserver = new SCNSO();
		communicatonWithSelfCheckout.add(newObserver);
		
		sccs.attachToSystem(newObserver);
	}
	
	// Method simulates the connection of all software components in the system
	public void connectLocalComponents(/* UI, */ SupervisorStationCoordinator ssc) {
		
		if(ssc == null) throw new NullPointerException();
		
		ssc.attach(new SSCO());
	}
	
	public boolean checkEquals(SCNotifierSystemObserver obs) {
		return communicatonWithSelfCheckout.contains(obs);
	}
	
	/* MESSAGES FROM UI */
	
	public void aproveAddBagVerificationFailure(SelfCheckoutControlSoftware scs, boolean ownBags) {
		notifyAproveAddOwnBagVerificationFailure(scs, ownBags);
	}
	
	public void approveWeightDiscrepancy(SelfCheckoutControlSoftware scs) {		
		notifyAproveWeightDiscrepancy(scs); // To appropriate self-checkout station and coordinator
	}
	
	public void approveProductNotBagged(SelfCheckoutControlSoftware scs) {
		notifyApproveProductNotBagged(scs);
	}
	
	public void blockStation(SelfCheckoutControlSoftware scs) {
		notifyBlockStation(scs);
	}

	public void unblockStation(SelfCheckoutControlSoftware scs) {
		notifyUnblockStation(scs);
	}
	
	public void startStation(SelfCheckoutControlSoftware scs) {
		notifyStartStation(scs);
	}
	
	public void shutDownStation(SelfCheckoutControlSoftware scs) {
		notifyShutDownStation(scs);
	}
	
	public void productRemovedFromPurchase(SelfCheckoutControlSoftware scs, Cart newCart) {
		notifyProductRemovedFromPurchase(scs, newCart);
	}
	
	public void productAddedToPurchse(SelfCheckoutControlSoftware scs, Product product) {
		notifyProductAddedToPurchase(scs, product); // To self-checkout station
	}
	
	public void login(Integer pin) {
		notifyLogin(pin); // To coordinator
	}
	
	public void logout() {
		notifyLogout();   // To coordinator
	}
	
	/* MESSAGES FROM ANY SELF-CHECKOUT STATION */
	
	private class SCNSO implements SCNotifierSystemObserver{

		@Override
		public void productWeightVerificationFailure(int id) {
			notifyProductVerficationFailure(id); // To coodinator
		}

		@Override
		public void printerLowOnInk(int id) {
			notifyPrinterLowOnInk(id); // To UI
		}

		@Override
		public void printerLowOnPaper(int id) {
			notifyPrinterLowOnPaper(id); // To UI
		}
			
		public void partialChangeReturned(int id, BigDecimal credit) {
			notifyPartialChangeReturned(id, credit); 	// To UI
		}

		@Override
		public void failedToPlaceItem(int id) {
			notifyFailedToPlaceItem(id); // To UI and coordinator
		}

		@Override
		public void addBagVerificationFailure(int id, boolean ownBag) {
			notifyAddBagVerificationFailure(id, ownBag); // To UI
		}
	}
		
	/* MESSAGES FROM COORDINATOR */	
	
	private class SSCO implements SSCoordinatorObserver{

		@Override
		public void wrongCredentials() {
			notifyWrongCredentials(); // To UI
		}

		@Override
		public void validCredentials(String employeeName) {
			notifyValidCredentials(employeeName); // To UI
		}

		@Override
		public void successfulLogout() {
			notifySuccesfulLogout(); // To UI
		}

		@Override
		public void productVerficationFailure(ArrayList<Integer> stations) {
			notifyProductVerficationFailure(stations); // To UI
		}

		@Override
		public void approveWeightDiscrepancy(ArrayList<Integer> stations) {
			notifyApproveWeightDiscrepancy(stations); // To UI
		}

		@Override
		public void failedToPlaceItem(ArrayList<Integer> stations) {
			notifyFailedToPlaceItem(stations); // To UI
		}

		@Override
		public void approveProductNotBagged(ArrayList<Integer> stations) {
			notifyApproveProductNotBagged(stations); // To UI	
		} 
	}
	
	/* OBSERVERS */
	
	// To Self-Checkout Stations
	
	private void notifyProductAddedToPurchase(SelfCheckoutControlSoftware scs, Product product) {
		for(SSNotifierObserver obs : observers) {
			
			// To any other external device 
			if(obs instanceof SSNotiferExternalObserver)
				((SSNotiferExternalObserver)obs).productAddedToPurchase(product);
			
			if(obs instanceof SSNotifierSystemObserver && scs.checkEquals((SSNotifierSystemObserver)obs))
				((SSNotifierSystemObserver)obs).productAddedToPurchase(product);
		}
	}
	
	private void notifyAproveAddOwnBagVerificationFailure(SelfCheckoutControlSoftware scs, boolean ownBag) {
		
		for(SSNotifierObserver obs : observers) {
			
			// To any other external device 
			if(obs instanceof SSNotiferExternalObserver)
				((SSNotiferExternalObserver)obs).approveBagWeightVerificationFailure(ownBag);
			
			if(obs instanceof SSNotifierSystemObserver && scs.checkEquals((SSNotifierSystemObserver)obs))
				((SSNotifierSystemObserver)obs).approveBagWeightVerificationFailure(ownBag);
			
			// To coordinator too
			if(obs instanceof SSNotifierCoordinatorObserver)
				((SSNotifierCoordinatorObserver)obs).approveBagWeightVerificationFailure(scs.ID);
		}	
	}
	
	private void notifyAproveWeightDiscrepancy(SelfCheckoutControlSoftware scs) {
		
		for(SSNotifierObserver obs : observers) {
			
			// To any other external device 
			if(obs instanceof SSNotiferExternalObserver)
				((SSNotiferExternalObserver)obs).approveWeightDiscrepancy();
			
			if(obs instanceof SSNotifierSystemObserver && scs.checkEquals((SSNotifierSystemObserver)obs))
				((SSNotifierSystemObserver)obs).approveWeightDiscrepancy();
			
			// To coordinator too
			if(obs instanceof SSNotifierCoordinatorObserver)
				((SSNotifierCoordinatorObserver)obs).approveWeightDiscrepancy(scs.ID);
		}
	}
	
	private void notifyApproveProductNotBagged(SelfCheckoutControlSoftware scs) {
		for(SSNotifierObserver obs : observers) {
			
			// To any other external device 
			if(obs instanceof SSNotiferExternalObserver)
				((SSNotiferExternalObserver)obs).approveProductNotBagged();
			
			if(obs instanceof SSNotifierSystemObserver && scs.checkEquals((SSNotifierSystemObserver)obs))
				((SSNotifierSystemObserver)obs).approveProductNotBagged();
			
			// To coordinator too
			if(obs instanceof SSNotifierCoordinatorObserver)
				((SSNotifierCoordinatorObserver)obs).approveProductNotBagged(scs.ID);
		}
	}
	
	private void notifyBlockStation(SelfCheckoutControlSoftware scs) {
		for(SSNotifierObserver obs : observers) {
			
			// To coordinator
			if(obs instanceof SSNotifierCoordinatorObserver)
				((SSNotifierCoordinatorObserver)obs).blockStation(scs.ID);
			
			// To any other external device 
			if(obs instanceof SSNotiferExternalObserver)
				((SSNotiferExternalObserver)obs).blockStation();
			
			if(obs instanceof SSNotifierSystemObserver && scs.checkEquals((SSNotifierSystemObserver)obs))
				((SSNotifierSystemObserver)obs).blockStation();
		}
	}

	private void notifyUnblockStation(SelfCheckoutControlSoftware scs) {
		for(SSNotifierObserver obs : observers) {

			// To coordinator
			if(obs instanceof SSNotifierCoordinatorObserver)
				((SSNotifierCoordinatorObserver)obs).unblockStation(scs.ID);

			// To any other external device 
			if(obs instanceof SSNotiferExternalObserver)
				((SSNotiferExternalObserver)obs).blockStation();

			if(obs instanceof SSNotifierSystemObserver && scs.checkEquals((SSNotifierSystemObserver)obs))
				((SSNotifierSystemObserver)obs).unblockStation();
		}
	}
	
	private void notifyShutDownStation(SelfCheckoutControlSoftware scs) {
		for(SSNotifierObserver obs : observers) {
			
			// To any other external device 
			if(obs instanceof SSNotiferExternalObserver)
				((SSNotiferExternalObserver)obs).shutDownStation();

			if(obs instanceof SSNotifierSystemObserver && scs.checkEquals((SSNotifierSystemObserver)obs))
				((SSNotifierSystemObserver)obs).shutDownStation();
		}
	}
	
	private void notifyStartStation(SelfCheckoutControlSoftware scs) {
		for(SSNotifierObserver obs : observers) {
			
			// To any other external device 
			if(obs instanceof SSNotiferExternalObserver) {
				((SSNotiferExternalObserver) obs).startStation();
			}
			
			if(obs instanceof SSNotifierSystemObserver && scs.checkEquals((SSNotifierSystemObserver)obs)) {
				((SSNotifierSystemObserver) obs).startStation();
			}
		}
	}
	
	private void notifyProductRemovedFromPurchase(SelfCheckoutControlSoftware scs, Cart newCart) {
		for(SSNotifierObserver obs : observers) {
			// To any other external device 
			if(obs instanceof SSNotiferExternalObserver)
				((SSNotiferExternalObserver)obs).productRemovedFromPurchase(newCart);
			
			if(obs instanceof SSNotifierSystemObserver && scs.checkEquals((SSNotifierSystemObserver)obs))
				((SSNotifierSystemObserver)obs).productRemovedFromPurchase(newCart);
		}
		
		try {
			UserInterfaceControlSoftware.productRemovedFromPurchase(newCart, false);
		} catch(Exception e) {}
	}
	
	// To Coordinator
	
	private void notifyLogin(Integer pin) {
		for(SSNotifierObserver obs : observers) {
			if(obs instanceof SSNotifierCoordinatorObserver)
				((SSNotifierCoordinatorObserver)obs).login(pin);
		}
	}
	
	private void notifyLogout() {
		for(SSNotifierObserver obs : observers) {
			if(obs instanceof SSNotifierCoordinatorObserver)
				((SSNotifierCoordinatorObserver)obs).logout();
		}	
	}
	
	private void notifyProductVerficationFailure(int id) {
		for(SSNotifierObserver obs : observers) {
			if(obs instanceof SSNotifierCoordinatorObserver)
				((SSNotifierCoordinatorObserver)obs).productVerficationFailure(id);
		}
	}
		
	// To UI
	
	private void notifyAddBagVerificationFailure(int id, boolean ownBag) {
		for(SSNotifierObserver obs : observers) {
			if(obs instanceof SSNotifierUIObserver)
				((SSNotifierUIObserver)obs).addBagVerificationFailure(id, ownBag);
		}
		
		try {
			UserInterfaceControlSoftware.addBagVerificationFailure(id, ownBag);
		} catch(Exception e) {}
	}

	private void notifyFailedToPlaceItem(int id) {
		
		for(SSNotifierObserver obs : observers) {
			if(obs instanceof SSNotifierUIObserver)
				((SSNotifierUIObserver)obs).failedToPlaceItem(id);
			
			// To coordinator too
			if(obs instanceof SSNotifierCoordinatorObserver)
				((SSNotifierCoordinatorObserver)obs).failedToPlaceItem(id);
		}	
	}
	
	private void notifyApproveProductNotBagged(ArrayList<Integer> stations) {
		for(SSNotifierObserver obs : observers) {
			if(obs instanceof SSNotifierUIObserver)
				((SSNotifierUIObserver)obs).approveProductNotBagged(stations);
		}	
		
		try {
			UserInterfaceControlSoftware.approveProductNotBagged(stations);
		} catch(Exception e) {}
	}
	
	private void notifyPartialChangeReturned(int id, BigDecimal credit) {
		for(SSNotifierObserver obs : observers) {
			if(obs instanceof SSNotifierUIObserver)
				((SSNotifierUIObserver)obs).partialChangeReturned(id, credit);
		}	
		
		try {
			UserInterfaceControlSoftware.partialChangeReturned(id, credit);
		} catch(Exception e) {}
	}
	
	private void notifyWrongCredentials() {
		for(SSNotifierObserver obs : observers) {
			if(obs instanceof SSNotifierUIObserver)
				((SSNotifierUIObserver)obs).wrongCredentials();
		}
		
		try {
			UserInterfaceControlSoftware.wrongCredentials();
		} catch(Exception e) {}
	}
	
	private void notifyValidCredentials(String employeeName) {
		for(SSNotifierObserver obs : observers) {
			if(obs instanceof SSNotifierUIObserver)
				((SSNotifierUIObserver)obs).validCredentials(employeeName);
		}	
		
		try {
			UserInterfaceControlSoftware.validCredentials(employeeName);
		} catch(Exception e) {}
	}
	
	private void notifySuccesfulLogout() {
		for(SSNotifierObserver obs : observers) {
			if(obs instanceof SSNotifierUIObserver)
				((SSNotifierUIObserver)obs).successfulLogout();
		}	
		
		try {
			UserInterfaceControlSoftware.successfulLogout();
		} catch(Exception e) {}
	}
	
	private void notifyApproveWeightDiscrepancy(ArrayList<Integer> stations) {
		for(SSNotifierObserver obs : observers) {
			if(obs instanceof SSNotifierUIObserver)
				((SSNotifierUIObserver)obs).approveWeightDiscrepancy(stations);
		}
		
		try {
			UserInterfaceControlSoftware.approveWeightDiscrepancy(stations);
		} catch(Exception e) {}
	}

	private void notifyProductVerficationFailure(ArrayList<Integer> stations) {
		for(SSNotifierObserver obs : observers) {
			if(obs instanceof SSNotifierUIObserver)
				((SSNotifierUIObserver)obs).productVerficationFailure(stations);
		}
		
		try {
			UserInterfaceControlSoftware.productVerficationFailure(stations);
		} catch(Exception e) {}
	}
	
	private void notifyFailedToPlaceItem(ArrayList<Integer> stations) {
		for(SSNotifierObserver obs : observers) {
			if(obs instanceof SSNotifierUIObserver)
				((SSNotifierUIObserver)obs).failedToPlaceItem(stations);
		}
		
		try {
			UserInterfaceControlSoftware.failedToPlaceItem(stations);
		} catch(Exception e) {}
	}
	
	private void notifyPrinterLowOnInk(int id) {
		for(SSNotifierObserver obs : observers) {
			if(obs instanceof SSNotifierUIObserver)
				((SSNotifierUIObserver)obs).printerLowOnInk(id);
		}
		
		try {
			UserInterfaceControlSoftware.printerLowOnInk(id);
		} catch(Exception e) {}
	}

	private void notifyPrinterLowOnPaper(int id) {
		for(SSNotifierObserver obs : observers) {
			if(obs instanceof SSNotifierUIObserver)
				((SSNotifierUIObserver)obs).printerLowOnPaper(id);
		}
		
		try {
			UserInterfaceControlSoftware.printerLowOnPaper(id);
		} catch(Exception e) {}
	}
}	

