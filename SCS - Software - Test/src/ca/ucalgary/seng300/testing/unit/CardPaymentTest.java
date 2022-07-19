package ca.ucalgary.seng300.testing.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import ca.ucalgary.seng300.selfcheckout.funds.CardPaymentObserver;
import ca.ucalgary.seng300.selfcheckout.funds.Payment;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;
import ca.ucalgary.seng300.testing.utility.TestUtility;

@RunWith(JUnit4.class)
public class CardPaymentTest {

	Payment payment;
	SelfCheckoutStation hw;
	private int actualFound;
	
	@Before
	public void setup() throws OverloadException {
		
		// Hardware Setup
		hw = TestUtility.getHarwareInstance1();
		for(int i = 0; i < 200; i++)  hw.coinDispensers.get(TestUtility.oneDollar.getValue()).load(new Coin[]{TestUtility.oneDollar});
		for(int i = 0; i < hw.coinStorage.getCapacity() - 1; i++)  hw.coinStorage.load(new Coin[] {TestUtility.oneDollar});
		for(int i = 0; i  <  hw.banknoteStorage.getCapacity() - 1; i++) hw.banknoteStorage.load(TestUtility.twentyDollarBill);

		// Software Setup
		payment = new Payment(hw);
		
		hw.cardReader.attach(payment.card);
		
		payment.card.setTotalDue(new BigDecimal(100.0));
		payment.updatePhase(SoftwarePhase.PAYING);
				
		actualFound = 0;	
	}
	
	
	/* CONSTRUCTOR */
	@Test(expected = NullPointerException.class)
	public void nullHardware() {
		new Payment(null);
	}
	
	/* OTHERS */
	
	@Test
	public void payInWrongPhaseGiftCard() {
		
		payment.updatePhase(SoftwarePhase.READY);
		
		payment.card.attach(new CardPaymentObserver() {

			@Override
			public void cardPaymentSuccesful() {
				fail();
				
			}

			@Override
			public void cardPaymentUnssesful() {
				fail();
			}

			@Override
			public void cardPaymentInWrongPhase() {
				actualFound++;
			}
		});
		
		
		payment.card.payWithGiftcard("123");
		
		assertEquals(1, actualFound);
	}
	
	@Test
	public void successfulPayGiftCard() {
				
		payment.card.attach(new CardPaymentObserver() {

			@Override
			public void cardPaymentSuccesful() {
				actualFound++;
			}

			@Override
			public void cardPaymentUnssesful() {
				fail();
			}

			@Override
			public void cardPaymentInWrongPhase() {
				fail();
			}
		});
		
		payment.card.payWithGiftcard(TestUtility.giftCard1);
		
		assertEquals(1, actualFound);
		assertEquals("Total due was not updated", new BigDecimal(0.0), payment.card.getTotalDue());
	}

	@Test
	public void nonSuccessfulPayGiftCard() {
		
		payment.card.attach(new CardPaymentObserver() {

			@Override
			public void cardPaymentSuccesful() {
				fail();
			}

			@Override
			public void cardPaymentUnssesful() {
				actualFound++;
			}

			@Override
			public void cardPaymentInWrongPhase() {
				fail();
			}
		});
		
		payment.card.payWithGiftcard(TestUtility.giftCard2);
		
		assertEquals(1, actualFound);
	}
	
	@Test
	public void payInWrongPhaseCardInserted() throws IOException {
		
		payment.updatePhase(SoftwarePhase.READY);
		
		payment.card.attach(new CardPaymentObserver() {

			@Override
			public void cardPaymentSuccesful() {
				fail();
				
			}

			@Override
			public void cardPaymentUnssesful() {
				fail();
			}

			@Override
			public void cardPaymentInWrongPhase() {
				actualFound++;
			}
		});
		
		while(true) {
			try {		
				hw.cardReader.insert(TestUtility.card1, "1234");
				break;
			}catch(Exception e) {}
		}
			
		assertEquals(1, actualFound);
	}
	
	@Test
	public void payInWrongPhaseCardSwiped() throws IOException {
		
		payment.updatePhase(SoftwarePhase.READY);
		
		payment.card.attach(new CardPaymentObserver() {

			@Override
			public void cardPaymentSuccesful() {
				fail();
				
			}

			@Override
			public void cardPaymentUnssesful() {
				fail();
			}

			@Override
			public void cardPaymentInWrongPhase() {
				actualFound++;
			}
		});
		
		while(true) {
			try {		
				hw.cardReader.swipe(TestUtility.card2);
				break;
			}catch(Exception e) {}
		}
		
		assertEquals("Observer not notified of attempted pay in wrong software phase", 1, actualFound);
	}
	
	@Test
	public void payInWrongPhaseCardTapped() throws IOException {
		
		payment.updatePhase(SoftwarePhase.READY);
		
		payment.card.attach(new CardPaymentObserver() {

			@Override
			public void cardPaymentSuccesful() {
				fail();
				
			}

			@Override
			public void cardPaymentUnssesful() {
				fail();
			}

			@Override
			public void cardPaymentInWrongPhase() {
				actualFound++;
			}
		});
		
		while(true) {
			try {
				hw.cardReader.tap(TestUtility.card2);
				break;
			}catch(Exception e) {}
		}
		
		assertEquals("Observer not notified of attempted pay in wrong software phase", 1, actualFound);
	}
	
	@Test
	public void succesfulPayCardInserted() throws IOException {
				
		payment.card.attach(new CardPaymentObserver() {

			@Override
			public void cardPaymentSuccesful() {
				actualFound++;
			}

			@Override
			public void cardPaymentUnssesful() {
				fail();
			}

			@Override
			public void cardPaymentInWrongPhase() {
				fail();
			}
		});
				
		while(actualFound != 1) {
			try {
				hw.cardReader.insert(TestUtility.card1, "1234");
			}catch(Exception e) {}
		}
		
		assertEquals(1, actualFound);
		assertEquals("Total due was not updated", new BigDecimal(0.0), payment.card.getTotalDue());
	}
	
	@Test
	public void succesfulPayCardTapped() throws IOException {
			
		payment.card.attach(new CardPaymentObserver() {

			@Override
			public void cardPaymentSuccesful() {
				actualFound++;
				
			}

			@Override
			public void cardPaymentUnssesful() {
				fail();
			}

			@Override
			public void cardPaymentInWrongPhase() {
				fail();
			}
		});
		
		while(true) {
			try {
				hw.cardReader.tap(TestUtility.card2);
				break;
			}catch(Exception e) {}
		}
	
		assertEquals(1, actualFound);
		assertEquals("Total due was not updated", new BigDecimal(0.0), payment.card.getTotalDue());
	}
	
	@Test
	public void succesfulPayCardSwiped() throws IOException {
			
		payment.card.attach(new CardPaymentObserver() {

			@Override
			public void cardPaymentSuccesful() {
				actualFound++;
			}

			@Override
			public void cardPaymentUnssesful() {
				fail();
			}

			@Override
			public void cardPaymentInWrongPhase() {
				fail();
			}
		});
		
		while(true) {
			try {
				hw.cardReader.swipe(TestUtility.card2);
				break;
			}catch(Exception e) {}
		}
			
		assertEquals(1, actualFound);
		assertEquals("Total due was not updated", new BigDecimal(0.0), payment.card.getTotalDue());
	}
	
	@Test
	public void nonSuccesfulPayCardInserted() throws IOException {
				
		payment.card.attach(new CardPaymentObserver() {

			@Override
			public void cardPaymentSuccesful() {
				fail();
			}

			@Override
			public void cardPaymentUnssesful() {
				actualFound++;
			}

			@Override
			public void cardPaymentInWrongPhase() {
				fail();
			}
		});
		
		while(true) {
			try {
				hw.cardReader.insert(TestUtility.card3, "1234");
				break;
			}catch(Exception e) {}
		}
		
		assertEquals(1, actualFound);
	}
	
	@Test
	public void nonSuccesfulPayCardTap() throws IOException {
		
		payment.card.attach(new CardPaymentObserver() {

			@Override
			public void cardPaymentSuccesful() {
				fail();
			}

			@Override
			public void cardPaymentUnssesful() {
				actualFound++;
			}

			@Override
			public void cardPaymentInWrongPhase() {
				fail();
			}
		});
		
		while(true) {
			try {
				hw.cardReader.tap(TestUtility.card4);
				break;
			}catch(Exception e) {}
		}
		
		assertEquals(1, actualFound);
	}
	
	@Test
	public void nonSuccesfulPayCardSwipe() throws IOException {
					
		payment.card.attach(new CardPaymentObserver() {

			@Override
			public void cardPaymentSuccesful() {
				fail();
			}

			@Override
			public void cardPaymentUnssesful() {
				actualFound++;
			}

			@Override
			public void cardPaymentInWrongPhase() {
				fail();
			}
		});
		
		while(true) {
			try {
				hw.cardReader.swipe(TestUtility.card4);
				break;
			}catch(Exception e) {}
		}
		
		assertEquals(1, actualFound);
	}	
}
