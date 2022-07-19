package ca.ucalgary.seng300.attendant.ui;

import ca.ucalgary.seng300.attendant.SupervisorStationControlSoftware;
import ca.ucalgary.seng300.selfcheckout.SelfCheckoutControlSoftware;
import ca.ucalgary.seng300.selfcheckout.ui.SelfCheckoutUI;
import ca.ucalgary.seng300.selfcheckout.ui.VisualCatalog;
import ca.ucalgary.seng300.selfcheckout.utility.Cart;
import ca.ucalgary.seng300.selfcheckout.utility.Database;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class SupervisorUI {
    public static Cart cart;
    public static JDialog loginDialog;
    public static JLabel loginLabel;
    public static JLabel[] placeHolder1s;
    public static JLabel[] placeHolder2s;
    public static JLabel[] placeHolder3s;
    public static JLabel[] placeHolder4s;
    public static JLabel[] placeHolder5s;
    public static JPanel[] panels;
    public static JLabel[] nameLabels;
    public static JLabel[] statusLabels;
    public static JButton[] approveButtons;
    public static JButton[] blockButtons;
    public static JButton[] unblockButtons;
    public static JButton[] startButtons;
    public static JButton[] shutdownButtons;
    public static JButton[] removeButtons;
    public static JButton[] addButtons;

    public static void showLogin(SupervisorStationControlSoftware sscs) {
        loginDialog = new JDialog();
        JPanel contentPanel = new JPanel();
        loginDialog.setBounds(300, 700, 200, 120);
        loginDialog.getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        loginDialog.getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));

        loginLabel = new JLabel("Enter pin");
        contentPanel.add(loginLabel, BorderLayout.NORTH);

        JTextField textField = new JTextField();
        contentPanel.add(textField, BorderLayout.CENTER);
        textField.setColumns(10);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            loginDialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                buttonPane.add(okButton);
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sscs.notifier.login(Integer.parseInt(textField.getText()));
                    }
                });
                loginDialog.getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        loginDialog.dispose();
                    }
                });
                buttonPane.add(cancelButton);
            }
            loginDialog.setVisible(true);
        }
    }


    public static void showHomeScreen(JFrame frame, SupervisorStationControlSoftware sscs, SelfCheckoutControlSoftware[] stations, int numStations) {
        placeHolder1s = new JLabel[numStations];
        placeHolder2s = new JLabel[numStations];
        placeHolder3s = new JLabel[numStations];
        placeHolder4s = new JLabel[numStations];
        placeHolder5s = new JLabel[numStations];
        panels = new JPanel[numStations];
        nameLabels = new JLabel[numStations];
        statusLabels = new JLabel[numStations];
        approveButtons = new JButton[numStations];
        blockButtons = new JButton[numStations];
        unblockButtons = new JButton[numStations];
        startButtons = new JButton[numStations];
        shutdownButtons = new JButton[numStations];
        removeButtons = new JButton[numStations];
        addButtons = new JButton[numStations];
        
        
    	frame.setBounds(50, 650, 650, 390);

    	frame.setResizable(false);
    	frame.getContentPane().removeAll(); // get rid of any other components

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        frame.setContentPane(contentPane);
        contentPane.setLayout(new GridLayout(0, 1, 0, 0));

        // Create panels for each station
        for (int i = 0; i < numStations; i ++) {
            // Panel
            panels[i] = new JPanel();
            panels[i].setBorder(new LineBorder(new Color(0, 0, 0)));
            contentPane.add(panels[i]);
            panels[i].setLayout(new GridLayout(0, 7, 0, 0));

            nameLabels[i] = new JLabel("Station " + i);
            panels[i].add(nameLabels[i]);

            statusLabels[i] = new JLabel("");
            panels[i].add(statusLabels[i]);

            placeHolder1s[i] = new JLabel("");
            panels[i].add(placeHolder1s[i]);

            placeHolder2s[i] = new JLabel("");
            panels[i].add(placeHolder2s[i]);

            placeHolder3s[i] = new JLabel("");
            panels[i].add(placeHolder3s[i]);

            placeHolder4s[i] = new JLabel("");
            panels[i].add(placeHolder4s[i]);

            placeHolder5s[i] = new JLabel("");
            panels[i].add(placeHolder5s[i]);

            // Approve button
            approveButtons[i] = new JButton("Approve");
            approveButtons[i].setActionCommand(Integer.toString(i)); // Action command
            approveButtons[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int stationNum = Integer.parseInt(((JButton) e.getSource()).getActionCommand());
                    notifyApproveWeightDiscrepancy(sscs, stations[stationNum]);
                }
            });
            panels[i].add(approveButtons[i]);

            // Block button
            blockButtons[i] = new JButton("Block");
            blockButtons[i].setActionCommand(Integer.toString(i));
            blockButtons[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int stationNum = Integer.parseInt(((JButton) e.getSource()).getActionCommand());
                    notifyBlockStation(sscs, stations[stationNum]);
                }
            });
            panels[i].add(blockButtons[i]);

            // Unblock button
            unblockButtons[i] = new JButton("Unblock");
            unblockButtons[i].setActionCommand(Integer.toString(i));
            unblockButtons[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int stationNum = Integer.parseInt(((JButton) e.getSource()).getActionCommand());
                    notifyUnblockStation(sscs, stations[stationNum]);
                }
            });
            panels[i].add(unblockButtons[i]);

            // Start button
            startButtons[i] = new JButton("Start");
            startButtons[i].setActionCommand(Integer.toString(i));
            startButtons[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int stationNum = Integer.parseInt(((JButton) e.getSource()).getActionCommand());
                    notifyStartStation(sscs, stations[stationNum]);
                }
            });
            panels[i].add(startButtons[i]);

            // Shutdown button
            shutdownButtons[i] = new JButton("Shutdown");
            shutdownButtons[i].setActionCommand(Integer.toString(i));
            shutdownButtons[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int stationNum = Integer.parseInt(((JButton) e.getSource()).getActionCommand());
                    notifyShutDownStation(sscs, stations[stationNum]);
                }
            });
            panels[i].add(shutdownButtons[i]);

            // Remove item button
            removeButtons[i] = new JButton("Remove item");
            removeButtons[i].setActionCommand(Integer.toString(i));
            removeButtons[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int stationNum = Integer.parseInt(((JButton) e.getSource()).getActionCommand());
                    showRemoveList(sscs, stations[stationNum]);
                }
            });
            panels[i].add(removeButtons[i]);

            // Add item button
            addButtons[i] = new JButton("Add item");
            addButtons[i].setActionCommand(Integer.toString(i));
            addButtons[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int stationNum = Integer.parseInt(((JButton) e.getSource()).getActionCommand());
                    SelfCheckoutUI.enterPLUKeyboard(stations[stationNum], frame);
                    frame.revalidate();
                }
            });
            panels[i].add(addButtons[i]);
        }

        // Logout button
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                notifyLogout(sscs);
            }
        });
        contentPane.add(btnLogout);

        frame.setContentPane(contentPane);
        frame.setVisible(true);
    }

    @SuppressWarnings({ "unchecked", "serial" })
	public static void showRemoveList(SupervisorStationControlSoftware sscs, SelfCheckoutControlSoftware sccs) {
        cart = SelfCheckoutUI.cart;

        HashMap<Barcode, Integer> barcodeHashMap = cart.getBarcodedProducts();
        HashMap<PriceLookupCode, Double> priceLookupCodeHashMap = cart.getPLUProducts();
        DefaultListModel<BarcodedListItem> barcodedItems = new DefaultListModel<>();
        DefaultListModel<PLUListItem> pluItems = new DefaultListModel<>();
        DefaultListModel<ListItem> itemsToRemove = new DefaultListModel<>();
        HashMap<Barcode, Integer> barcodedItemsToRemove = new HashMap<>();
        HashMap<PriceLookupCode, Double> pluItemToRemove = new HashMap<>();

        for (Barcode barcode : barcodeHashMap.keySet()) {

            barcodedItems.addElement(new BarcodedListItem(Database.database().readProductWithBarcode(barcode), barcodeHashMap.get(barcode)));
        }

        for (PriceLookupCode plu : cart.getPLUProducts().keySet()) {
            pluItems.addElement(new PLUListItem(Database.database().readProductWithPLU(plu), priceLookupCodeHashMap.get(plu)));
        }

        JFrame frame = new JFrame();
        frame.setBounds(100, 100, 1000, 300);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        frame.setContentPane(contentPane);
        contentPane.setLayout(new GridLayout(1, 0, 0, 0));

        JPanel panel = new JPanel();
        contentPane.add(panel);
        JLabel lblNewLabel = new JLabel("Barcoded Items");
        @SuppressWarnings("rawtypes")
		JList barcodedItemList = new JList(barcodedItems);
        barcodedItemList.setFixedCellWidth(200);
        barcodedItemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(lblNewLabel);
        panel.add(new JScrollPane(barcodedItemList));


        JPanel panel_1 = new JPanel();
        contentPane.add(panel_1);
        JLabel pluLabel = new JLabel("PLU Items");
        @SuppressWarnings("rawtypes")
		JList pluItemList = new JList(pluItems);
        pluItemList.setFixedCellWidth(200);
        pluItemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel_1.add(pluLabel);
        panel_1.add(new JScrollPane(pluItemList));


        JPanel panel_2 = new JPanel();
        contentPane.add(panel_2);
        JLabel lblItemToRemove = new JLabel("Items to remove");
        @SuppressWarnings("rawtypes")
		JList itemToRemoveList = new JList(itemsToRemove);
        itemToRemoveList.setFixedCellWidth(200);
        pluItemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel_2.add(lblItemToRemove);
        panel_2.add(new JScrollPane(itemToRemoveList));


        JPanel panel_3 = new JPanel();
        contentPane.add(panel_3);
        panel_3.setLayout(new GridLayout(0, 1, 0, 0));
        JButton removeOneButton = new JButton("Remove one");
        JButton removeAllButton = new JButton("Remove all");
        JButton saveButton = new JButton("Save");
        panel_3.add(removeOneButton);
        panel_3.add(removeAllButton);
        panel_3.add(saveButton);

        // Buttons
        removeOneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BarcodedListItem bi;
                if (barcodedItemList.getSelectedValue() != null) {
                    bi = (BarcodedListItem) barcodedItemList.getSelectedValue();
                    boolean flag = false;
                    for (int i = 0; i < itemsToRemove.size(); i ++) {
                        if (itemsToRemove.getElementAt(i).name.equals(bi.bp.getDescription())) {
                            itemsToRemove.getElementAt(i).quantity++;
                            flag = true;
                            break;
                        }
                    }
                    if (!flag)
                        itemsToRemove.addElement(new ListItem(bi.bp.getDescription(), 1));
                    bi.quantity --;
                    if (bi.quantity == 0)
                        barcodedItems.removeElement(bi);
                    barcodedItemsToRemove.putIfAbsent(bi.bp.getBarcode(), 0);
                    barcodedItemsToRemove.put(bi.bp.getBarcode(), barcodedItemsToRemove.get(bi.bp.getBarcode()) + 1);
                    barcodedItemList.updateUI();
                    itemToRemoveList.updateUI();
                }
            }
        });

        removeAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BarcodedListItem bi;
                PLUListItem pi;
                if (barcodedItemList.getSelectedValue() != null) {
                    bi = (BarcodedListItem) barcodedItemList.getSelectedValue();
                    boolean flag = false;
                    for (int i = 0; i < itemsToRemove.size(); i ++) {
                        if (itemsToRemove.getElementAt(i).name.equals(bi.bp.getDescription())) {
                            itemsToRemove.getElementAt(i).quantity += bi.quantity;
                            flag = true;
                            break;
                        }
                    }
                    if (!flag)
                        itemsToRemove.addElement(new ListItem(bi.bp.getDescription(), bi.quantity));
                    barcodedItems.removeElement(bi);
                    barcodedItemsToRemove.putIfAbsent(bi.bp.getBarcode(), bi.quantity);
                    barcodedItemList.updateUI();
                    itemToRemoveList.updateUI();
                }
                else if (pluItemList.getSelectedValue() != null) {
                    pi = (PLUListItem) pluItemList.getSelectedValue();
                    itemsToRemove.addElement(new ListItem(pi.pp.getDescription(), pi.weight));
                    pluItems.removeElement(pi);
                    pluItemToRemove.putIfAbsent(pi.pp.getPLUCode(), pi.weight);
                    pluItemList.updateUI();
                    itemToRemoveList.updateUI();
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (itemsToRemove.isEmpty()) {
                    frame.dispose();
                    return;
                }
                if (!barcodedItemsToRemove.isEmpty()) {
                    for (Barcode barcode : barcodedItemsToRemove.keySet()) {
                        cart.removeBarcodedItemFromCart(barcode, Database.database().readProductWithBarcode(barcode).getPrice(), barcodedItemsToRemove.get(barcode));
                    }
                }
                if (!pluItemToRemove.isEmpty()) {
                    for (PriceLookupCode plu : pluItemToRemove.keySet()) {
                        cart.removePLUItemFromCart(plu, Database.database().readProductWithPLU(plu).getPrice());
                    }
                }
                frame.dispose();
                notifyProductRemovedFromPurchase(sscs, sccs);
            }
        });

        // List
        barcodedItemList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                pluItemList.clearSelection();
                itemToRemoveList.clearSelection();
                removeOneButton.setEnabled(true);
                removeAllButton.setEnabled(true);
            }
        });

        pluItemList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                barcodedItemList.clearSelection();
                itemToRemoveList.clearSelection();
                removeOneButton.setEnabled(false);
                removeAllButton.setEnabled(true);
            }
        });

        itemToRemoveList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                barcodedItemList.clearSelection();
                pluItemList.clearSelection();
                removeOneButton.setEnabled(false);
                removeAllButton.setEnabled(false);
            }
        });

        // List renderer
        barcodedItemList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    BarcodedListItem item = (BarcodedListItem) value;
                    label.setText((item.bp.getDescription() + "(" + item.quantity + ")"));
                }
                return c;
            }
        });

        pluItemList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    PLUListItem item = (PLUListItem) value;
                    label.setText(item.pp.getDescription() + "(" + item.weight + ")");
                }
                return c;
            }
        });

        itemToRemoveList.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    ListItem item = (ListItem) value;
                    if (item.quantity != 0)
                        label.setText(item.name + "(" + item.quantity + ")");
                    else if (item.weight > 0) {
                        label.setText(item.name + "(" + item.weight + ")");
                    }
                }
                return c;
            }
        });

        frame.setVisible(true);
    }

    public static void showVisualCatalog() {
        cart = SelfCheckoutUI.cart;
        JFrame frame = new JFrame();
        VisualCatalog vc = new VisualCatalog(null, frame);
        vc.setName("supervisor");
        frame.add(vc);
        frame.setBounds(100, 100, 500, 300);
        frame.setVisible(true);
    }

    /* NOTIFICATION TO OBSERVERS */
    private static void notifyApproveWeightDiscrepancy(SupervisorStationControlSoftware sscs, SelfCheckoutControlSoftware sccs) {
        sscs.notifier.approveWeightDiscrepancy(sccs);
    }

    private static void notifyBlockStation(SupervisorStationControlSoftware sscs, SelfCheckoutControlSoftware sccs) {
        sscs.notifier.blockStation(sccs);
    }

    private static void notifyUnblockStation(SupervisorStationControlSoftware sscs, SelfCheckoutControlSoftware sccs) {
        sscs.notifier.unblockStation(sccs);
    }

    private static void notifyStartStation(SupervisorStationControlSoftware sscs, SelfCheckoutControlSoftware sccs) {
        sscs.notifier.startStation(sccs);
    }

    private static void notifyShutDownStation(SupervisorStationControlSoftware sscs, SelfCheckoutControlSoftware sccs) {
        sscs.notifier.shutDownStation(sccs);
    }

    private static void notifyProductRemovedFromPurchase(SupervisorStationControlSoftware sscs, SelfCheckoutControlSoftware sccs) {
        sscs.notifier.productRemovedFromPurchase(sccs, cart);
    }

    @SuppressWarnings("unused")
	private static void notifyProductAddedToPurchase(SupervisorStationControlSoftware sscs, SelfCheckoutControlSoftware sccs) {
    	// ignore
    }

    @SuppressWarnings("unused")
	private static void notifyLogin(SupervisorStationControlSoftware sscs, int pin) {
        sscs.notifier.login(pin);
    }

    private static void notifyLogout(SupervisorStationControlSoftware sscs) {
        sscs.notifier.logout();
    }
}

class BarcodedListItem{
    BarcodedProduct bp;
    int quantity;
    BarcodedListItem(BarcodedProduct bp, int quantity) {
        this.bp = bp;
        this.quantity = quantity;
    }
}

class PLUListItem{
    PLUCodedProduct pp;
    double weight;
    PLUListItem(PLUCodedProduct pp, double weight) {
        this.pp = pp;
        this.weight = weight;
    }
}

class ListItem{
    String name;
    int quantity;
    double weight;
    ListItem(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }
    ListItem(String name, double weight) {
        this.name = name;
        this.weight = weight;
    }
}