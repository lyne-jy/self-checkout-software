package ca.ucalgary.seng300.testing.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import ca.ucalgary.seng300.selfcheckout.product.BaggingArea;
import ca.ucalgary.seng300.selfcheckout.product.BaggingAreaObserver;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;
import ca.ucalgary.seng300.testing.utility.TestUtility;

@RunWith(JUnit4.class)
public class BaggingAreaTest {
	
	private SelfCheckoutStation hw;
	private BaggingArea bagging;
    private int actualFound;
    private final double epsilon = 0.0001;

    @Before
    public void setup() {
        hw = TestUtility.getHarwareInstance1();
    	bagging = new BaggingArea();
    	hw.baggingArea.attach(bagging);
    	bagging.updatePhase(SoftwarePhase.HOLDING);
    	actualFound = 0;
    }
    
    
    /* WEIGHT VERIFICATION */
    
    @Test
    public void succesfulWeightVerification() {
		
		bagging.setBarcodedItemToBeWeiged(TestUtility.normalItemBarcode);
    	
    	bagging.attach(new BaggingAreaObserver() {

			@Override
			public void productWeightVerificationSuccesful() {
				actualFound++;
			}

			@Override
			public void productWeightVerificationFailure() {
				fail();	
			}

			@Override
			public void addedProductInWrongPhase() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}

			@Override
			public void addingBagsInWrongPhase() {
				fail();
			}

			@Override
			public void placeBagsInBaggingArea() {
				fail();
			}

			@Override
			public void addBagVerificationSuccessful(boolean ownBag) {
				fail();
				
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
			}

			@Override
			public void overloadOfScale() {
				fail();
			}
    	});
    	
     	hw.baggingArea.add(TestUtility.normalItem);
    	
    	assertEquals("Observer not notified about succesful verificaton", 1, actualFound);
    	assertEquals("Wrong weight registered", TestUtility.normalItem.getWeight(), bagging.getCurWeightRegistered(), epsilon);
    }   

    @Test
	public void failedWeightVerification() {
		
		bagging.setBarcodedItemToBeWeiged(TestUtility.normalItemBarcode);
    	
    	bagging.attach(new BaggingAreaObserver() {

			@Override
			public void productWeightVerificationSuccesful() {
				fail();
			}

			@Override
			public void productWeightVerificationFailure() {
				actualFound++;
			}

			@Override
			public void addedProductInWrongPhase() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}

			@Override
			public void addingBagsInWrongPhase() {
				fail();
			}

			@Override
			public void placeBagsInBaggingArea() {
				fail();				
			}

			@Override
			public void addBagVerificationSuccessful(boolean ownBag) {
				fail();	
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
			}

			@Override
			public void overloadOfScale() {
				fail();
			}
    	});
    	
     	hw.baggingArea.add(TestUtility.normalItem2);
    	
    	assertEquals("Observer not notified about failure in verificaton", 1, actualFound);
    	assertEquals("Wrong weight registered", 0.0, bagging.getCurWeightRegistered(), epsilon);	
	}
	
	@Test
	public void weightVerificationInWrongPhase() {
		
		bagging.updatePhase(SoftwarePhase.READY);
		
		bagging.setBarcodedItemToBeWeiged(TestUtility.normalItemBarcode);
		
		bagging.attach(new BaggingAreaObserver() {

			@Override
			public void productWeightVerificationSuccesful() {
				fail();
			}

			@Override
			public void productWeightVerificationFailure() {
				actualFound++;
			}

			@Override
			public void addedProductInWrongPhase() {
				actualFound++;
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}
		
			@Override
			public void addingBagsInWrongPhase() {
				fail();
			}
			
			@Override
			public void placeBagsInBaggingArea() {
				fail();				
			}

			@Override
			public void addBagVerificationSuccessful(boolean ownBag) {
				fail();
				
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
			}

			@Override
			public void overloadOfScale() {
				fail();
			}
    	});
		
		
		hw.baggingArea.add(TestUtility.normalItem);
		
    	assertEquals("Observer not notified about succesful verificaton", 1, actualFound);
    	assertEquals("Wrong weight registered", 0.0, bagging.getCurWeightRegistered(), epsilon);
	}
	
	@Test
	public void multipleSuccesfulVerifications() {
		
		bagging.setBarcodedItemToBeWeiged(TestUtility.normalItemBarcode);
    	
    	bagging.attach(new BaggingAreaObserver() {

			@Override
			public void productWeightVerificationSuccesful() {
				actualFound++;
			}

			@Override
			public void productWeightVerificationFailure() {
				fail();	
			}

			@Override
			public void addedProductInWrongPhase() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}

			@Override
			public void addingBagsInWrongPhase() {
				fail();
			}
			
			@Override
			public void placeBagsInBaggingArea() {
				fail();				
			}

			@Override
			public void addBagVerificationSuccessful(boolean ownBag) {
				fail();	
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
			}

			@Override
			public void overloadOfScale() {
				fail();
			}
    	});
    	
     	hw.baggingArea.add(TestUtility.normalItem);
     	
     	bagging.setBarcodedItemToBeWeiged(TestUtility.normalItemBarcode);
     	
    	hw.baggingArea.add(new BarcodedItem(TestUtility.normalItem.getBarcode(), TestUtility.normalItem.getWeight()));
    	
    	assertEquals("Observer not notified about succesful verificaton", 2, actualFound);
    	assertEquals("Wrong weight registered", TestUtility.normalItem.getWeight() * 2, bagging.getCurWeightRegistered(), epsilon);	
	}
    
	@Test
	public void weightVerificationInCheckoutPhase() {
		
		bagging.updatePhase(SoftwarePhase.CHECKOUT);
		
		bagging.setBarcodedItemToBeWeiged(TestUtility.normalItemBarcode);
		
		bagging.attach(new BaggingAreaObserver() {

			@Override
			public void productWeightVerificationSuccesful() {
				fail();
			}

			@Override
			public void productWeightVerificationFailure() {
				fail();
				
			}

			@Override
			public void addedProductInWrongPhase() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}
			
			@Override
			public void addingBagsInWrongPhase() {
				fail();
			}
			
			@Override
			public void placeBagsInBaggingArea() {
				fail();				
			}

			@Override
			public void addBagVerificationSuccessful(boolean ownBag) {
				fail();	
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
			}

			@Override
			public void overloadOfScale() {
				fail();
			}
    	});
		
		
		hw.baggingArea.add(TestUtility.normalItem);

    	assertEquals("Wrong weight registered", 0.0, bagging.getCurWeightRegistered(), epsilon);
	}
	
	@Test
	public void scalesSuffersOverload() {
		
		bagging.setBarcodedItemToBeWeiged(TestUtility.heavyItemBarcode);
		
		bagging.attach(new BaggingAreaObserver() {

			@Override
			public void productWeightVerificationSuccesful() {
				fail();
			}

			@Override
			public void productWeightVerificationFailure() {
				fail();
			}

			@Override
			public void addedProductInWrongPhase() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}
			
			@Override
			public void addingBagsInWrongPhase() {
				fail();
			}
			
			@Override
			public void placeBagsInBaggingArea() {
				fail();				
			}

			@Override
			public void addBagVerificationSuccessful(boolean ownBag) {
				fail();	
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
			}

			@Override
			public void overloadOfScale() {
				actualFound++;
			}
    	});
		
		hw.baggingArea.add(TestUtility.heavyItem);
		
		assertEquals("Observer not notified about failure in verificaton", 1, actualFound);
    	assertEquals("Wrong weight registered", 0.0, bagging.getCurWeightRegistered(), epsilon);
	}
		
	@Test
	public void getItemToBeWeighed() {
		bagging.setBarcodedItemToBeWeiged(TestUtility.heavyItemBarcode);
		assertEquals(bagging.getBarcodedItemToBeWeighed(), TestUtility.heavyItemBarcode);		
	}
	
	@Test
	public void removeAllItemsInCheckOutPhase() {
		
		bagging.updatePhase(SoftwarePhase.CHECKOUT);
		
		BarcodedItem a = new BarcodedItem(TestUtility.normalItem.getBarcode(), TestUtility.normalItem.getWeight());
		BarcodedItem b = new BarcodedItem(TestUtility.normalItem.getBarcode(), TestUtility.normalItem.getWeight());
		BarcodedItem c = new BarcodedItem(TestUtility.normalItem.getBarcode(), TestUtility.normalItem.getWeight());
		hw.baggingArea.add(a);
		hw.baggingArea.add(b);
		hw.baggingArea.add(c);
	
		hw.baggingArea.remove(a);
		hw.baggingArea.remove(c);
		
    	bagging.attach(new BaggingAreaObserver() {

			@Override
			public void productWeightVerificationSuccesful() {
				fail();
			}

			@Override
			public void productWeightVerificationFailure() {
				fail();	
			}

			@Override
			public void addedProductInWrongPhase() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				actualFound++;
			}
			
			@Override
			public void addingBagsInWrongPhase() {
				fail();
			}
			
			@Override
			public void placeBagsInBaggingArea() {
				fail();				
			}

			@Override
			public void addBagVerificationSuccessful(boolean ownBag) {
				fail();	
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
			}

			@Override
			public void overloadOfScale() {
				fail();
			}
    	});
    	
    	hw.baggingArea.remove(b);
    		
    	assertEquals("Observer not notified about succesful verificaton", 1, actualFound);
    	assertEquals("Wrong weight registered", 0.0, bagging.getCurWeightRegistered(), epsilon);	
	}
	
	@Test
	public void addOwnBagInWrongPhase() {
		
		bagging.updatePhase(SoftwarePhase.READY);
		
		bagging.attach(new BaggingAreaObserver() {

			@Override
			public void productWeightVerificationSuccesful() {
				fail();
			}

			@Override
			public void productWeightVerificationFailure() {
				fail();	
			}

			@Override
			public void addedProductInWrongPhase() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				actualFound++;
			}
			
			@Override
			public void addingBagsInWrongPhase() {
				actualFound++;
			}
			
			@Override
			public void placeBagsInBaggingArea() {
				fail();				
			}

			@Override
			public void addBagVerificationSuccessful(boolean ownBag) {
				fail();	
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
			}

			@Override
			public void overloadOfScale() {
				fail();
			}
    	});

		bagging.addBags(2, true);
		
		assertEquals(1, actualFound);
	}
	
	/* PLU CODE TESTING */

	@Test
	public void getExpectedPLUTest() {
		bagging.setPLUItemToBeWeighed(TestUtility.normalPLUCode);
		PriceLookupCode actual = bagging.getPLUItemToBeWeighed();
		
		assertTrue("set and get PLU item to be weighed works correctly", actual == TestUtility.normalPLUCode);
	}
	
	@Test
	public void getExpectedPLUWeightTest() {
		bagging.setPLUExpectedWeight(TestUtility.normalPLUItem.getWeight());
		double actual = bagging.getExpectedPLUWeight();
		double expected = 16.0;
		
		assertTrue(actual == expected);
	}
	
	@Test
	public void VerificationSuccessPLUWeightTest() {
		
		bagging.setPLUItemToBeWeighed(TestUtility.normalPLUCode);
		bagging.setPLUExpectedWeight(TestUtility.normalPLUItem.getWeight());
		
		bagging.attach(new BaggingAreaObserver() {
			
			@Override
			public void productWeightVerificationSuccesful() {
				actualFound++;
			}

			@Override
			public void productWeightVerificationFailure() {
				fail();	
			}

			@Override
			public void addedProductInWrongPhase() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}
			

			@Override
			public void addingBagsInWrongPhase() {
				fail();
			}

			@Override
			public void placeBagsInBaggingArea() {
				fail();
				
			}


			@Override
			public void addBagVerificationSuccessful(boolean ownBag) {
				fail();
				
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
				
			}

			@Override
			public void overloadOfScale() {
				fail();
			}
    	});
		
		hw.baggingArea.add(TestUtility.normalPLUItem);
		
		assertEquals(1, actualFound);
	}
	
	
	@Test
	public void VerificationFailurePLUWeightTest() {
		
		bagging.setPLUItemToBeWeighed(TestUtility.normalPLUCode);
		
		bagging.attach(new BaggingAreaObserver() {
			
			@Override
			public void productWeightVerificationSuccesful() {
				fail();
			}

			@Override
			public void productWeightVerificationFailure() {
				actualFound++;
			}

			@Override
			public void addedProductInWrongPhase() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}
			

			@Override
			public void addingBagsInWrongPhase() {
				fail();
			}

			@Override
			public void placeBagsInBaggingArea() {
				fail();
				
			}


			@Override
			public void addBagVerificationSuccessful(boolean ownBag) {
				fail();
				
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
				
			}

			@Override
			public void overloadOfScale() {
				fail();
			}
    	});
		
		hw.baggingArea.add(TestUtility.normalPLUItem);
		
		assertEquals(1, actualFound);
	}
	
    @Test
    public void subtractWeight() throws OverloadException {
		
		bagging.setBarcodedItemToBeWeiged(TestUtility.normalItemBarcode);
    	
    	bagging.attach(new BaggingAreaObserver() {

			@Override
			public void productWeightVerificationSuccesful() {
				actualFound++;
			}

			@Override
			public void productWeightVerificationFailure() {
				fail();
			}

			@Override
			public void addedProductInWrongPhase() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}

			@Override
			public void addingBagsInWrongPhase() {
				fail();
			}

			@Override
			public void placeBagsInBaggingArea() {
				fail();
				
			}


			@Override
			public void addBagVerificationSuccessful(boolean ownBag) {
				fail();
				
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
				
			}

			@Override
			public void overloadOfScale() {
				fail();
			}
    	});
    	
     	hw.baggingArea.add(TestUtility.normalItem);
    	
		bagging.setBarcodedItemToBeWeiged(TestUtility.normalItemBarcode2);
		
		hw.baggingArea.add(TestUtility.normalItem2);
		
		bagging.subtractWeight(TestUtility.normalItem.getWeight());
		
     	hw.baggingArea.remove(TestUtility.normalItem);
		
     	double actual = bagging.getCurWeightRegistered();
     	double expected = TestUtility.normalItem.getWeight()+TestUtility.normalItem2.getWeight()-TestUtility.normalItem.getWeight();

     	assertTrue("baggingArea weight is correct", actual == expected);
    }   
    
    @Test(expected = IllegalArgumentException.class)
    public void subtractWeightException() throws OverloadException {
    	
		bagging.setBarcodedItemToBeWeiged(TestUtility.normalItemBarcode);
		
		hw.baggingArea.add(TestUtility.normalItem);
		
		bagging.subtractWeight(30.0);
    	
    	bagging.attach(new BaggingAreaObserver() {

			@Override
			public void productWeightVerificationSuccesful() {
				actualFound++;
			}

			@Override
			public void productWeightVerificationFailure() {
				fail();
			}

			@Override
			public void addedProductInWrongPhase() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}

			@Override
			public void addingBagsInWrongPhase() {
				fail();
			}

			@Override
			public void placeBagsInBaggingArea() {
				fail();
				
			}


			@Override
			public void addBagVerificationSuccessful(boolean ownBag) {
				fail();
				
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
				
			}

			@Override
			public void overloadOfScale() {
				fail();
			}
    	});
    	
    	
    }   

    /*	1. PHASE = ADDING_OWN_BAG
     * 	2. User does not use their own bag 
     * 	3. addOwnBagVerificationFailure since nothing needs to be verified 
     * 
     * */
    @Test
    public void SoftwarePhaseDoesNotAddOwnBag() {
    	bagging.updatePhase(SoftwarePhase.ADDING_OWN_BAG);
    	
		bagging.setBarcodedItemToBeWeiged(TestUtility.normalItemBarcode);
		
		bagging.attach(new BaggingAreaObserver() {

			@Override
			public void productWeightVerificationSuccesful() {
				fail();
			}

			@Override
			public void productWeightVerificationFailure() {
				fail();	
			}

			@Override
			public void addedProductInWrongPhase() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}

			@Override
			public void addingBagsInWrongPhase() {
				fail();
			}

			@Override
			public void placeBagsInBaggingArea() {
				actualFound++;
			}

			@Override
			public void addBagVerificationSuccessful(boolean ownBag) {
				fail();
				
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				actualFound++;
				
			}

			@Override
			public void overloadOfScale() {
				fail();
			}
    	});
    	
		bagging.addBags(0,true);
     	hw.baggingArea.add(TestUtility.normalItem);
     	
		assertEquals(0, bagging.getExpectedNumBags());
		assertEquals(0.0, bagging.getEpectedBagWeight(),0.0001);
		
		assertEquals(2, actualFound);
    	
    }
    
    /*	1. PHASE = ADDING_OWN_BAG
     * 	2. User adds 10 bag of their own - to match the sensitivity of 10 
     * 	3. User add bag with a weight of 11
     *	4. addOwnBagVerificationSuccess 
     * */
    @Test
    public void SoftwarePhaseAddingOwnBag() {
    	bagging.updatePhase(SoftwarePhase.ADDING_OWN_BAG);
    	
		bagging.setBarcodedItemToBeWeiged(TestUtility.elevenBag);
		
		bagging.attach(new BaggingAreaObserver() {

			@Override
			public void productWeightVerificationSuccesful() {
				fail();
			}

			@Override
			public void productWeightVerificationFailure() {
				fail();	
			}

			@Override
			public void addedProductInWrongPhase() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}

			@Override
			public void addingBagsInWrongPhase() {
				fail();
			}

			@Override
			public void placeBagsInBaggingArea() {
				actualFound++;
			}

			@Override
			public void addBagVerificationSuccessful(boolean ownBag) {
				actualFound++;
				
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				actualFound++;
			}

			@Override
			public void overloadOfScale() {
				fail();
			}
    	});

		bagging.addBags(10,true);
     	hw.baggingArea.add(TestUtility.elevenBagItem);
     	
		assertEquals(10, bagging.getExpectedNumBags());
		assertEquals(20.0, bagging.getEpectedBagWeight(),0.0001);
		
		assertEquals(2, actualFound);
    	
    }
    
    @Test
    public void AddWrongNumberofBags() {
    	bagging.updatePhase(SoftwarePhase.ADDING_OWN_BAG);
    	
		bagging.setBarcodedItemToBeWeiged(TestUtility.elevenBag);
		
		bagging.attach(new BaggingAreaObserver() {

			@Override
			public void productWeightVerificationSuccesful() {
				fail();
			}

			@Override
			public void productWeightVerificationFailure() {
				fail();	
			}

			@Override
			public void addedProductInWrongPhase() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}

			@Override
			public void addingBagsInWrongPhase() {
				fail();
			}

			@Override
			public void placeBagsInBaggingArea() {
				actualFound++;
			}


			@Override
			public void addBagVerificationSuccessful(boolean ownBag) {
				fail();
				
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				actualFound++;	
				
			}

			@Override
			public void overloadOfScale() {
				fail();
			}
    	});

		bagging.addBags(5,true);
     	hw.baggingArea.add(TestUtility.elevenBagItem);
     	
		assertEquals(5, bagging.getExpectedNumBags());
		assertEquals(10.0, bagging.getEpectedBagWeight(),0.0001);
		
		assertEquals(2, actualFound);
    	
    }

    @Test
    public void AddBagWrongPHASE() {    	
		bagging.setBarcodedItemToBeWeiged(TestUtility.elevenBag);
		
		bagging.attach(new BaggingAreaObserver() {

			@Override
			public void productWeightVerificationSuccesful() {
				fail();
			}

			@Override
			public void productWeightVerificationFailure() {
				fail();	
			}

			@Override
			public void addedProductInWrongPhase() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}

			@Override
			public void addingBagsInWrongPhase() {
				actualFound++;
			}

			@Override
			public void placeBagsInBaggingArea() {
				actualFound++;
			}

			@Override
			public void addBagVerificationSuccessful(boolean ownBag) {
				fail();
				
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
				
			}

			@Override
			public void overloadOfScale() {
				fail();
			}
    	});

		bagging.addBags(10,true);
		
		assertEquals(0, bagging.getExpectedNumBags());
		assertEquals(0.0, bagging.getEpectedBagWeight(),0.0001);
		
		assertEquals(1, actualFound);
    	
    }
    
    @Test
    public void NullPLU() {
		
		bagging.attach(new BaggingAreaObserver() {

			@Override
			public void productWeightVerificationSuccesful() {
				fail();
			}

			@Override
			public void productWeightVerificationFailure() {
				fail();
			}

			@Override
			public void addedProductInWrongPhase() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}

			@Override
			public void addingBagsInWrongPhase() {
				fail();
			}

			@Override
			public void placeBagsInBaggingArea() {
				fail();
			}


			@Override
			public void addBagVerificationSuccessful(boolean ownBag) {
				fail();
				
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
			}

			@Override
			public void overloadOfScale() {
				fail();
			}
    	});

     	hw.baggingArea.add(TestUtility.normalItem2);
		
		assertEquals(0, actualFound);
    	
    }
    
    @Test
    public void overloadHappyPath() {
    	bagging.updatePhase(SoftwarePhase.ADDING_OWN_BAG);
		
		bagging.setBarcodedItemToBeWeiged(TestUtility.heavyItemBarcode);
    	
    	bagging.attach(new BaggingAreaObserver() {

			@Override
			public void productWeightVerificationSuccesful() {
				fail();
			}

			@Override
			public void productWeightVerificationFailure() {
				fail();
			}

			@Override
			public void addedProductInWrongPhase() {
				fail();
			}

			@Override
			public void allProductsRemoved() {
				fail();
			}

			@Override
			public void addingBagsInWrongPhase() {
				fail();
			}

			@Override
			public void placeBagsInBaggingArea() {
				fail();
			}

			@Override
			public void addBagVerificationSuccessful(boolean ownBag) {
				fail();
				
			}

			@Override
			public void addBagVerificationFailure(boolean ownBag) {
				fail();
			}

			@Override
			public void overloadOfScale() {
				actualFound++;
			}
    	});
    	
     	hw.baggingArea.add(TestUtility.heavyItem);
    	
    	assertEquals(1, actualFound);
    }   
	
}