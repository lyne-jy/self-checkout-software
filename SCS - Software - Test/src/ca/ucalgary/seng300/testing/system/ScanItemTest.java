package ca.ucalgary.seng300.testing.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import java.io.IOException;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import ca.ucalgary.seng300.selfcheckout.SelfCheckoutControlSoftware;
import ca.ucalgary.seng300.selfcheckout.communication.SCNotifierUIObserver;
import ca.ucalgary.seng300.selfcheckout.utility.Cart;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;
import ca.ucalgary.seng300.testing.utility.TestUtility;

/*
 * Class tests any scenario that deals with scanning an item (be it a product or a membership card)
 * 
 */
@RunWith(JUnit4.class)
public class ScanItemTest {

	private SelfCheckoutStation hw;
	private SelfCheckoutControlSoftware sw;	
	private int actualFound;
	private int noScannerFailure;
	private int noValidatorFailure;
	private int productsAddedToCart;
	
	@Before
	public void setup() throws OverloadException {

		hw = TestUtility.getHarwareInstance1();
		
		for(int i = 0; i < 10; i++)  hw.coinDispensers.get(TestUtility.oneDollar.getValue()).load(new Coin[]{TestUtility.oneDollar});
		
		sw =  new SelfCheckoutControlSoftware(hw);
		sw.updatePhase(SoftwarePhase.READY);
    	actualFound = 0; 
    	noScannerFailure = 0;
    	noValidatorFailure = 0;
    	productsAddedToCart = 0;
	}
	
	/*
	 * 1. User scans an item existing in DB
	 * 2. User places item in bagging area on time
	 * 		- Product is verified successfully     
	 * 		- Total on display is updated
	 * 5. User decides to checkout
	 * 6. User pays with cash
	 * 7. Users gets change and receipt is printed
	 */
	@Test 
	public void happyPath() throws DisabledException, OverloadException {
	
		hw.printer.addInk(100);
		hw.printer.addPaper(100);
		
		sw.notifier.attach(new SCNotifierUIObserver() {

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
				noValidatorFailure++;
				assertEquals(new BigDecimal(5.0), totalAmount);
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
			public void allProductsRemoved() {
				actualFound++;
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
			public void placeItemBaggingArea() {
				fail();
			}

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
				productsAddedToCart++;
			}
			
			@Override
			public void placeBagsInBaggingArea() {
				fail();
			}

			@Override
			public void addBagVerficationSuccesful(boolean ownBag) {
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
			public void blockStation() {
				fail();	
			}
			
			@Override
			public void unblockStation() {
				fail();
			}
		});
		
		while(noScannerFailure != 1) {
			hw.mainScanner.scan(TestUtility.normalItem);
		}
		
		hw.baggingArea.add(TestUtility.normalItem);
		
		sw.notifier.checkoutButtonPressed();
		sw.notifier.payWithCashButtonPressed();
		
		while(noValidatorFailure != 1) {
			try {
				hw.banknoteInput.accept(TestUtility.fiveDollarBill);
			}catch(Exception e) {
				hw.banknoteInput.removeDanglingBanknotes();
			}
		}
			
		hw.baggingArea.remove(TestUtility.normalItem);
		
		assertEquals("Wrong number of notifications to UI", 2, actualFound);
		assertEquals(1, productsAddedToCart);
	}
	
	
	
	/*
	 * 1. User scans an item existing in DB
	 * 2. User places item in bagging area on time
	 * 		- Product is verified successfully     
	 * 		- Total on display is updated
	 * 5. User decides to checkout
	 * 6. User successfully pays with card
	 * 7. Users gets change and receipt is printed
	 */
	@Test 
	public void happyPathInsertCard() throws DisabledException, OverloadException, IOException {
		
		hw.printer.addInk(100);
		hw.printer.addPaper(100);
		
		sw.notifier.attach(new SCNotifierUIObserver() {

			@Override
			public void showThankYouMessage(boolean partialChange) {
				actualFound++;
				assertFalse(partialChange);
			}

			@Override
			public void cardPaymentError() {
				fail("[HARDWARE FAILURE] Card payment could not be processed");
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
			public void allProductsRemoved() {
				actualFound++;
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
			public void addBagVerficationSuccesful(boolean ownBag) {
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
			public void blockStation() {
				fail();
			}
			
			@Override
			public void unblockStation() {
				fail();
			}
		});
		
		while(noScannerFailure != 1) {
			hw.mainScanner.scan(TestUtility.normalItem);
		}
		
		hw.baggingArea.add(TestUtility.normalItem);
		
		sw.notifier.checkoutButtonPressed();
		sw.notifier.payWithCardButtonPressed();
		
		while(true) {
			try {
				hw.cardReader.insert(TestUtility.card1, "1234");	
				break;
			}catch(Exception e) {}
		}
			
		hw.baggingArea.remove(TestUtility.normalItem);
		
		assertEquals("Wrong number of notifications to UI", 3, actualFound);
		assertEquals(1, productsAddedToCart);
	}	
	
	/*
	 * 1. User scans his membership card
	 * 2. User scans an item existing in DB
	 * 3. User places item in bagging area on time
	 * 		- Product is verified successfully     
	 * 		- Total on display is updated
	 * 4. User decides to checkout
	 * 5. User pays with card
	 * 6. Receipt is printed
	 */
	@Test 
	public void scanMembershipCardSuccesfully() throws DisabledException, OverloadException {
		
		hw.printer.addInk(100);
		hw.printer.addPaper(100);
		
		sw.notifier.attach(new SCNotifierUIObserver() {

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
				noValidatorFailure++;
				assertEquals(new BigDecimal(5.0), totalAmount);
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
			public void allProductsRemoved() {
				actualFound++;
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
				noScannerFailure++;
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
			public void addBagVerficationSuccesful(boolean ownBag) {
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
			public void blockStation() {
				fail();
			}
			
			@Override
			public void unblockStation() {
				fail();
			}
		});
		
		// Scanning membership card 
		sw.notifier.scanMembershipButtonPressed();
		
		while(noScannerFailure != 1) {
			hw.mainScanner.scan(TestUtility.membershipCard1);
		}
		
		while(noScannerFailure != 2) {
			hw.mainScanner.scan(TestUtility.normalItem);
		}
			
		hw.baggingArea.add(TestUtility.normalItem);
		
		sw.notifier.checkoutButtonPressed();
		sw.notifier.payWithCashButtonPressed();
		
		while(noValidatorFailure != 1) {
			try {
				hw.banknoteInput.accept(TestUtility.fiveDollarBill);
			}catch(Exception e) {
				hw.banknoteInput.removeDanglingBanknotes();
			}
		}
				
		hw.baggingArea.remove(TestUtility.normalItem);
		
		assertEquals("Wrong number of notifications to UI", 2, actualFound);
		assertEquals(1, productsAddedToCart);
	}
	
	/*
	 * 1. User scans an item [not in DB], then scans one in DB
	 * 2. User places item in bagging area on time
	 * 		- Product is verified successfully     
	 * 		- Total on display is updated
	 * 5. User decides to checkout
	 * 6. User pays with cash
	 * 7. Users gets change and receipt is printed
	 */
	@Test 
	public void userScansItemNotInDatabase() throws DisabledException, OverloadException {
		
		hw.printer.addInk(100);
		hw.printer.addPaper(100);
		
		sw.notifier.attach(new SCNotifierUIObserver() {

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
				noValidatorFailure++;
				assertEquals(new BigDecimal(5.0), totalAmount);
			}

			@Override
			public void printerError() {
				fail();
			}

			@Override
			public void barcodeScannedNotInDatabase() {
				actualFound++;
			}

			@Override
			public void allProductsRemoved() {
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
			public void addBagVerficationSuccesful(boolean ownBag) {
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
			public void blockStation() {
				fail();
			}
			
			@Override
			public void unblockStation() {
				fail();
			}
		});
				
		// Start scanning items
		while(noScannerFailure != 1) {
			hw.mainScanner.scan(TestUtility.normalItem2);
		}
		
		sw.notifier.barcodeScannedNotInDatabase();
		
		hw.mainScanner.scan(TestUtility.normalItem);
		hw.baggingArea.add(TestUtility.normalItem);
		
		sw.notifier.checkoutButtonPressed();
		sw.notifier.payWithCashButtonPressed();
		
		while(noValidatorFailure != 1) {
			try {
				hw.banknoteInput.accept(TestUtility.fiveDollarBill);
			}catch(Exception e) {
				hw.banknoteInput.removeDanglingBanknotes();
			}
		}
			
		assertEquals("Wrong number of notifications to UI", 2, actualFound);
		assertEquals(1, productsAddedToCart);
	}
	
	/*
	 * 1. User scans his membership card [not existing in DB]
	 * 2. User scans an item existing in DB
	 * 3. User places item in bagging area on time
	 * 		- Product is verified successfully     
	 * 		- Total on display is updated
	 * 4. User decides to checkout
	 * 5. User pays with card
	 * 6. Receipt is printed
	 */
	@Test 
	public void membershipCardDoesNotExistInDB() throws DisabledException, OverloadException {
	
		hw.printer.addInk(100);
		hw.printer.addPaper(100);
		
		sw.notifier.attach(new SCNotifierUIObserver() {

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
				noValidatorFailure++;
				assertEquals(new BigDecimal(5.0), totalAmount);
			}

			@Override
			public void printerError() {
				fail();
			}

			@Override
			public void barcodeScannedNotInDatabase() {
				actualFound++;
			}

			@Override
			public void allProductsRemoved() {
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
			public void membershipCardNotInDatabase() {
				noScannerFailure++;
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
			public void addBagVerficationSuccesful(boolean ownBag) {
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
			public void blockStation() {
				fail();
			}
			
			@Override
			public void unblockStation() {
				fail();
			}
		});
		
		// Scanning a non-existing membership
		sw.notifier.scanMembershipButtonPressed();
	
		while(noScannerFailure != 1) {
			hw.mainScanner.scan(TestUtility.membershipCard2);
		}
		
		// Continue with normal flow (happy path)
		while(noScannerFailure != 2) {
			hw.mainScanner.scan(TestUtility.normalItem2);
		}
		
		sw.notifier.barcodeScannedNotInDatabase();
		
		hw.mainScanner.scan(TestUtility.normalItem);
		hw.baggingArea.add(TestUtility.normalItem);
		
		sw.notifier.checkoutButtonPressed();
		sw.notifier.payWithCashButtonPressed();
		
		while(noValidatorFailure != 1) {
			try {
				hw.banknoteInput.accept(TestUtility.fiveDollarBill);
			}catch(Exception e) {
				hw.banknoteInput.removeDanglingBanknotes();
			}
		}
					
		assertEquals("Wrong number of notifications to UI", 2, actualFound);
		assertEquals(1, productsAddedToCart);
	}
}
