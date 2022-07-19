package ca.ucalgary.seng300.simulations.ui;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;

import ca.ucalgary.seng300.attendant.coordination.SupervisorStationCoordinator;
import ca.ucalgary.seng300.attendant.ui.SupervisorUI;
import ca.ucalgary.seng300.selfcheckout.utility.Database;
import ca.ucalgary.seng300.ui.UserInterfaceControlSoftware;

public class CustomerGUI {
	
	private static JComboBox<String> selectStationDropDown;
	private static JComboBox<String> addItemDropDown;
	private static JComboBox<Barcode> selectMembershipCardDropDown;
	private static JComboBox<String> selectCardDropDown;
	private static JComboBox<String> selectBanknoteDropDown;
	private static JComboBox<String> selectCoinDropDown;
	
	private static JTextField enterPinTextField;
	
	private static ArrayList<PLUCodedItem> removedPLUItems = new ArrayList<>();
	private static ArrayList<PLUCodedItem> pluItems = new ArrayList<>();
	private static ArrayList<BarcodedItem> barcodeItems = new ArrayList<>();
	
	private static JButton placeItemBaggingBtn;
	private static JButton placeItemScanningBtn;
	private static JButton removeItemScanningBtn;
	private static JButton removeItemBaggingBtn;
	
	// For visual cues
	private static JLabel itemsInBaggingAreaLabel;
	private static JLabel itemsInScanningAreaLabel;
	
	private static int itemsInBaggingArea = 0;
	private static int itemsInScanningArea = 0;
	
	public static boolean weighing = false;

	public static void showMainScreen(JFrame frame) {
		
		frame.setBounds(1250, 0, 400, 800);
		frame.getContentPane().setLayout(new GridLayout(1, 2, 0, 0));
		
		frame.getContentPane().add(getLeftPanel());
		frame.getContentPane().add(getRightPanel());
		
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	private static JPanel getLeftPanel() {
		
		JPanel panel = new JPanel();
		
		panel.setLayout(new GridLayout(24, 1, 0, 0));
		
		JLabel customerSimulationLabel = new JLabel("Customer Simulation");
		customerSimulationLabel.setHorizontalAlignment(SwingConstants.CENTER);
		customerSimulationLabel.setFont(new Font("Lucida Grande", Font.BOLD, 15));
		panel.add(customerSimulationLabel);
		
		selectStationDropDown = new JComboBox<String>();
		for(Integer id : UserInterfaceControlSoftware.idToSelfCheckout.keySet()) {
			selectStationDropDown.addItem("Self-Checkout Station " + id);
		}
			
		panel.add(selectStationDropDown);
		
		JLabel addItemLabel = new JLabel("Add Item ");
		addItemLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		addItemLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(addItemLabel);
		
		addItemDropDown = new JComboBox<String>();
		for(Barcode barcode: Database.database().getBarcodes()) {
			addItemDropDown.addItem(Database.database().readProductWithBarcode(barcode).getDescription());
		}
		addItemDropDown.addItem("Barcode Orange [Not in DB]");
		
		for(PriceLookupCode plu: Database.database().getPLUs()) {
			addItemDropDown.addItem(Database.database().readProductWithPLU(plu).getDescription());
		}
		panel.add(addItemDropDown);
		
		JButton scanItemBtn = new JButton("Scan Item ");
		scanItemBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				int id = selectStationDropDown.getSelectedIndex();
				String itemName = ((String)addItemDropDown.getSelectedItem());
				
				Barcode itemBarcode;
				double weight;
				if(itemName.equals("Barcode Orange [Not in DB]")) {
					itemBarcode = new Barcode(new Numeral[] {Numeral.one, Numeral.two, Numeral.one});
					weight = 10.0;
				}else {
					itemBarcode = Database.database().nameToBarcode(itemName);
					if(itemBarcode == null) return;
					weight = Database.database().readProductWithBarcode(itemBarcode).getExpectedWeight();
				}
															
				BarcodedItem item =  new BarcodedItem(itemBarcode, weight);
								
				UserInterfaceControlSoftware.customer.scanProductWithHandHeldScanner(UserInterfaceControlSoftware.idToSelfCheckout.get(id).getFirst(), item);	
			}
			
		});
		panel.add(scanItemBtn);
		
		placeItemBaggingBtn = new JButton("Place Item Bagging Area");
		placeItemBaggingBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				ArrayList<Integer> station =  SupervisorStationCoordinator.stations;
				station.remove(Integer.valueOf(0));
				
				
				UserInterfaceControlSoftware.approveWeightDiscrepancy(station);
				int id = selectStationDropDown.getSelectedIndex();
				String itemName = ((String)addItemDropDown.getSelectedItem());
				Barcode itemBarcode = Database.database().nameToBarcode(itemName);
				

				
				if(itemBarcode == null) {
					
					PriceLookupCode itemPLU = Database.database().nameToPLU(itemName);
				
					if(weighing) return;
					if(itemPLU == null) return;
					
					
					double weight = Database.database().pluToWeight(itemPLU);
					PLUCodedItem item =  new PLUCodedItem(itemPLU, weight);
					pluItems.add(item);
					UserInterfaceControlSoftware.customer.placeItemInBaggingArea(UserInterfaceControlSoftware.idToSelfCheckout.get(id).getFirst(), item);
					
					itemsInBaggingArea++;
					itemsInBaggingAreaLabel.setText("Items in Bagging Area: " + itemsInBaggingArea);
					itemsInBaggingAreaLabel.repaint();
					
				}else {
					
					double weight = Database.database().readProductWithBarcode(itemBarcode).getExpectedWeight();
					BarcodedItem item = new BarcodedItem(itemBarcode, weight);
					barcodeItems.add(item);
					UserInterfaceControlSoftware.customer.placeItemInBaggingArea(UserInterfaceControlSoftware.idToSelfCheckout.get(id).getFirst(), item);
						
					itemsInBaggingArea++;
					itemsInBaggingAreaLabel.setText("Items in Bagging Area: " + itemsInBaggingArea);
					itemsInBaggingAreaLabel.repaint();
				}				 
			}				

		});

		panel.add(placeItemBaggingBtn);
		
		removeItemBaggingBtn = new JButton("Remove Item Bagging Area");
		removeItemBaggingBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				int id = selectStationDropDown.getSelectedIndex();
				String itemName = ((String)addItemDropDown.getSelectedItem());
				Barcode itemBarcode = Database.database().nameToBarcode(itemName);
				
				if(itemBarcode == null) {
					
					PriceLookupCode itemPLU = Database.database().nameToPLU(itemName);
				
					if(weighing) return;
					if(itemPLU == null) return;
								
					for(PLUCodedItem i : pluItems) {
						if(i.getPLUCode().equals(itemPLU)) {
							
							UserInterfaceControlSoftware.customer.removeItemFromBaggingArea(UserInterfaceControlSoftware.idToSelfCheckout.get(id).getFirst(), i);
							removedPLUItems.add(i);
							pluItems.remove(i);
							
							itemsInBaggingArea = Math.max(0, itemsInBaggingArea - 1);
							itemsInBaggingAreaLabel.setText("Items in Bagging Area: " + itemsInBaggingArea);
							itemsInBaggingAreaLabel.repaint();
							break;
						}
					}
							
				}else {
					
					for(BarcodedItem i : barcodeItems) {
						if(i.getBarcode().equals(itemBarcode)) {

							barcodeItems.remove(i);
							UserInterfaceControlSoftware.customer.removeItemFromBaggingArea(UserInterfaceControlSoftware.idToSelfCheckout.get(id).getFirst(), i);	
							
							itemsInBaggingArea = Math.max(0, itemsInBaggingArea - 1);
							itemsInBaggingAreaLabel.setText("Items in Bagging Area: " + itemsInBaggingArea);
							itemsInBaggingAreaLabel.repaint();
							break;
						}
					}	
				}				
			}
			
		});
		
		panel.add(removeItemBaggingBtn);
		
		placeItemScanningBtn = new JButton("Place Item Scanning Area");
		placeItemScanningBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				int id = selectStationDropDown.getSelectedIndex();
				String itemName = ((String)addItemDropDown.getSelectedItem());
				Barcode itemBarcode = Database.database().nameToBarcode(itemName);
								
				// Barcoded items are not supposed to be placed in the scanning area
				if(itemBarcode != null) return;
				
				PriceLookupCode itemPLU = Database.database().nameToPLU(itemName);
				
				if(itemPLU == null) return;
				
				if(removedPLUItems.size() > 0) {
					
					for(PLUCodedItem i : removedPLUItems) {
						if(i.getPLUCode().equals(itemPLU)) {
							removedPLUItems.remove(i);
							
							UserInterfaceControlSoftware.customer.placeItemScanningArea(UserInterfaceControlSoftware.idToSelfCheckout.get(id).getFirst(), i);
							itemsInScanningArea++;
							itemsInScanningAreaLabel.setText("Items in Scanning Area: " + itemsInScanningArea);
							itemsInScanningAreaLabel.repaint();
							break;
						
						}
					}
					
				}else {
					double weight = Database.database().pluToWeight(itemPLU);
					PLUCodedItem item =  new PLUCodedItem(itemPLU, weight);
					pluItems.add(item);
					
					UserInterfaceControlSoftware.customer.placeItemScanningArea(UserInterfaceControlSoftware.idToSelfCheckout.get(id).getFirst(), item);
					itemsInScanningArea++;
					itemsInScanningAreaLabel.setText("Items in Scanning Area: " + itemsInScanningArea);
					itemsInScanningAreaLabel.repaint();					
				}
			}
			
		});
		panel.add(placeItemScanningBtn);
		
		removeItemScanningBtn = new JButton("Remove Item Scanning Area");
		removeItemScanningBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				int id = selectStationDropDown.getSelectedIndex();
				String itemName = ((String)addItemDropDown.getSelectedItem());
				Barcode itemBarcode = Database.database().nameToBarcode(itemName);
				
				// Barcoded items are not supposed to be placed in the scanning area
				if(itemBarcode != null) return;
				
				PriceLookupCode itemPLU = Database.database().nameToPLU(itemName);
				
				if(itemPLU == null) return;
				
				if(removedPLUItems.size() > 0) {
									
					for(PLUCodedItem i : removedPLUItems) {
						if(i.getPLUCode().equals(itemPLU)) {
							UserInterfaceControlSoftware.customer.removeItemFromScanningArea(UserInterfaceControlSoftware.idToSelfCheckout.get(id).getFirst(), i);
							pluItems.add(i);
							removedPLUItems.remove(i);
							
							itemsInScanningArea = Math.max(itemsInScanningArea - 1, 0);
							itemsInScanningAreaLabel.setText("Items in Scanning Area: " + itemsInScanningArea);
							itemsInScanningAreaLabel.repaint();
							break;
						}
					}
				}else {
									
					for(PLUCodedItem i : pluItems) {
						if(i.getPLUCode().equals(itemPLU)) {

							pluItems.remove(i);
							removedPLUItems.add(i);
							UserInterfaceControlSoftware.customer.removeItemFromScanningArea(UserInterfaceControlSoftware.idToSelfCheckout.get(id).getFirst(), i);
							
							itemsInScanningArea = Math.max(itemsInScanningArea - 1, 0);
							itemsInScanningAreaLabel.setText("Items in Scanning Area: " + itemsInScanningArea);
							itemsInScanningAreaLabel.repaint();
							
							break;
						}
					}
				}
			}			
		});
		panel.add(removeItemScanningBtn);
		
		JLabel cahsPaymentLabel = new JLabel("Cash Payment");
		cahsPaymentLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		cahsPaymentLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(cahsPaymentLabel);
		
		JButton insertBanknoteBtn = new JButton("Insert Banknote");
		insertBanknoteBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				int id = selectStationDropDown.getSelectedIndex();
				String banknoteName = (String)selectBanknoteDropDown.getSelectedItem();
				Banknote banknote = Database.database().nameToBanknote(banknoteName);
				
				if(banknote == null) return;
						
				UserInterfaceControlSoftware.customer.insertBanknote(UserInterfaceControlSoftware.idToSelfCheckout.get(id).getFirst(), banknote);
			}
			
		});
		panel.add(insertBanknoteBtn);
		
		JButton insertCoinBtn = new JButton("Insert Coin");
		insertCoinBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				int id = selectStationDropDown.getSelectedIndex();
				String coinName = (String)selectCoinDropDown.getSelectedItem();
				Coin coin = Database.database().nameToCoin(coinName);
				
				if(coin == null) return;
						
				UserInterfaceControlSoftware.customer.insertCoin(UserInterfaceControlSoftware.idToSelfCheckout.get(id).getFirst(), coin);
				
			}
			
		});
		panel.add(insertCoinBtn);
		
		JLabel cardPaymentLabel = new JLabel("Card Payment");
		cardPaymentLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		cardPaymentLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(cardPaymentLabel);
		
		selectCardDropDown = new JComboBox<String>();
		for(String cardNum : Database.database().cardNumbers()) {
			if(cardNum == "123456789")
				selectCardDropDown.addItem(cardNum + " [enough funds] [pin=1234]");
			
			if(cardNum == "987456732")
				selectCardDropDown.addItem(cardNum + " [not enough funds] [pin=1234]");
		}
		panel.add(selectCardDropDown);
		
		JButton insertCardBtn = new JButton("Insert Card");
		insertCardBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				int id = selectStationDropDown.getSelectedIndex();
				
				String cardNum = ((String) selectCardDropDown.getSelectedItem()).substring(0, 9);
								
				Card card = Database.database().numberToCard(cardNum);
				
				if(card == null) return;
				
				String pin = enterPinTextField.getText().trim();
				
				if (pin.equals(Database.database().numberToPin(cardNum))) {
					UserInterfaceControlSoftware.customer.insertCardWithChip(UserInterfaceControlSoftware.idToSelfCheckout.get(id).getFirst(), card,Database.database().numberToPin(cardNum) );
				}
				
				else {
					UserInterfaceControlSoftware.cardPaymentError();
				}	
			}
			
		});
		panel.add(insertCardBtn);
		
		JButton swipeCardBtn = new JButton("Swipe Card");
		swipeCardBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				int id = selectStationDropDown.getSelectedIndex();
				
				String cardNum = ((String) selectCardDropDown.getSelectedItem()).substring(0, 9);
								
				Card card = Database.database().numberToCard(cardNum);
				
				if(card == null) return;
				
				UserInterfaceControlSoftware.customer.swipeCard(UserInterfaceControlSoftware.idToSelfCheckout.get(id).getFirst(), card);
			}
			
		});
		panel.add(swipeCardBtn);
		
		JButton tapCardBtn = new JButton("Tap Card");
		tapCardBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				int id = selectStationDropDown.getSelectedIndex();
				
				String cardNum = ((String) selectCardDropDown.getSelectedItem()).substring(0, 9);
								
				Card card = Database.database().numberToCard(cardNum);
				
				if(card == null) return;
				
				UserInterfaceControlSoftware.customer.tapCard(UserInterfaceControlSoftware.idToSelfCheckout.get(id).getFirst(), card);
			}
			
		});
		panel.add(tapCardBtn);
		
		JLabel checkoutLabel = new JLabel("Checkout ");
		checkoutLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		checkoutLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(checkoutLabel);
		
		JButton removeBanknotesBtn = new JButton("Remove Banknotes ");
		removeBanknotesBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int id = selectStationDropDown.getSelectedIndex();
				UserInterfaceControlSoftware.customer.removeBanknotes(UserInterfaceControlSoftware.idToSelfCheckout.get(id).getFirst());
			}
			
		});
		panel.add(removeBanknotesBtn);
		
		JButton removeCoinsBtn = new JButton("Remove Coins from Tray");
		removeCoinsBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int id = selectStationDropDown.getSelectedIndex();
				UserInterfaceControlSoftware.customer.removeCoinsFromTray(UserInterfaceControlSoftware.idToSelfCheckout.get(id).getFirst());
			}
			
		});
		panel.add(removeCoinsBtn);
		
		
		JButton removeReceipt = new JButton("Remove Receipt");
		removeReceipt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int id = selectStationDropDown.getSelectedIndex();				
				UserInterfaceControlSoftware.customer.removeReceipt(UserInterfaceControlSoftware.idToSelfCheckout.get(id).getFirst());
			}
			
		});
		panel.add(removeReceipt);
		
		
		JLabel membershipLabel = new JLabel("Membership");
		membershipLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		membershipLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(membershipLabel);
		
		JButton scanMembership = new JButton("Scan membership");
		scanMembership.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				int id = selectStationDropDown.getSelectedIndex();
				Barcode barcode = ((Barcode)selectMembershipCardDropDown.getSelectedItem());
				BarcodedItem item = new BarcodedItem(barcode, 0.5);
						
				UserInterfaceControlSoftware.customer.scanMembership(UserInterfaceControlSoftware.idToSelfCheckout.get(id).getFirst(), item);
			}
			
		});
		panel.add(scanMembership);
		
		return panel;
	}

	private static JPanel getRightPanel() {
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(24, 1, 0, 0));
		
		JLabel lblNewLabel = new JLabel("");
		panel.add(lblNewLabel);
		
		JLabel selectStationLabel = new JLabel("Select Station ");
		panel.add(selectStationLabel);
		
		JLabel lblNewLabel_1_11 = new JLabel("");
		panel.add(lblNewLabel_1_11);
		
		JLabel selectItemLabel = new JLabel("Select Item ");
		panel.add(selectItemLabel);
						
		JLabel lblNewLabel_1_8 = new JLabel("");
		panel.add(lblNewLabel_1_8);
		
		itemsInBaggingAreaLabel = new JLabel("Items in Bagging Area: " + itemsInBaggingArea);
		panel.add(itemsInBaggingAreaLabel);
		
		JLabel lblNewLabel_1_6 = new JLabel("");
		panel.add(lblNewLabel_1_6);
		
		itemsInScanningAreaLabel = new JLabel("Items in Scanning Area: " + itemsInScanningArea);
		panel.add(itemsInScanningAreaLabel);
		
		JLabel lblNewLabel_1_4 = new JLabel("");
		panel.add(lblNewLabel_1_4);
		
		JLabel lblNewLabel_1_3 = new JLabel("");
		panel.add(lblNewLabel_1_3);
		
		selectBanknoteDropDown = new JComboBox<String>();
		selectBanknoteDropDown.addItem("Five Dollars");
		selectBanknoteDropDown.addItem("Ten Dollars");
		selectBanknoteDropDown.addItem("Twenty Dollars");
		selectBanknoteDropDown.addItem("Fifty Dollars");
		selectBanknoteDropDown.addItem("One Hundred Dollars");
		panel.add(selectBanknoteDropDown);
		
		selectCoinDropDown = new JComboBox<String>();
		selectCoinDropDown.addItem("Five Cents");
		selectCoinDropDown.addItem("Ten Cents");
		selectCoinDropDown.addItem("Twenty Five Cents");
		selectCoinDropDown.addItem("One Dollar");
		selectCoinDropDown.addItem("Two Dollars");
		panel.add(selectCoinDropDown);
		
		JLabel lblNewLabel_1_12 = new JLabel("");
		panel.add(lblNewLabel_1_12);
		
		JLabel lblNewLabel_1 = new JLabel("Select Card");
		panel.add(lblNewLabel_1);
		
		enterPinTextField = new JTextField();
		enterPinTextField.setToolTipText("Enter Pin");
		panel.add(enterPinTextField);
		enterPinTextField.setColumns(10);
		
		JLabel enterPinLabel = new JLabel("Enter Pin");
		enterPinLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(enterPinLabel);
		
		JLabel lblNewLabel_1_1_1 = new JLabel("");
		panel.add(lblNewLabel_1_1_1);
		
		JLabel lblNewLabel_1_1_2 = new JLabel("");
		panel.add(lblNewLabel_1_1_2);
		
		JLabel lblNewLabel_1_1_3 = new JLabel("");
		panel.add(lblNewLabel_1_1_3);
		
		JLabel lblNewLabel_1_1_4 = new JLabel("");
		panel.add(lblNewLabel_1_1_4);
		
		JLabel lblNewLabel_1_1_5 = new JLabel("");
		panel.add(lblNewLabel_1_1_5);
		
		JLabel lblNewLabel_1_1_52 = new JLabel("");
		panel.add(lblNewLabel_1_1_52);
				
		selectMembershipCardDropDown = new JComboBox<Barcode>();
		for(Barcode barcode : Database.database().memberships()) {
			selectMembershipCardDropDown.addItem(barcode);
		}
		selectMembershipCardDropDown.addItem(new Barcode(new Numeral[] {Numeral.one, Numeral.two, Numeral.three}));
		panel.add(selectMembershipCardDropDown);
		
		JLabel selectMembershipLabel = new JLabel("Select Membership");
		selectMembershipLabel.setHorizontalAlignment(SwingConstants.CENTER);
		selectMembershipLabel.setLabelFor(selectMembershipCardDropDown);
		panel.add(selectMembershipLabel);
		
		return panel;
	}
}
