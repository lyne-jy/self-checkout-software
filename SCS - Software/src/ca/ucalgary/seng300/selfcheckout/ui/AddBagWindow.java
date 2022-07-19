package ca.ucalgary.seng300.selfcheckout.ui;

import javax.swing.*;

import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ca.ucalgary.seng300.selfcheckout.SelfCheckoutControlSoftware;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Font;

@SuppressWarnings("serial")
public class AddBagWindow extends JPanel {
	public JTextField searchBar;
	private boolean checked;
	
	/**
	 * Create the panel.
	 */
	public AddBagWindow(SelfCheckoutControlSoftware sccs, JFrame frame) {
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
		c.gridwidth=1;
		
		//String[] chars = {,,,," "};
		String[] row1 = {"1","2","3","4","5","6","7","8","9","0"}; //, "back"};
		
		for(int i = 0; i<row1.length; i++) {
			JButton btn = new JButton(row1[i]);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = i;
			c.gridy = 2;
			c.weightx = 1;
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
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Are these your personal bags?");
		chckbxNewCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 15));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 6;
		c.weightx = 0;
		c.gridwidth = 5;
		add(chckbxNewCheckBox,c);
		checked = true;
		chckbxNewCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checked = !checked;
			}
		});

		btn = new JButton("Add Bag");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 5;
		c.weightx = 0;
		c.gridwidth = 2;
		//c.weighty= 0;
		add(btn,c);
		btn.setPreferredSize(new Dimension(110,55));
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int number = Integer.parseInt(searchBar.getText());
					SelfCheckoutUI.addBagsButtonPressed(sccs, number, !checked);
					SelfCheckoutUI.messagePanel.setText("Please place bags to bagging area.");
					frame.dispose();
				} catch(Exception nullpointer) {
				}
			}	
			
		}); 
	}
	
	public static void keyMap(JTextField t, JButton b) {
		String current = t.getText();
		t.setText(current+(b.getText()));
	}

}
