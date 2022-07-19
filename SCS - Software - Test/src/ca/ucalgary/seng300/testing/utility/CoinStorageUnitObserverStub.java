package ca.ucalgary.seng300.testing.utility;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CoinStorageUnit;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.CoinStorageUnitObserver;

public class CoinStorageUnitObserverStub implements CoinStorageUnitObserver {

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// ignore
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// ignore
	}

	@Override
	public void coinsFull(CoinStorageUnit unit) {
		// ignore
	}

	@Override
	public void coinAdded(CoinStorageUnit unit) {
		// ignore
	}

	@Override
	public void coinsLoaded(CoinStorageUnit unit) {
		// ignore
	}

	@Override
	public void coinsUnloaded(CoinStorageUnit unit) {
		// ignore
	}
}
