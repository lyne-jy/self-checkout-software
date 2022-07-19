package ca.ucalgary.seng300.selfcheckout.funds;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteValidator;
import org.lsmr.selfcheckout.devices.CoinValidator;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.BanknoteValidatorObserver;
import org.lsmr.selfcheckout.devices.observers.CoinValidatorObserver;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;
/*
 * Class that handles all cash payments coins + banknotes
 */
public class CashPayment extends AbstractPayment<CashPaymentObserver> implements CoinValidatorObserver, BanknoteValidatorObserver {

	// Fields
	private SelfCheckoutStation hardware;
	private BigDecimal cashInserted;
	
	private ArrayList<BigDecimal> coinDenoms;
	private ArrayList<Integer> banknoteDenoms;


	public CashPayment(SelfCheckoutStation scs) {
		
		if(scs == null) throw new NullPointerException();
		
		this.hardware = scs;
			
		cashInserted = BigDecimal.ZERO;
					
		coinDenoms = new ArrayList<BigDecimal>(hardware.coinDenominations);
		Collections.sort(coinDenoms, new Comparator<BigDecimal>() {
			@Override
			public int compare(BigDecimal o1, BigDecimal o2) {
				return o2.compareTo(o1);
			}
		});	
		
		banknoteDenoms = new ArrayList<>();
		for(int denom : hardware.banknoteDenominations) banknoteDenoms.add(denom);
		Collections.sort(banknoteDenoms, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o2.compareTo(o1);
			}
		});	
		
		updatePhase(SoftwarePhase.READY);
	}

	/*
	 * @return the total amount of registered cash funds
	 */
	public BigDecimal getTotalInsertedCash() {
		return cashInserted;
	}
	public void resetTotalInsertedCash() {
		cashInserted = BigDecimal.ZERO;
	}
		
	/* OBSERVERS */

	/*
	 * Physically return all unused funds to the customer
	 * 
	 * 	@throws OverloadException 		When the receiving place is full
	 *  @throws EmptyException	  		When there are no available funds (either coins or banknote) to create change
	 *  @throws DisabledException 		When the appropriate dispenser/channel is disabled
	 *  @throws InternalError 	  		When the requested change is negative
	 *  @throws NullPointerException 	When the requested change is null
	 * 
	 */
	public void returnUnusedFunds() throws OverloadException, EmptyException, InternalError, DisabledException  {
		returnChange(getTotalInsertedCash());
	}
	
	/*
	 * Physically return the specified amount of change if possible. Otherwise, it will try its best
	 * and store what could not produce as change in the @field credit
	 * 
	 * @param amount of change to produce
	 * 
	 */
	public void returnChange(BigDecimal amountDue){
		
		if(amountDue == null) throw new NullPointerException("Change due cannot be null");
		else if(amountDue.compareTo(BigDecimal.ZERO) < 0) throw new InternalError("Requested change amount cannot be negative");
		else if(amountDue.compareTo(BigDecimal.ZERO) == 0) {notifyAllChangeReturned();return;} // ignore when there is no change to be returned
		else if(cashInserted.compareTo(amountDue) < 0) throw new InternalError("Change requested cannot be larger than available funds");
		
		try {
			
			// Returning banknotes
			int currentAvailableBanknotes;
			for(int denom : banknoteDenoms){
				
				if(amountDue.compareTo(BigDecimal.ZERO) == 0) break;
				
				currentAvailableBanknotes = hardware.banknoteDispensers.get(denom).size();
				
				BigDecimal newAmount = amountDue.subtract(new BigDecimal(denom));
				while(currentAvailableBanknotes > 0 && newAmount.compareTo(BigDecimal.ZERO) >= 0) {
					try {
						hardware.banknoteDispensers.get(denom).emit();
						amountDue = newAmount;
						currentAvailableBanknotes--;
					}catch(Exception e){}
				}	
			}
		
			int curAvailableCoins;
			for(BigDecimal curDenom : coinDenoms) {
				
				if(amountDue.compareTo(BigDecimal.ZERO) == 0) break;
				
				curAvailableCoins = hardware.coinDispensers.get(curDenom).size();
				
				while(curAvailableCoins > 0 && amountDue.subtract(curDenom).compareTo(BigDecimal.ZERO) >= 0) {
					try {
						hardware.coinDispensers.get(curDenom).emit();	
						amountDue = amountDue.subtract(curDenom);
						curAvailableCoins = hardware.coinDispensers.get(curDenom).size();
					}catch(Exception e) {}
				}	
			}
			
		}catch(Exception e) {
			notifyHardwareError();
			return;
		}
		
		cashInserted = BigDecimal.ZERO;
		totalDue = BigDecimal.ZERO;
		if(amountDue.compareTo(BigDecimal.ZERO) == 0) {
			notifyAllChangeReturned();
		}else {
			notifyPartialChangeReturned(amountDue);
		}		
	}
	
	/* OBSERVERS */
	private void notifyPartialPayment() {
		for (CashPaymentObserver obs : observers) {
			obs.partialPayment();
		}
	}
	private void notifyEnoughCashInserted() {
		for (CashPaymentObserver obs : observers) {
			obs.enoughCashInserted();
		}
	}
	
	private void notifyFundsRegistered(BigDecimal cashInserted) {
		for (CashPaymentObserver obs : observers) {
			obs.fundsRegistered(cashInserted);
		}
	}
		
	private void notifyAllChangeReturned() {
		for(CashPaymentObserver obs : observers) {
			obs.allChangeReturned();
		}
	}
	
	private void notifyPartialChangeReturned(BigDecimal credit) {
		for(CashPaymentObserver obs : observers) {
			obs.partialChangeReturned(credit);
		}
	}
	
	private void notifyHardwareError() {
		for(CashPaymentObserver obs : observers) {
			obs.hardwareError();
		}
	}
	
	/* COMMUNICATION WITH HARDWARE */
	
	@Override
	public void validBanknoteDetected(BanknoteValidator validator, Currency currency, int value) {
		
		// Check whether banknote was added to banknote storage unit
		if(hardware.banknoteStorage.hasSpace()) {
			cashInserted = cashInserted.add(new BigDecimal(value));
			
			
			notifyFundsRegistered(cashInserted);	
			if (cashInserted.compareTo(totalDue) >= 0) {
				notifyEnoughCashInserted();
			}else {
				notifyPartialPayment();
			}
		}
	}
		
	@Override
	public void validCoinDetected(CoinValidator validator, BigDecimal value) {
	
		// Check whether coin was added to dispenser or storage unit
		if(hardware.coinDispensers.get(value).hasSpace() || hardware.coinStorage.hasSpace()){
			cashInserted = cashInserted.add(value);
			
			notifyFundsRegistered(cashInserted);
			if (cashInserted.compareTo(totalDue) >= 0) {
				notifyEnoughCashInserted();
			}else {
				notifyPartialPayment();
			}
			return;
		}
		// [should never happen, unless the storage unit was full. However, the hardware code explicitly says that it should never happen]
	}
	
	@Override 
	public void updatePhase(SoftwarePhase newPhase) {
		
		if(newPhase == SoftwarePhase.PAYING) {
			hardware.coinSlot.enable();
			hardware.banknoteInput.enable();
		}else { // make sure hardware is always disabled to avoid accepting coins in the wrong phase
			hardware.coinSlot.disable();
			hardware.banknoteInput.disable();
		}
		this.curPhase = newPhase;
	}
	/* NOT IMPORTANT */
	
	@Override
	public void invalidBanknoteDetected(BanknoteValidator validator) {
		// ignore
	}

	@Override
	public void invalidCoinDetected(CoinValidator validator) {
		// ignore
	}
	
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// ignore
	}
	
	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// ignore
	}
}
