package ca.ucalgary.seng300.selfcheckout.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import ca.ucalgary.seng300.attendant.ui.SupervisorUI;
import ca.ucalgary.seng300.selfcheckout.SelfCheckoutControlSoftware;
import ca.ucalgary.seng300.selfcheckout.utility.Cart;
import ca.ucalgary.seng300.selfcheckout.utility.Database;
import ca.ucalgary.seng300.ui.UserInterfaceControlSoftware;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

@SuppressWarnings("serial")
public class Scroller extends JPanel {

	/**
	 * Create the panel.
	 */
	SelfCheckoutControlSoftware sccs;
	JFrame parent;
	public Scroller(SelfCheckoutControlSoftware softInstance, JFrame frame) {sccs = softInstance; parent = frame;}
	
	public void supervisorUpdate(char c) {
		removeAll();
		Cart cart = SupervisorUI.cart;
		for (BarcodedProduct b : ProductDatabases.BARCODED_PRODUCT_DATABASE.values()) {
			if(Character.toLowerCase(b.getDescription().charAt(0))==c) {
				
				JButton btn = new JButton(b.getDescription());
				btn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						cart.addBarcodedItemToCart(b.getBarcode(), b.getPrice());
						UserInterfaceControlSoftware.productAddedToPurchase(cart);
					}		
				});
				add(btn);
			}
		}
		for (PLUCodedProduct p : ProductDatabases.PLU_PRODUCT_DATABASE.values()) {
			if(Character.toLowerCase(p.getDescription().charAt(0))==c) {
				JButton btn = new JButton(p.getDescription());
				btn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						cart.addPLUItemToCart(p.getPLUCode(), p.getPrice(), Database.database().pluToWeight(p.getPLUCode()));
						UserInterfaceControlSoftware.productAddedToPurchase(cart);
					}
				});
				add(btn);
			}
		}
	}
	
	public void customerUpdate(char c) {
		removeAll();
		for (PLUCodedProduct p : ProductDatabases.PLU_PRODUCT_DATABASE.values()) {
			if(Character.toLowerCase(p.getDescription().charAt(0))==c) {

				JButton btn = new JButton(p.getDescription());
				btn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						SelfCheckoutUI.enterPLUButtonPressed(sccs, p.getPLUCode());
						parent.setVisible(false);
						parent.dispose();
					}
				});
				add(btn);
			}
		}
	}
}