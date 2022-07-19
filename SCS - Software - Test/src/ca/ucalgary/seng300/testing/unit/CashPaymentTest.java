package ca.ucalgary.seng300.testing.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import ca.ucalgary.seng300.selfcheckout.funds.CashPayment;
import ca.ucalgary.seng300.selfcheckout.funds.CashPaymentObserver;
import ca.ucalgary.seng300.selfcheckout.funds.Payment;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;
import ca.ucalgary.seng300.testing.utility.TestUtility;

@RunWith(JUnit4.class)
public class CashPaymentTest {

	private Payment payment;
	private SelfCheckoutStation hw;
	private int actualFound;
	private int actualFound2;
	
	@Before
	public void setup() throws OverloadException {
		
		// Hardware Setup
		hw = TestUtility.getHarwareInstance1();
		for(int i = 0; i < 200; i++)  hw.coinDispensers.get(TestUtility.oneDollar.getValue()).load(new Coin[]{TestUtility.oneDollar});
		for(int i = 0; i < hw.coinStorage.getCapacity() - 1; i++)  hw.coinStorage.load(new Coin[] {TestUtility.oneDollar});
		for(int i = 0; i  <  hw.banknoteStorage.getCapacity() - 1; i++) hw.banknoteStorage.load(TestUtility.twentyDollarBill);

		// Software Setup
		payment = new Payment(hw);
		
		hw.coinValidator.attach(payment.cash);
		hw.banknoteValidator.attach(payment.cash);
		
		payment.cash.setTotalDue(new BigDecimal(10.0));
		payment.updatePhase(SoftwarePhase.PAYING);
		
		actualFound = 0;	
		actualFound2 = 0;
	}
	
	/* CONSTRUCTOR */
	@Test(expected = NullPointerException.class)
	public void nullHardware() {
		new CashPayment(null);
	}
	
	/* INSERT COINS */
	
 	@Test
	public void registerCoinFundsSuccesfully() throws DisabledException, OverloadException {
			
		payment.cash.attach(new CashPaymentObserver() {

			@Override
			public void fundsRegistered(BigDecimal totalFunds) {
				BigDecimal expectedFunds = new BigDecimal(0.10);
				assertEquals(expectedFunds, totalFunds);
			}

			@Override
			public void enoughCashInserted() {
				fail();
				
			}

			@Override
			public void allChangeReturned() {
				fail();
				
			}

			@Override
			public void partialChangeReturned(BigDecimal credit) {
				fail();
			}

			@Override
			public void hardwareError() {
				fail();
			}
			@Override
			public void partialPayment() {
				actualFound++;
			}
		});
		
		while(actualFound != 1) {
			hw.coinSlot.accept(TestUtility.tenCents);
		}
		
		assertEquals(1, actualFound);
	}
	
	@Test (expected = DisabledException.class)
	public void registerCoinFundsWrongPhase() throws DisabledException, OverloadException {
		
		payment.updatePhase(SoftwarePhase.READY);
		
		payment.cash.attach(new CashPaymentObserver() {

			@Override
			public void enoughCashInserted() {
				fail();
				
			}

			@Override
			public void allChangeReturned() {
				fail();
				
			}

			@Override
			public void partialChangeReturned(BigDecimal credit) {
				fail();
				
			}

			@Override
			public void fundsRegistered(BigDecimal totalFunds) {
				fail();
			}
			
			@Override
			public void hardwareError() {
				fail();
			}
			@Override
			public void partialPayment() {
				fail();
			}
		});
				
		hw.coinSlot.accept(TestUtility.tenCents);
	}
	
	@Test 
	public void registerCoinFundsEnoughForDue() throws DisabledException, OverloadException {
		
		 payment.cash.attach(new CashPaymentObserver() {
 
		 	@Override
		 	public void enoughCashInserted() {
		 		actualFound++;				
		 	}

			@Override
			public void allChangeReturned() {
				fail();
				
			}

			@Override
			public void partialChangeReturned(BigDecimal credit) {
				fail();
			}

			@Override
			public void fundsRegistered(BigDecimal totalFunds) {
				actualFound2++;
				if(actualFound2 == 5) {
					BigDecimal expectedFunds = new BigDecimal(10.0);
		 			assertEquals("Funds registered incorrectly", expectedFunds, totalFunds);	
				}
			}
			
			@Override
			public void hardwareError() {
				fail();
			}

			@Override
			public void partialPayment() {
				// ignore parital payment here
			}
			
		 });
		 
		 while(actualFound2 < 5) {
			 hw.coinSlot.accept(TestUtility.twoDollars);
		 }
		 
		 assertEquals(5, actualFound2);
		 assertEquals(1, actualFound);
	}
	
	@Test
	public void registerCoinFundsDispenserFull() throws DisabledException, OverloadException {
		
		payment.cash.attach(new CashPaymentObserver() {

		 	@Override
		 	public void enoughCashInserted() {
		 		fail();			
		 	}

			@Override
			public void allChangeReturned() {
				fail();
				
			}

			@Override
			public void partialChangeReturned(BigDecimal credit) {
				fail();
			}

			@Override
			public void fundsRegistered(BigDecimal totalFunds) {
				BigDecimal expected = TestUtility.oneDollar.getValue();
				assertEquals("Funds were registered incorrectly", expected, totalFunds);
			}	
			
			@Override
			public void hardwareError() {
				fail();
			}
			@Override
			public void partialPayment() {
				actualFound++;
			}
		});
		
		while(actualFound != 1) {
			hw.coinSlot.accept(TestUtility.oneDollar);	
		}
		
		assertEquals(1, actualFound);
	}
	
	@Test (expected = OverloadException.class) 
	public void registerCoinFundsStorageFull() throws DisabledException, OverloadException {
		
		payment.cash.attach(new CashPaymentObserver() {

		 	@Override
		 	public void enoughCashInserted() {
		 		fail();			
		 	}

			@Override
			public void allChangeReturned() {
				fail();
			}

			@Override
			public void partialChangeReturned(BigDecimal credit) {
				fail();
			}

			@Override
			public void fundsRegistered(BigDecimal totalFunds) {
				actualFound2++;
			}	
			
			@Override
			public void hardwareError() {
				fail();
			}
			@Override
			public void partialPayment() {
				actualFound2++;
			}
		});
		
		while(actualFound2 != 1) {
			hw.coinSlot.accept(TestUtility.oneDollar);
		}
		
		while(actualFound != 2) {
			hw.coinSlot.accept(TestUtility.oneDollar);
		}
	}
	
	@Test
	public void returnAllCoinChange() throws DisabledException, InternalError, OverloadException, EmptyException {
		
	
		 payment.cash.attach(new CashPaymentObserver() {

		 	@Override
		 	public void enoughCashInserted() {
		 		fail();			
		 	}

			@Override
			public void allChangeReturned() {
				actualFound++;
			}

			@Override
			public void partialChangeReturned(BigDecimal credit) {
				fail();
			}

			@Override
			public void fundsRegistered(BigDecimal totalFunds) {
				actualFound2++;
			}	
			
			@Override
			public void hardwareError() {
				fail();
			}
			@Override
			public void partialPayment() {
				// ignore this here
			}
		});
		 
		while(actualFound2 != 4) {
			hw.coinSlot.accept(TestUtility.twoDollars);
		}
		 
		payment.cash.returnChange(new BigDecimal(3.0));
		 
		assertEquals("Observers were not notified of all change returned", 1, actualFound);		
		assertEquals("Total registered funds is non-zero", BigDecimal.ZERO, payment.cash.getTotalInsertedCash());			
	}
	
	@Test
	public void returnPartialCoinChange() throws OverloadException, InternalError, EmptyException, DisabledException {
		
		hw.coinDispensers.get(TestUtility.oneDollar.getValue()).unload();
		hw.coinDispensers.get(TestUtility.twoDollars.getValue()).load(new Coin[] {TestUtility.twoDollars});
		
		payment.cash.attach(new CashPaymentObserver() {

		 	@Override
		 	public void enoughCashInserted() {
		 		fail();			
		 	}
	
			@Override
			public void allChangeReturned() {
				fail();
			}
	
			@Override
			public void partialChangeReturned(BigDecimal credit) {
				assertEquals("Wrong credit registered", new BigDecimal(1.0), credit);
			}

			@Override
			public void fundsRegistered(BigDecimal totalFunds) {
				actualFound2++;
			}	
			
			@Override
			public void hardwareError() {
				fail();
			}
			@Override
			public void partialPayment() {
				// ignore this here
			}
		});
		 
		while(actualFound2 != 1) {
			try {
				hw.banknoteInput.accept(TestUtility.fiveDollarBill);
			}catch(Exception e) {
				hw.banknoteInput.removeDanglingBanknotes();
			}
		}
		
		payment.cash.returnChange(new BigDecimal(3.0));	
		assertEquals("Total registered funds is non-zero", BigDecimal.ZERO, payment.cash.getTotalInsertedCash());	
	}
	
	/* INSERT BANKNOTES */
	
	@Test
	public void registerBanknoteFundsSuccesfully() throws DisabledException, OverloadException {
		
		payment.cash.attach(new CashPaymentObserver() {

			@Override
			public void enoughCashInserted() {
				fail();
				
			}

			@Override
			public void allChangeReturned() {
				fail();
				
			}

			@Override
			public void partialChangeReturned(BigDecimal credit) {
				fail();
			}

			@Override
			public void fundsRegistered(BigDecimal totalFunds) {
				actualFound++;
				BigDecimal expectedFunds = new BigDecimal(5.0);
				assertEquals("Funds registered incorrectly", expectedFunds, totalFunds);	
			}
			
			@Override
			public void hardwareError() {
				fail();
			}
			
			@Override
			public void partialPayment() {
				actualFound++;
			}
			
		});
		
		while(actualFound != 2) {
			try {
				hw.banknoteInput.accept(TestUtility.fiveDollarBill);
			}catch(OverloadException e) {
				hw.banknoteInput.removeDanglingBanknotes();
			}
		}
			
		assertEquals(2, actualFound);
	}
	
	@Test (expected = DisabledException.class)
	public void registerBanknoteFundsWrongPhase() throws DisabledException, OverloadException {
		
		payment.updatePhase(SoftwarePhase.READY);
		
		payment.cash.attach(new CashPaymentObserver() {

			@Override
			public void enoughCashInserted() {
				fail();
			}

			@Override
			public void allChangeReturned() {
				fail();	
			}

			@Override
			public void partialChangeReturned(BigDecimal credit) {
				fail();
			}

			@Override
			public void fundsRegistered(BigDecimal totalFunds) {
				fail();
			}
			
			@Override
			public void hardwareError() {
				fail();
			}
			@Override
			public void partialPayment() {
				fail();
			}
		});
				
		hw.banknoteInput.accept(TestUtility.fiveDollarBill);
	}
	
	@Test
	public void registerBanknoteFundsEnoughForDue() throws DisabledException, OverloadException { 
	
		payment.cash.attach(new CashPaymentObserver() {

			@Override
			public void enoughCashInserted() {
				actualFound++;
			}

			@Override
			public void allChangeReturned() {
				fail();
			}

			@Override
			public void partialChangeReturned(BigDecimal credit) {
				fail();
			}

			@Override
			public void fundsRegistered(BigDecimal totalFunds) {	
				actualFound2++;
				BigDecimal expectedFunds = new BigDecimal(10.0);
				assertEquals("Funds registered incorrectly", expectedFunds, totalFunds);	
			}
			
			@Override
			public void hardwareError() {
				fail();
			}
			@Override
			public void partialPayment() {
				fail();
			}
		});
		
		while(actualFound2 != 1) {
			try {
				hw.banknoteInput.accept(TestUtility.tenDollarBill);
			}catch(Exception e) {
				hw.banknoteInput.removeDanglingBanknotes();
			}			
		}
		
		assertEquals(1, actualFound);
	}
	
	@Test
	public void registerBanknoteFundsStorageFull() throws DisabledException, OverloadException { 
		
		hw.banknoteInput.accept(TestUtility.twentyDollarBill);
		
		payment.cash.attach(new CashPaymentObserver() {

			@Override
			public void enoughCashInserted() {
				fail();
			}

			@Override
			public void allChangeReturned() {
				fail();	
			}

			@Override
			public void partialChangeReturned(BigDecimal credit) {
				fail();
			}

			@Override
			public void fundsRegistered(BigDecimal totalFunds) {
				fail();
			}
			
			@Override
			public void hardwareError() {
				fail();
			}
			@Override
			public void partialPayment() {
				fail();
			}
			
		});
		
		hw.banknoteInput.accept(TestUtility.twentyDollarBill);
	}

	@Test
	public void returnAllBanknoteChange() throws OverloadException, InternalError, EmptyException, DisabledException { 
		
		hw.banknoteDispensers.get(5).load(new Banknote[] {TestUtility.fiveDollarBill});
	
		payment.cash.attach(new CashPaymentObserver() {

		 	@Override
		 	public void enoughCashInserted() {
		 		// ignore in this case	
		 	}
	
			@Override
			public void allChangeReturned() {
				actualFound++;
				
			}
	
			@Override
			public void partialChangeReturned(BigDecimal credit) {
				fail();
			}

			@Override
			public void fundsRegistered(BigDecimal totalFunds) {
				actualFound2++;
				assertEquals(new BigDecimal(10.0), totalFunds);
			}	
			
			@Override
			public void hardwareError() {
				fail();
			}
			@Override
			public void partialPayment() {
				fail();
			}
		});
		
		while(actualFound2 != 1) {
			try {
				hw.banknoteInput.accept(TestUtility.tenDollarBill);	
			}catch(Exception e) {
				hw.banknoteInput.removeDanglingBanknotes();
			}
		}
		 
		payment.cash.returnChange(new BigDecimal(5.0));
		 
		assertEquals("Observers were not notified of all change returned", 1, actualFound);		
		assertEquals("Total registered funds is non-zero", BigDecimal.ZERO, payment.cash.getTotalInsertedCash());	
	}

	@Test
	public void returnPartialBanknoteChange() throws OverloadException, InternalError, EmptyException, DisabledException { 
		
		hw.coinDispensers.get(TestUtility.oneDollar.getValue()).unload();
		hw.banknoteDispensers.get(5).load(new Banknote[] {TestUtility.fiveDollarBill});
		
		payment.cash.attach(new CashPaymentObserver() {
 
		 	@Override
		 	public void enoughCashInserted() {
		 		// ignore in this case		
		 	}
	
			@Override
			public void allChangeReturned() {
				fail();
			}
	
			@Override
			public void partialChangeReturned(BigDecimal credit) {
				assertEquals("Wrong credit registered", new BigDecimal(5.0), credit);
			}

			@Override
			public void fundsRegistered(BigDecimal totalFunds) {
				actualFound2++;
				assertEquals(new BigDecimal(20.0),totalFunds);
			}	
			
			@Override
			public void hardwareError() {
				fail();
			}
			@Override
			public void partialPayment() {
				fail();
			}
		});
		 
		while(actualFound2 != 1) {
			try {
				hw.banknoteInput.accept(TestUtility.twentyDollarBill);
			}catch(Exception e) {
				hw.banknoteInput.removeDanglingBanknotes();
			}
		}
		
		payment.cash.returnChange(new BigDecimal(10.0));	
		assertEquals("Total registered funds is non-zero", BigDecimal.ZERO, payment.cash.getTotalInsertedCash());	
	}
	
	/* OTHERS */
	
	@Test (expected = NullPointerException.class)
	public void attachNullObserver() {
		payment.cash.attach(null);
	}
	
	@Test (expected = NullPointerException.class)
	public void returnNullChange() throws InternalError, OverloadException, EmptyException, DisabledException {
		payment.cash.returnChange(null);
	}
	
	@Test (expected = InternalError.class)
	public void returnNegativeChange() throws InternalError, OverloadException, EmptyException, DisabledException {
		payment.cash.returnChange(new BigDecimal(-1.0));
	}
	
	// No exception should occur
	@Test
	public void returnZeroChange() throws InternalError, OverloadException, EmptyException, DisabledException {
		payment.cash.returnChange(new BigDecimal(0.0));
	}
	
	@Test (expected = InternalError.class)
	public void returnChangeLargerThanEnteredFunds() throws InternalError, OverloadException, EmptyException, DisabledException {
	
		payment.cash.attach(new CashPaymentObserver() {

		 	@Override
		 	public void enoughCashInserted() {
		 		fail();			
		 	}
	
			@Override
			public void allChangeReturned() {
				fail();
			}
	
			@Override
			public void partialChangeReturned(BigDecimal credit) {
				fail();
			}

			@Override
			public void fundsRegistered(BigDecimal totalFunds) {
				fail();
			}	
			
			@Override
			public void hardwareError() {
				fail();
			}
			@Override
			public void partialPayment() {
				fail();	
			}
		});
		 
		payment.cash.returnChange(new BigDecimal(10.0));
	}
		
	@Test
	public void returnAllCombinedChange() throws  OverloadException, DisabledException, InternalError, EmptyException { // Banknotes and coins
		
		hw.banknoteDispensers.get(5).load(new Banknote[] {TestUtility.fiveDollarBill});
		
		payment.cash.attach(new CashPaymentObserver() {
 
		 	@Override
		 	public void enoughCashInserted() {
		 		// ignore this for now	
		 	}
	
			@Override
			public void allChangeReturned() {
				actualFound++;
			}
	
			@Override
			public void partialChangeReturned(BigDecimal credit) {
				fail();
			}

			@Override
			public void fundsRegistered(BigDecimal totalFunds) {
				actualFound2++;
			}	
			
			@Override
			public void hardwareError() {
				fail();
			}
			@Override
			public void partialPayment() {
				fail();	
			}
		});
		 
		while(actualFound2 != 1) {
			try {
				hw.banknoteInput.accept(TestUtility.twentyDollarBill);
			}catch(Exception e) {
				hw.banknoteInput.removeDanglingBanknotes();
			}
		}
		
		int expectedNumBanknotes = hw.banknoteDispensers.get(5).size() - 1;
		int expectedNumCoins = hw.coinDispensers.get(TestUtility.oneDollar.getValue()).size() - 5;
		
		payment.cash.returnChange(new BigDecimal(10.0));	
		
		int actualNumBanknotes = hw.banknoteDispensers.get(5).size();
		int actualNumCoins = hw.coinDispensers.get(TestUtility.oneDollar.getValue()).size();
		
		assertEquals("Observers were not notified of all change returned", 1, actualFound);	
		assertEquals("Total registered funds is non-zero", BigDecimal.ZERO, payment.cash.getTotalInsertedCash());	
		
		assertEquals("Five dollar banknote was supposed to be emitted", expectedNumBanknotes, actualNumBanknotes);
		assertEquals("Five one-dollar coins were supposed to be emitted", expectedNumCoins, actualNumCoins);
	}
	
	@Test
	public void returnAllUnusedFunds() throws OverloadException, DisabledException, InternalError, EmptyException {
		
		hw.banknoteDispensers.get(10).load(new Banknote[] {TestUtility.tenDollarBill});
		
		payment.cash.attach(new CashPaymentObserver() {

		 	@Override
		 	public void enoughCashInserted() {
		 		// ignore in this case		
		 	}
	
			@Override
			public void allChangeReturned() {
				actualFound++;
			}
	
			@Override
			public void partialChangeReturned(BigDecimal credit) {
				fail();
			}

			@Override
			public void fundsRegistered(BigDecimal totalFunds) {
				actualFound2++;
			}	
			
			@Override
			public void hardwareError() {
				fail();
			}
			@Override
			public void partialPayment() {
				fail();
			}
		});
		 
		while(actualFound2 != 1) {
			try {
				hw.banknoteInput.accept(TestUtility.tenDollarBill);
			}catch(Exception e) {
				hw.banknoteInput.removeDanglingBanknotes();
			}
		}
		
		while(actualFound2 != 2) {
			hw.coinSlot.accept(TestUtility.twoDollars);
		}
		
		int expectedNumBanknotes = hw.banknoteDispensers.get(10).size() - 1;
		int expectedNumCoins = hw.coinDispensers.get(TestUtility.twoDollars.getValue()).size() - 1;
		
		payment.cash.returnUnusedFunds();
		
		int actualNumBanknotes = hw.banknoteDispensers.get(10).size();
		int actualNumCoins = hw.coinDispensers.get(TestUtility.twoDollars.getValue()).size();
		
		assertEquals("Observers were not notified of all change returned", 1, actualFound);	
		assertEquals("Total registered funds is non-zero", BigDecimal.ZERO, payment.cash.getTotalInsertedCash());	
		
		assertEquals("Ten dollar banknote was supposed to be emitted", expectedNumBanknotes, actualNumBanknotes);
		assertEquals("One two-dollar coin was supposed to be emitted", expectedNumCoins, actualNumCoins);
	}
	
	@Test 
	public void returnAllUnusedFunds2() throws OverloadException, DisabledException, InternalError, EmptyException {
		
		hw.coinDispensers.get(TestUtility.oneDollar.getValue()).unload();
		hw.banknoteDispensers.get(10).load(new Banknote[] {TestUtility.tenDollarBill});
		
		payment.cash.attach(new CashPaymentObserver() {

		 	@Override
		 	public void enoughCashInserted() {
		 		// ignore in this case		
		 	}
	
			@Override
			public void allChangeReturned() {
				actualFound++;
			}
	
			@Override
			public void partialChangeReturned(BigDecimal credit) {
				fail();
			}

			@Override
			public void fundsRegistered(BigDecimal totalFunds) {
				actualFound2++;
			}	
			
			@Override
			public void hardwareError() {
				fail();
			}
			@Override
			public void partialPayment() {
				fail();
			}
		});
		 
		while(actualFound2 != 1) {
			try {
				hw.banknoteInput.accept(TestUtility.tenDollarBill);
			}catch(Exception e) {
				hw.banknoteInput.removeDanglingBanknotes();
			}
		}
		
		int expectedNumBanknotes = hw.banknoteDispensers.get(10).size() - 1;
		
		payment.cash.returnUnusedFunds();
		
		int actualNumBanknotes = hw.banknoteDispensers.get(10).size();
		
		assertEquals("Observers were not notified of all change returned", 1, actualFound);	
		assertEquals("Total registered funds is non-zero", BigDecimal.ZERO, payment.cash.getTotalInsertedCash());	
		
		assertEquals("Ten dollar banknote was supposed to be emitted", expectedNumBanknotes, actualNumBanknotes);
	}
	
	@Test
	public void returnPartialUnusedFunds() throws DisabledException, OverloadException, InternalError, EmptyException { // Banknote slot takes all bills to storage and there might not be enough bills for change in the dispenser.
		
		hw.coinDispensers.get(TestUtility.oneDollar.getValue()).unload();
				
		payment.cash.attach(new CashPaymentObserver() {

		 	@Override
		 	public void enoughCashInserted() {
		 		// ignore in this case	
		 	}
	
			@Override
			public void allChangeReturned() {
				fail();
			}
	
			@Override
			public void partialChangeReturned(BigDecimal credit) {
				assertEquals("Wrong credit registered", new BigDecimal(10.0), credit);
			}

			@Override
			public void fundsRegistered(BigDecimal totalFunds) {
				actualFound2++;
			}	
			
			@Override
			public void hardwareError() {
				fail();
			}
			@Override
			public void partialPayment() {
				fail();
			}
		});
		 
		while(actualFound2 != 1) {
			try {
				hw.banknoteInput.accept(TestUtility.tenDollarBill);
			}catch(Exception e) {
				hw.banknoteInput.removeDanglingBanknotes();
			}
		}
		
		while(actualFound2 != 2) {
			hw.coinSlot.accept(TestUtility.twoDollars);
		}
		
		int expectedNumBanknotes = 0;
		int expectedNumCoins = hw.coinDispensers.get(TestUtility.twoDollars.getValue()).size() - 1;
		
		payment.cash.returnUnusedFunds();
		
		int actualNumBanknotes = hw.banknoteDispensers.get(10).size();
		int actualNumCoins = hw.coinDispensers.get(TestUtility.twoDollars.getValue()).size();
		
		assertEquals("Total registered funds is non-zero", BigDecimal.ZERO, payment.cash.getTotalInsertedCash());	
		
		assertEquals("Zero banknote was supposed to be emitted", expectedNumBanknotes, actualNumBanknotes);
		assertEquals("One two-dollar coin was supposed to be emitted", expectedNumCoins, actualNumCoins);
	}
}