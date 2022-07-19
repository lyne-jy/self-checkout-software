package ca.ucalgary.seng300.testing.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.IOException;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
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
 * Class tests any scenario that deals with payment, partial payment (when using cash) and possible errors when returning change.  
 * 
 * */
public class PaymentTest {

	private SelfCheckoutStation hw;
	private SelfCheckoutControlSoftware sw;
	
	private int actualFound;
	private int noScannerFailure;
	private int noValidatorFailure;
	private int productsAddedToCart;
	private BigDecimal cashFundAmount;
	
	@Before
	public void setup() throws OverloadException {

		hw = TestUtility.getHarwareInstance1();
		
		for(int i = 0; i < 10; i++)  hw.coinDispensers.get(TestUtility.oneDollar.getValue()).load(new Coin[]{TestUtility.oneDollar});
		
		sw =  new SelfCheckoutControlSoftware(hw);
		sw.updatePhase(SoftwarePhase.READY);

    	actualFound = 0; 	
    	cashFundAmount = BigDecimal.ZERO;
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
	 * 7. Users gets partial change and receipt is printed
	 */
	@Test 
	public void partialChange() throws DisabledException, OverloadException {
		
		hw.coinDispensers.get(TestUtility.oneDollar.getValue()).unload();
		hw.printer.addInk(100);
		hw.printer.addPaper(100);
		
		sw.notifier.attach(new SCNotifierUIObserver() {

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
				assertEquals(new BigDecimal(10.0), totalAmount);
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
			public void bagsAddedToPurchase(Cart cart) {
				fail();
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
			}

			@Override
			public void addBagVerficationSuccesful(boolean ownBag) {
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
			hw.mainScanner.scan(TestUtility.defaultItem);
		}
	
		hw.baggingArea.add(TestUtility.defaultItem);
		
		sw.notifier.checkoutButtonPressed();
		sw.notifier.payWithCashButtonPressed();
		
		
		while(noValidatorFailure != 1) {
			try {
				hw.banknoteInput.accept(TestUtility.tenDollarBill);
			}catch(Exception e) {
				hw.banknoteInput.removeDanglingBanknotes();
			}
		}
		
		hw.baggingArea.remove(TestUtility.defaultItem);
		
		assertEquals("Wrong number of notifications to UI", 2, actualFound);
		assertEquals(1, productsAddedToCart);
	}	
	
	/*
	 * 1. User scans an item existing in DB
	 * 2. User places item in bagging area on time
	 * 		- Product is verified successfully     
	 * 		- Total on display is updated
	 * 3. User decides to checkout [partial payment]
	 * 4. User pays with cash 
	 * 5. User scans more items [repeat 2-4]
	 * 6. User decided to finishing by doing one last checkout
	 * 8. user pays with cash
	 * 9. Receipt is printed
	 */
	@Test 
	public void partialPayment() throws DisabledException, OverloadException {
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
				cashFundAmount = cashFundAmount.add(totalAmount);
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
			public void addBagVerficationSuccesful(boolean ownBag) {
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
		
		// scan one item
		while(noScannerFailure != 1) {
			hw.mainScanner.scan(TestUtility.normalItem);
		}
		
		hw.baggingArea.add(TestUtility.normalItem);
		
		// partial pay
		sw.notifier.checkoutButtonPressed();
		sw.notifier.payWithCashButtonPressed();
		
		while(noValidatorFailure != 1) {
			hw.coinSlot.accept(TestUtility.twoDollars);
		}
		
		// scan one more item
		sw.notifier.addMoreItemsButtonPresses();
		
		while(noScannerFailure != 2) {
			hw.mainScanner.scan(TestUtility.normalItem3);
		}
		
		hw.baggingArea.add(TestUtility.normalItem3);
	
		// full checkout
		sw.notifier.checkoutButtonPressed();
		sw.notifier.payWithCashButtonPressed();
		
		while(noValidatorFailure != 3) {
			hw.coinSlot.accept(TestUtility.twoDollars);
		}
		
		// removing products from scale
		hw.baggingArea.remove(TestUtility.normalItem);
		hw.baggingArea.remove(TestUtility.normalItem3);
		
		assertEquals("Wrong amount of total cash registered.",BigDecimal.valueOf(6), cashFundAmount);
		assertEquals("Wrong number of notifications to UI", 2, actualFound);
		assertEquals(2, productsAddedToCart);
	}

	/*
	 * 1. User scans an item existing in DB
	 * 2. User places item in bagging area on time
	 * 		- Product is verified successfully     
	 * 		- Total on display is updated
	 * 3. User decides to checkout [partial payment]
	 * 4. User pays with cash(coin)
	 * 5. Change is returned [fully or partially] 
	 * 5. User scans more items [repeat 2-4]
	 * 6. User decided to finishing by doing one last checkout
	 * 8. user pays with cash(banknote)
	 * 9. Receipt is printed
	 */
	@Test 
	public void partialPayment2() throws DisabledException, IOException, OverloadException {
		
		
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
				actualFound++;
				cashFundAmount = cashFundAmount.add(totalAmount);
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
		
		// scan one item
		while(noScannerFailure != 1) {
			hw.mainScanner.scan(TestUtility.normalItem);
		}
				
		hw.baggingArea.add(TestUtility.normalItem);
		
		// partial pay
		sw.notifier.checkoutButtonPressed();
		sw.notifier.payWithCashButtonPressed();
		
		while(noValidatorFailure != 1) {
			hw.coinSlot.accept(TestUtility.twoDollars);
		}
			
		// scan one more item
		sw.notifier.addMoreItemsButtonPresses();
		
		while(noScannerFailure != 2) {
			hw.mainScanner.scan(TestUtility.normalItem3);
		}
				
		hw.baggingArea.add(TestUtility.normalItem3);
		
		// full checkout with banknote
		sw.notifier.checkoutButtonPressed();
		sw.notifier.payWithCashButtonPressed(); 
		
		while(noValidatorFailure != 2) {
			try {
				hw.banknoteInput.accept(TestUtility.tenDollarBill);
			}catch(Exception e) {
				hw.banknoteInput.removeDanglingBanknotes();
			}
		}
		
		// removing products from scale
		hw.baggingArea.remove(TestUtility.normalItem);
		hw.baggingArea.remove(TestUtility.normalItem3);
				
		assertEquals("Wrong amount of total cash registered.",BigDecimal.valueOf(12), cashFundAmount);
		assertEquals("Wrong number of notifications to UI", 4, actualFound);
		assertEquals(2, productsAddedToCart);
	}
	
	/*
	 * 1. User scans an item existing in DB
	 * 2. User places item in bagging area on time
	 * 		- Product is verified successfully     
	 * 		- Total on display is updated
	 * 3. User decides to checkout [partial payment]
	 * 4. User pays with cash(coin)
	 * 5. Change is returned [fully or partially] 
	 * 5. User scans more items [repeat 2-4]
	 * 6. User decided to finishing by doing one last checkout
	 * 8. user pays with card
	 * 9. Receipt is printed
	 */
	@Test 
	public void partialPayment3() throws DisabledException, IOException, OverloadException {
		
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
				cashFundAmount = cashFundAmount.add(totalAmount);
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
		
		// scan one item
		while(noScannerFailure != 1) {
			hw.mainScanner.scan(TestUtility.normalItem);
		}
		
		hw.baggingArea.add(TestUtility.normalItem);
		
		// partial pay
		sw.notifier.checkoutButtonPressed();
		sw.notifier.payWithCashButtonPressed();
		
		while(noValidatorFailure != 1) {
			hw.coinSlot.accept(TestUtility.twoDollars);
		}
		
		// scan one more item
		sw.notifier.addMoreItemsButtonPresses();
		
		while(noScannerFailure != 2) {
			hw.mainScanner.scan(TestUtility.normalItem3);
		}
			
		hw.baggingArea.add(TestUtility.normalItem3);
	
		// full checkout with card
		sw.notifier.checkoutButtonPressed();
		sw.notifier.payWithCardButtonPressed();
		
		while(true) {
			try{
				hw.cardReader.insert(TestUtility.card1, "1234");
				break;
			}catch(Exception e) {}
		}
		
		// removing products from scale
		hw.baggingArea.remove(TestUtility.normalItem);
		hw.baggingArea.remove(TestUtility.normalItem3);
						
		assertEquals("Wrong amount of total cash registered.",BigDecimal.valueOf(2), cashFundAmount);
		assertEquals("Wrong number of notifications to UI", 3, actualFound);
		assertEquals(2, productsAddedToCart);
	}
	

	/*
	 * 1. User scans an item existing in DB
	 * 2. User places item in bagging area on time
	 * 		- Product is verified successfully     
	 * 		- Total on display is updated
	 * 5. User decides to checkout
	 * 6. User pays with a giftcard
	 * 7. Receipt is printed
	 */
	@Test
	public void payWithGiftCardHappyPath() throws OverloadException, DisabledException {
		
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
		
		// scan one item
		while(noScannerFailure != 1) {
			hw.mainScanner.scan(TestUtility.normalItem);
		}
		
		// add item to bagging area
		hw.baggingArea.add(TestUtility.normalItem);
		
		// full checkout with gift card
		sw.notifier.checkoutButtonPressed();
		sw.notifier.payWithGiftCardButtonPressed(TestUtility.giftCard1);
		
		// remove products from the bagging area
		hw.baggingArea.remove(TestUtility.normalItem);
		
		assertEquals(3, actualFound);
		assertEquals(1, productsAddedToCart);
	}
	
	/*
	 * 1. User scans an item existing in DB
	 * 2. User places item in bagging area on time
	 * 		- Product is verified successfully     
	 * 		- Total on display is updated
	 * 5. User decides to checkout
	 * 6. User pays with a giftcard [that giftcard has not enough funds]
	 * 7. User pays with cash [No change returned]
	 * 8. Receipt is printed
	 */
	@Test
	public void payWithGiftCardNotEnoughFunds() throws OverloadException, DisabledException {
		
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
				actualFound++;
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
		
		// scan one item
		while(noScannerFailure != 1) {
			hw.mainScanner.scan(TestUtility.normalItem);
		}
		
		// add item to bagging area
		hw.baggingArea.add(TestUtility.normalItem);
		
		// full checkout with gift card
		sw.notifier.checkoutButtonPressed();
		sw.notifier.payWithGiftCardButtonPressed(TestUtility.giftCard2);
		
		sw.notifier.payWithCashButtonPressed();
		
		while(noValidatorFailure != 3) {
			hw.coinSlot.accept(TestUtility.oneDollar);
		}
		
		// remove products from the bagging area
		hw.baggingArea.remove(TestUtility.normalItem);
		
		assertEquals(3, actualFound);
		assertEquals(1, productsAddedToCart);
	}
	
	/*
	 * 1. User scans an item existing in DB
	 * 2. User places item in bagging area on time
	 * 		- Product is verified successfully     
	 * 		- Total on display is updated
	 * 5. User decides to checkout
	 * 6. User pays with a giftcard [that giftcard has not enough funds]
	 * 7. User pays with cash [only partial change returned]
	 * 8. Receipt is printed
	 */
	@Test
	public void payWithGiftCardNotEnoughFunds2() throws DisabledException, OverloadException {
		
		hw.coinDispensers.get(TestUtility.oneDollar.getValue()).unload();
		hw.printer.addInk(100);
		hw.printer.addPaper(100);
		
		sw.notifier.attach(new SCNotifierUIObserver() {

			@Override
			public void showThankYouMessage(boolean partialChange) {
				actualFound++;
				assertTrue(partialChange);
			}

			@Override
			public void cardPaymentError() {
				actualFound++;
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
		
		// scan one item
		while(noScannerFailure != 1) {
			hw.mainScanner.scan(TestUtility.normalItem);
		}
		
		// add item to bagging area
		hw.baggingArea.add(TestUtility.normalItem);
		
		// full checkout with gift card
		sw.notifier.checkoutButtonPressed();
		sw.notifier.payWithGiftCardButtonPressed(TestUtility.giftCard2);
		
		sw.notifier.payWithCashButtonPressed();
		
		while(noValidatorFailure != 1) {
			try {
				hw.banknoteInput.accept(TestUtility.tenDollarBill);
			}catch(Exception e) {
				hw.banknoteInput.removeDanglingBanknotes();
			}
		}
		
		// remove products from the bagging area
		hw.baggingArea.remove(TestUtility.normalItem);
		
		assertEquals(3, actualFound);
		assertEquals(1, productsAddedToCart);
	}
	
	
	/*
	 * 1. User scans an item existing in DB
	 * 2. User places item in bagging area on time
	 * 		- Product is verified successfully     
	 * 		- Total on display is updated
	 * 5. User decides to checkout
	 * 6. User pays with a giftcard [that giftcard has not enough funds]
	 * 7. User pays with credit/debit card 
	 * 8. Receipt is printed
	 */
	@Test
	public void payWithGiftCardNotEnoughFunds3() throws OverloadException {
		
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
				actualFound++;
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
		
		// scan one item
		while(noScannerFailure != 1) {
			hw.mainScanner.scan(TestUtility.normalItem);
		}
		
		// add item to bagging area
		hw.baggingArea.add(TestUtility.normalItem);
		
		// full checkout with gift card
		sw.notifier.checkoutButtonPressed();
		sw.notifier.payWithGiftCardButtonPressed(TestUtility.giftCard2);
		
		sw.notifier.payWithCardButtonPressed();
		
		while(true) {
			try{
				hw.cardReader.insert(TestUtility.card1, "1234");
				break;
			}catch(Exception e) {}
		}
		
		// remove products from the bagging area
		hw.baggingArea.remove(TestUtility.normalItem);
		
		assertEquals(4, actualFound);
		assertEquals(1, productsAddedToCart);
	}
}