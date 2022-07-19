package ca.ucalgary.seng300.testing.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import ca.ucalgary.seng300.selfcheckout.SelfCheckoutControlSoftware;
import ca.ucalgary.seng300.selfcheckout.communication.SCNotifierUIObserver;
import ca.ucalgary.seng300.selfcheckout.utility.Cart;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;
import ca.ucalgary.seng300.testing.utility.TestUtility;

/*
 * Class tests any scenario that have to do with the bagging area and verification of products
 */
public class BaggingAreaTest {

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
	 * 1. User scans an item [not in DB],
	 * 2. User places invalid item in bagging area 
	 * 		- System should remain in READY phase and ignore the placement of the item
	 * 3. User scans an item 
	 * 4. User places item in bagging area 
	 * 5. User decides to checkout
	 * 6. User pays with cash
	 * 7. Users gets change and receipt is printed
	 */
	@Test 
	public void notInDBplacedInBaggingArea() throws DisabledException, OverloadException {
		
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
				noScannerFailure++;
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
				assertEquals(TestUtility.normalItem.getBarcode(), barcode);
			}

			@Override
			public void membershipCardScannedSuccesfully(Barcode barcode) {
				fail();
			}

			@Override
			public void PLUEnteredNotInDatabase() {
				fail();
			}
			
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
		
		// Trying to scan an item not in the database
		BarcodedItem item = new BarcodedItem(new Barcode(new Numeral[]{Numeral.two, Numeral.five}), 10.0);
		
		while(noScannerFailure != 1) {
			hw.mainScanner.scan(item);
		}
		
		hw.baggingArea.add(item);
		hw.baggingArea.remove(item);
		
		// Scanning a valid item and happy path follows
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
				
		assertEquals("Wrong number of notifications to UI", 1, actualFound);
		assertEquals(1, productsAddedToCart);
	}
	
	/*
	 * 1. User scans an item existing in DB
	 * 2. User does not place item in bagging area on time
	 * 		- UI should be notified 
	 * 		- Product is verified succesfully     
	 * 		- Total on display is updated
	 * 5. User decides to checkout
	 * 6. User pays with cash
	 * 7. Users gets change and receipt is printed
	 */
	@Test 
	public void userFailsToPlaceItemInBaggingArea() throws InterruptedException, OverloadException, DisabledException {

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
				actualFound++;
			}

			@Override
			public void couldNotReturnChange() {
				fail();
			}

			@Override
			public void placeItemBaggingArea() {
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
				productsAddedToCart++;
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
			public void placeBagsInBaggingArea() {
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
		
		Thread.sleep(6000);

		hw.baggingArea.add(TestUtility.normalItem);

		sw.notifier.checkoutButtonPressed();
		sw.notifier.payWithCashButtonPressed();

		hw.banknoteInput.accept(TestUtility.fiveDollarBill);

		hw.baggingArea.remove(TestUtility.normalItem);

		assertEquals("Wrong number of notifications to UI", 3, actualFound);
		assertEquals(1, productsAddedToCart);
	}
	
	/*
	 * 1. User scans an item existing in DB
	 * 2. User places item in bagging area on time
	 * 		- Product is not verified successfully     
	 * 		- Total on display is updated
	 * 5. User decides to checkout
	 * 6. User pays with cash
	 * 7. Users gets change and receipt is printed
	 */
	@Test 
	public void weightVerficationFails() throws DisabledException, OverloadException {
		
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
				actualFound++;
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
			public void placeBagsInBaggingArea() {
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
		
		hw.baggingArea.add(TestUtility.normalItem2);
		
		// At this point, the system should notify of an weight verification error
		// and wait for the correct item to be placed
	
		hw.baggingArea.remove(TestUtility.normalItem2);
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
		
		assertEquals("Wrong number of notifications to UI", 3, actualFound);
		assertEquals(1, productsAddedToCart);
	}
	
	/*
	 * 1. User decides to use own bag (verified successfully by Attedant Station)
	 * 2. User scans an item existing in DB
	 * 3. User places item in bagging area on time
	 * 		- Product is not verified successfully     
	 * 		- Total on display is updated
	 * 4. User decides to checkout
	 * 5. User pays with cash
	 * 6. Users gets change and receipt is printed
	 */
//	@Test  TODO: TEST CASE FAILS QA MUST DEAL WITH IT
	public void bagsAddedSuccesfully() throws DisabledException, OverloadException {
		
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
			public void addBagVerficationSuccesful(boolean ownBag) {
				actualFound++;
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
			public void placeBagsInBaggingArea() {
				actualFound++;
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
		
		sw.notifier.addBagsButtonPressed(2, false);
		hw.baggingArea.add(TestUtility.bag);
		
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
		
		assertEquals("Wrong number of notifications to UI", 3, actualFound);	
		assertEquals(1, productsAddedToCart);
	}	
}
