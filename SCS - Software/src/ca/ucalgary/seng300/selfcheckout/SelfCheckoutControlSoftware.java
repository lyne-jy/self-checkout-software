package ca.ucalgary.seng300.selfcheckout;

import ca.ucalgary.seng300.selfcheckout.utility.PlaceItemTimer;
import ca.ucalgary.seng300.selfcheckout.utility.SimpleAbstractSoftware;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;

import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import ca.ucalgary.seng300.attendant.SupervisorStationControlSoftware;
import ca.ucalgary.seng300.attendant.communication.SSNotifierSystemObserver;
import ca.ucalgary.seng300.selfcheckout.communication.SCNotifierObserver;
import ca.ucalgary.seng300.selfcheckout.communication.SelfCheckoutNotifier;
import ca.ucalgary.seng300.selfcheckout.coordination.SelfCheckoutCoordinator;
import ca.ucalgary.seng300.selfcheckout.funds.Payment;
import ca.ucalgary.seng300.selfcheckout.product.BaggingArea;
import ca.ucalgary.seng300.selfcheckout.product.Checkout;
import ca.ucalgary.seng300.selfcheckout.product.PLULogic;
import ca.ucalgary.seng300.selfcheckout.product.Scan;
import ca.ucalgary.seng300.selfcheckout.product.EnterMembership;

/*
 * Class to instantiate and connects software components with hardware 
 */
public final class SelfCheckoutControlSoftware extends SimpleAbstractSoftware{
    
	// Fields
	private static int numberOfInstances = 0;
	public final int ID;
	
	private SelfCheckoutStation hw;
	private Scan scan;
	private BaggingArea bagging;
	private Checkout checkout;
	private PlaceItemTimer timer;
	private SelfCheckoutCoordinator coordinator;
	private PLULogic plu;
	private Payment payment;
	private EnterMembership enterMembership;
	
	public final SelfCheckoutNotifier notifier;
	
	public SelfCheckoutControlSoftware(SelfCheckoutStation hw) {	
		
		if(hw == null) throw new NullPointerException();
		
		this.hw = hw;
		
		ID = numberOfInstances;
		numberOfInstances++;
		
		plu = new PLULogic(hw);
		scan = new Scan();
		payment = new Payment(hw);
		bagging = new BaggingArea();
		checkout = new Checkout(hw);
		timer = new PlaceItemTimer();
		enterMembership = new EnterMembership();

		notifier = new SelfCheckoutNotifier(ID);
		coordinator = new SelfCheckoutCoordinator(plu, scan, payment, bagging, checkout, timer, enterMembership, ID);
		
		config();
	}
	
	// Attach all the respective observers
	private void config() {
		
		// Hardware 
		hw.mainScanner.attach(scan);
		hw.handheldScanner.attach(scan);
		hw.mainScanner.attach(enterMembership);
		hw.handheldScanner.attach(enterMembership);
		
		hw.coinValidator.attach(payment.cash);
		hw.banknoteValidator.attach(payment.cash);
		hw.cardReader.attach(payment.card);
		
		hw.baggingArea.attach(bagging);
		hw.baggingArea.attach(plu);
		
		// All components
		scan.attach(notifier);
		payment.attach(notifier);
		bagging.attach(notifier);
		checkout.attach(notifier);
		checkout.attach(coordinator);
		plu.attach(notifier);
		timer.attach(notifier);
		enterMembership.attach(notifier);

		// For notifier
		notifier.attach(coordinator);
	
		// For coodinator
		coordinator.attach(notifier);
	}
		
	/*
	 * Connect this software system with another system (ex: the selfcheckout system)
	 * This simulates some form of wirelss/wired connection among the supervisor station
	 * and the self-checkout stations.
	 */
	public void connectToSoftwareSystem(SupervisorStationControlSoftware sscs) {
		notifier.connectToSupervisorSystem(sscs);
	}
	
	/*
	 * Connects to separate software systems [this is a simulation of some form of wireless communication
	 * among two different hardware systems]
	 * Here we are connecting the Supervisor Station to the Selfcheckout Station
	 */
	public void attachToSystem(SCNotifierObserver obs) {
		notifier.attach(obs);
	}
	
	/*
	 * Checks wether a notifier is equal to the notifier in this system
	 */
	public boolean checkEquals(SSNotifierSystemObserver notifier) {
		return this.notifier.checkEquals(notifier);
	}	
	
	/*
	 * Get the hardware
	 */
	public SelfCheckoutStation getHw() {
		return hw;
	}
	
	@Override
	public void updatePhase(SoftwarePhase newPhase) {
		coordinator.updatePhase(newPhase);
	}
}