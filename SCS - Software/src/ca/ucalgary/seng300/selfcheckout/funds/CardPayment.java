package ca.ucalgary.seng300.selfcheckout.funds;

import java.math.BigDecimal;

import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.CardReaderObserver;
import org.lsmr.selfcheckout.external.CardIssuer;

import ca.ucalgary.seng300.selfcheckout.utility.Database;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;

/*
 * Class that handles payment with debit/credit card and Giftcards
 */
public class CardPayment extends AbstractPayment<CardPaymentObserver> implements CardReaderObserver {
	
	public CardPayment() {		
		totalDue = BigDecimal.ZERO;
		updatePhase(SoftwarePhase.READY);
	}
	
	public void payWithGiftcard(String cardNum) {
		
		// Software is in the paying phase
		if(curPhase == SoftwarePhase.PAYING){
					
			// Verify information with Card Issuer
			CardIssuer giftCardIssuer = Database.database().getCardIssuer(cardNum);
			int holdNum = giftCardIssuer.authorizeHold(cardNum, totalDue);
			boolean successful = giftCardIssuer.postTransaction(cardNum, holdNum, totalDue);
						
			if(successful) {
				notifyCardPaymentSuccesful();
				totalDue = BigDecimal.ZERO;
			}else {
				notifyCardPaymentUnssesful();
			}
		}else {
			 nofityCardPaymentInWrongPhase();
		}
	}
	
	// For debit and credit cards
	@Override
	public void cardDataRead(CardReader reader, CardData data) {
		
		// Software is in the paying phase
		if(curPhase == SoftwarePhase.PAYING){
					
			// Verify information with Card Issuer
			CardIssuer cardIssuer = Database.database().getCardIssuer(data.getNumber());
			
			int holdNum = cardIssuer.authorizeHold(data.getNumber(), totalDue);
			boolean succesful = cardIssuer.postTransaction(data.getNumber(), holdNum, totalDue);
					
			if(succesful) {
				notifyCardPaymentSuccesful();
				totalDue = BigDecimal.ZERO;
			}else {
				notifyCardPaymentUnssesful();
			}			
		}else {
			 nofityCardPaymentInWrongPhase();
		}
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
		for(CardPaymentObserver obs: observers) {
			obs.cardPaymentInWrongPhase();
		}
	}
	
	/* NOT IMPORTANT */
	
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// ignore
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// ignore
	}
	
	@Override
	public void cardInserted(CardReader reader) {
		// ignore
	}

	@Override
	public void cardRemoved(CardReader reader) {
		// ignore
	}
	
	@Override
	public void cardTapped(CardReader reader) {
		// ignore
	}

	@Override
	public void cardSwiped(CardReader reader) {
		// ignore
	}
}
