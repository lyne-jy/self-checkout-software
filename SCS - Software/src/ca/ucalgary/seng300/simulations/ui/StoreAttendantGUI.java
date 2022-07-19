package ca.ucalgary.seng300.simulations.ui;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Currency;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.SimulationException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import ca.ucalgary.seng300.selfcheckout.utility.Database;
import ca.ucalgary.seng300.ui.UserInterfaceControlSoftware;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class StoreAttendantGUI {
	
	private static int stationID;
	private static int paperAddQuantity = 1; 
	private static int inkAddQuantity = 1;
	private static BigDecimal coinRefilled;
	private static int banknoteRefilled = 1;

	private static int Num;
	
	public static void showMainScreen(JFrame frame) {
				
		frame.setBounds(0, 80, 500, 320);
		
		frame.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
		
		frame.getContentPane().add(getButtonsPanel());
		frame.getContentPane().add(getDropDownsPanel());
		frame.getContentPane().add(getLabelsPanel());
		frame.setResizable(false);
		
		frame.setVisible(true);
	}
	
	private static JPanel getButtonsPanel() {
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(10, 1, 0, 0));
		
		JLabel lblNewLabel_2 = new JLabel("Attendant Simulation");
		lblNewLabel_2.setFont(new Font("Lucida Grande", Font.BOLD, 15));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblNewLabel_2);
		
		JComboBox<String> selectStationDropDown = new JComboBox<String>();
		
		for(Integer id : UserInterfaceControlSoftware.idToSelfCheckout.keySet()) {
			selectStationDropDown.addItem("Self-Checkout Station " + id);
		}		
		panel.add(selectStationDropDown);
		
		JButton addPaperButton = new JButton("Add Paper");
		panel.add(addPaperButton);
		addPaperButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					notifyPaperAdded(paperAddQuantity, stationID);
				} catch (OverloadException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		JButton addInkBtn = new JButton("Add Ink");		
		panel.add(addInkBtn);
		addInkBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					notifyInkAdded(inkAddQuantity, stationID);
				} catch (OverloadException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		JButton refillCoinDispenserBtn = new JButton("Refill C. Dispenser");		
		panel.add(refillCoinDispenserBtn);
		refillCoinDispenserBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getUserInput();
				//coinNum = Integer.valueOf(JOptionPane.showInputDialog("Enter Number of Coins"));
				try {
					notifyCoinsAdded(coinRefilled, Num, stationID);
				} catch (SimulationException | OverloadException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		
		JButton refillBanknoteDispenserBtn = new JButton("Refill B. Dispenser");
		panel.add(refillBanknoteDispenserBtn);
		refillBanknoteDispenserBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getUserInput();
				
				try {
					notifyBanknotesAdded(banknoteRefilled, Num, stationID);
				} catch (OverloadException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});
		
		JLabel lblNewLabel = new JLabel("");
		panel.add(lblNewLabel);
		
		JButton emptyCoinStorageBtn = new JButton("Empty C. Storage ");
		panel.add(emptyCoinStorageBtn);
		emptyCoinStorageBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				notifyEmptiedCoinsBin(stationID);
			}
		});
		
		JButton emptyBanknoteStorageBtn = new JButton("Empty B. Storage ");
		panel.add(emptyBanknoteStorageBtn);
		emptyBanknoteStorageBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				notifyEmptiedBanknotesBin(stationID);
			}

		});
		
		
		return panel;
		
	}
	
	private static JPanel getDropDownsPanel() {
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(10, 1, 0, 0));
		
		JLabel lblNewLabel_1_1 = new JLabel("");
		panel.add(lblNewLabel_1_1);
		
		JLabel selectStationLabel = new JLabel("Select Station ");
		selectStationLabel.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(selectStationLabel);
		
		JComboBox<Integer> selectUnitsDropdown = new JComboBox<Integer>();
		selectUnitsDropdown.addItem(1);
		selectUnitsDropdown.addItem(5);
		selectUnitsDropdown.addItem(10);
		selectUnitsDropdown.addItem(25);
		selectUnitsDropdown.addItem(50);
		panel.add(selectUnitsDropdown);
		selectUnitsDropdown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				paperAddQuantity = Integer.valueOf(selectUnitsDropdown.getSelectedItem().toString());
			}
		});
		
		JComboBox<Integer> selectQuantityDropDown = new JComboBox<Integer>();
		selectQuantityDropDown.addItem(1);
		selectQuantityDropDown.addItem(5);
		selectQuantityDropDown.addItem(10);
		selectQuantityDropDown.addItem(25);
		selectQuantityDropDown.addItem(50);
		panel.add(selectQuantityDropDown);
		selectQuantityDropDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inkAddQuantity = Integer.valueOf(selectQuantityDropDown.getSelectedItem().toString());
			}
		});
		
		
		JComboBox<String> selectCoinDropDown = new JComboBox<String>();
		selectCoinDropDown.addItem("Five Cents");
		selectCoinDropDown.addItem("Ten Cents");
		selectCoinDropDown.addItem("Twenty Five Cents");
		selectCoinDropDown.addItem("One Dollar");
		selectCoinDropDown.addItem("Two Dollars");		
		panel.add(selectCoinDropDown);
		selectCoinDropDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String coinName = (String)selectCoinDropDown.getSelectedItem();
				coinRefilled = Database.database().nameToCoin(coinName).getValue();
				
			}
		});
		
		JComboBox<String> selectBanknoteDropDown = new JComboBox<String>();
		selectBanknoteDropDown.addItem("Five Dollars");
		selectBanknoteDropDown.addItem("Ten Dollars");
		selectBanknoteDropDown.addItem("Twenty Dollars");
		selectBanknoteDropDown.addItem("Fifty Dollars");
		selectBanknoteDropDown.addItem("One Hundred Dollars");
		panel.add(selectBanknoteDropDown);
		selectBanknoteDropDown.addActionListener(new ActionListener () {

			@Override
			public void actionPerformed(ActionEvent e) {
				String banknoteName = (String) selectBanknoteDropDown.getSelectedItem();
				banknoteRefilled = Database.database().nameToBanknote(banknoteName).getValue();
				
				// TODO Auto-generated method stub
				
			}
			
		});
		
		return panel;
	}
	
	private static JPanel getLabelsPanel() {
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(10, 1, 0, 0));
		
		JLabel lblNewLabel_1 = new JLabel("");
		panel.add(lblNewLabel_1);
		
		JLabel lblNewLabel_1_2 = new JLabel("");
		panel.add(lblNewLabel_1_2);
		
		JLabel selectUnitsLabel = new JLabel("Select Units");
		selectUnitsLabel.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(selectUnitsLabel);
		
		JLabel selectQuantityLabel = new JLabel("Select Quantity");
		panel.add(selectQuantityLabel);
		
		JLabel selectCoinLabel = new JLabel("Select Coin");
		panel.add(selectCoinLabel);
		
		JLabel selectQuantityLabel2 = new JLabel("Select Banknote");
		panel.add(selectQuantityLabel2);
		
		/*JLabel selectBanknoteLabel = new JLabel("Select Banknote");
		panel.add(selectBanknoteLabel);*/
		
		/*JLabel selectQuantityLabel3 = new JLabel("Select Quantity ");
		panel.add(selectQuantityLabel3);*/
		
		return panel;
	}
	
	public static int getUserInput(){
		Boolean entry = true;
		
		while(entry = true) {
		String input = JOptionPane.showInputDialog("Enter quantity");
		if (input == null ) {
			JOptionPane.showMessageDialog(null, "Did not enter quantity");
		}
		else {
			try {
				Num = Integer.parseInt(input);
				if (Num > 0) {
					entry = false;
					break;
				}
				else {
					JOptionPane.showMessageDialog(null, "Can't be negative quantity");
				}
			}
			catch(NumberFormatException a) { 
		          JOptionPane.showMessageDialog(null, "Invalid quantity");
	        }
				
		}
		
	}
		return Num;	
}
		
	
	
	
	/*
	 * Call methods in here to notify
	 */
	private static void notifyPaperAdded(int paperAddQuantity, int stationID) throws OverloadException {
		UserInterfaceControlSoftware.storeAttendant.addPaperToPrinter(UserInterfaceControlSoftware.idToSelfCheckout.get(stationID).getFirst(), paperAddQuantity, stationID);
		// TODO Auto-generated method stub
		
	}
	
	private static void notifyInkAdded(int inkAddQuantity, int stationID) throws OverloadException {
		UserInterfaceControlSoftware.storeAttendant.addInkToPrinter(UserInterfaceControlSoftware.idToSelfCheckout.get(stationID).getFirst(), inkAddQuantity, stationID);
		// TODO Auto-generated method stub
		
	}

	private static void notifyCoinsAdded(BigDecimal coinRefilled, int coinNum, int stationID) throws SimulationException, OverloadException {
		
		Coin[] coinRefill = new Coin [coinNum];
		
		for (int i = 0; i < coinNum; i ++) {
			Coin coin = new Coin(Currency.getInstance("CAD"), coinRefilled );
			coinRefill [i] = coin;
		}
		UserInterfaceControlSoftware.storeAttendant.refiilsCoinDispenser(UserInterfaceControlSoftware.idToSelfCheckout.get(stationID).getFirst(), coinRefill);
		// TODO Auto-generated method stub
		
	}

	private static void notifyBanknotesAdded(int banknoteRefilled,int bankNum, int stationID) throws OverloadException {
		Banknote[] bankNoteRefill = new Banknote [bankNum];
		
		for (int i = 0; i < bankNum; i ++) {
			bankNoteRefill [i] = new Banknote (Currency.getInstance("CAD"), banknoteRefilled );
		}
		UserInterfaceControlSoftware.storeAttendant.refillBanknoteDispenser(UserInterfaceControlSoftware.idToSelfCheckout.get(stationID).getFirst(), bankNoteRefill);
		// TODO Auto-generated method stub
		
	}
	
	private static void notifyEmptiedCoinsBin(int stationID) {
		UserInterfaceControlSoftware.storeAttendant.emptyCoinStorage(UserInterfaceControlSoftware.idToSelfCheckout.get(stationID).getFirst());
		// TODO Auto-generated method stub
		
	}
	

	private static void notifyEmptiedBanknotesBin(int stationID) {
		SelfCheckoutStation hw = UserInterfaceControlSoftware.idToSelfCheckout.get(stationID).getFirst();
		UserInterfaceControlSoftware.storeAttendant.emptyBanknoteStorage(UserInterfaceControlSoftware.idToSelfCheckout.get(stationID).getFirst(), hw.banknoteStorage);;
		// TODO Auto-generated method stub
		
	}
	
	
}