package ca.ucalgary.seng300.testing.unit;

import static org.junit.Assert.*;
import java.math.BigDecimal;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import ca.ucalgary.seng300.selfcheckout.product.Checkout;
import ca.ucalgary.seng300.selfcheckout.product.CheckoutObserver;
import ca.ucalgary.seng300.selfcheckout.utility.Cart;
import ca.ucalgary.seng300.selfcheckout.utility.Database;
import ca.ucalgary.seng300.selfcheckout.utility.IllegalActionException;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;
import ca.ucalgary.seng300.testing.utility.TestUtility;

@RunWith(JUnit4.class)
public class CartTest {

	private SelfCheckoutStation hw;
	private Cart cart;
	private Checkout testObject;
	private int actualFound;
    
	@Before
	public void setUp() throws Exception {
		hw = TestUtility.getHarwareInstance1();
		cart = new Cart(0);
		testObject = new Checkout(hw);
		actualFound = 0;
		testObject.updatePhase(SoftwarePhase.PAYING);
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void addPLUItemCode() throws NullPointerException, IllegalArgumentException, IllegalActionException {
		
		testObject.attach(new CheckoutObserver() {
			
			@Override
			public void totalCalculatedSuccesfully() {
				actualFound++;
			}
			
			@Override
			public void receiptPrintedSuccesfully() {
				fail();
			}
			
			@Override
			public void InWrongPhase() {
				fail();
			}

			@Override
			public void printerError() {
				fail();
			}

			@Override
			public void printerLowOnInk() {
				fail();
				
			}

			@Override
			public void printerLowOnPaper() {
				fail();
				
			}
		});
		
		
		cart.addPLUItemToCart(TestUtility.normalPLUCode,Database.database().readProductWithPLU(TestUtility.normalPLUCode).getPrice(), 10.00);
		assertTrue("PLU item was property added", cart.getPLUProducts().containsKey(Database.database().readProductWithPLU(TestUtility.normalPLUCode).getPLUCode()));
		testObject.calculateTotal(cart, new BigDecimal(0.05));
		assertEquals(actualFound, 1);
	}
	
	@Test
	public void addPLUItemTwice() throws NullPointerException, IllegalArgumentException, IllegalActionException {
		
		testObject.attach(new CheckoutObserver() {
			
			@Override
			public void totalCalculatedSuccesfully() {
				actualFound++;
			}
			
			@Override
			public void receiptPrintedSuccesfully() {
				fail();
			}
			
			@Override
			public void InWrongPhase() {
				fail();
			}

			@Override
			public void printerError() {
				fail();
			}

			@Override
			public void printerLowOnInk() {
				fail();
				
			}

			@Override
			public void printerLowOnPaper() {
				fail();
				
			}
		});
		
		
		cart.addPLUItemToCart(TestUtility.normalPLUCode,Database.database().readProductWithPLU(TestUtility.normalPLUCode).getPrice(), 10.00);
		cart.addPLUItemToCart(TestUtility.normalPLUCode,Database.database().readProductWithPLU(TestUtility.normalPLUCode).getPrice(), 10.00);
		assertTrue("PLU item was property added", cart.getPLUProducts().containsKey(Database.database().readProductWithPLU(TestUtility.normalPLUCode).getPLUCode()));
		assertEquals("The weight of added PLU items is correct", 20.00, cart.getWeightOfPLUProduct(TestUtility.normalPLUCode),0.1);
		testObject.calculateTotal(cart, new BigDecimal(0.05));
		assertEquals(actualFound, 1);
	}

	@Test
	public void addBarcodeItemTwice() throws NullPointerException, IllegalArgumentException, IllegalActionException {
		testObject.attach(new CheckoutObserver() {
					
			@Override
			public void totalCalculatedSuccesfully() {
				actualFound++;
			}
					
			@Override
			public void receiptPrintedSuccesfully() {
				fail();
			}
				
			@Override
			public void InWrongPhase() {
				fail();
			}
	
			@Override
			public void printerError() {
				fail();
			}
		
			@Override
			public void printerLowOnInk() {
				fail();
			}
			
			@Override
			public void printerLowOnPaper() {
				fail();
			}
		});
		cart.addBarcodedItemToCart(TestUtility.normalItemBarcode, Database.database().readProductWithBarcode(TestUtility.normalItemBarcode).getPrice());
		cart.addBarcodedItemToCart(TestUtility.normalItemBarcode, Database.database().readProductWithBarcode(TestUtility.normalItemBarcode).getPrice());
		assertTrue("Barcode item was property added", cart.getBarcodedProducts().containsKey(Database.database().readProductWithBarcode(TestUtility.normalItemBarcode).getBarcode()));
		testObject.calculateTotal(cart, new BigDecimal(0.05));
		assertEquals(actualFound, 1);
	}
	
	@Test
	public void addBags() throws NullPointerException, IllegalArgumentException, IllegalActionException {
		testObject.attach(new CheckoutObserver() {
			
			@Override
			public void totalCalculatedSuccesfully() {
				actualFound++;
			}
			
			@Override
			public void receiptPrintedSuccesfully() {
				fail();
			}
			
			@Override
			public void InWrongPhase() {
				fail();
			}

			@Override
			public void printerError() {
				fail();
			}

			@Override
			public void printerLowOnInk() {
				fail();
			}

			@Override
			public void printerLowOnPaper() {
				fail();
			}
		});
		cart.addBarcodedItemToCart(TestUtility.normalItemBarcode, Database.database().readProductWithBarcode(TestUtility.normalItemBarcode).getPrice());
		cart.addBags(2, false);
		assertEquals("Bags are added", 2, cart.getNumBags());
		testObject.calculateTotal(cart, new BigDecimal(0.05));
		assertEquals(1, actualFound);
	}
	
	@Test
	public void addOwnBags() throws NullPointerException, IllegalArgumentException, IllegalActionException {
		testObject.attach(new CheckoutObserver() {
			
			@Override
			public void totalCalculatedSuccesfully() {
				actualFound++;
			}
			
			@Override
			public void receiptPrintedSuccesfully() {
				fail();
			}
			
			@Override
			public void InWrongPhase() {
				fail();
			}

			@Override
			public void printerError() {
				fail();
			}

			@Override
			public void printerLowOnInk() {
				fail();
			}

			@Override
			public void printerLowOnPaper() {
				fail();
			}
		});
		cart.addBarcodedItemToCart(TestUtility.normalItemBarcode, Database.database().readProductWithBarcode(TestUtility.normalItemBarcode).getPrice());
		cart.addBags(2, true);
		assertEquals("Bags are added", 2, cart.getNumBags());
		testObject.calculateTotal(cart, new BigDecimal(0.05));
		assertEquals(1, actualFound);
	}
}
