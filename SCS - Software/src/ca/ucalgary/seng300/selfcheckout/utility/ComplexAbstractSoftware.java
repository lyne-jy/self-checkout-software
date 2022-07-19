package ca.ucalgary.seng300.selfcheckout.utility;

import java.util.ArrayList;

/*
 * Represents a software component that consists not only of a software phase, but also a list of observers 
 * and a way of attaching them.
 */
public class ComplexAbstractSoftware<T> extends SimpleAbstractSoftware {

	protected ArrayList<T> observers;
	
	public ComplexAbstractSoftware() {
		observers = new ArrayList<>();
	}
	
	public final void attach(T obs) {
		if(obs == null) throw new NullPointerException();
		observers.add(obs);
	}
}