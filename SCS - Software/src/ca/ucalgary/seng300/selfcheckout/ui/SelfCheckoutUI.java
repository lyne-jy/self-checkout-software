package ca.ucalgary.seng300.selfcheckout.ui;

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.*;

import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.PriceLookupCode;
import ca.ucalgary.seng300.selfcheckout.SelfCheckoutControlSoftware;
import ca.ucalgary.seng300.selfcheckout.utility.Cart;
import ca.ucalgary.seng300.selfcheckout.utility.Database;

/*
 * Class that provides all functionality related to the user interface
 */
public class SelfCheckoutUI{
	
	public static JTextPane messagePanel = null;
	public static JTextPane cartPane = null;
	
	public static Cart cart = null;
	
	public static JButton lookupByNameBtn = null; 
	public static JButton enterPLUCodeBtn = null;
	public static JButton membershipBtn = null;
	public static JButton membershipScanBtn = null;
	public static JButton useOwnBagBtn = null;
	public static JButton checkoutBtn = null;
	
	public static JButton cashPay = null;
	public static JButton cardPay = null;
	public static JButton giftcardPay = null;
	public static JButton addMoreItems = null;
	public static JButton cancelPayment = null;
	
	public static JTextField messageBox;
	public static JTextField txtWelcome;
	public static JTextField extraInfoTextField;
	
	public static BigDecimal cashInserted = BigDecimal.ZERO;
	//public static BigDecimal cashInserted;
	
	/*
	 * Shows the first screen when the station is ready to scan items
	 */
	public static void showStartScreen(JFrame frame, SelfCheckoutControlSoftware sccs, boolean restart) {
		
		if(cart == null || restart == true) cart = new Cart(sccs.ID);
		
		frame.setBounds(50, 0, 1150, 580);
		frame.setLocation(0,0);
		
		
		frame.getContentPane().removeAll(); // get rid of any other components
		
		// Left panel for showing cart items
		frame.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(1, 0, 0, 0));

		if(cartPane == null || restart == true) {
			cartPane = new JTextPane();
			cartPane.setFont(new Font("Lucida Grande", Font.BOLD, 30));
			cartPane.setText(cart.toString());
			cartPane.setEditable(false);
		}
		
		leftPanel.add(new JScrollPane(cartPane));
		frame.getContentPane().add(leftPanel);
		
		// Right panel to who messages and buttons
		JPanel rightPanel = new JPanel();
		frame.getContentPane().add(rightPanel);
		GridBagLayout gbl_rightPanel = new GridBagLayout();
		
		gbl_rightPanel.columnWidths = new int[]{0, 0};
		gbl_rightPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_rightPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_rightPanel.rowWeights = new double[]{1.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
		rightPanel.setLayout(gbl_rightPanel);
		
		// Message panel for showing messages
		messagePanel = new JTextPane();
		messagePanel.setDropMode(DropMode.INSERT);
		messagePanel.setFont(new Font("Lucida Grande", Font.PLAIN, 50));
		messagePanel.setText("Ready to Add Item");
		messagePanel.setEditable(false);
		messagePanel.setBackground(new Color(0, 206, 209));
		
		StyledDocument doc = messagePanel.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		
		GridBagConstraints gbc_messagePanel = new GridBagConstraints();
		gbc_messagePanel.gridheight = 2;
		gbc_messagePanel.insets = new Insets(0, 0, 10, 0);
		gbc_messagePanel.fill = GridBagConstraints.BOTH;
		gbc_messagePanel.gridx = 0;
		gbc_messagePanel.gridy = 0;
		rightPanel.add(messagePanel, gbc_messagePanel);
		
		// Panel that includes all buttons
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(new Color(192, 192, 192));
		GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
		gbc_buttonPanel.gridheight = 2;
		gbc_buttonPanel.fill = GridBagConstraints.BOTH;
		gbc_buttonPanel.gridx = 0;
		gbc_buttonPanel.gridy = 2;
		rightPanel.add(buttonPanel, gbc_buttonPanel);
		buttonPanel.setLayout(new GridLayout(3, 3, 1, 1));		
		
		
		// All Buttons
		lookupByNameBtn = new JButton("Lookup By Name");
		lookupByNameBtn.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		lookupByNameBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				raiseVisualCatalog(sccs,frame);
				frame.revalidate();
			}
			
		});
		buttonPanel.add(lookupByNameBtn);
		
		enterPLUCodeBtn = new JButton("Enter Item PLU");
		enterPLUCodeBtn.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		enterPLUCodeBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				enterPLUKeyboard(sccs, frame);
				frame.revalidate();
			}
			
		});
		buttonPanel.add(enterPLUCodeBtn);
		
		
		membershipBtn = new JButton("Enter Membership Number");
		membershipBtn.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		membershipBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				scanMembershipButtonPressed(sccs);
				enterMembershipKeyboard(sccs, frame);
				frame.revalidate();
			}
			
		});
		buttonPanel.add(membershipBtn);
		
		membershipScanBtn = new JButton("Scan Membership Card");
		membershipScanBtn.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		membershipScanBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				messagePanel.setText("Please scan membership card");
				scanMembershipButtonPressed(sccs);
				frame.revalidate();
			}
			
		});
		buttonPanel.add(membershipScanBtn);
		
		useOwnBagBtn = new JButton("Add Bag");
		useOwnBagBtn.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		useOwnBagBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addBagNumPad(sccs, frame);
			}
			
		});
		buttonPanel.add(useOwnBagBtn);
		
		checkoutBtn = new JButton("Checkout");
		
		checkoutBtn.setOpaque(true);
		checkoutBtn.setBackground(new Color(156, 203, 99));
		checkoutBtn.setForeground(Color.BLACK);
		checkoutBtn.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
				
		if(!SelfCheckoutUI.cart.isEmpty()) checkoutBtn.setEnabled(true);
		else 							   checkoutBtn.setEnabled(false);
		
		checkoutBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				checkoutButtonPressed(sccs);
				showPaymentSelectionScreen(frame, sccs);				
			}

		});
		
		buttonPanel.add(checkoutBtn);
		
		
		
		//;
		frame.setVisible(true);
	}	
	
	public static void showThankyouScreen(JFrame frame, boolean partialChange) {
		
		frame.setBounds(50, 0, 1150, 580);
	
		//;
		frame.getContentPane().removeAll();
		frame.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		
		JTextField thankYouMessageTextField = new JTextField();
		thankYouMessageTextField.setFont(new Font("Lucida Grande", Font.BOLD, 30));
		thankYouMessageTextField.setHorizontalAlignment(SwingConstants.CENTER);
		thankYouMessageTextField.setEditable(false);
		thankYouMessageTextField.setText("Thank you for purchasing with us!!");
		frame.getContentPane().add(thankYouMessageTextField);
		thankYouMessageTextField.setColumns(10);
		
		extraInfoTextField = new JTextField();
		extraInfoTextField.setEditable(false);
		extraInfoTextField.setHorizontalAlignment(SwingConstants.CENTER);
		extraInfoTextField.setForeground(Color.RED);
		extraInfoTextField.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
		
		if(partialChange == true) extraInfoTextField.setText("Ask attendant for remaning change ");
		else extraInfoTextField.setText("");
		
		frame.getContentPane().add(extraInfoTextField);
		extraInfoTextField.setColumns(10);	
		
		frame.setVisible(true);
	}
	
	public static void showOffScreen(JFrame frame) {
		frame.setBounds(50, 0, 1150, 580);
		
		frame.getContentPane().removeAll();
		
		frame.getContentPane().setBackground(Color.BLACK);
		
		;
		
		frame.setVisible(true);
	}
	
	private static void showPaymentSelectionScreen(JFrame frame, SelfCheckoutControlSoftware sccs) {
		
		frame.getContentPane().removeAll(); // get rid of any other components
		
		// Left panel for showing cart items
		frame.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
		JScrollPane leftScrollPanel = new JScrollPane(cartPane);
		
		;
		frame.getContentPane().add(leftScrollPanel);
				
		// Right panel 
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridLayout(6, 1, 5, 5));
		
		messageBox = new JTextField();
		messageBox.setEditable(false);
		messageBox.setFont(new Font("Lucida Grande", Font.PLAIN, 25));
		messageBox.setText("Please Select a Payment Method");
		messageBox.setHorizontalAlignment(SwingConstants.CENTER);
		messageBox.setBackground(new Color(0, 206, 209));
		rightPanel.add(messageBox);
		messageBox.setColumns(10);
		//cashInserted = BigDecimal.ZERO;
		
		//connect to user simulator

		cashPay = new JButton("Pay with Cash");

		cashPay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cashInserted = cashInserted.setScale(2,RoundingMode.FLOOR);
				messageBox.setText("Inserted Funds: $ " + (SelfCheckoutUI.cashInserted));
				payWithCashButtonPressed(sccs);
				disablePayingFrame(false);
				cancelPayment.setEnabled(true);
			}
			
		});
		rightPanel.add(cashPay);
		
		cardPay = new JButton("Pay with Card");
		cardPay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				messageBox.setText("Insert, Swipe, or Tap Card");
				payWithCardButtonPressed(sccs);
				disablePayingFrame(true);
				cancelPayment.setEnabled(true);
			}
			
		});
		rightPanel.add(cardPay);
		
		giftcardPay = new JButton("Pay with Giftcard");
		giftcardPay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				messageBox.setText("Insert Giftcard Number");
				disablePayingFrame(true);
				cancelPayment.setEnabled(true);

				payWithGiftCardKeyboard(sccs, frame);

			}
			
		});
		rightPanel.add(giftcardPay);
		
		addMoreItems = new JButton("Add more Items");
		addMoreItems.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showStartScreen(frame, sccs, false);
				addMoreItemsButtonPresses(sccs);	
			}
		});
		rightPanel.add(addMoreItems);
		
		cancelPayment = new JButton("Cancel Payment");
		cancelPayment.setEnabled(false);
		cancelPayment.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showPaymentSelectionScreen(frame, sccs);
				// return unused funds
			}
		});
		rightPanel.add(cancelPayment);
		
		
		frame.getContentPane().add(rightPanel);
		
		frame.revalidate();
		frame.repaint();
		
		frame.setVisible(true);
	}

	/* POP UPS */
	
	public static void  showMembershipScannedSuccesfully(JFrame mainFrame, Barcode barcode) {
		
		disableMainFrame(mainFrame);
		
		JFrame frame = new JFrame();
		frame.setBounds(100, 100, 600, 200);
		;
	    JPanel contentPane = new JPanel();
	    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	    frame.setContentPane(contentPane);
	    contentPane.setLayout(new GridLayout(1, 0, 0, 0));
	    
	    JTextField txtWelcome = new JTextField();
	    txtWelcome.setEditable(false);
	    txtWelcome.setHorizontalAlignment(SwingConstants.CENTER);
	    txtWelcome.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
	    txtWelcome.setText("Welcome " + Database.database().membershipToName(barcode));
	    contentPane.add(txtWelcome);
	    txtWelcome.setColumns(10);
	    
	    frame.addWindowListener((WindowListener) new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowClosing(WindowEvent e) {	
				enableMainFrame(mainFrame);
				SelfCheckoutUI.messagePanel.setText("Scan Item and Place it in Bagging Area");
				mainFrame.revalidate();
				mainFrame.repaint();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				enableMainFrame(mainFrame);
				mainFrame.revalidate();
				mainFrame.repaint();
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// ignore
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// ignore
				
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// ignore
			}
	    	
	    });
	   
		frame.setVisible(true);
	}
	
	public static void showItemNotInDatabase(JFrame mainFrame, String source) {
		
		disableMainFrame(mainFrame);
	   
		JFrame frame = new JFrame();
		frame.setBounds(100, 100, 600, 200);
		;
				
	    JPanel contentPane = new JPanel();
	    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	    frame.setContentPane(contentPane);
	    contentPane.setLayout(new GridLayout(1, 0, 0, 0));
	    
	    JTextField txtWelcome = new JTextField();
	    txtWelcome.setEditable(false);
	    txtWelcome.setHorizontalAlignment(SwingConstants.CENTER);
	    txtWelcome.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
	    txtWelcome.setText(source + " Product does not exist");
	    contentPane.add(txtWelcome);
	    txtWelcome.setColumns(10);
	    
	    frame.addWindowListener((WindowListener) new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowClosing(WindowEvent e) {	
				enableMainFrame(mainFrame);
				mainFrame.revalidate();
				mainFrame.repaint();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				enableMainFrame(mainFrame);
				mainFrame.revalidate();
				mainFrame.repaint();
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// ignore
			}
	    	
	    });
	        
		frame.setVisible(true);
	}
	
	public static void showMembershipCardNotInDatabase(JFrame mainFrame) {
		
		disableMainFrame(mainFrame);
		mainFrame.revalidate();
		
		JFrame frame = new JFrame();
		;
		frame.setBounds(100, 100, 600, 200);
	    JPanel contentPane = new JPanel();
	    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	    frame.setContentPane(contentPane);
	    contentPane.setLayout(new GridLayout(1, 0, 0, 0));
	    
	    JTextField txtWelcome = new JTextField();
	    txtWelcome.setEditable(false);
	    txtWelcome.setHorizontalAlignment(SwingConstants.CENTER);
	    txtWelcome.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
	    txtWelcome.setText("Membership Card not found");
	    contentPane.add(txtWelcome);
	    txtWelcome.setColumns(10);
	      	    
	    
	    frame.addWindowListener((WindowListener) new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowClosing(WindowEvent e) {	
				enableMainFrame(mainFrame);
				SelfCheckoutUI.messagePanel.setText("Ready to Add Item");
				mainFrame.revalidate();
				mainFrame.repaint();
				enableMainFrame(mainFrame);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				enableMainFrame(mainFrame);
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// ignore
			}
	    	
	    });
	    
	    
		frame.setVisible(true);			
	}
	
	public static void showCardPaymentError(JFrame mainFrame) {
		
			
		disablePayingFrame(false);
		cancelPayment.setEnabled(false);
	
		JFrame frame = new JFrame();
		frame.setBounds(100, 100, 600, 200);
		;
	    JPanel contentPane = new JPanel();
	    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	    frame.setContentPane(contentPane);
	    contentPane.setLayout(new GridLayout(1, 0, 0, 0));
	    
	    JTextField txtWelcome = new JTextField();
	    txtWelcome.setEditable(false);
	    txtWelcome.setHorizontalAlignment(SwingConstants.CENTER);
	    txtWelcome.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
	    txtWelcome.setText("Not enough funds in Card/Giftcard");
	    contentPane.add(txtWelcome);
	    txtWelcome.setColumns(10);
	    
	    frame.addWindowListener((WindowListener) new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowClosing(WindowEvent e) {					
				cashPay.setEnabled(true);
				cardPay.setEnabled(true);
				giftcardPay.setEnabled(true);
				addMoreItems.setEnabled(true);
				cancelPayment.setEnabled(true);
			}

			@Override
			public void windowClosed(WindowEvent e) {		
				cashPay.setEnabled(true);
				cardPay.setEnabled(true);
				giftcardPay.setEnabled(true);
				addMoreItems.setEnabled(true);
				cancelPayment.setEnabled(true);
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// ignore
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// ignore
			}
	    	
	    });
	    
		frame.setVisible(true);
	}
	
	/* OTHER HELPER METHODS */

	public static void enterPLUKeyboard(SelfCheckoutControlSoftware sccs, JFrame mainFrame) {
		
		disableMainFrame(mainFrame);

		JFrame frame = new JFrame();
		KeyboardWindow keyboard = new KeyboardWindow(sccs, "PLU", frame);
		frame.setBounds(100, 100, 800, 200);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5,5,5,5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(1,0,0,0));
		contentPane.add(keyboard);
	
		frame.addWindowListener((WindowListener) new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowClosing(WindowEvent e) {
				enableMainFrame(mainFrame);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				enableMainFrame(mainFrame);	
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// ignore				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// ignore
				
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// ignore				
			}

        });
		
		frame.setVisible(true);
	    
	}
	
	private static void payWithGiftCardKeyboard(SelfCheckoutControlSoftware sccs, JFrame mainFrame) {
		
	
		JFrame frame = new JFrame();
		KeyboardWindow keyboard = new KeyboardWindow(sccs, "GiftCard",frame);
		frame.setBounds(100, 100, 800, 200);
		;
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5,5,5,5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(1,0,0,0));
		contentPane.add(keyboard);
	
		frame.addWindowListener((WindowListener) new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// ignore
				
			}

			@Override
			public void windowClosing(WindowEvent e) {
				// enable the button
				enableMainFrame(mainFrame);
				
			}

			@Override
			public void windowClosed(WindowEvent e) {
				enableMainFrame(mainFrame);
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// ignore
			}

        });
		
		frame.setVisible(true);
	}
	
	public static void updateTotal(JFrame frame, SelfCheckoutControlSoftware sccs) {
		cashInserted = cashInserted.setScale(2,RoundingMode.FLOOR);
		messageBox.setText("Inserted Funds: $ " + (SelfCheckoutUI.cashInserted));
		payWithCashButtonPressed(sccs);
		disablePayingFrame(false);
		cancelPayment.setEnabled(true);
	}
	
	private static void raiseVisualCatalog(SelfCheckoutControlSoftware sccs, JFrame mainFrame) {

		disableMainFrame(mainFrame);
		
		JFrame frame = new JFrame();
		VisualCatalog vc = new VisualCatalog(sccs, frame);
		vc.setName("customer");
		frame.setBounds(100, 100, 800, 200);
		;
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5,5,5,5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(1,0,0,0));
		contentPane.add(vc);
		
		frame.addWindowListener((WindowListener) new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowClosing(WindowEvent e) {
				// enable the button
				enableMainFrame(mainFrame);
				
			}

			@Override
			public void windowClosed(WindowEvent e) {
				enableMainFrame(mainFrame);
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// ignore
			}

        });
		
		frame.setVisible(true);
	}
	
	public static void disableMainFrame(JFrame mainFrame) {
		//disable all the buttons 
		lookupByNameBtn.setEnabled(false);
		enterPLUCodeBtn.setEnabled(false);
		membershipBtn.setEnabled(false);
		membershipScanBtn.setEnabled(false);
		useOwnBagBtn.setEnabled(false);
		checkoutBtn.setEnabled(false);
	}
	
	public static void enableMainFrame(JFrame mainFrame) {
		//enable all the buttons 
		lookupByNameBtn.setEnabled(true);
		enterPLUCodeBtn.setEnabled(true);
		membershipBtn.setEnabled(true);
		membershipScanBtn.setEnabled(true);
		useOwnBagBtn.setEnabled(true);
		checkoutBtn.setEnabled(true);

	}
	
	private static void disablePayingFrame(boolean disableAddMoreItems) {
		cashPay.setEnabled(false);
		cardPay.setEnabled(false);
		giftcardPay.setEnabled(false);
		
		if(disableAddMoreItems)
			addMoreItems.setEnabled(false);
	}
	
	
	@SuppressWarnings("unused")
	private static void enablePayingFrame() {
		cashPay.setEnabled(false);
		cardPay.setEnabled(false);
		giftcardPay.setEnabled(false);
		addMoreItems.setEnabled(false);
	}
	
	private static void enterMembershipKeyboard(SelfCheckoutControlSoftware sccs, JFrame mainFrame) {
		
		disableMainFrame(mainFrame);

		JFrame frame = new JFrame();
		KeyboardWindow keyboard = new KeyboardWindow(sccs, "Membership", frame);
		frame.setBounds(100, 100, 800, 200);
		;
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5,5,5,5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(1,0,0,0));
		contentPane.add(keyboard);
		
		frame.setVisible(true);
		
		frame.addWindowListener((WindowListener) new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowClosing(WindowEvent e) {
				// enable the button
				enableMainFrame(mainFrame);
				
			}

			@Override
			public void windowClosed(WindowEvent e) {
				enableMainFrame(mainFrame);
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// ignore				
			}

        });
		
	}
	
	private static void addBagNumPad(SelfCheckoutControlSoftware sccs, JFrame mainFrame) {
		
		disableMainFrame(mainFrame);
		
		JFrame frame = new JFrame();
		AddBagWindow keyboard = new AddBagWindow(sccs, frame);
		;
		frame.setBounds(100, 100, 800, 200);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5,5,5,5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(1,0,0,0));
		contentPane.add(keyboard);
		
		frame.setVisible(true);
		
		frame.addWindowListener((WindowListener) new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowClosing(WindowEvent e) {
				// enable the button
				enableMainFrame(mainFrame);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				enableMainFrame(mainFrame);
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// ignore
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// ignore
			}

        });
		
	}
	
	/* NOTIFICATION TO OBSERVERS */
	
	public static void addBagsButtonPressed(SelfCheckoutControlSoftware sccs, int qtty, boolean ownBags) {
		sccs.notifier.addBagsButtonPressed(qtty, ownBags);
	}
	
	private static void checkoutButtonPressed(SelfCheckoutControlSoftware sccs) {
		sccs.notifier.checkoutButtonPressed();
	}
	
	private static void payWithCashButtonPressed(SelfCheckoutControlSoftware sccs) {
		sccs.notifier.payWithCashButtonPressed();
	}
	
	private static void payWithCardButtonPressed(SelfCheckoutControlSoftware sccs) {
		sccs.notifier.payWithCardButtonPressed();
	}
	
	public static void payWithGiftCardButtonPressed(SelfCheckoutControlSoftware sccs, String cardNum) {
		sccs.notifier.payWithGiftCardButtonPressed(cardNum);
	}
	
	private static void addMoreItemsButtonPresses(SelfCheckoutControlSoftware sccs) {
		sccs.notifier.addMoreItemsButtonPresses();
	}
	
	private static void scanMembershipButtonPressed(SelfCheckoutControlSoftware sccs) {
		sccs.notifier.scanMembershipButtonPressed();
	}
	
	public static void membershipCardEntered(SelfCheckoutControlSoftware sccs, Numeral[] listOfDigitsInMembership) { 
		sccs.notifier.membershipCardEntered(listOfDigitsInMembership);
	}
	
	public static void enterPLUButtonPressed(SelfCheckoutControlSoftware sccs, PriceLookupCode code) {
		sccs.notifier.enterPLUButtonPressed(code);
	}
}
