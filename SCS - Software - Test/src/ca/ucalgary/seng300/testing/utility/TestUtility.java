package ca.ucalgary.seng300.testing.utility;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.PLUCodedItem;

import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

public class TestUtility {

	// Coins
	public static final Coin fiveCents = new Coin(getCurrency(), new BigDecimal(0.05));
	public static final Coin tenCents = new Coin(getCurrency(), new BigDecimal(0.1));
	public static final Coin twentyFiveCents =new Coin(getCurrency(), new BigDecimal(0.25));
	public static final Coin oneDollar = new Coin(getCurrency(), new BigDecimal(1.0));
	public static final Coin twoDollars = new Coin(getCurrency(), new BigDecimal(2.0));
	
	// Banknotes
	public static final Banknote fiveDollarBill = new Banknote(getCurrency(), 5);
	public static final Banknote tenDollarBill = new Banknote(getCurrency(), 10);
	public static final Banknote twentyDollarBill = new Banknote(getCurrency(), 20);
	public static final Banknote fiftyDollarBill = new Banknote(getCurrency(), 50);
	public static final Banknote hundredDollarBill = new Banknote(getCurrency(), 100);
	
	// Barcoded Items 
	public static final Barcode defaultItemBarcode = new Barcode(new Numeral[] {Numeral.four, Numeral.three});
	public static final BarcodedItem defaultItem = new BarcodedItem(defaultItemBarcode, 20.9);
	public static final Barcode normalItemBarcode = new Barcode(new Numeral[] {Numeral.five, Numeral.three});
	public static final BarcodedItem normalItem = new BarcodedItem(normalItemBarcode, 20.9);
	public static final Barcode normalItemBarcode2 = new Barcode(new Numeral[] {Numeral.five, Numeral.five});
	public static final BarcodedItem normalItem2 = new BarcodedItem(normalItemBarcode, 11.0);
	public static final Barcode  normalItem3Barcode = new Barcode(new Numeral[] {Numeral.five, Numeral.six});
	public static final BarcodedItem normalItem3 = new BarcodedItem(normalItem3Barcode, 20.9);
	public static final Barcode lightItemBarcode = new Barcode(new Numeral[] {Numeral.two, Numeral.seven});
	public static final BarcodedItem lightItem = new BarcodedItem(lightItemBarcode, 5);
	public static final Barcode heavyItemBarcode = new Barcode(new Numeral[] {Numeral.three, Numeral.three});
	public static final BarcodedItem heavyItem = new BarcodedItem(heavyItemBarcode, 600.0);
	public static final Barcode expensiveItemBarcode = new Barcode(new Numeral[] {Numeral.four, Numeral.three});
	public static final BarcodedItem expensiveItem = new BarcodedItem(expensiveItemBarcode, 20.9);
	
	public static final Barcode longNameItemBarcode = new Barcode(new Numeral[] {Numeral.six, Numeral.nine});
	
	public static final BarcodedItem bag = new BarcodedItem(new Barcode(new Numeral[] {Numeral.one}), 2.0);
	public static final BarcodedItem tenBags = new BarcodedItem(new Barcode(new Numeral[] {Numeral.one}), 20.0);
	public static final Barcode elevenBag = new Barcode(new Numeral[] {Numeral.six, Numeral.four});
	public static final BarcodedItem elevenBagItem = new BarcodedItem(elevenBag, 22.0);
	public static final BarcodedItem twentyBags = new BarcodedItem(new Barcode(new Numeral[] {Numeral.one}), 40.0);
	
	// Barcoded Producs
	public static final BarcodedProduct apple = new BarcodedProduct(new Barcode(new Numeral[] {Numeral.five, Numeral.three}), "Apple", new BigDecimal(3.0), 20.9);
	
	// PLU Coded Products
	public static final PriceLookupCode normalPLUCode = new PriceLookupCode("5678");
	public static final PLUCodedItem normalPLUItem = new PLUCodedItem(normalPLUCode, 16.0);
	public static final PriceLookupCode plu1 = new PriceLookupCode("1234"); 
	public static final PLUCodedItem plu1Item = new PLUCodedItem(plu1, 18.0);
	public static final PriceLookupCode plu2 = new PriceLookupCode("1434"); 
	public static final PLUCodedProduct mango = new PLUCodedProduct(normalPLUCode, "Mango", new BigDecimal(3.0));
	// not in database
	public static final PriceLookupCode plu3 = new PriceLookupCode("2364"); 
	public static final PLUCodedItem plu3Item = new PLUCodedItem(plu3, 25.0);
	
	// Cards
	public static final Card card1 = new Card("debit", "123456789", "anonymous", "321", "1234", false, true); // Has a chip  [visa]
	public static final Card card2 = new Card("debit", "987654321", "anonymous", "456", null, true, false);  // No chip     [mastercard]
	
	public static final Card card3 = new Card("credit", "143265798", "anonymous", "345", "1234", false, true);  // has chip    [visa]
	public static final Card card4 = new Card("credit", "987456732", "anonymous", "435", null, true, false);	// No chip 	   [mastercard]
		
	public static final Barcode existingMembershipBarcode = new Barcode(new Numeral[] {Numeral.six, Numeral.two, Numeral.six});
	public static final Barcode nonExistingMembershipBarcode = new Barcode(new Numeral[] {Numeral.three, Numeral.nine});
	public static final BarcodedItem membershipCard1 = new BarcodedItem(existingMembershipBarcode, 1.0);
	public static final BarcodedItem membershipCard2 = new BarcodedItem(nonExistingMembershipBarcode, 1.0);
	
	public static final String giftCard1 = "132547698";
	public static final String giftCard2 = "896745231";
	
	public static final int pin1 = 1234;
	public static final int pin2 = 3421;
	
	public static SelfCheckoutStation getHarwareInstance1() {
		return new SelfCheckoutStation(getCurrency(), getBanknoteDenominations(), getCoinDenominations(), 500, 10);
	}
	
	public static BigDecimal[] getCoinDenominations() {
		return new BigDecimal[] {new BigDecimal(0.05), new BigDecimal(0.10), new BigDecimal(0.25), new BigDecimal(1.0), new BigDecimal(2.0)};
	}
	
	public static int[] getBanknoteDenominations() {
		int[] d = {5, 10, 20, 50, 100};
		return d;
	}
	
	public static Currency getCurrency() {
		return Currency.getInstance(Locale.CANADA);	
	}
}
