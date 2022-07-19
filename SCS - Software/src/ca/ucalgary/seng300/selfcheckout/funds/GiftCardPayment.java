package ca.ucalgary.seng300.selfcheckout.funds;

import org.lsmr.selfcheckout.devices.observers.CardReaderObserver;
import java.math.BigDecimal;
import org.lsmr.selfcheckout.external.*;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;
/*
 * Class that handled payment with gift cards
 * Main issuses:
 * 		Not sure if user scans gift card, or enters gift card code? Maybe both?
 * 		If entering a code, is it the same way as entering a PLU Code? I assumed so
 */

public class GiftCardPayment extends AbstractPayment<CardPaymentObserver> implements CardReaderObserver {
	
	public GiftCardPayment() {
		totalDue = BigDecimal.ZERO;
		updatePhase(SoftwarePhase.READY);
	}
	
	// Similar to CardDataRead in CardPayment, minor changes
	public void giftCardDataRead(CardIssuer CardIssuer, String cardNumber, BigDecimal CardAmount) {
		
		// software is in the paying phase
		if (curPhase == SoftwarePhase.PAYING) {
					
		// verify info with card issuer? Unblock card?
			if (CardIssuer.block(cardNumber) == true) {
				
				CardIssuer.unblock(cardNumber);
				// if card has more money than price of goods...
				// Come back to this later???
				if (CardAmount.compareTo(totalDue) >= 0) {
					
					notifyCardPaymentSuccesful();
					CardAmount = CardAmount.subtract(totalDue);
					
					if (CardAmount.compareTo(CardAmount) <= 0) {
						
						CardAmount = BigDecimal.ZERO;
					}
				}
				
				else if (CardAmount.compareTo(totalDue) < 0) {
					
					notifyCardPaymentSuccesful();
					CardAmount = CardAmount.subtract(totalDue);
							
					if (CardAmount.compareTo(CardAmount) <= 0) {
						
						CardAmount = BigDecimal.ZERO;
					}
							
				}
						
			}
			else {
				notifyCardPaymentUnssesful();
			}
		}
		else {
			nofityCardPaymentInWrongPhase();
		}
	}
	
	public void pay (GiftCardCode code) {
		// The same as above???
	}
	
	/* OBSERVERS */
	private void notifyCardPaymentSuccesful() {
		for(CardPaymentObserver obs: observers) {
			obs.cardPaymentSuccesful();
		}
		
	}
	private void notifyCardPaymentUnssesful() {
		for(CardPaymentObserver obs: observers) {
			obs.cardPaymentUnssesful();
		}
		
	}
	
	private void nofityCardPaymentInWrongPhase() {
		for (CardPaymentObserver obs: observers) {
			obs.cardPaymentInWrongPhase();
		}
		
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cardInserted(CardReader reader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cardRemoved(CardReader reader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cardTapped(CardReader reader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cardSwiped(CardReader reader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cardDataRead(CardReader reader, CardData data) {
		// TODO Auto-generated method stub
		
	}

}
