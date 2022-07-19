package ca.ucalgary.seng300.attendant;

import org.lsmr.selfcheckout.devices.SupervisionStation;

import ca.ucalgary.seng300.attendant.communication.SSNotifierObserver;
import ca.ucalgary.seng300.attendant.communication.SupervisorStationNotifier;
import ca.ucalgary.seng300.attendant.coordination.SupervisorStationCoordinator;
import ca.ucalgary.seng300.selfcheckout.SelfCheckoutControlSoftware;
import ca.ucalgary.seng300.selfcheckout.communication.SCNotifierSystemObserver;

/*
 * Class to instantiates and connects applicable software components with attendant station's UI
 * It will also allow this current system to be connected to any other external software system.
 */
public class SupervisorStationControlSoftware {
	
	public final SupervisorStationNotifier notifier;
	private SupervisorStationCoordinator coordinator;
	
	public SupervisorStationControlSoftware(SupervisionStation ss  /*,  Attendant station UI */) {
		
		if(ss == null) throw new NullPointerException();
				
		notifier = new SupervisorStationNotifier();
		coordinator = new SupervisorStationCoordinator();
		
		connectComponents();
	}
	
	/*
	 * Connect all software components of this system for receiving and sending messages
	 */
	private void connectComponents() {
		notifier.connectLocalComponents(coordinator);
		coordinator.connectLocalComponents(notifier);
	}
	
	/*
	 * Connect this software system with another system (ex: the selfcheckout system)
	 * This simulates some form of wirelss/wired connection among the supervisor station
	 * and the self-checkout stations.
	 */
	public void connectToSoftwareSystem(SelfCheckoutControlSoftware sccs) {
		notifier.connectToSelfCheckoutSystem(sccs);
	}
	
	/*
	 * Connects two separate software systems [this is a simulation of some form of wireless communication
	 * among two different hardware systems]
	 * Here we are connecting the SelfCheckout station with the Supervisor station.
	 */
	public void attachToSystem(SSNotifierObserver obs) {
		notifier.attach(obs);
	}
		
	/*
	 * Checks wether a notifier is equal to the notifier in this system
	 */
	public boolean checkEquals(SCNotifierSystemObserver notifier) {
		return this.notifier.checkEquals(notifier);
	}
}
