package ca.ucalgary.seng300.simulations;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

/*
 * This class just WRAPS actions realted to the hardware provided by Dr. Walker.
 * 
 * For this reason, we are not testing any method as all methods are just DELEGATING work
 * to the hardware code that we are ASSUMING has been FULLY tested.
 */
public class Customer {
	
	/* FOR THE UI [Copied from TOUCH SCREEN] */
	
	private JFrame frame;
	private volatile boolean ready = false;

	/**
	 * Creates a touch screen. The frame herein will initially be invisible.
	 */
	public Customer() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame = createGUI();
				ready = true;
			}
		});
		while(!ready);
	}

	private JFrame createGUI() {
		JFrame frame = new JFrame();

		frame.setVisible(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				super.windowOpened(e);
				ready = true;
			}
		});

		return frame;
	}

	/**
	 * Gets the {@link JFrame} object that should be in fullscreen mode for the
	 * touch screen. The frame will be invisible until it is explicitly made
	 * visible, by calling {@link #setVisible(boolean)} with a {@code true}
	 * argument.
	 * 
	 * @return The frame on which the graphical user interface can be deployed.
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * Allows the visibility of the {@link JFrame} to be changed.
	 * 
	 * @param state
	 *            True if the frame should be made visible; otherwise, false.
	 */
	public void setVisible(boolean state) {
		frame.setVisible(state);
	}
	
	/* ADDING ITEM */
		
	public void scanProductWithHandHeldScanner(SelfCheckoutStation scStation, Item item) {
		scStation.handheldScanner.scan(item);
	}
	
	public void placeItemInBaggingArea(SelfCheckoutStation scStation, Item item) {
		try {
			scStation.baggingArea.add(item);
		}catch(Exception e) {}
	}
	
	public void removeItemFromBaggingArea(SelfCheckoutStation scStation, Item item) {
		try {
			scStation.baggingArea.remove(item);
		}catch(Exception e) {
			
		}
		
	}
	
	public void placeItemScanningArea(SelfCheckoutStation scStation, Item item) {
		try {
			scStation.scanningArea.add(item);
		}catch(Exception e) {}
	}
	
	public void removeItemFromScanningArea(SelfCheckoutStation scStation, Item item) {
		try {
			scStation.scanningArea.remove(item);
		}catch(Exception e) {}
	}
		
	/* PAYMENT */
	
	public void insertBanknote(SelfCheckoutStation scStation, Banknote banknote) {
		try { 
			scStation.banknoteInput.accept(banknote);
		}catch(Exception e) {
			try {
				scStation.banknoteInput.removeDanglingBanknotes();
			}catch(Exception e2) {}
		}
	}
	
	public void insertCoin(SelfCheckoutStation scStation, Coin coin) {
		
		try {
			scStation.coinSlot.accept(coin);
			
		}catch(Exception e) {}
		
		// In case any coin is returned 
		try {
			removeCoinsFromTray(scStation);
		}catch(Exception e) {}
	}
	
	public void insertCardWithChip(SelfCheckoutStation scStation, Card card, String pin) {
		try {
			scStation.cardReader.insert(card, pin);
			
		}catch(Exception e) {}
	}
	
	public void swipeCard(SelfCheckoutStation scStation, Card card) {
		try {
			scStation.cardReader.swipe(card);
		}catch(Exception e) {}
	}
	
	public void tapCard(SelfCheckoutStation scStation, Card card) {
		try {
			scStation.cardReader.tap(card);
		}catch(Exception e) {}
	}
	
	/* MEMBERHSIP */
	
	public void scanMembership(SelfCheckoutStation scStation, Item item) {
		scStation.handheldScanner.scan(item);
	}
	
	/* CHANGE RETURN */
	
	public void removeBanknotes(SelfCheckoutStation scStation) {
		try {
			scStation.banknoteOutput.removeDanglingBanknotes();
		}catch(Exception e) {}
	}
	
	public void removeCoinsFromTray(SelfCheckoutStation scStation) {
		try {
			scStation.coinTray.collectCoins();
		}catch(Exception e) {}
	}
	
	public void removeReceipt(SelfCheckoutStation scStation) {
		try {
			scStation.printer.removeReceipt();
		}catch(Exception e) {}
	}
}
