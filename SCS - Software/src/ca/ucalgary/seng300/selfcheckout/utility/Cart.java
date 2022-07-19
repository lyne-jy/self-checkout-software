package ca.ucalgary.seng300.selfcheckout.utility;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.PriceLookupCode;
import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Class representing the virtual cart of the 
 * self checkout station software.
 * 
 * @field cart
 * 		Items to be paid for
 * 
 * @field productInWaitingArea
 * 		Barcode of product/item that has been scanned but not weighed
 *
 * @field subtotal
 * 		Running subtotal, the cost of all items in cart before tax
 */
public final class Cart extends SimpleAbstractSoftware{
	
	public final int id;
    private HashMap<Barcode, Integer> barcodeCart;
    private HashMap<PriceLookupCode, Double> pluCart;
    private int numBags;
   
    private BigDecimal subtotal;
    private BigDecimal subtotalPrice;
    private BigDecimal subtotalTax;
    private BigDecimal totalRemaining;
    private BigDecimal taxRate;
    
    private final BigDecimal BAG_PRICE;
	
    public Cart(int id) {
		this.id = id;
		subtotal = BigDecimal.ZERO;
    	totalRemaining = BigDecimal.ZERO;
    	subtotalPrice = BigDecimal.ZERO;
    	taxRate = BigDecimal.ZERO;
    	subtotalTax = BigDecimal.ZERO;
    	barcodeCart = new HashMap<Barcode, Integer>();
    	pluCart = new HashMap<PriceLookupCode, Double>();
    	numBags = 0;
    	BAG_PRICE = new BigDecimal(0.10);
    	reset();
    }
    
    /** 
     * Adds a Barcoded Product into the cart.
     * @param barcode of product
     * Usually this will be productInWaitingArea, but it is left
     * open as a parameter for changeability
     * @param price
     * The realized price of the product(s) added
     * Note: this is different than product.getPrice() when
     * !product.isPerUnit()
     */
    public void addBarcodedItemToCart(Barcode barcode, BigDecimal price) {
    	
    	if (barcodeCart.containsKey(barcode)) {
    		Integer currentQtty = barcodeCart.get(barcode);
    		barcodeCart.replace(barcode, currentQtty + 1);
    	} else {
    		barcodeCart.put(barcode, 1);
    	}
    	subtotalPrice = subtotalPrice.add(price);
    	BigDecimal priceWithTax =  price.add(price.multiply(taxRate));
    	totalRemaining = totalRemaining.add(priceWithTax);
    	subtotal = subtotal.add(priceWithTax);
    }
    
//    /** 
//     * Adds a PLU Coded Product into the cart.
//     * @param PriceLookupCode of product
//     * Usually this will be productInWaitingArea, but it is left
//     * open as a parameter for changeability
//     * @param price per weight
//     * @param weight
//     */
    public void addPLUItemToCart(PriceLookupCode code, BigDecimal pricePerWeight, double weight) {
    	
    	if (pluCart.containsKey(code)) {
    		Double currentWeight = pluCart.get(code);
    		pluCart.replace(code, currentWeight + weight);
    	} else {
    		pluCart.put(code, weight);
    	}
    	
    	subtotalPrice = subtotalPrice.add(pricePerWeight.multiply(new BigDecimal(weight)));
    	BigDecimal priceWithTax =  pricePerWeight.add(pricePerWeight.multiply(taxRate));
    	totalRemaining = totalRemaining.add(priceWithTax);
    	subtotal = subtotal.add(priceWithTax);
    }
    
    /**
     * Removes a PLU item from the cart
     * 
     * @param code Price lookup code of the item to remove
     * @param pricePerWeight
     * 
     * @return the weight removed from the cart
     */
    public Double removePLUItemFromCart(PriceLookupCode code, BigDecimal pricePerWeight) {
    	
    	if(!pluCart.containsKey(code)) return null;
    	
    	double weightToRemove = pluCart.get(code);
    	BigDecimal priceToSubtract = new BigDecimal(weightToRemove).multiply(pricePerWeight);
		
		pluCart.remove(code);
    	subtotal = subtotal.subtract(priceToSubtract);
    	totalRemaining = totalRemaining.subtract(priceToSubtract);
    	
    	return weightToRemove;
    }

	public void removeBarcodedItemFromCart(Barcode barcode, BigDecimal price, int quantity) {
		if(!barcodeCart.containsKey(barcode)) return;

		BigDecimal priceToSubtract = new BigDecimal(quantity).multiply(price);

		barcodeCart.put(barcode, barcodeCart.get(barcode) - quantity);
		if (barcodeCart.get(barcode) == 0)
			barcodeCart.remove(barcode);
		subtotal = subtotal.subtract(priceToSubtract);
		totalRemaining = totalRemaining.subtract(priceToSubtract);
	}
 
	/*
	 * Simple getter containing a list of barcoded items added in the cart
	 */
	public HashMap<Barcode, Integer> getBarcodedProducts() {
		return barcodeCart;
	}
	
	/*
	 * Simple getter containing a list of plu items added in the cart
	 */
	public HashMap<PriceLookupCode, Double> getPLUProducts() {
		return pluCart;
	}
	
	/*
	 * Returns whether the list containing items in the cart is empty
	 */
	public boolean isEmpty() {
		return (barcodeCart.size() == 0 && pluCart.size() == 0);
	}
	
	/*
	 * Returns total after tax
	 */
	public BigDecimal getSubtotal() {
		return subtotal;
	}
	
	/**
	 * return subtotal if no tax was applied
	 * @return
	 */
	public BigDecimal getSubtotalPrice() {
		return subtotalPrice;
	}
	
	/**
	 * subtracts payment from total remaining
	 * @param payment
	 */
	public void partialPayment(BigDecimal payment) {
		totalRemaining = totalRemaining.subtract(payment);
	}
	
	/**
	 * returns total remaining 
	 * @return
	 */
	public BigDecimal getTotalRemaining() {
		return totalRemaining;
	}
	
	public BigDecimal getSubtotalTax() {
		return subtotalTax;
	}
	
	/**
	 * Setter for tax rate
	 * @param taxRate
	 */
	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}
	
	/*
	 * Add bags into the cart
	 */
	public void addBags(int qtty, boolean ownBags) {
		numBags += qtty;
		
		BigDecimal extraCost;
		if(ownBags == false) {
			extraCost = BAG_PRICE.multiply(new BigDecimal(qtty));
		}else {
			extraCost = BigDecimal.ZERO;
		}
		subtotalPrice = subtotalPrice.add(extraCost);
    	totalRemaining = totalRemaining.add(extraCost);
	}
	
	public int getNumBags() {
		return numBags;
	}
	
	public double getWeightOfPLUProduct(PriceLookupCode plu) {
		if(!pluCart.containsKey(plu)) return 0.0;
	
		return pluCart.get(plu);
	}
	
	public String toString() {
		String body = "";
		for(Barcode b : barcodeCart.keySet()) {
			body+=(barcodeCart.get(b)+"  "+Database.database().readProductWithBarcode(b).getDescription()+ " @ "+Database.database().readProductWithBarcode(b).getPrice()+ " ea \n");
		}
		for(PriceLookupCode p : pluCart.keySet()) {
			body+=(pluCart.get(p)+"  "+Database.database().readProductWithPLU(p).getDescription()+ " @ "+Database.database().readProductWithPLU(p).getPrice()+ " ea \n");
		}
		body+=("Bags: " + numBags);
		body+=("\nSubtotal: $"+String.format("%.2f", totalRemaining));
		
		return body;
	}
	
	/*
	 * Empties the cart and updates the subtotal
	 */
	public void reset() {
		pluCart.clear();
		barcodeCart.clear();
		subtotal = new BigDecimal(0.0);
		totalRemaining = BigDecimal.ZERO;
    	subtotalPrice = BigDecimal.ZERO;
    	taxRate = BigDecimal.ZERO;
    	subtotalTax = BigDecimal.ZERO;
    	numBags = 0;
	}
}
