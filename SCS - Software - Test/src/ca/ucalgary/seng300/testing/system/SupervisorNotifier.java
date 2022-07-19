package ca.ucalgary.seng300.testing.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.SupervisionStation;

import ca.ucalgary.seng300.attendant.SupervisorStationControlSoftware;
import ca.ucalgary.seng300.attendant.communication.SSNotifierUIObserver;

public class SupervisorNotifier {

	private SupervisorStationControlSoftware sw;
	private int methodCalled;
	
	@Before
	public void setup() {
		
		SupervisionStation hw = new SupervisionStation();
		sw = new SupervisorStationControlSoftware(hw);
		methodCalled = 0;
	}
	
	/*
	 * 1. Attendant logs in
	 * 2. Attendant logs out
	 */
	@Test
	public void happyPath() {
	
		sw.notifier.attach(new SSNotifierUIObserver() {

			@Override
			public void wrongCredentials() {
				fail();
			}

			@Override
			public void validCredentials(String employeeName) {
				assertEquals("John", employeeName);
				methodCalled++;
			}

			@Override
			public void successfulLogout() {
				methodCalled++;
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
		
		sw.notifier.login(1234);
		sw.notifier.logout();
		
		assertEquals(2, methodCalled);
	}
	
}
