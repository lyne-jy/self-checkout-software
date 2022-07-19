package ca.ucalgary.seng300.selfcheckout.product;

import java.math.BigDecimal;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

import ca.ucalgary.seng300.selfcheckout.utility.Cart;
import ca.ucalgary.seng300.selfcheckout.utility.ComplexAbstractSoftware;
import ca.ucalgary.seng300.selfcheckout.utility.Database;
import ca.ucalgary.seng300.selfcheckout.utility.IllegalActionException;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;

/*
 *	Helper class for printing receipt and calculating total cost of a purchase
 */
public class Checkout extends ComplexAbstractSoftware<CheckoutObserver> {

	// Constants ---
	/* low ink/paper will be defined as less than 10% of the maximum capacity
	in the receipt printer */
	public static final int LOW_INK = ReceiptPrinter.MAXIMUM_INK / 10000;   // 104 
	public static final int LOW_PAPER = ReceiptPrinter.MAXIMUM_PAPER / 10;  // 102 

	// Fields ---
	private SelfCheckoutStation hw;

	// all four of these ink/paper fields are reset every time the printer is refilled
	// to keep track of how much ink/paper has been used
	private int charactersOfInkRemaining = 0;
	private int linesOfPaperRemaining = 0;

	/* these booleans are to ensure low ink/paper warnings are only issued once
	before the printer is reloaded */
	private boolean wasLowInkNotified = false;
	private boolean wasLowPaperNotified = false;
	
	public Checkout(SelfCheckoutStation hw) {
		if(hw == null) throw new NullPointerException();
		this.hw = hw;
	}
				
	/*
	 * Physically prints the receipt containing the purchase information
	 * 
	 * @param cart Cart of products in the bagging area
	 * 
	 * @throws SimulationException When the printer has no paper, or the lines being printed is too long, or there is no ink
	 * @throws IllegalArgumentException When the cart contains no items
	 */
	public void printReceipt(Cart cart, Barcode membership) throws NullPointerException{

		if(cart == null) {
			throw new NullPointerException();
		}else if(curPhase != SoftwarePhase.PAYING){
        	notifyInWrongPhase();
        	return;
        }else if(cart.isEmpty()) {
        	notifyReceiptPrintedSuccesfully();
        	return;
        }
		
		try {
			// Printing all required information
			StringBuilder preliminaryInfo = getPreliminaryInfo();
			performPrinting(preliminaryInfo);
			
			// Print the membership if customer has one
			if(membership != null) {
				performPrinting(new StringBuilder(membership.toString()));
				System.out.println("Membership No: " + membership);
			}
			
			// Print all products in the cart
			for(Barcode b : cart.getBarcodedProducts().keySet()) {	
				performPrinting(getBarcodedProductInfo(b, cart.getBarcodedProducts().get(b)));
			}
			
			for(PriceLookupCode p : cart.getPLUProducts().keySet()) {
				performPrinting(getPLUProductInfo(p, cart.getPLUProducts().get(p)));
			}
			
			// Printing total
			performPrinting(priceInfo(cart));
			
			// Any extra information 
			StringBuilder extraInfo = getExtraInfo(cart);
			performPrinting(extraInfo);
			
		}catch(Exception e) {	
			notifyPrinterError();
			return;
		}
						
		// Cut the receipt	
		hw.printer.cutPaper();
		
		notifyReceiptPrintedSuccesfully();
	}
	
	/*
	 * Used to calculate the total amount to be payed 
	 * 
	 * @param cart 		Contains a list of products that have been scanned by the user
	 * @param taxRate 	Tax rate applied to the entire purchase
	 * 
	 * @throws SimulationException 		When the printer has no paper, or the lines being printed is too long, or there is no ink
	 * @throws IllegalArgumentException When the cart contains no items
	 * @throws NullPointerException 	When the cart is null
	 * 
	 */
	public BigDecimal calculateToPay(Cart cart,BigDecimal taxRate) throws IllegalActionException, NullPointerException, IllegalArgumentException{
		if(cart == null) throw new NullPointerException("Cart cannot be null");

		if(cart.isEmpty()) throw new IllegalArgumentException("Cart cannot be empty");

		if(curPhase != SoftwarePhase.PAYING){
        	notifyInWrongPhase();
        	return null;
        }

		BigDecimal total = cart.getTotalRemaining();

		notifyTotalCalculatedSuccesfully();
		return total;
	}

	/*
	 * Used to calculate the total total price of all the items added to bag. This will be what is printed on the receipt. 
	 * 
	 * @param cart 		Contains a list of products that have been scanned by the user
	 * @param taxRate 	Tax rate applied to the entire purchase
	 * 
	 * @throws SimulationException 		When the printer has no paper, or the lines being printed is too long, or there is no ink
	 * @throws IllegalArgumentException When the cart contains no items
	 * @throws NullPointerException 	When the cart is null
	 * 
	 */
	public BigDecimal calculateTotal(Cart cart, BigDecimal taxRate) throws IllegalActionException, NullPointerException, IllegalArgumentException{
		
		if(cart == null) throw new NullPointerException("Cart cannot be null");
		
		if(cart.isEmpty()) return BigDecimal.ZERO;
		
		if(curPhase != SoftwarePhase.PAYING){
        	notifyInWrongPhase();
        	return null;
        }
		
		BigDecimal total = cart.getSubtotal();
		
		notifyTotalCalculatedSuccesfully();
		return total;
	}
		
	/*
	 * Helper function used to print any type of information passed
	 * 
	 * @param info A string builder containing some sort of information
	 * 
	 * @throws SimulationException 		When the printer has no paper, or the lines being printed is too long, or there is no ink
	 * @throws IllegalArgumentException When the cart contains no items
	 * @throws NullPointerException 	When the cart is null
	 */
	private void performPrinting(StringBuilder info) throws IllegalActionException, NullPointerException, OverloadException, EmptyException {
				
		if(info.length() == 0) return;
			
		@SuppressWarnings("static-access")
		int cpl = hw.printer.CHARACTERS_PER_LINE;
		
		int ch = 0;
		int lineCount = 0;
		while(ch < info.length()) {
			
			if (lineCount + 2 < cpl) {	// There is space in the current line
				hw.printer.print(info.charAt(ch));
				System.out.print(info.charAt(ch));
				--charactersOfInkRemaining;
				lineCount++;
				ch++;
			}
			else {				// Go to next line
				lineCount = 0;
				hw.printer.print('\n');
				System.out.print('\n');
				--linesOfPaperRemaining;
			}

			// Low ink/paper warnings are only issued once before refilling
			// check and notify if printer is low on ink
			if (!wasLowInkNotified && charactersOfInkRemaining <= LOW_INK) {
				notifyPrinterLowOnInk();
				wasLowInkNotified = true;
			}

			// check and notify if printer is low on paper
			if (!wasLowPaperNotified && linesOfPaperRemaining <= LOW_PAPER) {
				notifyPrinterLowOnPaper();
				wasLowPaperNotified = true;
			}
		}
	}

	/**
	 * Updates the quantity of ink known to be left in the printer
	 * 
	 * @param charactersAdded
	 * The number of characters of ink loaded into the receipt printer
	 */
	public void loadedInkInPrinter(int charactersAdded)
	{
		charactersOfInkRemaining += charactersAdded;

		// reset warning
		wasLowInkNotified = false;
	}

	/**
	 * Updates the quantity of paper known to be left in the printer
	 * 
	 * @param linesAdded
	 * The number of lines of paper loaded into the receipt printer
	 */
	public void loadedPaperInPrinter(int linesAdded)
	{
		linesOfPaperRemaining += linesAdded;

		// reset warning
		wasLowPaperNotified = false;
	}
	
	/*
	 * Helper function used to get a formatted string builder for any information that needs to be printed before the  product information
	 * 
	 * Ex: Name of store, time stamp, membership card #, etc
	 * 
	 * @throws NullPointerException When the product is null
	 */
	
	private StringBuilder getPreliminaryInfo() {
		return new StringBuilder("************RECEIPT***********\n");
	}	
	
	/*
	 * Helper function used to get a formatted string builder for printing product information a receipt
	 * 
	 * @param p an object containing all information of a product in the cart
	 * 
	 * @throws NullPointerException When the product is null
	 */
	private StringBuilder getBarcodedProductInfo(Barcode barcode, int quantity) {
			
		/* Name, Price, Quantity\n */
	
		BarcodedProduct product = Database.database().readProductWithBarcode(barcode);
		
		StringBuilder info = new StringBuilder();
		
		info.append(product.getDescription().toString());
		info.append(", ");
		info.append(quantity);
		info.append(", ");
		info.append(product.getPrice());
		info.append('\n');
		
		return info;
	}	
	
	/*
	 * Helper function used to get a formatted string builder for printing product information a receipt
	 * 
	 * @param p an object containing all information of a product in the cart
	 * 
	 * @throws NullPointerException When the product is null
	 */
	private StringBuilder getPLUProductInfo(PriceLookupCode plu, double weight) {
		
		/* Name, Price, Quantity\n */
	
		PLUCodedProduct product = Database.database().readProductWithPLU(plu);
		
		StringBuilder info = new StringBuilder();
		
		info.append(product.getDescription().toString());
		info.append(", ");
		info.append(weight);
		info.append(", ");
		info.append(product.getPrice());
		info.append('\n');
		
		return info;
	}
	
	/**
	 * helper method to get the total price of items and the tax
	 * @param cart
	 * @return
	 */
	private StringBuilder priceInfo(Cart cart) {
		StringBuilder totalPayed = new StringBuilder();
		totalPayed.append("Total price: ");
		totalPayed.append(cart.getSubtotalPrice());
		totalPayed.append("\n");
		totalPayed.append("Tax: ");
		totalPayed.append(cart.getSubtotalTax());
		totalPayed.append("\n");
		return totalPayed;
	}
	
	/*
	 *  Helper method used to get a formatted string builder for any information that needs to be printed after the  product information
	 *  
	 *  Ex: "Thank You!", "Total Saved: x", etc
	 * 
	 * @throws NullPointerException When the product is null
	 */
	private StringBuilder getExtraInfo(Cart cart) {
		return new StringBuilder();
	}	
	
	/* OBSERVERS */	
	private void notifyTotalCalculatedSuccesfully() {
		for(CheckoutObserver obs : observers) {
			obs.totalCalculatedSuccesfully();
		}
	}
	
	private void notifyReceiptPrintedSuccesfully() {
		for(CheckoutObserver obs : observers) {
			obs.receiptPrintedSuccesfully();
		}
	}
	
	private void notifyInWrongPhase() {
		for(CheckoutObserver obs : observers) {
			obs.InWrongPhase();
		}
	}
	
	private void notifyPrinterError() {
		for(CheckoutObserver obs : observers) {
			obs.printerError();
		}
	}

	private void notifyPrinterLowOnInk()
	{
		for(CheckoutObserver obs : observers) {
			obs.printerLowOnInk();
		}
	}

	private void notifyPrinterLowOnPaper()
	{
		for(CheckoutObserver obs : observers) {
			obs.printerLowOnPaper();
		}
	}
}
