package ca.ucalgary.seng300.testing.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import ca.ucalgary.seng300.selfcheckout.SelfCheckoutControlSoftware;
import ca.ucalgary.seng300.selfcheckout.communication.SCNotifierSystemObserver;
import ca.ucalgary.seng300.selfcheckout.communication.SCNotifierUIObserver;
import ca.ucalgary.seng300.selfcheckout.utility.Cart;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;
import ca.ucalgary.seng300.testing.utility.TestUtility;

/*
 * /*
 * Test suite test whether all messages from self checkout station are correctly being send to the attendant stations
 */
public class SelfCheckoutStationTest {
	
	private SelfCheckoutControlSoftware selfCheckout;
	private SelfCheckoutStation hw;
	
	private int actualFound;
	private int noScannerFailure;
	private int productsAddedToCart;
	private int noValidatorFailure;
	
	@Before
	public void setup() {
		hw = TestUtility.getHarwareInstance1();
		selfCheckout = new SelfCheckoutControlSoftware(hw);
		selfCheckout.updatePhase(SoftwarePhase.READY);
		
		actualFound = 0;
		noScannerFailure = 0;
		productsAddedToCart = 0;
		noValidatorFailure = 0;
	}
	
	@Test
	public void productVerficiationFailed() throws OverloadException {
		
		hw.printer.addInk(100);
		hw.printer.addPaper(100);
		
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
				actualFound++;
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
		
		selfCheckout.notifier.attach(new SCNotifierSystemObserver() {

			
			@Override
			public void productWeightVerificationFailure(int id) {
				actualFound++;
				assertEquals(selfCheckout.ID, id);
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
			hw.mainScanner.scan(TestUtility.normalItem);
		}
		
		hw.baggingArea.add(TestUtility.normalItem2);
								
		assertEquals(2, actualFound);
	}
	
	@Test
	public void printerLowOnInk() throws OverloadException {
		
		hw.printer.addInk(105);
		hw.printer.addPaper(1024);
		
		selfCheckout.notifier.inkAddedToPrinter(105);
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
				productsAddedToCart++;
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
		
		selfCheckout.notifier.attach(new SCNotifierSystemObserver() {

			
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
		
		
		while(noScannerFailure != 1) {
			hw.mainScanner.scan(TestUtility.normalItem);
		}
		
		hw.baggingArea.add(TestUtility.normalItem);
		
		selfCheckout.notifier.checkoutButtonPressed();
		selfCheckout.notifier.payWithCardButtonPressed();
		
		while(true) {
			try {
				hw.cardReader.insert(TestUtility.card1, "1234");	
				break;
			}catch(Exception e) {}
		}
			
		hw.baggingArea.remove(TestUtility.normalItem);
		
		assertEquals(4, actualFound);
		assertEquals(1, productsAddedToCart);						
	}
	
	@Test
	public void printerLowOnPaper() throws OverloadException {
		
		hw.printer.addInk(1024);
		hw.printer.addPaper(4);
		
		selfCheckout.notifier.inkAddedToPrinter(1024);
		selfCheckout.notifier.paperAddedToPrinter(4);
		
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
				productsAddedToCart++;
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
		
		selfCheckout.notifier.attach(new SCNotifierSystemObserver() {

			
			@Override
			public void productWeightVerificationFailure(int id) {
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
			public void failedToPlaceItem(int id) {
				fail();		
			}

			@Override
			public void addBagVerificationFailure(int id, boolean ownBag) {
				fail();
			}
		});	
		
		
		while(noScannerFailure != 1) {
			hw.mainScanner.scan(TestUtility.normalItem);
		}
		
		hw.baggingArea.add(TestUtility.normalItem);
		
		selfCheckout.notifier.checkoutButtonPressed();
		selfCheckout.notifier.payWithCardButtonPressed();
		
		while(true) {
			try {
				hw.cardReader.insert(TestUtility.card1, "1234");	
				break;
			}catch(Exception e) {}
		}
			
		hw.baggingArea.remove(TestUtility.normalItem);
		
		assertEquals(4, actualFound);
		assertEquals(1, productsAddedToCart);							
	}
	
	@Test
	public void partialChangeReturned() throws OverloadException {
		
		hw.printer.addInk(1024);
		hw.printer.addPaper(1024);
		
		selfCheckout.notifier.inkAddedToPrinter(1024);
		selfCheckout.notifier.paperAddedToPrinter(1024);
		
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
				productsAddedToCart++;
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
		
		selfCheckout.notifier.attach(new SCNotifierSystemObserver() {

			
			@Override
			public void productWeightVerificationFailure(int id) {
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
			public void failedToPlaceItem(int id) {
				fail();		
			}

			@Override
			public void addBagVerificationFailure(int id, boolean ownBag) {
				fail();
			}
		});	
		
		
		while(noScannerFailure != 1) {
			hw.mainScanner.scan(TestUtility.normalItem);
		}
		
		hw.baggingArea.add(TestUtility.normalItem);
		
		selfCheckout.notifier.checkoutButtonPressed();
		selfCheckout.notifier.payWithCashButtonPressed();
		
		while(noValidatorFailure != 1) {
			try {
				hw.banknoteInput.accept(TestUtility.fiveDollarBill);
			}catch(Exception e) {
				hw.banknoteInput.removeDanglingBanknotes();
			}
		}
			
		hw.baggingArea.remove(TestUtility.normalItem);
		
		assertEquals(3, actualFound);
		assertEquals(1, productsAddedToCart);	
	}
	
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
		
		selfCheckout.notifier.attach(new SCNotifierSystemObserver() {

			@Override
			public void productWeightVerificationFailure(int id) {
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
			public void failedToPlaceItem(int id) {
				actualFound++;
				assertEquals(selfCheckout.ID, id);
			}

			@Override
			public void addBagVerificationFailure(int id, boolean ownBag) {
				fail();
			}
		});	
		
		while(noScannerFailure != 1) {
			hw.mainScanner.scan(TestUtility.normalItem);
		}
		
		Thread.sleep(6000);
		
		hw.baggingArea.add(TestUtility.normalItem);
		
		assertEquals(3, actualFound);
	}
	
	@Test
	public void addOwnBagVerificationFailure() throws OverloadException {
		
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
				fail();
			}
			
			@Override
			public void placeBagsInBaggingArea() {
				actualFound++;
			}

			@Override
			public void bagsAddedToPurchase(Cart cart) {
				fail();
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				actualFound++;
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
		
		selfCheckout.notifier.attach(new SCNotifierSystemObserver() {

			@Override
			public void productWeightVerificationFailure(int id) {
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
			public void failedToPlaceItem(int id) {
				fail();		
			}

			@Override
			public void addBagVerificationFailure(int id, boolean ownBag) {
				actualFound++;
				assertEquals(selfCheckout.ID, id);
				assertTrue(ownBag);
			}
		});	
		
		// Add one bag
		selfCheckout.notifier.addBagsButtonPressed(10, true);
		
		hw.baggingArea.add(TestUtility.twentyBags);
								
		assertEquals(3, actualFound);
	}
	
	@Test
	public void addPlasticBagVerificationFailure() {
		
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
				fail();
			}
			
			@Override
			public void placeBagsInBaggingArea() {
				actualFound++;
			}

			@Override
			public void bagsAddedToPurchase(Cart cart) {
				fail();
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				actualFound++;
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
		
		selfCheckout.notifier.attach(new SCNotifierSystemObserver() {

			@Override
			public void productWeightVerificationFailure(int id) {
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
			public void failedToPlaceItem(int id) {
				fail();		
			}

			@Override
			public void addBagVerificationFailure(int id, boolean ownBag) {
				actualFound++;
				assertEquals(selfCheckout.ID, id);
				assertFalse(ownBag);
			}
		});	
		
		// Add one bag
		selfCheckout.notifier.addBagsButtonPressed(10, false);
		
		hw.baggingArea.add(TestUtility.twentyBags);
								
		assertEquals(3, actualFound);		
	}
	
	@Test
	public void addOwnBagVerificationSuccesful() throws OverloadException {
		
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
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
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
			public void blockStation() {
				fail();
				
			}
			
			@Override
			public void unblockStation() {
				fail();
			}
		});
		
		selfCheckout.notifier.attach(new SCNotifierSystemObserver() {

			@Override
			public void productWeightVerificationFailure(int id) {
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
			public void failedToPlaceItem(int id) {
				fail();		
			}

			@Override
			public void addBagVerificationFailure(int id, boolean ownBag) {
				fail();
			}
		});	
		
		// Add one bag
		selfCheckout.notifier.addBagsButtonPressed(11, true);
		
		hw.baggingArea.add(TestUtility.elevenBagItem);
								
		assertEquals(3, actualFound);
	}
}
