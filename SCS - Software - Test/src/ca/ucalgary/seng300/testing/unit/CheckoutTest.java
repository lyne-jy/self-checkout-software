package ca.ucalgary.seng300.testing.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import ca.ucalgary.seng300.selfcheckout.product.Checkout;
import ca.ucalgary.seng300.selfcheckout.product.CheckoutObserver;
import ca.ucalgary.seng300.selfcheckout.utility.Cart;
import ca.ucalgary.seng300.selfcheckout.utility.Database;
import ca.ucalgary.seng300.selfcheckout.utility.IllegalActionException;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;
import ca.ucalgary.seng300.testing.utility.TestUtility;

@RunWith(JUnit4.class)
public class CheckoutTest {
	
	private SelfCheckoutStation hardware;
	private Checkout testObject;
	private Cart cart;
	private int actualFound;

	@Before
	public void setup() {
		hardware = TestUtility.getHarwareInstance1();
		cart = new Cart(0);
		testObject = new Checkout(hardware);
		actualFound = 0;
		testObject.updatePhase(SoftwarePhase.PAYING);
	}
	
	/* CONSTRUCTOR */
	@Test(expected = NullPointerException.class)
	public void nullHardware() {
		new Checkout(null);
	}
	
	/* COMPUTING TOTAL */
	
	@Test(expected = NullPointerException.class)
	public void calculateTotalNullCart() throws NullPointerException, IllegalArgumentException, IllegalActionException {
		testObject.calculateTotal(null, BigDecimal.ZERO);
	}
	
	@Test
	public void calculateTotalEmptyCart() throws NullPointerException, IllegalArgumentException, IllegalActionException {
		assertEquals(new BigDecimal(0.0), testObject.calculateTotal(cart, new BigDecimal(0.05)));
	}
		
	@Test
	public void calculateTotalSuccesfully() throws NullPointerException, IllegalArgumentException, IllegalActionException {
		
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
		testObject.calculateTotal(cart, new BigDecimal(0.05));
		assertEquals(actualFound, 1);
	}
	
	@Test
	public void calculateTotalWrongPhase() throws NullPointerException, IllegalArgumentException, IllegalActionException {
		
		testObject.updatePhase(SoftwarePhase.READY);
		
		testObject.attach(new CheckoutObserver() {

			@Override
			public void totalCalculatedSuccesfully() {
				fail();
				
			}

			@Override
			public void receiptPrintedSuccesfully() {
				fail();
			}

			@Override
			public void InWrongPhase() {
				actualFound++;
			}

			@Override
			public void printerLowOnInk() {
				fail();
			}

			@Override
			public void printerLowOnPaper() {
				fail();
			}

			@Override
			public void printerError() {
				fail();
			}
			
		});
		
		
		
		cart.addBarcodedItemToCart(TestUtility.normalItemBarcode, Database.database().readProductWithBarcode(TestUtility.normalItemBarcode).getPrice());
		testObject.calculateTotal(cart, new BigDecimal(0.05));
		assertEquals(1, actualFound);
	}

	/* COMPUTING TO PAY */
	
	@Test(expected = NullPointerException.class)
	public void calculateToPayNullCart() throws NullPointerException, IllegalArgumentException, IllegalActionException {
		testObject.calculateToPay(null, BigDecimal.ZERO);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void calculateToPayEmptyCart() throws NullPointerException, IllegalArgumentException, IllegalActionException {
		testObject.calculateToPay(cart, BigDecimal.ZERO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void calculateToPayEmptyCartOnlyBags() throws NullPointerException, IllegalArgumentException, IllegalActionException {
		cart.addBags(3, false);
		testObject.calculateToPay(cart, BigDecimal.ZERO);
	}
		
	@Test
	public void calculateToPaySuccesfully() throws NullPointerException, IllegalArgumentException, IllegalActionException {
		
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
		
		cart.addBags(10, false);
		cart.addBarcodedItemToCart(TestUtility.normalItemBarcode, Database.database().readProductWithBarcode(TestUtility.normalItemBarcode).getPrice());
		testObject.calculateToPay(cart, new BigDecimal(0.05));
		assertEquals(actualFound, 1);
	}
	
	@Test
	public void calculateToPayWrongPhase() throws NullPointerException, IllegalArgumentException, IllegalActionException {
			
		testObject.attach(new CheckoutObserver() {

			@Override
			public void totalCalculatedSuccesfully() {
				fail();
			}

			@Override
			public void receiptPrintedSuccesfully() {
				fail();
			}

			@Override
			public void InWrongPhase() {
				actualFound++;
			}

			@Override
			public void printerLowOnInk() {
				fail();
			}

			@Override
			public void printerLowOnPaper() {
				fail();
			}

			@Override
			public void printerError() {
				fail();
			}
		});
		
		testObject.updatePhase(SoftwarePhase.READY);
		
		cart.addBarcodedItemToCart(TestUtility.normalItemBarcode, Database.database().readProductWithBarcode(TestUtility.normalItemBarcode).getPrice());
		testObject.calculateToPay(cart, new BigDecimal(0.05));
		assertEquals(1, actualFound);
	}
		
	/* PRINT RECEIPT */
	
	@Test (expected = NullPointerException.class)
	public void printReceiptNullCart() {
		testObject.printReceipt(null, null);
	}
	
	@Test 
	public void printReceiptEmptyCart() {
		
		testObject.attach(new CheckoutObserver() {

			@Override
			public void totalCalculatedSuccesfully() {
				fail();
				
			}

			@Override
			public void receiptPrintedSuccesfully() {
				actualFound++;
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
		
		testObject.printReceipt(cart, null);
		
		assertEquals(1, actualFound);
	}
	
	@Test
	public void printReceiptSuccesfully() throws OverloadException {
		
		hardware.printer.addInk(1024);
		hardware.printer.addPaper(1024);
	
		testObject.attach(new CheckoutObserver() {
			
			@Override
			public void totalCalculatedSuccesfully() {
				fail();
			}

			@Override
			public void receiptPrintedSuccesfully() {
				actualFound++;
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
				// ignore in this case
			}

			@Override
			public void printerLowOnPaper() {
				// ignore in this case
			}
			
		});
		
		cart.addBarcodedItemToCart(TestUtility.normalItemBarcode, Database.database().readProductWithBarcode(TestUtility.normalItemBarcode).getPrice());
		cart.addPLUItemToCart(TestUtility.plu1, Database.database().readProductWithPLU(TestUtility.plu1).getPrice(), 1.0);
		
		testObject.printReceipt(cart, null);
		assertEquals(1, actualFound);
	}
	
	@Test
	public void printReceiptSuccesfullyWithMembership() throws OverloadException {
		
		hardware.printer.addInk(2000);
		hardware.printer.addPaper(1024);
	
		testObject.attach(new CheckoutObserver() {
			
			@Override
			public void totalCalculatedSuccesfully() {
				fail();
				
			}

			@Override
			public void receiptPrintedSuccesfully() {
				actualFound++;
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
				// ignore in this case
			}

			@Override
			public void printerLowOnPaper() {
				// ignore in this case
			}
			
		});
		
		cart.addBarcodedItemToCart(TestUtility.normalItemBarcode, Database.database().readProductWithBarcode(TestUtility.normalItemBarcode).getPrice());
		
		testObject.printReceipt(cart, TestUtility.existingMembershipBarcode);
		assertEquals(1, actualFound);
	}
	
	@Test
	public void printReceiptNotEnoughInk() throws OverloadException {
		
		hardware.printer.addPaper(20);
		testObject.loadedPaperInPrinter(20);
	
		testObject.attach(new CheckoutObserver() {

			@Override
			public void totalCalculatedSuccesfully() {
				fail();
				
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
				actualFound++;
			}

			@Override
			public void printerLowOnInk() {
				// ignore in this case
				
			}

			@Override
			public void printerLowOnPaper() {
				// ignore in this case
			}
			
		});
		
		cart.addBarcodedItemToCart(TestUtility.normalItemBarcode, Database.database().readProductWithBarcode(TestUtility.normalItemBarcode).getPrice());
		testObject.printReceipt(cart, null);
		Assert.assertEquals(1, actualFound);
	}
	
	@Test
	public void printReceiptNotEnoughPaper() throws OverloadException {
		
		hardware.printer.addInk(1024);
		
		testObject.attach(new CheckoutObserver() {

			@Override
			public void totalCalculatedSuccesfully() {
				fail();
				
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
				actualFound++;
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
		testObject.printReceipt(cart, null);
		Assert.assertEquals(1, actualFound);
	}
	
	@Test
	public void printReceiptWrongPhase() throws OverloadException {
		
		hardware.printer.addInk(1024);
		hardware.printer.addPaper(1024);

		testObject.attach(new CheckoutObserver() {

			@Override
			public void totalCalculatedSuccesfully() {
				fail();
			}

			@Override
			public void receiptPrintedSuccesfully() {
				fail();
			}

			@Override
			public void InWrongPhase() {
				actualFound++;
			}

			@Override
			public void printerLowOnInk() {
				fail();
			}

			@Override
			public void printerLowOnPaper() {
				fail();
				
			}

			@Override
			public void printerError() {
				fail();
			}
			
		});
		
		testObject.updatePhase(SoftwarePhase.CHECKOUT);
		
		cart.addBarcodedItemToCart(TestUtility.normalItemBarcode, Database.database().readProductWithBarcode(TestUtility.normalItemBarcode).getPrice());
		testObject.printReceipt(cart, null);
		assertEquals(1, actualFound);
	}
	
	@Test (expected = NullPointerException.class)
	public void attachNullObserver() {
		testObject.attach(null);
	}
}