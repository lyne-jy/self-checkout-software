package ca.ucalgary.seng300.testing.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

import ca.ucalgary.seng300.selfcheckout.SelfCheckoutControlSoftware;
import ca.ucalgary.seng300.selfcheckout.communication.SCNotifierUIObserver;
import ca.ucalgary.seng300.selfcheckout.product.PLULogic;
import ca.ucalgary.seng300.selfcheckout.product.PLULogicObserver;
import ca.ucalgary.seng300.selfcheckout.utility.Cart;
import ca.ucalgary.seng300.selfcheckout.utility.Database;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;
import ca.ucalgary.seng300.testing.utility.TestUtility;

public class PLULogicTest {

	SelfCheckoutStation hw;
	SelfCheckoutControlSoftware sw;
	PLULogic logic;
	PLUCodedProduct PLUCode;
	ProductDatabases database;
	private Cart cart;
	private int methodVisited;
	
	@Before
	public void setup() {
		hw = TestUtility.getHarwareInstance1();
		logic = new PLULogic(hw);
		logic.updatePhase(SoftwarePhase.READY);
		methodVisited = 0;
		sw = new SelfCheckoutControlSoftware(TestUtility.getHarwareInstance1());

		cart = new Cart(0);
	}

	/**
	 * Entering a PLU code that is correct in the wrong phase
	 */
	@Test
	public void enterPLUWrongPhase() {
		logic.updatePhase(SoftwarePhase.HOLDING);
		logic.attach(new PLULogicObserver() {

			@Override
			public void PLUEnteredInWrongPhase() {
				methodVisited++;				
			}

			@Override
			public void PLUEnteredNotInDatabase() {
				fail();
			}

			@Override
			public void productAdded(PriceLookupCode c) {
				fail();

			}

			@Override
			public void productPlacedInScanningArea(double expectedWeight) {
				fail();
			}
		});

		logic.PLUCodeEntered(TestUtility.plu1);
		assertEquals(1, methodVisited);
	}
	
	
	/**
	 * Entering a correct PLU code that is in the database while in the correct phase
	 */
	@Test
	public void enterPLUCorrectPhase() {
		logic.updatePhase(SoftwarePhase.READY);
		
		logic.attach(new PLULogicObserver() {

			@Override
			public void PLUEnteredInWrongPhase() {
				fail();

			}

			@Override
			public void PLUEnteredNotInDatabase() {
				fail();
			}

			@Override
			public void productAdded(PriceLookupCode c) {
				methodVisited++;				
			}

			@Override
			public void productPlacedInScanningArea(double expectedWeight) {
				fail();
			}
		});

		logic.PLUCodeEntered(TestUtility.plu1);
		assertEquals(1, methodVisited);
	}
	
	
	/**
	 * Entering a PLU code that is not in the database while in the correct phase
	 */
	@Test
	public void enterPLUCodeNotInDatabase() {
	
		logic.updatePhase(SoftwarePhase.READY);
		
		logic.attach(new PLULogicObserver() {

			@Override
			public void PLUEnteredInWrongPhase() {
				fail();
			}

			@Override
			public void PLUEnteredNotInDatabase() {
				methodVisited++;				
			}

			@Override
			public void productAdded(PriceLookupCode c) {
				fail();	
			}
			
			@Override
			public void productPlacedInScanningArea(double expectedWeight) {
				fail();
			}
		});
		logic.PLUCodeEntered(TestUtility.plu2);
		assertEquals(1, methodVisited);
	}
	/**
	 * The customer looks up a product and adds it to cart
	 */
	@Test
	public void CustomerLooksupAProduct() {
		
		sw.notifier.attach(new SCNotifierUIObserver() {

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
			public void placeItemBaggingArea() {
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
			public void productAddedToPurchase(Cart cart) {
				methodVisited++;	
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
		cart.addPLUItemToCart(TestUtility.normalPLUCode,Database.database().readProductWithPLU(TestUtility.normalPLUCode).getPrice(), 10.00);
		sw.notifier.productAddedToPurchase(cart);
		assertEquals(1, methodVisited);
	}
		
}
