package ca.ucalgary.seng300.selfcheckout.funds;

import java.math.BigDecimal;

import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import ca.ucalgary.seng300.selfcheckout.utility.ComplexAbstractSoftware;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;

/*
 * Payment facades that groups all forms of payment
 */
public class Payment extends ComplexAbstractSoftware<PaymentObserver> implements CardPaymentObserver, CashPaymentObserver{

	public final CashPayment cash;
	public final CardPayment card;
	
	public Payment(SelfCheckoutStation scs){
		
		if(scs == null) throw new NullPointerException();
		
		this.cash = new CashPayment(scs);
		this.card = new CardPayment();
		
		cash.attach(this);
		card.attach(this);
		
		updatePhase(SoftwarePhase.READY);
	}
		
	@Override
	public void updatePhase(SoftwarePhase newPhase) {
		this.curPhase = newPhase;
		cash.updatePhase(newPhase);
		card.updatePhase(newPhase);
	}

	@Override
	public void fundsRegistered(BigDecimal totalAmount) {
			
		for(PaymentObserver obs: observers) {
			obs.fundsRegistered(totalAmount);
		}
	}

	@Override
	public void enoughCashInserted() {
		for(PaymentObserver obs: observers) {
			obs.enoughCashInserted();
		}
	}

	@Override
	public void allChangeReturned() {
		for(PaymentObserver obs: observers) {
			obs.allChangeReturned();
		}
	}

	@Override
	public void partialChangeReturned(BigDecimal credit) {
		for(PaymentObserver obs: observers) {
			obs.partialChangeReturned(credit);
		}
	}

	@Override
	public void cardPaymentSuccesful() {
		for(PaymentObserver obs: observers) {
			obs.cardPaymentSuccesful();
		}
	}

	@Override
	public void cardPaymentUnssesful() {
		for(PaymentObserver obs: observers) {
			obs.cardPaymentUnssesful();
		}
	}

	@Override
	public void cardPaymentInWrongPhase() {
		for(PaymentObserver obs: observers) {
			obs.cardPaymentInWrongPhase();
		}
	}

	@Override
	public void hardwareError() {
		for(PaymentObserver obs: observers) {
			obs.hardwareError();
		}
	}

	@Override
	public void partialPayment() {
		for(PaymentObserver obs: observers) {
			obs.partialPayment();
		}
		
	}
}
