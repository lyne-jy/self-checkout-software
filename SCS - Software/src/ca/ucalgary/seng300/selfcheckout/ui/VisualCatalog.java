package ca.ucalgary.seng300.selfcheckout.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import ca.ucalgary.seng300.selfcheckout.SelfCheckoutControlSoftware;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.SpringLayout;

//This one is barely done
@SuppressWarnings("serial")
public class VisualCatalog extends JPanel {

	/**
	 * Create the panel.
	 */
	@SuppressWarnings("rawtypes")
	public static JComboBox alphabox;
	Scroller sc;

	SelfCheckoutControlSoftware sccs;
	JFrame parent;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public VisualCatalog(SelfCheckoutControlSoftware softInstance, JFrame frame) {

		//testing with barcodes in the database
		alphabox = new JComboBox();
		sccs=softInstance;
		parent = frame;
        //alpabox.setFont(new Font("Sitka Heading", Font.PLAIN, 18));
        alphabox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                if(arg0.getStateChange()==ItemEvent.SELECTED){
                    update();
                }
            }
        });
        SpringLayout springLayout = new SpringLayout();
        setLayout(springLayout);
        
        JButton cancelBtn = new JButton("Back");
        springLayout.putConstraint(SpringLayout.NORTH, cancelBtn, 0, SpringLayout.NORTH, alphabox);
        springLayout.putConstraint(SpringLayout.WEST, cancelBtn, 25, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.SOUTH, cancelBtn, 5, SpringLayout.SOUTH, alphabox);

		//c.weighty= 0;
		add(cancelBtn);
		cancelBtn.setPreferredSize(new Dimension(110,55));
		cancelBtn.addActionListener(new ActionListener() {	
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//return to checkout screen, do nothing
				parent.setVisible(false);
				parent.dispose();
				
			}
			
		}); 
        alphabox.setModel(new DefaultComboBoxModel(new String[] {"", "A",
        		"B",
        		"C",
        		"D",
        		"E",
        		"F",
        		"G",
        		"H",
        		"I",
        		"J",
        		"K",
        		"L",
        		"M",
        		"N",
        		"O",
        		"P",
        		"Q",
        		"R",
        		"S",
        		"T",
        		"U",
        		"V",
        		"W",
        		"X",
        		"Y",
        		"Z"}));
        alphabox.setSelectedIndex(0);
        add(alphabox);
        
        JLabel lblNewLabel = new JLabel("Alphabetical Lookup");
        springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel, 8, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, lblNewLabel, 235, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.NORTH, alphabox, -3, SpringLayout.NORTH, lblNewLabel);
        springLayout.putConstraint(SpringLayout.EAST, alphabox, -8, SpringLayout.WEST, lblNewLabel);
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblNewLabel);
        
		sc = new Scroller(sccs, parent);
		springLayout.putConstraint(SpringLayout.NORTH, sc, 47, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, sc, 30, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, sc, 290, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, sc, 548, SpringLayout.WEST, this);
		FlowLayout flowLayout = (FlowLayout) sc.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
        add(sc);
	}
	
	public void update() {	
		sc.removeAll();
		String test = alphabox.getSelectedItem().toString();
		if(test.length()==0)
			return;
		
		char alpha = Character.toLowerCase(test.charAt(0));
		//remove(sc);
		if (this.getName().equals("supervisor"))
			sc.supervisorUpdate(alpha);
		else
			sc.customerUpdate(alpha);
		//add(sc);
		revalidate();
		repaint();
	}
}