package ca.ucalgary.seng300.testing.utility;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteStorageUnit;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.BanknoteStorageUnitObserver;

public class BanknoteStorageUnitObserverStub implements BanknoteStorageUnitObserver {

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// ignore	
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// ignore
	}

	@Override
	public void banknotesFull(BanknoteStorageUnit unit) {
		// ignore		
	}

	@Override
	public void banknoteAdded(BanknoteStorageUnit unit) {
		// ignore		
	}

	@Override
	public void banknotesLoaded(BanknoteStorageUnit unit) {
		// ignore		
	}

	@Override
	public void banknotesUnloaded(BanknoteStorageUnit unit) {
		// ignore
		
	}
}
