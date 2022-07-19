package ca.ucalgary.seng300.selfcheckout.utility;

/*
 * Exception used to represent when something in the database was not found
 */
@SuppressWarnings("serial")
public class ItemNotFoundException extends Exception {
	
	public ItemNotFoundException(String string) {
	}
}
