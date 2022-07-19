package ca.ucalgary.seng300.selfcheckout.utility;

import java.util.Timer;
import java.util.TimerTask;

public class PlaceItemTimer extends ComplexAbstractSoftware<TimerObserver>{
    private final int TIMEOUT = 5 * 1000;
    private Timer timer;

    public void scheduleTimer(boolean isForBaggingArea) {
    	
    	timer = new Timer();
    	
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for(TimerObserver obs: observers) {
                	
                	try {
                		if(isForBaggingArea) {
                    		obs.failedToPlaceItemInBaggingArea();
                    	}else {
                    		obs.failedToPlaceItemInScanningArea();
                    	}
                	}catch(Exception e){}                	
                }
                timer.cancel();
            }
        }, TIMEOUT);
    }

    public void cancelTimer() {
        timer.cancel();
    }
}
