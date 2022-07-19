package ca.ucalgary.seng300.selfcheckout.ui;

import javax.swing.JPanel;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.PriceLookupCode;
import ca.ucalgary.seng300.selfcheckout.SelfCheckoutControlSoftware;
import ca.ucalgary.seng300.selfcheckout.utility.Database;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

@SuppressWarnings("serial")
public class KeyboardWindow extends JPanel {
	private JTextField searchBar;
	/**
	 * Create the panel.
	 * @param parent 
	 */
	public KeyboardWindow(SelfCheckoutControlSoftware sccs, String name, JFrame parent) {
		//JFrame parent = (JFrame)SwingUtilities.getWindowAncestor(this);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0};
		gridBagLayout.rowHeights = new int[] {5};
		gridBagLayout.columnWeights = new double[]{Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{Double.MIN_VALUE};
		setLayout(gridBagLayout);
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 1;
		c.weightx = 0;
		c.gridwidth = 5;
		searchBar = new JTextField();
		add(searchBar, c);
		searchBar.setEditable(false);
		//textField.setColumns(55);
		c.gridwidth=1;
		
		//String[] chars = {,,,," "};
		String[] row1 = {"1","2","3","4","5","6","7","8","9","0"}; //, "back"};
		String[] row2 = {"q","w","e","r","t","y","u","i","o","p"};
		String[] row3 = {"a","s","d","f","g","h","j","k","l"};
		String[] row4 = {"z","x","c","v","b","n","m"};
		
		for(int i = 0; i<row1.length; i++) {
			JButton btn = new JButton(row1[i]);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = i;
			c.gridy = 2;
			c.weightx = 0.5;
			//c.weighty= 0;
			add(btn,c);
			btn.setPreferredSize(new Dimension(55,55));
			btn.addActionListener(new ActionListener() {	
				@Override
				public void actionPerformed(ActionEvent e) {
					keyMap(searchBar, btn);
					
				}
				
			});
		}
		
		for(int i = 0; i<row2.length; i++) {
			JButton btn = new JButton(row2[i]);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = i;
			c.gridy = 3;
			c.weightx = 0.5;
			//c.weighty= 0;
			add(btn,c);
			btn.setPreferredSize(new Dimension(55,55));
			btn.addActionListener(new ActionListener() {	
				@Override
				public void actionPerformed(ActionEvent e) {
					keyMap(searchBar, btn);
					
				}
				
			});
		}
		
		for(int i = 0; i<row3.length; i++) {
			JButton btn = new JButton(row3[i]);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = i;
			c.gridy = 4;
			c.weightx = 0.5;
			//c.weighty= 0;
			add(btn,c);
			btn.setPreferredSize(new Dimension(55,55));
			btn.addActionListener(new ActionListener() {	
				@Override
				public void actionPerformed(ActionEvent e) {
					keyMap(searchBar, btn);
					
				}
				
			});
		}
		
		for(int i = 0; i<row4.length; i++) {
			JButton btn = new JButton(row4[i]);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = i +1;
			c.gridy = 5;
			c.weightx = 0.5;
			//c.weighty= 0;
			add(btn,c);
			btn.setPreferredSize(new Dimension(55,55));
			btn.addActionListener(new ActionListener() {	
				@Override
				public void actionPerformed(ActionEvent e) {
					keyMap(searchBar, btn);
					
				}
				
			});
		}
		
		JButton btn = new JButton("backspace");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = row4.length+1;
		c.gridy = 6;
		c.weightx = 0;
		c.gridwidth = 2;
		//c.weighty= 0;
		add(btn,c);
		btn.setPreferredSize(new Dimension(110,55));
		btn.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				String current = searchBar.getText();
				if(current.length() != 0)
					searchBar.setText(current.substring(0,current.length()-1));
			}
			
		});
		
		btn = new JButton("Search");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = row4.length+1;
		c.gridy = 5;
		c.weightx = 0;
		c.gridwidth = 2;
		//c.weighty= 0;
		add(btn,c);
		btn.setPreferredSize(new Dimension(110,55));
		btn.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				if (name.equals("PLU")) {

					if(searchBar.getText().length() < 4) return;
					
					try {
						PriceLookupCode PLUEntered = new PriceLookupCode(searchBar.getText());
						SelfCheckoutUI.enterPLUButtonPressed(sccs, PLUEntered);
						parent.setVisible(false);
						parent.dispose();
					}catch(Exception notPLU){
						
						SelfCheckoutUI.showItemNotInDatabase(parent, "PLU");
					}		

				} else if (name.equals("lookupByName")) {
					
					PriceLookupCode PLUEntered = Database.database().nameToPLU(searchBar.getText());
					SelfCheckoutUI.enterPLUButtonPressed(sccs, PLUEntered);
					
				} else if (name.equals("Membership")) {
					
					Numeral [] membership = new Numeral[searchBar.getText().length()];
					for (int i = 0; i < searchBar.getText().length(); i++){
					    char c = searchBar.getText().charAt(i); 
				        Boolean flag = Character.isDigit(searchBar.getText().charAt(i));
					    if (flag) {
					    	membership[i] = Numeral.valueOfString(c);
					    } else {
							membership = new Numeral[0]; 
							break;
					    }
					}
					SelfCheckoutUI.membershipCardEntered(sccs, membership);
					try {
						SelfCheckoutUI.membershipCardEntered(sccs, membership);
						parent.setVisible(false);
						parent.dispose();
					}catch(Exception notMem){
						
						SelfCheckoutUI.showMembershipCardNotInDatabase(parent);
					}
					
				} else if(name.equals("GiftCard")) {
					
					SelfCheckoutUI.payWithGiftCardButtonPressed(sccs, searchBar.getText());
				}
			}
			
		}); 
		
		btn = new JButton("Cancel");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 6;
		c.weightx = 0;
		c.gridwidth = 2;
		//c.weighty= 0;
		add(btn,c);
		btn.setPreferredSize(new Dimension(110,55));
		btn.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				//return to checkout screen, do nothing
				parent.setVisible(false);
				parent.dispose();
			}
			
		}); 
		
		btn = new JButton();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 6;
		c.weightx = 0;
		c.gridwidth = 5;
		//c.weighty= 0;
		add(btn,c);
		btn.setPreferredSize(new Dimension(275,55));
		btn.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				String current = searchBar.getText();
				searchBar.setText(current+(" "));	
			}
			
		});


	}
	
	
	public static void keyMap(JTextField t, JButton b) {
		String current = t.getText();
		t.setText(current+(b.getText()));
	}

}
