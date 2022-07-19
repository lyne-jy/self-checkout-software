package ca.ucalgary.seng300.ui;

import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import javax.swing.JFrame;
import ca.ucalgary.seng300.attendant.ui.SupervisorUI;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SupervisionStation;
import ca.ucalgary.seng300.attendant.SupervisorStationControlSoftware;
import ca.ucalgary.seng300.selfcheckout.SelfCheckoutControlSoftware;
import ca.ucalgary.seng300.selfcheckout.ui.SelfCheckoutUI;
import ca.ucalgary.seng300.selfcheckout.utility.Cart;
import ca.ucalgary.seng300.selfcheckout.utility.Pair;
import ca.ucalgary.seng300.simulations.Customer;
import ca.ucalgary.seng300.simulations.StoreAttendant;
import ca.ucalgary.seng300.simulations.ui.CustomerGUI;
import ca.ucalgary.seng300.simulations.ui.StoreAttendantGUI;

public class UserInterfaceControlSoftware{
	private static int NUM_STATIONS = 1;
	//public static boolean show;

	// Mappings
	public static HashMap<Integer, Pair<SelfCheckoutStation, SelfCheckoutControlSoftware>> idToSelfCheckout = new HashMap<>();
	//public static int stations =0 ;
	// UI components 
	private static JFrame scFrame;
	private static JFrame ssFrame;
	private static JFrame saFrame;
	private static JFrame cFrame;

	// Hardware Components
	private static SelfCheckoutStation selfCheckoutStation;
	private static SupervisionStation supervisionStation;

	// Software Components

	private static SelfCheckoutControlSoftware scControlSoftware;
	private static SupervisorStationControlSoftware ssControlSoftware;

	// Physical people simulations
	public static StoreAttendant storeAttendant;
	public static Customer customer;
	
	/* CONSTRUCTOR */

	public static void main(String[] args) {
		// Initialize all hardware components
		selfCheckoutStation = getSelfCheckoutStationInstance();
		supervisionStation = getSupervisionStationInstance();
		connectHardwareSystems(selfCheckoutStation, supervisionStation);

		// Initialize Software Components
		scControlSoftware = new SelfCheckoutControlSoftware(selfCheckoutStation);
		ssControlSoftware = new SupervisorStationControlSoftware(supervisionStation);
		connectedSoftwareSystemns(scControlSoftware, ssControlSoftware);

		// Initialize physical simulation
		storeAttendant = new StoreAttendant(new SelfCheckoutControlSoftware[] {scControlSoftware});
		customer = new Customer();

		// Create mappings
		idToSelfCheckout.put(scControlSoftware.ID, new Pair<SelfCheckoutStation, SelfCheckoutControlSoftware>(selfCheckoutStation, scControlSoftware));

		// All frames
		scFrame = selfCheckoutStation.screen.getFrame();
		ssFrame = supervisionStation.screen.getFrame();
		saFrame = storeAttendant.getFrame();
		cFrame = customer.getFrame();
		
		

		// Showing GUI for all components
		SelfCheckoutUI.showOffScreen(scFrame);
		SupervisorUI.showLogin(ssControlSoftware);
		StoreAttendantGUI.showMainScreen(saFrame);
		CustomerGUI.showMainScreen(cFrame);
		
		//scFrame.pack();
		scFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        scFrame.setLocation(0,0);
        
        saFrame.pack();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        int x = (int) rect.getMaxX() - saFrame.getWidth();
        int y = (int) rect.getMaxY() - saFrame.getHeight();
        saFrame.setLocation(x, y);
        saFrame.setVisible(true);
        
        ssFrame.pack();
        ssFrame.setLocation(0,9);
        ssFrame.setVisible(true);
        
        /*
        ssFrame.pack();
        ssFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ssFrame.setLocationRelativeTo(null);*/
        
		
		
	}
	
	/* HELPER METHODS */
	
	private static SelfCheckoutStation getSelfCheckoutStationInstance() {
		return new SelfCheckoutStation(Currency.getInstance(Locale.CANADA), new int[]{5, 10, 20, 50, 100}, new BigDecimal[] {new BigDecimal(0.05), new BigDecimal(0.10), new BigDecimal(0.25), new BigDecimal(1.0), new BigDecimal(2.0)}, 500, 1);
	}
	
	private static SupervisionStation getSupervisionStationInstance() {
		return new SupervisionStation();
	}
	
	private static void connectHardwareSystems(SelfCheckoutStation selfCheckoutStation, SupervisionStation supervisionStation) {
		supervisionStation.add(selfCheckoutStation);
	}
	
	private static void connectedSoftwareSystemns(SelfCheckoutControlSoftware scControlSoftware, SupervisorStationControlSoftware ssControlSoftware) {
		scControlSoftware.connectToSoftwareSystem(ssControlSoftware);
		ssControlSoftware.connectToSoftwareSystem(scControlSoftware);
	}
		
	/* MESSAGES FROM SELF-CHECKOUT */
	
	public static void showThankYouMessage(boolean partialChange) {
		SelfCheckoutUI.showThankyouScreen(scFrame, partialChange);
	}

	public static void cashFundsRegistered(BigDecimal totalAmount) {
		SelfCheckoutUI.cashInserted = SelfCheckoutUI.cashInserted.add(totalAmount);
		SelfCheckoutUI.updateTotal(scFrame, scControlSoftware);
	}
	
	public static void cardPaymentSuccesful() {
		SelfCheckoutUI.showThankyouScreen(scFrame, false);
			
	}
	
	public static void allProductsRemoved() {
		SelfCheckoutUI.showStartScreen(scFrame, scControlSoftware, true);
	}
	
	// ON / OFF

	public static void shutDownStation() {
		scFrame.getContentPane().removeAll();
		scFrame.setBackground(Color.BLACK);
		scFrame.setVisible(true);
		scFrame.revalidate();
		scFrame.repaint();
	}

	public static void startStation() {
		SelfCheckoutUI.showStartScreen(scFrame, scControlSoftware, true);
	}
	
	public static void blockStation() {
		SelfCheckoutUI.disableMainFrame(scFrame);
	}

	public static void unblockStation() {
		SelfCheckoutUI.enableMainFrame(scFrame);
		if(SelfCheckoutUI.cart == null || SelfCheckoutUI.cart.isEmpty())
			SelfCheckoutUI.checkoutBtn.setEnabled(false);
	}

	// Cart
	public static void productRemovedFromPurchase(Cart cart, boolean showMsg) {
		SelfCheckoutUI.cart = cart;
		SelfCheckoutUI.cartPane.setText(cart.toString());
		scFrame.revalidate();
		scFrame.repaint();
		
		if(cart.isEmpty()) {
			SelfCheckoutUI.checkoutBtn.setEnabled(false);
		}else {
			SelfCheckoutUI.checkoutBtn.setEnabled(true);
		}
		
		if(showMsg) {
			SelfCheckoutUI.messagePanel.setText("Weight verification failure, Place remove Item from Bagging Area!!");
		}
	}

	public static void productAddedToPurchase(Cart cart) {
		SelfCheckoutUI.messagePanel.setText("Ready to Add Item");
		SelfCheckoutUI.cart = cart;
		SelfCheckoutUI.cartPane.setText(cart.toString());
		scFrame.revalidate();
		scFrame.repaint();
		
		if(cart.isEmpty()) {
			SelfCheckoutUI.checkoutBtn.setEnabled(false);
		}else {
			SelfCheckoutUI.checkoutBtn.setEnabled(true);
		}
		
		SelfCheckoutUI.enableMainFrame(scFrame);
		scFrame.revalidate();
		scFrame.repaint();
	}
	
	public static void bagsAddedToPurchase(Cart cart) {
		
		if(cart == null) return;
		
		SelfCheckoutUI.cart = cart;
		SelfCheckoutUI.cartPane.setText(cart.toString());
		SelfCheckoutUI.messagePanel.setText("Ready to Add Item");
		scFrame.revalidate();
		scFrame.repaint();
	}

	// Messages on display
	
	public static void placeItemBaggingArea() {
		if (SelfCheckoutUI.messagePanel.getText().equals("Weight verification failure, Place correct Item in Bagging Area!!")) return;
		if (SelfCheckoutUI.messagePanel.getText().equals("Scale in Bagging Area has been overload!")) return;
		SelfCheckoutUI.messagePanel.setText("Please Place Item in Bagging Area!!");
	}
	
	public static void productWeightVerificationFailure() {

		for(int i = 0; i < SupervisorUI.approveButtons.length; i ++) {
			SupervisorUI.approveButtons[i].setEnabled(true);
		}
		
		SelfCheckoutUI.messagePanel.setText("Weight verification failure, Place correct Item in Bagging Area!!");
		SelfCheckoutUI.disableMainFrame(scFrame);
	}
	
	public static void placeBagsInBaggingArea() {
		SelfCheckoutUI.messagePanel.setText("Place Bags in Bagging Area!!");
	}
	
	public static void addBagVerificationFailure(boolean ownBag) {
		SelfCheckoutUI.messagePanel.setText("Weight verification of bags failed, Place correct amount of bags");
	}
	
	public static void overloadOfScaleInBagging() {
		SelfCheckoutUI.messagePanel.setText("Scale in Bagging Area has been overload!");
	}
	
	// Other pop ups
	
	public static void membershipCardScannedSuccesfully(Barcode barcode) {
		SelfCheckoutUI.showMembershipScannedSuccesfully(scFrame, barcode);;
	}
	
	// Pop up errors
		
	public static void couldNotReturnChange() {
		SelfCheckoutUI.txtWelcome.setText("Change could not be returned, Talk to Attendant");
	}
	
	public static void barcodeScannedNotInDatabase() {
		SelfCheckoutUI.showItemNotInDatabase(scFrame, "Barcoded");
	}

	public static void PLUEnteredNotInDatabase() {
		SelfCheckoutUI.showItemNotInDatabase(scFrame, "PLU");
	}
		
	public static void membershipCardNotInDatabase() {
		SelfCheckoutUI.showMembershipCardNotInDatabase(scFrame);
	}

	public static void cardPaymentError() {
		SelfCheckoutUI.showCardPaymentError(scFrame);
	}
	
	public static void printerError() {
		
		if(SelfCheckoutUI.extraInfoTextField.getText().length() == 0){
			SelfCheckoutUI.extraInfoTextField.setText("Receipt cannot be printed"); 
		}else {
			SelfCheckoutUI.extraInfoTextField.setText(SelfCheckoutUI.extraInfoTextField.getText() + " + Receipt cannot be printed"); 
		}
	}
	
	public static void productScanned(Barcode barcode) {
		SelfCheckoutUI.messagePanel.setText("Barcode Item Scanned");
		SelfCheckoutUI.disableMainFrame(scFrame);
		scFrame.revalidate();
		scFrame.repaint();
	}
	
	public static void productAdded(PriceLookupCode plu) {
		SelfCheckoutUI.messagePanel.setText("PLU Item Added");
		SelfCheckoutUI.disableMainFrame(scFrame);
		scFrame.revalidate();
		scFrame.repaint();
		
		CustomerGUI.weighing = true;
	}
	
	public static void placeItemScanningArea() {
		SelfCheckoutUI.messagePanel.setText("Please Place Item in Scanning Area");
		SelfCheckoutUI.disableMainFrame(scFrame);
		scFrame.revalidate();
		scFrame.repaint();
	}
	
	public static void producPlacedInScanningArea(){
		SelfCheckoutUI.messagePanel.setText("Please Place Item in Bagging Area!!");
		SelfCheckoutUI.disableMainFrame(scFrame);
		scFrame.revalidate();
		scFrame.repaint();
		CustomerGUI.weighing = false;
	}
	
	public static void addBagVerficationSuccesful(boolean ownBag) {
		// IGNORE
	}
	
	/* MESSAGES FROM SUPERVISION STATION */
	
	public static void wrongCredentials() {
		SupervisorUI.loginLabel.setText("Invalid pin, try again.");
	}

	public static void validCredentials(String employeeName) {
		SupervisorUI.loginDialog.dispose();
		SupervisorUI.showHomeScreen(ssFrame, ssControlSoftware, new SelfCheckoutControlSoftware[]{scControlSoftware}, NUM_STATIONS);
	} 

	public static void successfulLogout() {
		ssFrame.dispose();
		SupervisorUI.showLogin(ssControlSoftware);
	}

	public static void productVerficationFailure(ArrayList<Integer> stations) {
		int id = 0;
		if (stations.contains(id)) {
			SupervisorUI.placeHolder3s[id].setText(SupervisorUI.placeHolder3s[id].getText() + "VerifFailed");
			ssFrame.revalidate();
			ssFrame.repaint();
		}
	}
	

	public static void approveWeightDiscrepancy(ArrayList<Integer> stations) {
		
	
		System.out.print(stations);
		int id = 0;
		if(!stations.contains(id)) {
			SupervisorUI.statusLabels[id].setText(SupervisorUI.placeHolder1s[id].getText() + "");
			
			ssFrame.revalidate();
			ssFrame.repaint();
			stations.add(0);
		}
		
	}

	public static void printerLowOnInk(int id) {
		SupervisorUI.placeHolder1s[id].setText(SupervisorUI.placeHolder1s[id].getText() + "LowInk");
		ssFrame.revalidate();
		ssFrame.repaint();
	}

	public static void printerLowOnPaper(int id) {
		SupervisorUI.placeHolder2s[id].setText(SupervisorUI.placeHolder2s[id].getText() + "LowPaper");
		ssFrame.revalidate();
		ssFrame.repaint();
	}

	public static void partialChangeReturned(int id, BigDecimal credit) {
		SupervisorUI.placeHolder4s[id].setText(SupervisorUI.placeHolder4s[id].getText() + "PartialChange");
		ssFrame.revalidate();
		ssFrame.repaint();
	}

	public static void failedToPlaceItem(ArrayList<Integer> stations) {
		
		for (int id: stations) {
			SupervisorUI.statusLabels[id].setText(SupervisorUI.placeHolder1s[id].getText() + "FailedToPlace");
			ssFrame.revalidate();
			ssFrame.repaint();
		}
	}

	public static void approveProductNotBagged(ArrayList<Integer> stations) {
		// ignore
	}

	public static void addBagVerificationFailure(int id, boolean ownBag) {
		SupervisorUI.placeHolder5s[id].setText(SupervisorUI.placeHolder5s[id].getText() + "BagVeriFailed");
		ssFrame.revalidate();
		ssFrame.repaint();
	}
}
