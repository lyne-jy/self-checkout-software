package ca.ucalgary.seng300.testing.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Before;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import ca.ucalgary.seng300.selfcheckout.product.EnterMembership;
import ca.ucalgary.seng300.selfcheckout.product.EnterMembershipObserver;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;
import ca.ucalgary.seng300.testing.utility.TestUtility;

@RunWith(JUnit4.class)
public class EnterMembershipTest {
	
	  private EnterMembership enterMemb;
	  private SelfCheckoutStation hw;
	  private int actualFound;
	  
	  @Before
	  public void setup() {
	    	
	    	hw = TestUtility.getHarwareInstance1();
	    	enterMemb = new EnterMembership();
	
	    	hw.handheldScanner.attach(enterMemb);
	    	hw.mainScanner.attach(enterMemb);
	    	
	    	enterMemb.updatePhase(SoftwarePhase.ENTERING_MEMBERSHIP);
	    	   	
	    	this.actualFound = 0;
	  }
	  
	  @Test 
	  public void nonexistentMembershipMainScanner() {

		  enterMemb.attach(new EnterMembershipObserver() {

			@Override
			public void membershipCardVerifiedSuccesfully(Barcode barcode) {
				fail();
			}

			@Override
			public void membershipCardNotInDatabase() {
				actualFound++;
			}

			@Override
			public void scannedMemberCardInWrongPhase() {
				fail();
			}
			  
		  });
		  
		  while(actualFound != 1) {
			  hw.mainScanner.scan(TestUtility.membershipCard2);
		  }
		  
		  assertEquals(1, actualFound);
		  
	  }
	  
	  @Test
	  public void scanMembershipInWrongPhaseMainScanner() {
		  
		  enterMemb.updatePhase(SoftwarePhase.READY);
		  
		  enterMemb.attach(new EnterMembershipObserver() {

			@Override
			public void membershipCardVerifiedSuccesfully(Barcode barcode) {
				fail();
			}

			@Override
			public void membershipCardNotInDatabase() {
				fail();
			}

			@Override
			public void scannedMemberCardInWrongPhase() {
				actualFound++;
			}
			  
		  });
		  
		  while(actualFound != 1) {
			  hw.mainScanner.scan(TestUtility.membershipCard1);
		  }
		  
		  assertEquals(1, actualFound);
	  }
	  
	  @Test
	  public void successfulMembershipScanMainScanner() {
		  
		  enterMemb.attach(new EnterMembershipObserver() {

				@Override
				public void membershipCardVerifiedSuccesfully(Barcode barcode) {
					actualFound++;
					assertEquals(TestUtility.existingMembershipBarcode, barcode);
				}
		
				@Override
				public void membershipCardNotInDatabase() {
					
				}
		
				@Override
				public void scannedMemberCardInWrongPhase() {
					fail();
				}
				  
		  });
			  
		  
		  while(actualFound != 1) {
			  hw.mainScanner.scan(TestUtility.membershipCard1);
		  }
	  }
	  
	  @Test 
	  public void nonexistentMembershipHandHeldScanner() {
		  
		  enterMemb.attach(new EnterMembershipObserver() {

				@Override
				public void membershipCardVerifiedSuccesfully(Barcode barcode) {
					fail();
				}
		
				@Override
				public void membershipCardNotInDatabase() {
					actualFound++;
				}
		
				@Override
				public void scannedMemberCardInWrongPhase() {
					fail();
				}
				  
		  });
		  
		  while(actualFound != 1) {
			  hw.handheldScanner.scan(TestUtility.membershipCard2);
		  }
		  
		  assertEquals(1, actualFound);
	  }
	  
	  @Test
	  public void scanMembershipInWrongPhaseHandHeldScanner() {
		  
		  enterMemb.updatePhase(SoftwarePhase.READY);
		  
		  enterMemb.attach(new EnterMembershipObserver() {

			@Override
			public void membershipCardVerifiedSuccesfully(Barcode barcode) {
				fail();
			}

			@Override
			public void membershipCardNotInDatabase() {
				fail();
			}

			@Override
			public void scannedMemberCardInWrongPhase() {
				actualFound++;
			}
			  
		  });
		  
		  while(actualFound != 1) {
		 	hw.handheldScanner.scan(TestUtility.membershipCard1);
		  }
		  
		  assertEquals(1, actualFound);  
	  }
	  
	  @Test
	  public void successfulMembershipScanHandHeldScanner() {
		  
		  enterMemb.attach(new EnterMembershipObserver() {

				@Override
				public void membershipCardVerifiedSuccesfully(Barcode barcode) {
					actualFound++;
					assertEquals(TestUtility.existingMembershipBarcode, barcode);
				}
		
				@Override
				public void membershipCardNotInDatabase() {
					fail();
				}
		
				@Override
				public void scannedMemberCardInWrongPhase() {
					fail();
				}
				  
		  });
			 
		  while(actualFound != 1) {
			  hw.handheldScanner.scan(TestUtility.membershipCard1);
		  }
	  }
	
	  @Test
	  public void nonexistentMembershipEnter() {
		  
		  enterMemb.attach(new EnterMembershipObserver() {

				@Override
				public void membershipCardVerifiedSuccesfully(Barcode barcode) {
					fail();
				}
		
				@Override
				public void membershipCardNotInDatabase() {
					actualFound++;
				}
		
				@Override
				public void scannedMemberCardInWrongPhase() {
					fail();
				}
				 
		  });
				
			enterMemb.barcodeEntered(TestUtility.nonExistingMembershipBarcode);
			assertEquals(1, actualFound);
	  }
	  
	  @Test
	  public void enterMembershipInWrongPhase() {
		  
		  enterMemb.updatePhase(SoftwarePhase.READY);
		  
		  enterMemb.attach(new EnterMembershipObserver() {

				@Override
				public void membershipCardVerifiedSuccesfully(Barcode barcode) {
					fail();
				}
		
				@Override
				public void membershipCardNotInDatabase() {
					fail();
				}
		
				@Override
				public void scannedMemberCardInWrongPhase() {
					actualFound++;
				}
				 
		  });
		  
		  enterMemb.barcodeEntered(TestUtility.existingMembershipBarcode);
	  }
	  
	  @Test
	  public void successfulMembershipEnter() {
		  enterMemb.attach(new EnterMembershipObserver() {

				@Override
				public void membershipCardVerifiedSuccesfully(Barcode barcode) {
					actualFound++;
					assertEquals(TestUtility.existingMembershipBarcode, barcode);
				}
		
				@Override
				public void membershipCardNotInDatabase() {
					fail();
				}
		
				@Override
				public void scannedMemberCardInWrongPhase() {
					fail();
				}
				  
		  });
		  
		  enterMemb.barcodeEntered(TestUtility.existingMembershipBarcode);
		  
		  assertEquals(1, actualFound);  
	  }
}
