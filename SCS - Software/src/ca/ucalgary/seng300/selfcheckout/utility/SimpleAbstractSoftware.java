package ca.ucalgary.seng300.selfcheckout.utility;

/*
 * Simple abstract class to synchronize software "state" among different facades.
 */
 public abstract class SimpleAbstractSoftware {

	protected SoftwarePhase curPhase;
	
	protected SimpleAbstractSoftware(){
		curPhase = SoftwarePhase.READY;
	}
	
	/*
	 * Updates the current phase of the software facade
	 * 
	 * @Param newPhase 
	 */
	public void updatePhase(SoftwarePhase newPhase) {
		this.curPhase = newPhase;
	}
	
	/*
	 * Used to get the current phase of the software facade
	 * 
	 * @Return Software Phase
	 */
	public SoftwarePhase getPhase() {
		return this.curPhase;
	}
}
