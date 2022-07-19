package ca.ucalgary.seng300.selfcheckout.utility;

/*
 * Represents an illegal operation that happened in software
 */
@SuppressWarnings("serial")
public class IllegalActionException extends Exception {
	@SuppressWarnings("unused")
	private String nested;

	/**
	 * Constructor used to nest other exceptions.
	 * 
	 * @param nested
	 *            An underlying exception that is to be wrapped.
	 */
	public IllegalActionException(Exception nested) {
		this.nested = nested.toString();
	}

	/**
	 * Basic constructor.
	 * 
	 * @param message
	 *            An explanatory message of the problem.
	 */
	public IllegalActionException(String message) {
		nested = message;
	}
}
