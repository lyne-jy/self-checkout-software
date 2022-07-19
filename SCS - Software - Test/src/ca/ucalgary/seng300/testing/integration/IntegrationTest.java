package ca.ucalgary.seng300.testing.integration;

import static org.junit.Assert.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SupervisionStation;
import org.lsmr.selfcheckout.products.Product;
import ca.ucalgary.seng300.attendant.SupervisorStationControlSoftware;
import ca.ucalgary.seng300.attendant.communication.SSNotiferExternalObserver;
import ca.ucalgary.seng300.attendant.communication.SSNotifierUIObserver;
import ca.ucalgary.seng300.selfcheckout.SelfCheckoutControlSoftware;
import ca.ucalgary.seng300.selfcheckout.communication.SCNotifierExternalObserver;
import ca.ucalgary.seng300.selfcheckout.communication.SCNotifierUIObserver;
import ca.ucalgary.seng300.selfcheckout.utility.Cart;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;
import ca.ucalgary.seng300.simulations.StoreAttendant;
import ca.ucalgary.seng300.testing.utility.TestUtility;

public class IntegrationTest {

	private SupervisorStationControlSoftware supervisor;
	private SelfCheckoutStation selfCheckoutHardware;
	private SelfCheckoutControlSoftware selfCheckout;
	private StoreAttendant attendant;
	
	private int actualFound;
	private int productAdded;
	private int productRemoved;

	private int noScannerFailure;
	private int noValidatorFailure;
	
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
		noValidatorFailure = 0;
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 	Customer enters a PLU that is not in the database
	 * 
	 * 	before checkout:
	 *  attendant adds ink and paper to printer
	 * 
	 * 1. User enters a PLUCode [not in DB] - should notify the user
	 * @throws OverloadException 
	 */
	@Test 
	public void PLUEnteredNotInDatabase() throws OverloadException {
			//before checkout 
			selfCheckoutHardware.printer.addInk(100);
			selfCheckoutHardware.printer.addPaper(100);
			
			//attach observers
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
					fail();
				}

				@Override
				public void membershipCardScannedSuccesfully(Barcode barcode) {
					fail();
				}

				@Override
				public void PLUEnteredNotInDatabase() {
					actualFound++;
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
					fail();
				}

				@Override
				public void bagsAddedToPurchase(Cart cart) {
					fail();
				}

				@Override
				public void cardPaymentSuccesful() {
					fail();
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
			selfCheckout.notifier.enterPLUButtonPressed(TestUtility.plu3);
			
			assertEquals(1, actualFound);		
	}

	/**
	 * 	Customer adds more bags than expected
	 * 		- approveAddBagVerificationFailure
	 * 
	 * 	before checkout:
	 *  attendant adds ink and paper to printer
	 *  
	 * 	1. scan an item 
	 * 	2. add bags (5)
	 * 	3. place more bag in bagging area (>5)
	 * 		- failure in weight verification of bag
	 * 	4. attendant approves
	 * 	5. checkout
	 * @return 
	 * @throws OverloadException 
	 * 	
	 * */
	@Test
	public void approveAddBagVerificationFailure() throws OverloadException {
		
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
				actualFound++;
			}

			@Override
			public void bagsAddedToPurchase(Cart cart) {
				actualFound++;
			}

			@Override
			public void cardPaymentSuccesful() {
				actualFound++;
			}

			@Override
			public void addBagVerficationSuccesful(boolean ownBag) {
				fail(); // For this path there is no notify method being called for this 
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
				fail(); // For this path there is no notify method being called for this 
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

		//place in bagging area 
		selfCheckoutHardware.baggingArea.add(TestUtility.normalItem);
		
		// indicates how many bags 
		selfCheckout.notifier.addBagsButtonPressed(5, true);
		
		//place bag in bagging area 
		selfCheckoutHardware.baggingArea.add(TestUtility.bag);
		
		supervisor.notifier.aproveAddBagVerificationFailure(selfCheckout, true);
		
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
		
		assertEquals(5, actualFound);
		
	}
	
	/**
	 * 	Customer pays more money than necessary and receives change
	 * 		- notifyPartialChangeReturned
	 * 
	 * 	before checkout:
	 *  attendant adds ink and paper to printer
	 *  
	 *  notifyPartialChangeReturned
	 *  
	 * 	1. scan an item 
	 * 	2. place in bagging area
	 * 	3. pay with cash
	 * 	4. get partial change
	 * 	5. remove item from bagging area
	 * 	5. checkout
	 * @throws OverloadException 
	 * @throws DisabledException 
	 * 	
	 * */
	@Test
	public void partialChangeReturned() throws OverloadException, DisabledException {
		//before checkout 
		attendant.addInkToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		attendant.addPaperToPrinter(selfCheckoutHardware, 1024, selfCheckout.ID);
		
		selfCheckout.notifier.attach(new SCNotifierUIObserver() {

			@Override
			public void showThankYouMessage(boolean partialChange) {
				actualFound++;
				assertTrue(partialChange);
			}

			@Override
			public void cardPaymentError() {
				fail();
			}

			@Override
			public void cashFundsRegistered(BigDecimal totalAmount) {
				noValidatorFailure++;
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
				fail();
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
				actualFound++;
				assertEquals(selfCheckout.ID, id);
				assertEquals(new BigDecimal(2.0), credit);
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
		while(noScannerFailure != 1) {
			selfCheckoutHardware.mainScanner.scan(TestUtility.normalItem);
		}
		
		selfCheckoutHardware.baggingArea.add(TestUtility.normalItem);
		
		selfCheckout.notifier.checkoutButtonPressed();
		selfCheckout.notifier.payWithCashButtonPressed();
		
		while(noValidatorFailure != 1) {
			try {
				selfCheckoutHardware.banknoteInput.accept(TestUtility.fiveDollarBill);
			}catch(Exception e) {
				selfCheckoutHardware.banknoteInput.removeDanglingBanknotes();
			}
		}
		selfCheckoutHardware.baggingArea.remove(TestUtility.normalItem);
		
		assertEquals(4,actualFound);
	}
	
	/** 
	 * 	Notify that the printer is low on paper 
	 * 		- notifyPrinterLowOnPaper
	 *  
	 *  look at the PrinterLowOnPaper Test in SelfCheckoutStationTest 
	 * 	add supervisor station 
	 * @throws OverloadException 
	 * 
	 * */
	@Test
	public void printerLowOnPaper() throws OverloadException {
		//before checkout 
		selfCheckoutHardware.printer.addInk(200);
		selfCheckoutHardware.printer.addPaper(5);
		
		selfCheckout.notifier.inkAddedToPrinter(200);
		selfCheckout.notifier.paperAddedToPrinter(5);
		
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
			public void productWeightVerificationFailure() {
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
			public void placeItemBaggingArea() {
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
			public void addBagVerificationFailure(boolean ownBag) {
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
				assertEquals(selfCheckout.ID, id);
				actualFound++;
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
		
		//scan the item
		while(noScannerFailure != 1) {
			selfCheckoutHardware.mainScanner.scan(TestUtility.normalItem);
		}
		
		//place the item to bagging area
		selfCheckoutHardware.baggingArea.add(TestUtility.normalItem);
		
		//user wants to checkout
		selfCheckout.notifier.checkoutButtonPressed();
		
		//user pays with card
		selfCheckout.notifier.payWithCardButtonPressed();
		while(true) {
			try {
				selfCheckoutHardware.cardReader.insert(TestUtility.card1, "1234");	
				break;
			}catch(Exception e) {}
		}
		
		selfCheckoutHardware.baggingArea.remove(TestUtility.normalItem);
		
		assertEquals(5, actualFound);							
	}
	
	/** 
	 * 	Notify the printer is low on ink
	 *   - notifyPrinterLowOnInk
	 *  
	 *  look at the PrinterLowOnInk Test in SelfCheckoutStationTest 
	 * 	add supervisor station 
	 * @throws OverloadException 
	 * 
	 * */
	@Test
	public void printerLowOnInk() throws OverloadException {
		//before checkout 
		selfCheckoutHardware.printer.addInk(100);
		selfCheckoutHardware.printer.addPaper(1024);
		
		selfCheckout.notifier.inkAddedToPrinter(100);
		selfCheckout.notifier.paperAddedToPrinter(1024);
		
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
			public void productWeightVerificationFailure() {
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
			public void placeItemBaggingArea() {
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
			public void addBagVerificationFailure(boolean ownBag) {
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
				assertEquals(selfCheckout.ID, id);
				actualFound++;
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
		
		selfCheckout.notifier.attach(new SCNotifierExternalObserver() {
		
			@Override
			public void productWeightVerificationFailure(int id) {
				fail();
			}
		
			@Override
			public void printerLowOnInk(int id) {
				assertEquals(selfCheckout.ID, id);
				actualFound++;
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
			public void failedToPlaceItem(int id) {
				fail();		
			}

			@Override
			public void addBagVerificationFailure(int id, boolean ownBag) {
				fail();
			}
		});	
		
		//scan the item
		while(noScannerFailure != 1) {
			selfCheckoutHardware.mainScanner.scan(TestUtility.normalItem);
		}
		
		//place the item to bagging area
		selfCheckoutHardware.baggingArea.add(TestUtility.normalItem);
		
		//user wants to checkout
		selfCheckout.notifier.checkoutButtonPressed();
		
		//user pays with card
		selfCheckout.notifier.payWithCardButtonPressed();
		while(true) {
			try {
				selfCheckoutHardware.cardReader.insert(TestUtility.card1, "1234");	
				break;
			}catch(Exception e) {}
		}
			
		selfCheckoutHardware.baggingArea.remove(TestUtility.normalItem);
		
		assertEquals(7, actualFound);							
	}

	
	/** 
	 * 	Notify because the customer failed to place item
	 *   - notifyFailedToPlaceItem
	 *  
	 *  look at the PrinterLowOnInk Test in SelfCheckoutStationTest 
	 * 	add supervisor station 
	 * @throws InterruptedException 
	 * 
	 * */
	@Test
	public void failedToPlaceItem() throws InterruptedException {
		
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
			public void productWeightVerificationFailure() {
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
			public void placeItemBaggingArea() {
				actualFound++;
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
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
			}

			@Override
			public void cardPaymentSuccesful() {
				fail();
			}

			@Override
			public void addBagVerficationSuccesful(boolean ownBag) {
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
				fail();
			}

			@Override
			public void failedToPlaceItem(int id) {
				assertEquals(selfCheckout.ID, id);
				actualFound++;
			}

			@Override
			public void addBagVerificationFailure(int id, boolean ownBag) {
				fail();
			}
		});
		while(noScannerFailure != 1) {
			selfCheckoutHardware.mainScanner.scan(TestUtility.normalItem);
		}
		
		Thread.sleep(6000);
		
		selfCheckoutHardware.baggingArea.add(TestUtility.normalItem);
		
		assertEquals(4, actualFound);
	}
	
	/** 
	 * 	User enters membership information
	 *   - membershipEntered
	 *  
	 *  Copy any happy path and enter the membership at the top of the test
	 * @throws OverloadException 
	 * 
	 * */
	@Test
	public void enterMembership() throws OverloadException {
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
				noScannerFailure++;
				actualFound++;
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
		
		// Enter membership
		selfCheckout.notifier.scanMembershipButtonPressed();
		while(noScannerFailure != 1) {
			selfCheckoutHardware.mainScanner.scan(TestUtility.membershipCard1);
		}
		  
		// Enter PLU
		selfCheckout.notifier.enterPLUButtonPressed(TestUtility.normalPLUCode);
		
		// weight item 
		selfCheckoutHardware.scanningArea.add(TestUtility.normalPLUItem);
		selfCheckoutHardware.scanningArea.remove(TestUtility.normalPLUItem);
		
		//place in bagging area 
		selfCheckoutHardware.baggingArea.add(TestUtility.normalPLUItem);
		
		selfCheckout.notifier.checkoutButtonPressed();
		selfCheckout.notifier.payWithCardButtonPressed();
		
		// pay with card
		while(true) {
			try{
				selfCheckoutHardware.cardReader.insert(TestUtility.card1, "1234");
				break;
			}catch(Exception e) {}
		}
		
		assertEquals(3, actualFound);
		assertEquals(1, noScannerFailure);
	}
	
	/** 
	 * 	Start station
	 *   - notifyStartStation
	 *  
	 *	1. Start in the off phase 
	 *	2. Start station 
	 * @throws OverloadException 
	 * */
	@Test
	public void startStation() throws OverloadException {
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
				actualFound++;
			}

			@Override
			public void startStation() {
				actualFound++;
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
				// ignore it
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
				actualFound++;
				
			}

			@Override
			public void startStation() {
				actualFound++;
				
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
		// add an item 
		while(noScannerFailure != 1) {
			selfCheckoutHardware.mainScanner.scan(TestUtility.normalItem);
		}
		//place in bagging area 
		selfCheckoutHardware.baggingArea.add(TestUtility.normalItem);
		
		// turn the station off
		supervisor.notifier.shutDownStation(selfCheckout);
		
		// do some commands on station to verify its off
		selfCheckoutHardware.baggingArea.remove(TestUtility.normalItem); // under normal function addProductsRemoved() will be called
		selfCheckoutHardware.baggingArea.add(TestUtility.normalItem); // still should be no software reaction
		
		// restart station
		supervisor.notifier.startStation(selfCheckout);
		
		//user wants to checkout 
		selfCheckout.notifier.checkoutButtonPressed();
		
		selfCheckoutHardware.baggingArea.remove(TestUtility.normalItem); // now addProductsRemoved() will be called
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
	 * 	Attendant removes PLU product from purchase
	 *  	- productRemovedFromPurchase
	 * 
	 * 	exists in system test 1, but instead of adding two barcoded products you add one barcoded and one plu 
	 * 	attendant remove item
	 * @throws OverloadException 
	 * 
	 * */
	@Test
	public void removePLUProductFromPurchase() throws OverloadException {
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
				actualFound++;
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
		
		// Enter PLU
		selfCheckout.notifier.enterPLUButtonPressed(TestUtility.normalPLUCode);
		//place in bagging area 
		selfCheckoutHardware.baggingArea.add(TestUtility.normalPLUItem);
		
		//customer asks to remove item
		Cart c = new Cart(0);
		c.addPLUItemToCart(TestUtility.normalPLUCode, BigDecimal.TEN, TestUtility.normalPLUItem.getWeight());
		//c.addBarcodedItemToCart(TestUtility.normalItem.getBarcode(), BigDecimal.TEN);
		supervisor.notifier.productRemovedFromPurchase(selfCheckout, c);
		selfCheckoutHardware.baggingArea.remove(TestUtility.normalPLUItem);
		
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
		assertEquals(1, noScannerFailure);
		assertEquals(1, productAdded);	
		assertEquals(1, productRemoved);
		
	}
	
	/** 
	 * 	Attendant adds PLU product to purchase
	 *  	- productAddToPurchase
	 * 	
	 * 	attendant add a plu product
	 * @throws OverloadException 
	 * 
	 * */
	@Test
	public void addPLUProductToPurchase() throws OverloadException {
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
				fail();
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
				productAdded++;
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
		
		//customer asks to add item
		supervisor.notifier.productAddedToPurchse(selfCheckout, TestUtility.mango);

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

		assertEquals(2, actualFound);
		assertEquals(1, noScannerFailure);
		assertEquals(2, productAdded);	
		assertEquals(0, productRemoved);
		
	}
}
