package ca.ucalgary.seng300.attendant.coordination;

import java.util.ArrayList;

import ca.ucalgary.seng300.attendant.communication.SSNotifierCoordinatorObserver;
import ca.ucalgary.seng300.attendant.communication.SupervisorStationNotifier;
import ca.ucalgary.seng300.selfcheckout.utility.ComplexAbstractSoftware;
import ca.ucalgary.seng300.selfcheckout.utility.Database;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;

public class SupervisorStationCoordinator extends ComplexAbstractSoftware<SSCoordinatorObserver> {

	// Fields
	public static ArrayList<Integer> stations = new ArrayList<Integer>(); // list of statation for which the weight verificaton of an item has failed
	protected SoftwarePhase phaseBeforeBlock;
	protected Integer employeePin;
	
	// Constructor
	public SupervisorStationCoordinator() {	
		stations.add(0);
		
		
		phaseBeforeBlock = null;
		employeePin = null;
	}
	
	public void connectLocalComponents(SupervisorStationNotifier ssn) {
		
		if(ssn == null) throw new NullPointerException();
		
		ssn.attach(new SSNCO());
	}
	
	// MESSAGES FROM UI PASSED THROUGH NOTIFIER
	
	private class SSNCO implements SSNotifierCoordinatorObserver{

		@Override
		public void login(Integer pin) {
		
			String employeeName = Database.database().readEmployee(pin);
			
			// Check if pin is valid and employee exists in DB
			if(pin == null || employeeName == null) {
				notifyWrongCredentials();
			}else {
				notifyValidCredentials(employeeName);
			}
			
			// Update the pin of the currently logged in employee
			employeePin = pin;
		}

		@Override
		public void logout() {
			// Reset the pin and notify the UI
			employeePin = null;
			notifySuccessfulLogout();		
		}

		@Override
		public void approveWeightDiscrepancy(int id) {
			
			if(!stations.contains(id)) return;
			stations.remove(Integer.valueOf(id));
			notifyApproveWeightDiscrepancy(stations);
		}

		@Override
		public void productVerficationFailure(int id) {
			if(stations.contains(Integer.valueOf(id))) return;
			stations.add((Integer.valueOf(id)));
			notifyProductVerficationFailure(stations);
		}

		@Override
		public void approveProductNotBagged(int id) {
			
			if(!stations.contains((Integer.valueOf(id)))) return;
			
			stations.remove((Integer.valueOf(id)));
			notifyApproveProductNotBagged(stations);
		}

		@Override
		public void approveBagWeightVerificationFailure(int id) {
			
			if(!stations.contains((Integer.valueOf(id)))) return;
			stations.remove((Integer.valueOf(id)));
			notifyApproveProductNotBagged(stations);
		}

		@Override
		public void blockStation(int id) {
			
			if(stations.contains((Integer.valueOf(id)))) return;
			stations.add((Integer.valueOf(id)));
		}

		@Override
		public void unblockStation(int id) {
			
			if(stations.contains((Integer.valueOf(id)))) return;
			stations.add((Integer.valueOf(id)));
		}

		@Override
		public void failedToPlaceItem(int id) {
			System.out.print(stations+"!");
			if(!stations.contains((Integer.valueOf(id)))) return;
			//stations.add((Integer.valueOf(id)));
			notifyFailedToPlaceItem(stations);
		}
	}
	
	/* OBSERVER */
	private void notifyFailedToPlaceItem(ArrayList<Integer> stations) {
		for(SSCoordinatorObserver obs : observers) {
			obs.failedToPlaceItem(stations);
		}
	}
	
	private void notifyApproveProductNotBagged(ArrayList<Integer> stations) {
		for(SSCoordinatorObserver obs : observers) {
			obs.approveProductNotBagged(stations);
		}
	}
	
	private void notifyWrongCredentials() {
		for(SSCoordinatorObserver obs : observers) {
			obs.wrongCredentials();
		}
	}
	
	private void notifyValidCredentials(String employeeName) {
		for(SSCoordinatorObserver obs : observers) {
			obs.validCredentials(employeeName);
		}
	}	
	
	private void notifySuccessfulLogout() {
		for(SSCoordinatorObserver obs : observers) {
			obs.successfulLogout();
		}
	}
	
	private void notifyApproveWeightDiscrepancy(ArrayList<Integer> stations) {
		for(SSCoordinatorObserver obs : observers) {
			obs.approveWeightDiscrepancy(stations);
		}
	}

	private void notifyProductVerficationFailure(ArrayList<Integer> stations) {
		for(SSCoordinatorObserver obs : observers) {
			obs.productVerficationFailure(stations);
		}
	}
}