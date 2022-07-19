
package ca.ucalgary.seng300.testing.utility;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.CoinDispenserObserver;

public class CoinDispenserObserverStub implements CoinDispenserObserver{

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// ignore
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// ignore
	}

	@Override
	public void coinsFull(CoinDispenser dispenser) {
		// ignore
	}

	@Override
	public void coinsEmpty(CoinDispenser dispenser) {
		// ignore
	}

	@Override
	public void coinAdded(CoinDispenser dispenser, Coin coin) {
		// ignore
	}

	@Override
	public void coinRemoved(CoinDispenser dispenser, Coin coin) {
		// ignore
	}

	@Override
	public void coinsLoaded(CoinDispenser dispenser, Coin... coins) {
		// ignore
	}

	@Override
	public void coinsUnloaded(CoinDispenser dispenser, Coin... coins) {
		// ignore
	}
}
