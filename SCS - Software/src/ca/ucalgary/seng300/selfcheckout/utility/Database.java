package ca.ucalgary.seng300.selfcheckout.utility;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.external.CardIssuer;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;

/*
 * Class simulates a database with simple CRUD operations (built on top of the product and cardrecord database)
 */
public class Database {
	
	private static final Database INSTANCE = new Database();
	private HashMap<Barcode, String> memberships;
	private HashMap<Integer, String> employees;
	
	// For card payments
	private HashMap<String, CardIssuer> nameToIssuer; // Maps a card company name to issuer object
	private HashMap<String, String> cardNumToIssuer;
	
	// For names to PLU or Barcode
	private HashMap<String, PriceLookupCode> nameToPLU;
	private HashMap<String, Barcode> nameToBarcode;
	
	// For weights of PLU coded items
	private HashMap<PriceLookupCode, Double> pluToWeight;
	
	// For cards
	private HashMap<String, Card> numberToCard;
	
	private HashMap <String, String> cardNumtoPin;
	
	
	

	private Database() {
		
		this.memberships = new HashMap<>();
		this.employees = new HashMap<>();
		this.nameToIssuer = new HashMap<>();
		this.cardNumToIssuer = new HashMap<>();
		this.nameToPLU = new HashMap<>();
		this.nameToBarcode = new HashMap<>();
		this.pluToWeight = new HashMap<>();
		this.numberToCard = new HashMap<>();
		this.cardNumtoPin = new HashMap<>();
		
		
		// Add two common issuers [visa, mastercard]
		nameToIssuer.put("visa", new CardIssuer("visa"));
		nameToIssuer.put("mastercard", new CardIssuer("mastercard"));
		nameToIssuer.put("grocery-store", new CardIssuer("grocery-store"));
	
		// Adding some Barcoded products to the database
		createNewProductWithBarcode(new BarcodedProduct(new Barcode(new Numeral[] {Numeral.zero, Numeral.zero}), "Bag", new BigDecimal(0.1), 2.0));
		createNewProductWithBarcode(new BarcodedProduct(new Barcode(new Numeral[] {Numeral.five, Numeral.three}), "Bag of Chips (Barcoded)", new BigDecimal(3.0), 20.9));
		createNewProductWithBarcode(new BarcodedProduct(new Barcode(new Numeral[] {Numeral.two, Numeral.seven}), "Pasta (Barcoded)", new BigDecimal(5.0), 5.0));
		
		createNewProductWithBarcode(new BarcodedProduct(new Barcode(new Numeral[] {Numeral.three, Numeral.three}), "Keg Of Beer (Barcoded)", new BigDecimal(500.0), 600.0));
		createNewProductWithBarcode(new BarcodedProduct(new Barcode(new Numeral[] {Numeral.five, Numeral.five}), "Protein Powder (Barcoded)", new BigDecimal(3.0), 11.0));
		createNewProductWithBarcode(new BarcodedProduct(new Barcode(new Numeral[] {Numeral.six, Numeral.nine}), "Chocolate Bar (Barcoded)", new BigDecimal(3.0), 11.0));
		createNewProductWithBarcode(new BarcodedProduct(new Barcode(new Numeral[] {Numeral.five, Numeral.six}), "Bubble Gum (Barcoded)", new BigDecimal(3.0), 20.9));
		
		createNewProductWithBarcode(new BarcodedProduct(new Barcode(new Numeral[] {Numeral.four, Numeral.three}), "Tomato Sauce (Barcoded)", new BigDecimal(7.0), 20.9));
		createNewProductWithBarcode(new BarcodedProduct(new Barcode(new Numeral[] {Numeral.six, Numeral.seven}), "Cookies (Barcode)", new BigDecimal(1101.0), 20.9));
				
		// Adding some items to the database
		
		// Adding some PLU products to database
		createNewProductWithPLU(new PLUCodedProduct(new PriceLookupCode("1234"), "Tomato (PLU)", new BigDecimal(10.0)), 15.0);
		createNewProductWithPLU(new PLUCodedProduct(new PriceLookupCode("5678"), "Mango (PLU)", BigDecimal.valueOf(8.0)), 12.5);
		
		//Add one membership to DB
		createNewMembership(new Barcode(new Numeral[] {Numeral.six, Numeral.two, Numeral.six}), "Michael Johnny");
		
		// Adding Employees
		createNewEmployee(1234, "John");
		
		// Add a new credit/debit card to the database
		createNewCardData("visa", "credit", "123456789", "anonymous", "321", "1234" , true, true, new GregorianCalendar(2030,0,31), new BigDecimal((100000.0)));
		createNewCardData("mastercard", "debit", "987654321", "anonymous", "456", null, true, false, new GregorianCalendar(2050,0,31), new BigDecimal(500.0));
		
		createNewCardData("visa", "debit", "143265798", "anonymous", "345", "1234" , true, true, new GregorianCalendar(2023,0,31), new BigDecimal(10.0));
		createNewCardData("mastercard", "debit", "987456732", "anonymous", "435", null, true, false, new GregorianCalendar(2027,0,31), new BigDecimal(10.0));
		
		// Add new giftcards to the database 
		createNewCardData("grocery-store", "giftcard", "132547698", "anonymous", "000", null, false, false, new GregorianCalendar(2026,0,31), new BigDecimal(150.0));
		createNewCardData("grocery-store", "giftcard", "896745231", "anonymous", "000", null, false, false, new GregorianCalendar(2025,0,31), new BigDecimal(1.0));
	}
	
	public static Database database() {
		return INSTANCE;
	}
	
	public Set<Barcode> getBarcodes(){
		 return ProductDatabases.BARCODED_PRODUCT_DATABASE.keySet();
	}
	
	public Set<PriceLookupCode> getPLUs(){
		return ProductDatabases.PLU_PRODUCT_DATABASE.keySet();
	}
	
	
	public Set<String> cardNumbers(){
		return cardNumToIssuer.keySet();
	}
	
	
	public Set<Barcode> memberships(){
		return this.memberships.keySet();
	}
	
	public HashMap<Integer, String> getEmployees(){
		return this.employees;
	}
	
	public Barcode nameToBarcode(String name) {
		if(!nameToBarcode.containsKey(name)) return null;
		return nameToBarcode.get(name);
	}
	
	public PriceLookupCode nameToPLU(String name) {
		if(!nameToPLU.containsKey(name)) return null;
		return nameToPLU.get(name);
	}
	
	public double pluToWeight(PriceLookupCode plu) {
		if(!pluToWeight.containsKey(plu)) return 0.0;
		return pluToWeight.get(plu);
	}
	
	public Card numberToCard(String cardNum) {
		if(!numberToCard.containsKey(cardNum)) return null;
		
		return numberToCard.get(cardNum);
	}
	
	public String membershipToName(Barcode barcode) {
		if(!memberships.containsKey(barcode)) return null;
		return memberships.get(barcode);
	}
	
	public String numberToPin(String cardNum) {
		if (!cardNumtoPin.containsKey(cardNum)) return null;
		
		return cardNumtoPin.get(cardNum);
	}
		
	public Banknote nameToBanknote(String name) {
		
		if(name.equals("Five Dollars")) {
			return  new Banknote(Currency.getInstance(Locale.CANADA), 5);
		}else if(name.equals("Ten Dollars")) {
			return new Banknote(Currency.getInstance(Locale.CANADA), 10);
		}else if(name.equals("Twenty Dollars")) {
			return  new Banknote(Currency.getInstance(Locale.CANADA), 20);
		}else if(name.equals("Fifty Dollars")) {
			return  new Banknote(Currency.getInstance(Locale.CANADA), 50);
		}else if(name.equals("One Hundred Dollars")) {
			return new Banknote(Currency.getInstance(Locale.CANADA), 100);
		}else {
			return null;
		}
	}
			
	public Coin nameToCoin(String name) {
		
		if(name.equals("Five Cents")) {
			 return new Coin(Currency.getInstance(Locale.CANADA), new BigDecimal(0.05));
		}else if(name.equals("Ten Cents")) {
			return new Coin(Currency.getInstance(Locale.CANADA), new BigDecimal(0.10));
		}else if(name.equals("Twenty Five Cents")) {
			return  new Coin(Currency.getInstance(Locale.CANADA), new BigDecimal(0.25));
		}else if(name.equals("One Dollar")) {
			return new Coin(Currency.getInstance(Locale.CANADA), new BigDecimal(1.0));
		}else if(name.equals("Two Dollars")) {
			return new Coin(Currency.getInstance(Locale.CANADA), new BigDecimal(2.0));
		}else {
			return null;
		}
	}
	
	/* CRUD operations for CardIssuer */
	
	public void createNewCardData(String issuerName, String type, String number, String cardholder, String ccv, String pin, boolean isTapEnabled, boolean hasChip, Calendar expiry, BigDecimal amount) {
		if(!nameToIssuer.containsKey(issuerName)) throw new IllegalArgumentException();
		
		Card newCard = new Card(type, number, cardholder, ccv, pin, isTapEnabled, hasChip);
		nameToIssuer.get(issuerName).addCardData(number, cardholder, expiry, ccv, amount);	
		cardNumToIssuer.put(number, issuerName);		
		numberToCard.put(number, newCard);
		cardNumtoPin.put(number,pin);
		
		
		
	}
	
	/*
	 * Find the card issuer of the respective card number
	 */
	public CardIssuer getCardIssuer(String cardNumber) {
		if(!cardNumToIssuer.containsKey(cardNumber)) throw new IllegalArgumentException();
		
		String issuerName = cardNumToIssuer.get(cardNumber);
		
		if(!nameToIssuer.containsKey(issuerName)) throw new IllegalArgumentException();
	
		return nameToIssuer.get(issuerName);
	}
	
	/* CRUD Operations for Barcoded Products */
	
	/*
	 * Creates a new product in the database 
	 * 
	 * @param product The new product the be added to the database
	 * 
	 * @throw IllegalArgumentException when the product already exsits in the database
	 *
	 */
	public void createNewProductWithBarcode(BarcodedProduct product) throws IllegalArgumentException {
		if(ProductDatabases.BARCODED_PRODUCT_DATABASE.containsKey(product.getBarcode())) throw new IllegalArgumentException();
		
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(product.getBarcode(), product);
		nameToBarcode.put(product.getDescription(), product.getBarcode());
	}
	
	/*
	 * Updates the value of an already existing product in the database
	 * 
	 * @param product the new version of an existing product in the database
	 * 
	 * @throws IllegalArgumentException If the product does not exist in the database
	 */
	public void updateProductWithBarcode(BarcodedProduct product) throws IllegalArgumentException{
		if(!ProductDatabases.BARCODED_PRODUCT_DATABASE.containsKey(product.getBarcode())) throw new IllegalArgumentException();
		ProductDatabases.BARCODED_PRODUCT_DATABASE.replace(product.getBarcode(), product);
	}
	
	/*
	 * Get's the information of a product from the database
	 * 
	 * @param barcode of the product we are looking for
	 * 
	 * @return BarcodedProdcut associated to the input barcode
	 * 
	 * @throws IllegalArgumentException If the product associated to the barcode does not exist in the DB
	 * 
	 */
	public BarcodedProduct readProductWithBarcode(Barcode barcode) throws IllegalArgumentException {
		if(!ProductDatabases.BARCODED_PRODUCT_DATABASE.containsKey(barcode)) throw new IllegalArgumentException();
		return ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
	}
	
	/*
	 * 
	 * Deletes the information of product in the database. If the product does not exist, nothing happens
	 * 
	 * @param barcode of the product to delete from the database
	 */
	public void deleteProductWithBarcode(Barcode barcode) {
		if(!ProductDatabases.BARCODED_PRODUCT_DATABASE.containsKey(barcode)) return;
		ProductDatabases.BARCODED_PRODUCT_DATABASE.remove(barcode);
	}	
	
	/*
	 * Empties the Barcoded Prodcut database
	 */
	public void emptyProductsWithBarcode() {
		ProductDatabases.BARCODED_PRODUCT_DATABASE.clear();
	}
	
	/* CRUD Operations for PLU Products */
	
	/*
	 * Creates a new product in the database 
	 * 
	 * @param product The new product the be added to the database
	 * 
	 * @throw IllegalArgumentException when the product already exsits in the database
	 *
	 */
	public void createNewProductWithPLU(PLUCodedProduct product, double expectedWeight) throws IllegalArgumentException {
		if(ProductDatabases.PLU_PRODUCT_DATABASE.containsKey(product.getPLUCode())) throw new IllegalArgumentException();
		ProductDatabases.PLU_PRODUCT_DATABASE.put(product.getPLUCode(), product);
		nameToPLU.put(product.getDescription(), product.getPLUCode());
		pluToWeight.put(product.getPLUCode(), expectedWeight);
	}
	
	
	/*
	 * Updates the value of an already existing product in the database
	 * 
	 * @param product the new version of an exsiting product in the database
	 * 
	 * @throws IllegalArgumentException If the product does not exist in the database
	 */
	public void updateProductWithPLU(PLUCodedProduct product) throws IllegalArgumentException{
		if(!ProductDatabases.PLU_PRODUCT_DATABASE.containsKey(product.getPLUCode())) throw new IllegalArgumentException();
		ProductDatabases.PLU_PRODUCT_DATABASE.replace(product.getPLUCode(), product);
	}
	
	/*
	 * Get's the information of a product from the database
	 * 
	 * @param plu of the product we are looking for
	 * 
	 * @return PLUCodedProduct associated to the input plu
	 * 
	 * @throws IllegalArgumentException If the product associated to the plu does not exist in the DB
	 * 
	 */
	public PLUCodedProduct readProductWithPLU(PriceLookupCode plu) throws IllegalArgumentException {
		if(!ProductDatabases.PLU_PRODUCT_DATABASE.containsKey(plu)) throw new IllegalArgumentException();
		return ProductDatabases.PLU_PRODUCT_DATABASE.get(plu);
	}
	
	/*
	 * 
	 * Deletes the information of product in the database. If the product does not exist, nothing happens
	 * 
	 * @param plu of the produdct to delete from the database
	 */
	public void deleteProductWithPLU(PriceLookupCode plu) {
		if(!ProductDatabases.PLU_PRODUCT_DATABASE.containsKey(plu)) return;
		ProductDatabases.PLU_PRODUCT_DATABASE.remove(plu);
	}	
	
	/*
	 * Empties the BarcodedProduct database
	 */
	public void emptyProductsWithPLU() {
		ProductDatabases.PLU_PRODUCT_DATABASE.clear();
	}
	
	
	
	/**
	 * Update the inventory to have a new product in it
	 * @param product
	 */
	public void updateInventory(Product product) {
		if(!ProductDatabases.INVENTORY.containsKey(product)) throw new IllegalArgumentException();
		Integer currentQtty = ProductDatabases.INVENTORY.get(product);
		ProductDatabases.INVENTORY.replace(product, currentQtty + 1);
	}
	
	/* CRUD Operations for Membership Products */
	
	/*
	 * Creates a new product in the database 
	 * 
	 * @param membership The new membership we want to create
	 * 
	 * @throw IllegalArgumentException when the membership already exists in the database
	 *
	 */
	public void createNewMembership(Barcode membership, String name) throws IllegalArgumentException {
		if(memberships.containsKey(membership)) throw new IllegalArgumentException();
		memberships.put(membership, name);
	}
	/*
	 * Get's the information of a product from the database
	 * 
	 * @param barcode of the membership we are looking for
	 * 
	 * @return boolean signifying whether the membership exists or not
	 * 
	 */
	public boolean readMembership(Barcode barcode) throws IllegalArgumentException {
		return memberships.containsKey(barcode);
	}
	
	/*
	 * 
	 * Deletes the membership from the database
	 * 
	 * @param barcode of the membership to delete from the database
	 */
	public void deleteMembership(Barcode barcode) {
		if(!memberships.containsKey(barcode)) return;
		memberships.remove(barcode);
		
	}	
	
	/* CRUD Operations for Employees */
	
	/*
	 * Creates a new employee in the database 
	 * 
	 * @param pin Unique pin of for the employee
	 * @param name Name of the employee
	 * 
	 * @throw IllegalArgumentException when the employee already exists in the database
	 *
	 */
	public void createNewEmployee(Integer pin, String name) throws IllegalArgumentException {
		if(employees.containsKey(pin)) throw new IllegalArgumentException();
		employees.put(pin, name);
	}
	
	
	/*
	 * Updates the value of an already existing employee in the database
	 * 
	 * @param pin of the employee we want to modify
	 * @param name New name of the employee
	 * 
	 * @throws IllegalArgumentException If the employee does not exist in the database
	 */
	public void updateEmployee(Integer pin, String name) throws IllegalArgumentException{
		if(!employees.containsKey(pin)) throw new IllegalArgumentException();
		employees.replace(pin, name);
	}
	
	/*
	 * Get's the information of an employee from the database
	 * 
	 * @param pin of the employee
	 * 
	 * @return String the name of the employee (if he/she exists)
	 * 
	 */
	public String readEmployee(Integer pin) throws IllegalArgumentException {
		if(!employees.containsKey(pin)) return null;
		return employees.get(pin);
	}
	
	/*
	 * 
	 * Deletes the information of employee in the database. If the employee does not exist, nothing happens
	 * 
	 * @param pin associated with employee to be deleted from DB
	 */
	public void deleteEmployee(Integer pin) {
		if(!employees.containsKey(pin)) return;
		employees.remove(pin);
	}	
	
}
