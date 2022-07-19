package ca.ucalgary.seng300.simulations;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.SimulationException;
import org.lsmr.selfcheckout.devices.BanknoteStorageUnit;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import ca.ucalgary.seng300.selfcheckout.SelfCheckoutControlSoftware;
import ca.ucalgary.seng300.selfcheckout.communication.SelfCheckoutNotifier;
import ca.ucalgary.seng300.selfcheckout.utility.ComplexAbstractSoftware;

/*
 * Class is used to simulate the actions taken in the real world by the attendant 
 * This mixes the functionality a Coordinator and Notifier to simplify things as 
 * this is just a simulation of physical actions.
 */
public class StoreAttendant extends ComplexAbstractSoftware<StoreAttendantObserver>{

	/* FOR THE UI [Copied from TOUCH SCREEN] */
	
	private JFrame frame;
	private volatile boolean ready = false;
		
	public StoreAttendant(SelfCheckoutControlSoftware... controlSoftwares) {
		
		for(SelfCheckoutControlSoftware sccs : controlSoftwares) {
			attach(sccs.notifier);
		}
		
		
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
	
	/* MESSAGES FROM UI */
	
	// adds paper to the printer
	public void addPaperToPrinter(SelfCheckoutStation hw, int units, int id) throws OverloadException {
		hw.printer.addPaper(units);
		notifyPaperAddedToPrinter(units, id);
	}
	
	// adds ink to the printer
	public void addInkToPrinter(SelfCheckoutStation hw, int quantity, int id) throws OverloadException {
		hw.printer.addInk(quantity);
		notifyInkAddedToPrinter(quantity, id);
	}
	
	// attenandt empties coin storage unit
	public void emptyCoinStorage(SelfCheckoutStation hw) {
		hw.coinStorage.unload();
	}
	
	// attendant empties banknote storage unit
	public void emptyBanknoteStorage(SelfCheckoutStation hw, BanknoteStorageUnit storage) {
		hw.banknoteStorage.unload();
	}
	
	// attendant refills coin dispenser
	public void refiilsCoinDispenser(SelfCheckoutStation hw, Coin... coins) throws SimulationException, OverloadException {
		
		if(coins.length == 0) return;
		
		BigDecimal coinDenom = (coins[0].getValue());
		
		for(Coin coin : coins) {
				if(!hw.coinDispensers.get(coinDenom).hasSpace()) break;
				hw.coinDispensers.get(coinDenom).load(coin);
		}
	}
	
	// attendant refills banknote dispenser
	public void refillBanknoteDispenser(SelfCheckoutStation hw, Banknote... banknotes) throws OverloadException {
		if(banknotes.length == 0) return;
		
		for(Banknote banknote : banknotes) {
			
			if(hw.banknoteDispensers.get(banknote.getValue()).getCapacity() - hw.banknoteDispensers.get(banknote.getValue()).size() <= 0) break;
			hw.banknoteDispensers.get(banknote.getValue()).load(banknote);		
		}
	}
	
	/* OBSERVERS */
	
	private void notifyPaperAddedToPrinter(int unit, int id) {
		for(StoreAttendantObserver obs: observers) {
			if(obs instanceof SelfCheckoutNotifier && ((SelfCheckoutNotifier)obs).id == id) {
				obs.paperAddedToPrinter(unit);
			}
		}
	}
	
	private void notifyInkAddedToPrinter(int quantity, int id) {
		for(StoreAttendantObserver obs : observers) {
			obs.inkAddedToPrinter(quantity);
		}
	}
}
