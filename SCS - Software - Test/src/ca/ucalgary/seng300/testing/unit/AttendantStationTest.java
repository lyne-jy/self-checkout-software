package ca.ucalgary.seng300.testing.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.lsmr.selfcheckout.devices.SupervisionStation;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.Product;

import ca.ucalgary.seng300.attendant.SupervisorStationControlSoftware;
import ca.ucalgary.seng300.attendant.communication.SSNotiferExternalObserver;
import ca.ucalgary.seng300.attendant.communication.SSNotifierUIObserver;
import ca.ucalgary.seng300.selfcheckout.SelfCheckoutControlSoftware;
import ca.ucalgary.seng300.selfcheckout.utility.Cart;
import ca.ucalgary.seng300.testing.utility.TestUtility;

/*
 * Test suite test whether all messages from attendant station are correctly being send to the coordinator, UI, and selfcheckout stations
 */
@RunWith(JUnit4.class)
public class AttendantStationTest {

	private SupervisorStationControlSoftware supervisor;
	private SelfCheckoutControlSoftware selfCheckout;
	private int actualFound;

	@Before
	public void setup() {
		
		supervisor = new SupervisorStationControlSoftware(new SupervisionStation());
		selfCheckout = new SelfCheckoutControlSoftware(TestUtility.getHarwareInstance1());
				
		selfCheckout.connectToSoftwareSystem(supervisor);
		
		actualFound = 0;
	}
	
	/* MESSAGES TO SELF-CHECKOUT STATION */
	
	@Test
	public void attendantBlocksAStation() {
		
		supervisor.notifier.attach(new SSNotiferExternalObserver() {

			@Override
			public void approveWeightDiscrepancy() {
				fail();
			}

			@Override
			public void blockStation() {
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
			public void productRemovedFromPurchase(Cart newCart) {
				fail();
			}

			@Override
			public void approveProductNotBagged() {
				fail();				
			}

			@Override
			public void productAddedToPurchase(Product product) {
				fail();
			}

			@Override
			public void approveBagWeightVerificationFailure(boolean addOwnBag) {
				fail();
			}

			@Override
			public void unblockStation() {
				fail();
			}
		});
		
		supervisor.notifier.blockStation(selfCheckout);
		
		assertEquals(1, actualFound);	
	}
	
	@Test
	public void attendantAprovesWeightDiscrepancy() {
		
		supervisor.notifier.blockStation(selfCheckout);
		
		supervisor.notifier.attach(new SSNotiferExternalObserver() {

			@Override
			public void approveWeightDiscrepancy() {
				actualFound++;
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
			public void productAddedToPurchase(Product product) {
				fail();
			}

			@Override
			public void approveBagWeightVerificationFailure(boolean addOwnBag) {
				fail();
			}

			@Override
			public void unblockStation() {
				fail();
			}
		});
		
		supervisor.notifier.approveWeightDiscrepancy(selfCheckout);
		
		assertEquals(1, actualFound);	
	}
	
	@Test
	public void attendantStartUpStation() {
		
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
			public void productAddedToPurchase(Product product) {
				fail();
			}

			@Override
			public void approveBagWeightVerificationFailure(boolean addOwnBag) {
				fail();
			}

			@Override
			public void unblockStation() {
				fail();
			}
		});
		
		supervisor.notifier.startStation(selfCheckout);
		
		assertEquals(1, actualFound);
	}

	@Test
	public void attendantShutsDownStation() {
	
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
			public void productAddedToPurchase(Product product) {
				fail();
			}

			@Override
			public void approveBagWeightVerificationFailure(boolean addOwnBag) {
				fail();
			}

			@Override
			public void unblockStation() {
				fail();
			}
		});
		
		supervisor.notifier.shutDownStation(selfCheckout);
		
		assertEquals(1, actualFound);
	}
	
	
	@Test
	public void attendantRemovesProductFromPurchases() {
				
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
				// empty cart
				assertEquals(0, newCart.getBarcodedProducts().size());
				actualFound++;
			}

			@Override
			public void approveProductNotBagged() {
				fail();				
			}

			@Override
			public void productAddedToPurchase(Product product) {
				fail();
			}

			@Override
			public void approveBagWeightVerificationFailure(boolean addOwnBag) {
				fail();
			}

			@Override
			public void unblockStation() {
				fail();
			}
		});
		
		supervisor.notifier.productRemovedFromPurchase(selfCheckout, new Cart(0));
		
		assertEquals(1, actualFound);
	}
	
	@Test
	public void attendantLooksupAProduct() {
		
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
			public void productAddedToPurchase(Product product) {
				actualFound++;
				assertEquals(TestUtility.apple, (BarcodedProduct)product);
			}

			@Override
			public void approveBagWeightVerificationFailure(boolean addOwnBag) {
				fail();
			}

			@Override
			public void unblockStation() {
				fail();
			}
		});
		
		supervisor.notifier.productAddedToPurchse(selfCheckout, TestUtility.apple);
		
		assertEquals(1, actualFound);
	}
	
	
	/* MESSAGES WITHIN THE SUPERVISOR STATION */
	
	@Test
	public void attendantLogsInSuccesfully() {
		
		supervisor.notifier.attach(new SSNotifierUIObserver() {

			@Override
			public void wrongCredentials() {
				fail();
			}

			@Override
			public void validCredentials(String employeeName) {
				actualFound++;
				assertEquals("John", employeeName);
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
		
		supervisor.notifier.login(TestUtility.pin1);
		
		assertEquals(1, actualFound);	
	}
	
	@Test
	public void attendantLogsFailsToLogIn() {
		
		supervisor.notifier.attach(new SSNotifierUIObserver() {

			@Override
			public void wrongCredentials() {
				actualFound++;
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
		
		supervisor.notifier.login(TestUtility.pin2);
		
		assertEquals(1, actualFound);	
	}
	
	@Test
	public void attendantLogsOutSuccesfully() {
		
		supervisor.notifier.login(TestUtility.pin1);
		
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
				actualFound++;
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
		
		supervisor.notifier.logout();
		
		assertEquals(1, actualFound);	
	}
}