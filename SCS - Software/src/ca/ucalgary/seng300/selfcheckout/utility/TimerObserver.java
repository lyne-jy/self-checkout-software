package ca.ucalgary.seng300.selfcheckout.utility;

public interface TimerObserver {
    /*
     * Notifies observer that user failed to place item to bagging area
     */
    public void failedToPlaceItemInBaggingArea();
    
    /*
     * Notifies observer that user failed to place item to scanning area
     */
    public void failedToPlaceItemInScanningArea();
}