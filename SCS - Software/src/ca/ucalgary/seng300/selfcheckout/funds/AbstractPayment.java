package ca.ucalgary.seng300.selfcheckout.funds;

import java.math.BigDecimal;

import ca.ucalgary.seng300.selfcheckout.utility.ComplexAbstractSoftware;

/*
 * Abstract class that represents some basic information for any form of payment
 */
abstract class AbstractPayment<T> extends ComplexAbstractSoftware<T>{

	protected BigDecimal totalDue;
	
	public AbstractPayment() {
		totalDue = BigDecimal.ZERO;
	}
	
	/*
	 * Sets the total amount due 
	 * 
	 * @param totalDue amount due
	 */
	public final void setTotalDue(BigDecimal totalDue) {
		if(totalDue.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException();
		this.totalDue = totalDue;
	}
	
	/*
	 * @return Total amount due
	 */
	public final BigDecimal getTotalDue() {
		return this.totalDue;
	}
}
