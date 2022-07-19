package ca.ucalgary.seng300.testing.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SupervisionStation;
import org.lsmr.selfcheckout.products.Product;
import ca.ucalgary.seng300.attendant.SupervisorStationControlSoftware;
import ca.ucalgary.seng300.attendant.communication.SSNotiferExternalObserver;
import ca.ucalgary.seng300.attendant.communication.SSNotifierUIObserver;
import ca.ucalgary.seng300.selfcheckout.SelfCheckoutControlSoftware;
import ca.ucalgary.seng300.selfcheckout.communication.SCNotifierCoordinatorObserver;
import ca.ucalgary.seng300.selfcheckout.communication.SCNotifierUIObserver;
import ca.ucalgary.seng300.selfcheckout.utility.Cart;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;
import ca.ucalgary.seng300.simulations.StoreAttendant;
import ca.ucalgary.seng300.testing.utility.TestUtility;

public class IntegrationTest2 {

	private SupervisorStationControlSoftware supervisor;
	private SelfCheckoutStation selfCheckoutHardware;
	private SelfCheckoutControlSoftware selfCheckout;
	private StoreAttendant attendant;
	
	private int actualFound;
	private int productAdded;
	private int productRemoved;
	private int cpCount;
	private int noScannerFailure;
	
	@Before
	public void setup() {
		
		selfCheckoutHardware = TestUtility.getHarwareInstance1();
		supervisor = new SupervisorStationControlSoftware(new SupervisionStation());
		selfCheckout = new SelfCheckoutControlSoftware(selfCheckoutHardware);
		selfCheckout.updatePhase(SoftwarePhase.READY);
		
		attendant = new StoreAttendant(new SelfCheckoutControlSoftware[] {selfCheckout});
				
		selfCheckout.connectToSoftwareSystem(supervisor);
		supervisor.connectToSoftwareSystem(selfCheckout);
		
		productAdded = 0;
		actualFound = 0;
		productRemoved = 0;
		noScannerFailure = 0;
		cpCount = 0;
	}
	
	/**
	 * Customer does not want to bag a scanned item
	 * 
	 * 	before checkout:
	 *  attendant adds ink and paper to printer
	 * 
	 * 1. User scans an item [in DB]
	 * 2. User waits for 6 seconds
	 * 3. Attendant approves of item not being added to bagging area
	 * 4. User decides to checkout
	 * 5. User pays with card
	 * 6. Receipt is printed and thank you message is shown
	 * 7. Customers takes their items
	 * @throws OverloadException 
	 * @throws InterruptedException 
	 */
	@Test
	public void customerNotBagItemTest() throws OverloadException, InterruptedException {	
		
		//before checkout 
		attendant.addInkToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		attendant.addPaperToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		
		selfCheckout.notifier.attach(new SCNotifierUIObserver() {

			@Override
			public void showThankYouMessage(boolean partialChange) {
				actualFound++;
				assertFalse(partialChange);
			}

			@Override
			public void cardPaymentError() {
				fail();
			}

			@Override
			public void cashFundsRegistered(BigDecimal totalAmount) {
				fail();
			}

			@Override
			public void printerError() {
				fail();
			}

			@Override
			public void barcodeScannedNotInDatabase() {
				fail();
			}

			@Override
			public void couldNotReturnChange() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}
			
			@Override
			public void membershipCardNotInDatabase() {
				fail();
			}

			@Override
			public void productScanned(Barcode barcode) {
				noScannerFailure++;	
			}

			@Override
			public void membershipCardScannedSuccesfully(Barcode barcode) {
				fail();
			}

			@Override
			public void PLUEnteredNotInDatabase() {
				fail();
			}
	
			@Override
			public void shutDownStation() {
				fail();
			}

			@Override
			public void startStation() {
				fail();
			}
			
			@Override
			public void productRemovedFromPurchase(Cart cart) {
				fail();
			}

			@Override
			public void productAddedToPurchase(Cart cart) {
				actualFound++;
				assertEquals(1, cart.getBarcodedProducts().size());
			}
			
			@Override
			public void placeBagsInBaggingArea() {
				fail();
			}

			@Override
			public void bagsAddedToPurchase(Cart cart) {
				fail();
			}

			@Override
			public void cardPaymentSuccesful() {
				actualFound++;
			}

			@Override
			public void addBagVerficationSuccesful(boolean ownBag) {
				fail();
			}

			@Override
			public void productWeightVerificationFailure() {
				fail();
			}

			@Override
			public void placeItemBaggingArea() {
				actualFound++;
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
			}

			@Override
			public void blockStation() {
				fail();
			}
			
			@Override
			public void unblockStation() {
				fail();
			}
		});
		
		//attach observer 
		supervisor.notifier.attach(new SSNotifierUIObserver() {

			@Override
			public void wrongCredentials() {
				fail();
			}

			@Override
			public void validCredentials(String employeeName) {
				fail();		
			}

			@Override
			public void successfulLogout() {
				fail();				
			}

			@Override
			public void productVerficationFailure(ArrayList<Integer> stations) {
				fail();			
			}

			@Override
			public void approveWeightDiscrepancy(ArrayList<Integer> stations) {
				fail();
			}

			@Override
			public void printerLowOnInk(int id) {
				fail();
			}

			@Override
			public void printerLowOnPaper(int id) {
				fail();
			}

			@Override
			public void partialChangeReturned(int id, BigDecimal credit) {
				fail();
			}

			@Override
			public void failedToPlaceItem(ArrayList<Integer> stations) {
				actualFound++;
			}

			@Override
			public void approveProductNotBagged(ArrayList<Integer> stations) {
				actualFound++;
			}

			@Override
			public void failedToPlaceItem(int id) {
				actualFound++;
			}

			@Override
			public void addBagVerificationFailure(int id, boolean ownBag) {
				fail();
			}
		});
		
		// scan one item
		while(noScannerFailure != 1) {
			selfCheckoutHardware.mainScanner.scan(TestUtility.normalItem);
		}
	
		//waits 6 seconds 
		Thread.sleep(6000);
		
		//attendant approves product not bagged 
		supervisor.notifier.approveProductNotBagged(selfCheckout);
		
		//user wants to checkout 
		selfCheckout.notifier.checkoutButtonPressed();
		
		//user pays with card
		selfCheckout.notifier.payWithCardButtonPressed();

		while(true) {
			try{
				selfCheckoutHardware.cardReader.insert(TestUtility.card1, "1234");
				break;
			}catch(Exception e) {}
		}
		
		assertEquals(7, actualFound);
	}
		
	/**
	 * Attendant approves of a weight discrepancy
	 * 
	 * 	before checkout:
	 *  attendant adds ink and paper to printer
	 * 
	 * 1. User scans an item [in DB]
	 * 2. User places wrong item in bagging area
	 * 3. Attednant approves weight discrepancy
	 * 4. User decides to checkout
	 * 5. User pays with giftcard
	 * 6. Receipt is printed and thank you message is shown
	 * 7. Customers takes their items
	 * @throws OverloadException 
	 * @throws InterruptedException 
	 */
	@Test
	public void attendantApproveWeightDiscrTest() throws OverloadException, InterruptedException {
	
		//before checkout 
		attendant.addInkToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		attendant.addPaperToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		
		selfCheckout.notifier.attach(new SCNotifierUIObserver() {

			@Override
			public void showThankYouMessage(boolean partialChange) {
				actualFound++;
				assertFalse(partialChange);
			}

			@Override
			public void cardPaymentError() {
				fail();
			}

			@Override
			public void cashFundsRegistered(BigDecimal totalAmount) {
				fail();
			}

			@Override
			public void printerError() {
				fail();
			}

			@Override
			public void barcodeScannedNotInDatabase() {
				fail();
			}

			@Override
			public void couldNotReturnChange() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				actualFound++;
			}
			
			@Override
			public void membershipCardNotInDatabase() {
				fail();
			}

			@Override
			public void productScanned(Barcode barcode) {
				noScannerFailure++;	
			}

			@Override
			public void membershipCardScannedSuccesfully(Barcode barcode) {
				fail();
			}

			@Override
			public void PLUEnteredNotInDatabase() {
				fail();
			}
	
			@Override
			public void shutDownStation() {
				fail();
			}

			@Override
			public void startStation() {
				fail();
			}
			
			@Override
			public void productRemovedFromPurchase(Cart cart) {
				fail();
			}

			@Override
			public void productAddedToPurchase(Cart cart) {
				actualFound++;
				assertEquals(1, cart.getBarcodedProducts().size());
			}
			
			@Override
			public void placeBagsInBaggingArea() {
				fail();
			}

			@Override
			public void bagsAddedToPurchase(Cart cart) {
				fail();
			}

			@Override
			public void cardPaymentSuccesful() {
				actualFound++;
			}

			@Override
			public void addBagVerficationSuccesful(boolean ownBag) {
				fail();
			}

			@Override
			public void productWeightVerificationFailure() {
				actualFound++;
			}

			@Override
			public void placeItemBaggingArea() {
				fail();
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
			}

			@Override
			public void blockStation() {
				fail();	
			}
			
			@Override
			public void unblockStation() {
				fail();
			}
		});
		
		//attach observer 
		supervisor.notifier.attach(new SSNotifierUIObserver() {

			@Override
			public void wrongCredentials() {
				fail();
			}

			@Override
			public void validCredentials(String employeeName) {
				fail();		
			}

			@Override
			public void successfulLogout() {
				fail();				
			}

			@Override
			public void productVerficationFailure(ArrayList<Integer> stations) {
				actualFound++;	
			}

			@Override
			public void approveWeightDiscrepancy(ArrayList<Integer> stations) {
				actualFound++;
			}

			@Override
			public void printerLowOnInk(int id) {
				fail();
			}

			@Override
			public void printerLowOnPaper(int id) {
				fail();
			}

			@Override
			public void partialChangeReturned(int id, BigDecimal credit) {
				fail();
			}

			@Override
			public void failedToPlaceItem(ArrayList<Integer> stations) {
				fail();
			}

			@Override
			public void approveProductNotBagged(ArrayList<Integer> stations) {
				fail();
			}

			@Override
			public void failedToPlaceItem(int id) {
				fail();
			}

			@Override
			public void addBagVerificationFailure(int id, boolean ownBag) {
				fail();
			}
		});
		
		// scan one item
		while(noScannerFailure != 1) {
			selfCheckoutHardware.mainScanner.scan(TestUtility.normalItem);
		}
		
		selfCheckoutHardware.baggingArea.add(TestUtility.normalItem2);
		
		//attendant approves product not bagged 
		supervisor.notifier.approveWeightDiscrepancy(selfCheckout);
		
		//user wants to checkout 
		selfCheckout.notifier.checkoutButtonPressed();
		
		//user pays with card
		selfCheckout.notifier.payWithGiftCardButtonPressed(TestUtility.giftCard1);

		selfCheckoutHardware.baggingArea.remove(TestUtility.normalItem2);
		
		assertEquals(7, actualFound);	
	}
	
	/**
	 * Attendant removes product from purchases
	 * 
	 * 
	 * before checkout:
	 *  attendant adds ink and paper to printer
	 *  
	 * 1. User scans an item and places it in the bagging area 
	 * 2. User scans another item and places it in the bagging area  
	 * 3. Customer asks the attendant to remove an item ( This will be through GUI)
	 * 4. Attendant removes an item from cart
	 * 5. User decides to checkout
	 * 6. User pays with card
	 * 7. Receipt is printed and thank you message is shown
	 * @throws OverloadException 
	 * @throws InterruptedException 
	 */
	
	@Test
	public void attendantRemovesItemTest() throws OverloadException {
		
		//before checkout 
		attendant.addInkToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		attendant.addPaperToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		
		//attach observer 
		selfCheckout.notifier.attach(new SCNotifierUIObserver() {

			@Override
			public void showThankYouMessage(boolean partialChange) {
				actualFound++;
				assertFalse(partialChange);
			}

			@Override
			public void cardPaymentError() {
				fail();
			}

			@Override
			public void cashFundsRegistered(BigDecimal totalAmount) {
				fail();
			}

			@Override
			public void printerError() {
				fail();
			}

			@Override
			public void barcodeScannedNotInDatabase() {
				fail();
			}

			@Override
			public void couldNotReturnChange() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}
			
			@Override
			public void membershipCardNotInDatabase() {
				fail();
			}

			@Override
			public void productScanned(Barcode barcode) {
				noScannerFailure++;	
			}

			@Override
			public void membershipCardScannedSuccesfully(Barcode barcode) {
				fail();
			}

			@Override
			public void PLUEnteredNotInDatabase() {
				fail();
			}
	
			@Override
			public void shutDownStation() {
				fail();
			}

			@Override
			public void startStation() {
				fail();
			}
			
			@Override
			public void productRemovedFromPurchase(Cart cart) {
				actualFound++;
			}

			@Override
			public void productAddedToPurchase(Cart cart) { 
				productAdded++;
			}
			
			@Override
			public void placeBagsInBaggingArea() {
				fail();
			}

			@Override
			public void bagsAddedToPurchase(Cart cart) {
				fail();
			}

			@Override
			public void cardPaymentSuccesful() {
				actualFound++;
			}

			@Override
			public void addBagVerficationSuccesful(boolean ownBag) {
				fail();
			}

			@Override
			public void productWeightVerificationFailure() {
				fail();
			}

			@Override
			public void placeItemBaggingArea() {
				fail();
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
			}

			@Override
			public void blockStation() {
				fail();
			}
			
			@Override
			public void unblockStation() {
				fail();
			}
		});
		
		supervisor.notifier.attach(new SSNotiferExternalObserver() {

			@Override 
			public void approveWeightDiscrepancy() {
				fail();
			}

			@Override
			public void blockStation() {
				fail();
				
			}

			@Override
			public void shutDownStation() {
				fail();
				
			}

			@Override
			public void startStation() {
				fail();
				
			}

			@Override
			public void productRemovedFromPurchase(Cart newCart) {
				productRemoved++;
			}

			@Override
			public void approveProductNotBagged() {
				fail();
				
			}

			@Override
			public void approveBagWeightVerificationFailure(boolean addOwnBag) {
				fail();
				
			}

			@Override
			public void productAddedToPurchase(Product product) {
				fail();
			}

			@Override
			public void unblockStation() {
				fail();			
			}
		});
		
		//scan an item 1 
		while(noScannerFailure != 1) {
			selfCheckoutHardware.mainScanner.scan(TestUtility.normalItem);
		}
		//place in bagging area 1
		selfCheckoutHardware.baggingArea.add(TestUtility.normalItem);
		
		//scan an item 2
		while(noScannerFailure != 2) {
			selfCheckoutHardware.mainScanner.scan(TestUtility.defaultItem);
		}
		//place in bagging area 2
		selfCheckoutHardware.baggingArea.add(TestUtility.defaultItem);
		
		//customer asks to remove item
		Cart c = new Cart(0);
		c.addBarcodedItemToCart(TestUtility.normalItem.getBarcode(), BigDecimal.TEN);
		supervisor.notifier.productRemovedFromPurchase(selfCheckout, c);
		selfCheckoutHardware.baggingArea.remove(TestUtility.defaultItem);
		
		//user wants to checkout 
		selfCheckout.notifier.checkoutButtonPressed();
		
		//user pays with card
		selfCheckout.notifier.payWithCardButtonPressed();
		while(true) {
			try{
				selfCheckoutHardware.cardReader.insert(TestUtility.card1, "1234");
				break;
			}catch(Exception e) {}
		}

		assertEquals(3, actualFound);
		assertEquals(2, noScannerFailure);
		assertEquals(3, productAdded);	
		assertEquals(1, productRemoved);
	}
	
	
	/**
	 * Customer enters PLU code for a product
	 * 
	 * 
	 * before checkout:
	 *  attendant adds ink and paper to printer
	 *  
	 *  
	 * 1. User enters PLU code for a product 
	 * 2. User puts the item in the bagging area
	 * 3. User decides to checkout
	 * 4. User pays with card
	 * 5. Receipt is printed and thank you message is shown
	 * @throws OverloadException 
	 */
	
	@Test
	public void customerAddsPLUItemTest() throws OverloadException { 
		//before checkout 
		attendant.addInkToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		attendant.addPaperToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		
		//attach observers
		selfCheckout.notifier.attach(new SCNotifierUIObserver() {

			@Override
			public void showThankYouMessage(boolean partialChange) {
				actualFound++;
				assertFalse(partialChange);
			}

			@Override
			public void cardPaymentError() {
				fail();
			}

			@Override
			public void cashFundsRegistered(BigDecimal totalAmount) {
				fail();
			}

			@Override
			public void printerError() {
				fail();
			}

			@Override
			public void barcodeScannedNotInDatabase() {
				fail();
			}

			@Override
			public void couldNotReturnChange() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}
			
			@Override
			public void membershipCardNotInDatabase() {
				fail();
			}

			@Override
			public void productScanned(Barcode barcode) {
				fail();
			}

			@Override
			public void membershipCardScannedSuccesfully(Barcode barcode) {
				fail();
			}

			@Override
			public void PLUEnteredNotInDatabase() {
				fail();
			}
	
			@Override
			public void shutDownStation() {
				fail();
			}

			@Override
			public void startStation() {
				fail();
			}
			
			@Override
			public void productRemovedFromPurchase(Cart cart) {
				fail();
			}

			@Override
			public void productAddedToPurchase(Cart cart) {
				actualFound++;
			}
			
			@Override
			public void placeBagsInBaggingArea() {
				fail();
			}

			@Override
			public void bagsAddedToPurchase(Cart cart) {
				fail();
			}

			@Override
			public void cardPaymentSuccesful() {
				actualFound++;
			}

			@Override
			public void addBagVerficationSuccesful(boolean ownBag) {
				fail();
			}

			@Override
			public void productWeightVerificationFailure() {
				actualFound++; // should not be called
			}

			@Override
			public void placeItemBaggingArea() {
				// This is called by the timer so don't fail
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
			}

			@Override
			public void blockStation() {
				fail();
				
			}

			@Override
			public void unblockStation() {
				fail();
			}
		});
		
		supervisor.notifier.attach(new SSNotiferExternalObserver() {

			@Override 
			public void approveWeightDiscrepancy() {
				fail();
			}

			@Override
			public void blockStation() {
				fail();
				
			}

			@Override
			public void shutDownStation() {
				fail();
				
			}

			@Override
			public void startStation() {
				fail();
				
			}

			@Override
			public void productRemovedFromPurchase(Cart newCart) {
				fail();
			}

			@Override
			public void approveProductNotBagged() {
				fail();
				
			}

			@Override
			public void approveBagWeightVerificationFailure(boolean addOwnBag) {
				fail();
				
			}

			@Override
			public void productAddedToPurchase(Product product) {
				fail();
			}

			@Override
			public void unblockStation() {
				fail();
			}
		
		});
		
		// Enter PLU
		selfCheckout.notifier.enterPLUButtonPressed(TestUtility.normalPLUCode);
		
		selfCheckoutHardware.scanningArea.add(TestUtility.normalPLUItem);
		selfCheckoutHardware.scanningArea.remove(TestUtility.normalPLUItem);
		
		//place in bagging area 
		selfCheckoutHardware.baggingArea.add(TestUtility.normalPLUItem);
		selfCheckout.notifier.checkoutButtonPressed();
		selfCheckout.notifier.payWithCardButtonPressed();
		while(true) {
			try{
				selfCheckoutHardware.cardReader.insert(TestUtility.card1, "1234");
				break;
			}catch(Exception e) {}
		}
		
		assertEquals(2, actualFound);
	}
	
	/**
	 * Attendant blocks a station 
	 * 
	 * before checkout:
	 *  attendant adds ink and paper to printer
	 *  
	 *  
	 *  1. User scans an item 
	 *  2. User puts the item in the bagging area
	 *  3. Attendant blocks the system
	 *  4. User scans another item
	 *  5. User puts the item in the bagging area
	 *  6. attendant unblocks the system
	 *  7. User decides to checkout
	 *  8. User pays with card
	 *  9. Receipt is printed and thank you message is shown
	 */
	@Test
	public void attendantBlockBetweenScanTest() throws OverloadException, IOException {
		//before checkout 
		attendant.addInkToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		attendant.addPaperToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		
		selfCheckout.notifier.attach(new SCNotifierCoordinatorObserver() {

			@Override
			public void cardPaymentSuccesful() {
				actualFound++;
			}

			@Override
			public void enoughCashInserted() {
				fail();
			}

			@Override
			public void partialPayment() {
				fail();
			}

			@Override
			public void addMoreItemsButtonPresses() {
				fail();
			}

			@Override
			public void payWithCashButtonPressed() {
				fail();
			}

			@Override
			public void productScanned(Barcode barcode) {
				noScannerFailure++;
				actualFound++;
			}

			@Override
			public void productAdded(PriceLookupCode code) {
				fail();
			}

			@Override
			public void productVerificationSuccesful() {
				actualFound++;
			}

			@Override
			public void checkoutButtonPressed() {
				actualFound++;
			}

			@Override
			public void payWithCardButtonPressed() {
				actualFound++;
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}

			@Override
			public void addBagsButtonPressed(int numOfBags, boolean ownBags) {
				fail();
			}

			@Override
			public void membershipCardVerifiedSuccesfully(Barcode barcode) {
				fail();
			}

			@Override
			public void membershipCardNotInDatabase() {
				fail();
			}

			@Override
			public void enterMembershipButtonPressed() {
				fail();
			}

			@Override
			public void membershipEntered(Barcode barcode) {
				fail();
			}

			@Override
			public void approveWeightDiscrepancy() {
				actualFound++;
			}

			@Override
			public void blockStation() {
				actualFound++;
			}

			@Override
			public void productRemovedFromPurchase(Cart cart) {
				fail();
			}

			@Override
			public void shutDownStation() {
				fail();
			}

			@Override
			public void startStation() {
				fail();
			}

			@Override
			public void paperAddedToPrinter(int units) {
				fail();
			}

			@Override
			public void inkAddedToPrinter(int quantity) {
				fail();
			}

			@Override
			public void enterPLUButtonPressed(PriceLookupCode code) {
				fail();
			}

			@Override
			public void approveProductNotBagged() {
				fail();
			}

			@Override
			public void addBagVerficationSuccesful(boolean ownBag) {
				fail();
			}

			@Override
			public void approveBagWeightVerificationFailure(boolean addOwnBag) {
				fail();
			}

			@Override
			public void productAddedToPurchase(Product product) {
				fail();
			}

			@Override
			public void payWithGiftCardButtonPressed(String cardNum) {
				fail();
			}

			@Override
			public void unblockStation() {
				fail();
			}

			@Override
			public void productPlacedInScanningArea(double expectedWeight) {
				fail();
			}

		});
		
		// scan and add item to bagging area
		while(noScannerFailure != 1) {
			selfCheckoutHardware.handheldScanner.scan(TestUtility.defaultItem);
		}
		selfCheckoutHardware.baggingArea.add(TestUtility.defaultItem);
		
		// block the station
		supervisor.notifier.blockStation(selfCheckout);
		
		// scans and places items in blocked phase
		selfCheckoutHardware.handheldScanner.scan(TestUtility.normalItem);
		selfCheckoutHardware.baggingArea.add(TestUtility.normalItem);
		
		// removes item scanned in blocked phase and approves the bagging area
		supervisor.notifier.approveWeightDiscrepancy(selfCheckout);
		assertEquals(4,actualFound);
		
		// start checkout
		selfCheckout.notifier.checkoutButtonPressed();
		selfCheckout.notifier.payWithCardButtonPressed();
		
		while(true) {
			try {		
				selfCheckoutHardware.cardReader.insert(TestUtility.card1, "1234");
				break;
			}catch(Exception e) {}
		}
		
		assertEquals(7,actualFound);
	}
	
	
	/**
	 * Attendant blocks a station -> not sure if it's a system test
	 * 
	 * before checkout:
	 *  attendant adds ink and paper to printer
	 *  
	 *  1. User scans an item 
	 *  2. User puts the item in the bagging area  
	 *  3. User decides to checkout
	 *  4. attendant blocks the station
	 *  5. User pays with card
	 *  6  attendant unblocks the station
	 *  7. User pays with card
	 *  8. Receipt is printed and thank you message is shown
	 */
//	@Test TODO: 
	public void attendantBlockInCheckoutTest() throws OverloadException, IOException {
		attendant.addInkToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		attendant.addPaperToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		
		selfCheckout.notifier.attach(new SCNotifierCoordinatorObserver() {

			@Override
			public void cardPaymentSuccesful() {
				cpCount++;
			}

			@Override
			public void enoughCashInserted() {
				fail();
			}

			@Override
			public void partialPayment() {
				fail();
			}

			@Override
			public void addMoreItemsButtonPresses() {
				fail();
			}

			@Override
			public void payWithCashButtonPressed() {
				fail();
			}

			@Override
			public void productScanned(Barcode barcode) {
				noScannerFailure++;
				actualFound++;
			}

			@Override
			public void productAdded(PriceLookupCode code) {
				fail();
			}

			@Override
			public void productVerificationSuccesful() {
				actualFound++;
			}

			@Override
			public void checkoutButtonPressed() {
				actualFound++;
			}

			@Override
			public void payWithCardButtonPressed() {
				actualFound++;
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}

			@Override
			public void addBagsButtonPressed(int numOfBags, boolean ownBags) {
				fail();
			}

			@Override
			public void membershipCardVerifiedSuccesfully(Barcode barcode) {
				fail();
			}

			@Override
			public void membershipCardNotInDatabase() {
				fail();
			}

			@Override
			public void enterMembershipButtonPressed() {
				fail();
			}

			@Override
			public void membershipEntered(Barcode barcode) {
				fail();
			}

			@Override
			public void approveWeightDiscrepancy() {
				actualFound++;
			}

			@Override
			public void blockStation() {
				actualFound++;
			}

			@Override
			public void productRemovedFromPurchase(Cart cart) {
				fail();
			}

			@Override
			public void shutDownStation() {
				fail();
			}

			@Override
			public void startStation() {
				fail();
			}

			@Override
			public void paperAddedToPrinter(int units) {
				fail();
			}

			@Override
			public void inkAddedToPrinter(int quantity) {
				fail();
			}

			@Override
			public void enterPLUButtonPressed(PriceLookupCode code) {
				fail();
			}

			@Override
			public void approveProductNotBagged() {
				fail();
			}

			@Override
			public void addBagVerficationSuccesful(boolean ownBag) {
				fail();
			}

			@Override
			public void approveBagWeightVerificationFailure(boolean addOwnBag) {
				fail();
			}

			@Override
			public void productAddedToPurchase(Product product) {
				fail();
			}

			@Override
			public void payWithGiftCardButtonPressed(String cardNum) {
				fail();
			}

			@Override
			public void unblockStation() {
				fail();
			}
			
			@Override
			public void productPlacedInScanningArea(double expectedWeight) {
				fail();
			}
		});
		
		// add item
		while(noScannerFailure != 1) {
			selfCheckoutHardware.handheldScanner.scan(TestUtility.normalItem);
		}
		selfCheckoutHardware.baggingArea.add(TestUtility.normalItem);
		
		// block the station
		supervisor.notifier.blockStation(selfCheckout);
		
		// try to pay
		while(true) {
			try {		
				selfCheckoutHardware.cardReader.insert(TestUtility.card1, "1234");
				break;
			}catch(Exception e) {}
		}
		
		// Verifies the station and unblocks
		supervisor.notifier.approveWeightDiscrepancy(selfCheckout);

		// make sure card reader is empty
		selfCheckoutHardware.cardReader.remove();
		// pays
		selfCheckout.notifier.checkoutButtonPressed();
		selfCheckout.notifier.payWithCardButtonPressed();
		
		while(true) {
			try {		
				selfCheckoutHardware.cardReader.insert(TestUtility.card1, "1234");
				break;
			}catch(Exception e) {}
		}
		assertEquals(1, cpCount);
		assertEquals(6, actualFound);
	}
	
	
	/**
	 * Customer enters their membership card information
	 * 
	 * before checkout:
	 *  attendant adds ink and paper to printer
	 *  
	 *  
	 * 1. User scans an item
	 * 2. User puts the item in the bagging area
	 * 3. User decides to checkout
	 * 4. User adds their membership card info
	 * 5. User pays with card
	 */
	// @Test
	public void customerAddMembershipTest() throws OverloadException {

		attendant.addInkToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		attendant.addPaperToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		
		selfCheckout.notifier.attach(new SCNotifierCoordinatorObserver() {

			@Override
			public void cardPaymentSuccesful() {
				actualFound++;
			}

			@Override
			public void enoughCashInserted() {
				fail();
			}

			@Override
			public void partialPayment() {
				fail();
			}

			@Override
			public void addMoreItemsButtonPresses() {
				fail();
			}

			@Override
			public void payWithCashButtonPressed() {
				fail();
			}

			@Override
			public void productScanned(Barcode barcode) {
				noScannerFailure++;
				actualFound++;
			}

			@Override
			public void productAdded(PriceLookupCode code) {
				fail();
			}

			@Override
			public void productVerificationSuccesful() {
				actualFound++;
			}

			@Override
			public void checkoutButtonPressed() {
				actualFound++;
			}

			@Override
			public void payWithCardButtonPressed() {
				actualFound++;
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}

			@Override
			public void addBagsButtonPressed(int numOfBags, boolean ownBags) {
				fail();
			}

			@Override
			public void membershipCardVerifiedSuccesfully(Barcode barcode) {
				actualFound++;
				noScannerFailure++;
			}

			@Override
			public void membershipCardNotInDatabase() {
				fail();
			}

			@Override
			public void enterMembershipButtonPressed() {
				actualFound++;
			}

			@Override
			public void membershipEntered(Barcode barcode) {
			}

			@Override
			public void approveWeightDiscrepancy() {
				fail();
			}

			@Override
			public void blockStation() {
				fail();
			}

			@Override
			public void productRemovedFromPurchase(Cart cart) {
				fail();
			}

			@Override
			public void shutDownStation() {
				fail();
			}

			@Override
			public void startStation() {
				fail();
			}

			@Override
			public void paperAddedToPrinter(int units) {
				fail();
			}

			@Override
			public void inkAddedToPrinter(int quantity) {
				fail();
			}

			@Override
			public void enterPLUButtonPressed(PriceLookupCode code) {
				fail();
			}

			@Override
			public void approveProductNotBagged() {
				fail();
			}

			@Override
			public void addBagVerficationSuccesful(boolean ownBag) {
				fail();
			}

			@Override
			public void approveBagWeightVerificationFailure(boolean addOwnBag) {
				fail();
			}

			@Override
			public void productAddedToPurchase(Product product) {
				fail();
			}

			@Override
			public void payWithGiftCardButtonPressed(String cardNum) {
				fail();
			}

			@Override
			public void unblockStation() {
				fail();
			}
			
			@Override
			public void productPlacedInScanningArea(double expectedWeight) {
				fail();
			}
		});
		
		// add item
		while(noScannerFailure != 1) {
			selfCheckoutHardware.handheldScanner.scan(TestUtility.normalItem);
		}
		selfCheckoutHardware.baggingArea.add(TestUtility.normalItem);
		
		// checkout
		selfCheckout.notifier.scanMembershipButtonPressed(); // phase is ENTERING_MEMBERSHIP
		
		while(noScannerFailure != 2) {
			selfCheckoutHardware.mainScanner.scan(TestUtility.membershipCard1); // phase is READY
		}
		
		selfCheckout.notifier.checkoutButtonPressed();
		selfCheckout.notifier.payWithCardButtonPressed();
		
		while(true) {
			try {		
				selfCheckoutHardware.cardReader.insert(TestUtility.card1, "1234");
				break;
			}catch(Exception e) {}
		}
		
		assertEquals(7, actualFound);
	}
	
	
	
	/**
	 * Customer pays with gift card v2
	 * 
	 * before checkout:
	 *  attendant adds ink and paper to printer
	 *  
	 * 1. User scans an item
	 * 2. User puts the item in the bagging area
	 * 3. User decides to checkout
	 * 4. User pays with card gift card that has less money than the amount that needs to be payed 
	 * 
	 * 5. Receipt is printed and thank you message is shown
	 * @throws OverloadException 
	 */
	@Test
	public void customerPayGiftCardTestV2() throws OverloadException {
		attendant.addInkToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		attendant.addPaperToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		
		selfCheckout.notifier.attach(new SCNotifierCoordinatorObserver() {

			@Override
			public void cardPaymentSuccesful() {
				actualFound++;
			}

			@Override
			public void enoughCashInserted() {
				fail();
			}

			@Override
			public void partialPayment() {
				fail();
			}

			@Override
			public void addMoreItemsButtonPresses() {
				fail();
			}

			@Override
			public void payWithCashButtonPressed() {
				fail();
			}

			@Override
			public void productScanned(Barcode barcode) {
				noScannerFailure++;
				actualFound++;
			}

			@Override
			public void productAdded(PriceLookupCode code) {
				fail();
			}

			@Override
			public void productVerificationSuccesful() {
				actualFound++;
			}

			@Override
			public void checkoutButtonPressed() {
				actualFound++;
			}

			@Override
			public void payWithCardButtonPressed() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}

			@Override
			public void addBagsButtonPressed(int numOfBags, boolean ownBags) {
				fail();
			}

			@Override
			public void membershipCardVerifiedSuccesfully(Barcode barcode) {
				fail();
			}

			@Override
			public void membershipCardNotInDatabase() {
				fail();
			}

			@Override
			public void enterMembershipButtonPressed() {
				fail();
			}

			@Override
			public void membershipEntered(Barcode barcode) {
				fail();
			}
			
			@Override
			public void approveWeightDiscrepancy() {
				fail();
			}

			@Override
			public void blockStation() {
				fail();
			}

			@Override
			public void productRemovedFromPurchase(Cart cart) {
				fail();
			}

			@Override
			public void shutDownStation() {
				fail();
			}

			@Override
			public void startStation() {
				fail();
			}

			@Override
			public void paperAddedToPrinter(int units) {
				fail();
			}

			@Override
			public void inkAddedToPrinter(int quantity) {
				fail();
			}

			@Override
			public void enterPLUButtonPressed(PriceLookupCode code) {
				fail();
			}

			@Override
			public void approveProductNotBagged() {
				fail();
			}

			@Override
			public void addBagVerficationSuccesful(boolean ownBag) {
				fail();
			}

			@Override
			public void approveBagWeightVerificationFailure(boolean addOwnBag) {
				fail();
			}

			@Override
			public void payWithGiftCardButtonPressed(String cardNum) {
				actualFound++;
			}

			@Override
			public void productAddedToPurchase(Product product) {
				fail();
			}
			
			@Override
			public void unblockStation() {
				fail();
			}
			
			@Override
			public void productPlacedInScanningArea(double expectedWeight) {
				fail();
			}
		});
		
		// add an item
		while(noScannerFailure != 1) {
			selfCheckoutHardware.handheldScanner.scan(TestUtility.normalItem);
		}
		selfCheckoutHardware.baggingArea.add(TestUtility.normalItem);
		// checkout with gift card
		selfCheckout.notifier.checkoutButtonPressed();
		selfCheckout.notifier.payWithGiftCardButtonPressed(TestUtility.giftCard1);
		
		assertEquals(5, actualFound);
	}
	
	
	
	/**
	 * Station detects that the weight in the bagging area does not conform to expectations
	 * 
	 *  before checkout:
	 *  attendant adds ink and paper to printer
	 *  
	 * 1. User scans an item
	 * 2. User puts **another** item in the bagging area
	 * 3. User removes the wrong item
	 * 4. User adds the right item 
	 * 5. User decides to checkout
	 * 
	 */
	
	@Test
	public void weightDiscrepancyTest() throws OverloadException {
		//before checkout 
		attendant.addInkToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		attendant.addPaperToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		
		selfCheckout.notifier.attach(new SCNotifierUIObserver() {

			@Override
			public void showThankYouMessage(boolean partialChange) {
				actualFound++;
				assertFalse(partialChange);
			}

			@Override
			public void cardPaymentError() {
				fail();
			}

			@Override
			public void cashFundsRegistered(BigDecimal totalAmount) {
				fail();
			}

			@Override
			public void printerError() {
				fail();
			}

			@Override
			public void barcodeScannedNotInDatabase() {
				fail();
			}

			@Override
			public void couldNotReturnChange() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}
			
			@Override
			public void membershipCardNotInDatabase() {
				fail();
			}

			@Override
			public void productScanned(Barcode barcode) {
				noScannerFailure++;
			}

			@Override
			public void membershipCardScannedSuccesfully(Barcode barcode) {
				fail();
			}

			@Override
			public void PLUEnteredNotInDatabase() {
				fail();
			}
	
			@Override
			public void shutDownStation() {
				fail();
			}

			@Override
			public void startStation() {
				fail();
			}
			
			@Override
			public void productRemovedFromPurchase(Cart cart) {
				fail();
			}

			@Override
			public void productAddedToPurchase(Cart cart) {
				actualFound++;
			}
			
			@Override
			public void placeBagsInBaggingArea() {
				fail();
			}

			@Override
			public void bagsAddedToPurchase(Cart cart) {
				fail();
			}

			@Override
			public void cardPaymentSuccesful() {
				actualFound++;
			}

			@Override
			public void addBagVerficationSuccesful(boolean ownBag) {
				fail();
			}

			@Override
			public void productWeightVerificationFailure() {
				actualFound++;
			}

			@Override
			public void placeItemBaggingArea() {
				fail();
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
			}

			@Override
			public void blockStation() {
				fail();
			}
			
			@Override
			public void unblockStation() {
				fail();
			}
		});
		
		//attach observer 
		supervisor.notifier.attach(new SSNotifierUIObserver() {

			@Override
			public void wrongCredentials() {
				fail();
			}

			@Override
			public void validCredentials(String employeeName) {
				fail();		
			}

			@Override
			public void successfulLogout() {
				fail();				
			}

			@Override
			public void productVerficationFailure(ArrayList<Integer> stations) {  
				actualFound++;			
			}

			@Override
			public void approveWeightDiscrepancy(ArrayList<Integer> stations) {
				fail();
			}

			@Override
			public void printerLowOnInk(int id) {
				fail();
			}

			@Override
			public void printerLowOnPaper(int id) {
				fail();
			}

			@Override
			public void partialChangeReturned(int id, BigDecimal credit) {
				fail();
			}

			@Override
			public void failedToPlaceItem(ArrayList<Integer> stations) {
				fail();
			}

			@Override
			public void approveProductNotBagged(ArrayList<Integer> stations) {
				fail();
			}

			@Override
			public void failedToPlaceItem(int id) {
				fail();
			}

			@Override
			public void addBagVerificationFailure(int id, boolean ownBag) {
				fail();
			}
		});
		
		
		// scan one item
		while(noScannerFailure != 1) {
			selfCheckoutHardware.mainScanner.scan(TestUtility.normalItem);
		}
		
		
		// add the two items
		selfCheckoutHardware.baggingArea.add(TestUtility.normalItem2);
	
		selfCheckoutHardware.baggingArea.remove(TestUtility.normalItem2); 
		
		selfCheckoutHardware.baggingArea.add(TestUtility.normalItem);
		
		selfCheckout.notifier.checkoutButtonPressed();
		selfCheckout.notifier.payWithCardButtonPressed();
		
		while(true) {
			try {		
				selfCheckoutHardware.cardReader.insert(TestUtility.card1, "1234");
				break;
			}catch(Exception e) {}
		}
		
		assertEquals(5 , actualFound);
		
	}
	
	
	
	/**
	 * Customer enters number of plastic bags used
	 * 
	 *  before checkout:
	 *  attendant adds ink and paper to printer
	 *  
	 * 1. User scans an item
	 * 2. User adds item in the bagging area
	 * 3. Customer chooses the number of bags they want 
	 * 4. User decides to checkout 
	 * 5. User pays with card
	 * 6. Receipt is printed and thank you message is shown
	 * @throws OverloadException 
	 */
	
	@Test
	public void getPlasticBagsTest() throws OverloadException	{
		//before checkout 
		attendant.addInkToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		attendant.addPaperToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		
		selfCheckout.notifier.attach(new SCNotifierUIObserver() {

			@Override
			public void showThankYouMessage(boolean partialChange) {
				fail();
			}

			@Override
			public void cardPaymentError() {
				fail();
			}

			@Override
			public void cashFundsRegistered(BigDecimal totalAmount) {
				fail();
			}

			@Override
			public void printerError() {
				fail();
			}

			@Override
			public void barcodeScannedNotInDatabase() {
				fail();
			}

			@Override
			public void couldNotReturnChange() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}
			
			@Override
			public void membershipCardNotInDatabase() {
				fail();
			}

			@Override
			public void productScanned(Barcode barcode) {
				noScannerFailure++;
				assertEquals(1,noScannerFailure);
			}

			@Override
			public void membershipCardScannedSuccesfully(Barcode barcode) {
				fail();
			}

			@Override
			public void PLUEnteredNotInDatabase() {
				fail();
			}
	
			@Override
			public void shutDownStation() {
				fail();
			}

			@Override
			public void startStation() {
				fail();
			}
			
			@Override
			public void productRemovedFromPurchase(Cart cart) {
				fail();
			}

			@Override
			public void productAddedToPurchase(Cart cart) {
				fail();
			}
			
			@Override
			public void placeBagsInBaggingArea() {
				actualFound++;
			}

			@Override
			public void bagsAddedToPurchase(Cart cart) {
				actualFound++;
			}

			@Override
			public void cardPaymentSuccesful() {
				fail();
			}

			@Override
			public void addBagVerficationSuccesful(boolean ownBag) {
				actualFound++;
			}

			@Override
			public void productWeightVerificationFailure() {
				fail();
			}

			@Override
			public void placeItemBaggingArea() {
				actualFound++;
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
			}

			@Override
			public void blockStation() {
				fail();	
			}
			
			@Override
			public void unblockStation() {
				fail();
			}
		});
		
		//attach observer 
		supervisor.notifier.attach(new SSNotifierUIObserver() {

			@Override
			public void wrongCredentials() {
				fail();
			}

			@Override
			public void validCredentials(String employeeName) {
				fail();		
			}

			@Override
			public void successfulLogout() {
				fail();				
			}

			@Override
			public void productVerficationFailure(ArrayList<Integer> stations) {
				fail();			
			}

			@Override
			public void approveWeightDiscrepancy(ArrayList<Integer> stations) {
				fail();
			}

			@Override
			public void printerLowOnInk(int id) {
				fail();
			}

			@Override
			public void printerLowOnPaper(int id) {
				fail();
			}

			@Override
			public void partialChangeReturned(int id, BigDecimal credit) {
				fail();
			}

			@Override
			public void failedToPlaceItem(ArrayList<Integer> stations) {
				// This is called by the timer so don't fail
			}

			@Override
			public void approveProductNotBagged(ArrayList<Integer> stations) {
				fail();
			}

			@Override
			public void failedToPlaceItem(int id) {
				// This is called by the timer so don't fail
			}

			@Override
			public void addBagVerificationFailure(int id, boolean ownBag) {
				fail();
			}
		});
		
		// scan one item
		while(noScannerFailure != 1) {
			selfCheckoutHardware.mainScanner.scan(TestUtility.normalItem);
		}
		// user adds bags
		selfCheckout.notifier.addBagsButtonPressed(11, false);
		selfCheckoutHardware.baggingArea.add(TestUtility.elevenBagItem);
		// attendant approves bags 
		selfCheckout.notifier.addBagVerificationSuccessful(false);
		selfCheckout.notifier.checkoutButtonPressed();
		
		assertEquals(5,actualFound);
	}
	
	
	/**
	 * Customer add their own bag 
	 * 
	 *  before checkout:
	 *  attendant adds ink and paper to printer
	 *  
	 * 1. User scans an item
	 * 2. User adds item in the bagging area
	 * 3. Customer chooses to add their own bag
	 * 4. User decides to checkout 
	 * @throws OverloadException 
	 */
	@Test
	public void addOwnBagTest() throws OverloadException {
		//before checkout 
		attendant.addInkToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		attendant.addPaperToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		
		selfCheckout.notifier.attach(new SCNotifierUIObserver() {

			@Override
			public void showThankYouMessage(boolean partialChange) {
				fail();
			}

			@Override
			public void cardPaymentError() {
				fail();
			}

			@Override
			public void cashFundsRegistered(BigDecimal totalAmount) {
				fail();
			}

			@Override
			public void printerError() {
				fail();
			}

			@Override
			public void barcodeScannedNotInDatabase() {
				fail();
			}

			@Override
			public void couldNotReturnChange() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}
			
			@Override
			public void membershipCardNotInDatabase() {
				fail();
			}

			@Override
			public void productScanned(Barcode barcode) {
				noScannerFailure++;
				assertEquals(1,noScannerFailure);
			}

			@Override
			public void membershipCardScannedSuccesfully(Barcode barcode) {
				fail();
			}

			@Override
			public void PLUEnteredNotInDatabase() {
				fail();
			}
	
			@Override
			public void shutDownStation() {
				fail();
			}

			@Override
			public void startStation() {
				fail();
			}
			
			@Override
			public void productRemovedFromPurchase(Cart cart) {
				fail();
			}

			@Override
			public void productAddedToPurchase(Cart cart) {
				fail();
			}
			
			@Override
			public void placeBagsInBaggingArea() {
				actualFound++;
			}

			@Override
			public void bagsAddedToPurchase(Cart cart) {
				actualFound++;
			}

			@Override
			public void cardPaymentSuccesful() {
				fail();
			}

			@Override
			public void addBagVerficationSuccesful(boolean ownBag) {
				actualFound++;
			}

			@Override
			public void productWeightVerificationFailure() {
				fail();
			}

			@Override
			public void placeItemBaggingArea() {
				actualFound++;
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
			}

			@Override
			public void blockStation() {
				fail();
			}
			
			@Override
			public void unblockStation() {
				fail();
			}
		});
		
		//attach observer 
		supervisor.notifier.attach(new SSNotifierUIObserver() {

			@Override
			public void wrongCredentials() {
				fail();
			}

			@Override
			public void validCredentials(String employeeName) {
				fail();		
			}

			@Override
			public void successfulLogout() {
				fail();				
			}

			@Override
			public void productVerficationFailure(ArrayList<Integer> stations) {
				fail();			
			}

			@Override
			public void approveWeightDiscrepancy(ArrayList<Integer> stations) {
				fail();
			}

			@Override
			public void printerLowOnInk(int id) {
				fail();
			}

			@Override
			public void printerLowOnPaper(int id) {
				fail();
			}

			@Override
			public void partialChangeReturned(int id, BigDecimal credit) {
				fail();
			}

			@Override
			public void failedToPlaceItem(ArrayList<Integer> stations) {

			}

			@Override
			public void approveProductNotBagged(ArrayList<Integer> stations) {
				fail();
			}

			@Override
			public void failedToPlaceItem(int id) {
				
			}
			
			@Override
			public void addBagVerificationFailure(int id, boolean ownBag) {
				fail();
			}
		});
		
		// scan one item
		while(noScannerFailure != 1) {
			selfCheckoutHardware.mainScanner.scan(TestUtility.normalItem);
		}
		// user adds bags
		selfCheckout.notifier.addBagsButtonPressed(11, true);
		selfCheckoutHardware.baggingArea.add(TestUtility.elevenBagItem);
		// attendant approves bags 
		selfCheckout.notifier.addBagVerificationSuccessful(true);
		selfCheckout.notifier.checkoutButtonPressed();
		
		assertEquals(5,actualFound);
	}
	
}
