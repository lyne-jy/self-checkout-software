package ca.ucalgary.seng300.testing.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import ca.ucalgary.seng300.selfcheckout.product.Scan;
import ca.ucalgary.seng300.selfcheckout.product.ScanObserver;
import ca.ucalgary.seng300.selfcheckout.utility.SoftwarePhase;
import ca.ucalgary.seng300.testing.utility.TestUtility;

@RunWith(JUnit4.class)
public class ScanTest {
	
    private Scan scan;
    private SelfCheckoutStation hw;
    private int actualFound;
    
    /**
     * Add an apple with weight 15.19, price 3.00 and barcode 69 to the database 
     */
    @Before
    public void setup() {
    	
    	hw = TestUtility.getHarwareInstance1();
    	
    	scan = new Scan();
    	
    	hw.handheldScanner.attach(scan);
    	hw.mainScanner.attach(scan);
    	
    	scan.updatePhase(SoftwarePhase.READY);
    	   	
    	this.actualFound = 0;
    }
  
    /* SCANNING */
    
    @Test 
    public void nonexistentBarcodeMainScanner() {
    	
    	scan.attach(new ScanObserver(){

			@Override
			public void barcodeScannedInWrongPhase() {
				fail();
			}

			@Override
			public void barcodeScannedNotInDatabase() {
				actualFound++;	
			}

			@Override
			public void productScanned(Barcode barcode) {
				fail();
			}
			
    	});
    	  
    	BarcodedItem item = new BarcodedItem(new Barcode(new Numeral[]{Numeral.two, Numeral.five}), 10.0);
    	
    	while(actualFound != 1) {
    		hw.mainScanner.scan(item);
    	}
    	
    	assertEquals("Observers not notified product is not in the database", 1, actualFound);
    }

    @Test
    public void scanInWrongPhaseMainScanner() {
    	
    	scan.updatePhase(SoftwarePhase.PAYING);
    	
    	scan.attach(new ScanObserver(){

			@Override
			public void barcodeScannedInWrongPhase() {
				actualFound++;
			}

			@Override
			public void barcodeScannedNotInDatabase() {
				fail();
				
			}

			@Override
			public void productScanned(Barcode barcode) {
				fail();
			}
			
    	});
    	    	
    	BarcodedItem item = new BarcodedItem(new Barcode(new Numeral[]{Numeral.two, Numeral.five}), 10.0);
    	
    	while(actualFound != 1) {
    		hw.mainScanner.scan(item);
    	}
    		
    	assertEquals("Observers not notified product tried to be scanned in wrong phase", 1, actualFound);
    }
      
    @Test
    public void successfulScanMainScanner(){
    	
    	scan.attach(new ScanObserver(){

			@Override
			public void barcodeScannedInWrongPhase() {
				fail();
			}

			@Override
			public void barcodeScannedNotInDatabase() {
				fail();
			}

			@Override
			public void productScanned(Barcode barcode) {
				actualFound++;
				assertEquals(TestUtility.normalItemBarcode, barcode);
				
			}
    	});
    	
    	while(actualFound != 1) {
    		hw.mainScanner.scan(TestUtility.normalItem);
    	}
    }      
    
    @Test 
    public void nonexistentBarcodeHandHeldScanner() {
    	
    	scan.attach(new ScanObserver(){

			@Override
			public void barcodeScannedInWrongPhase() {
				fail();
			}

			@Override
			public void barcodeScannedNotInDatabase() {
				actualFound++;	
			}

			@Override
			public void productScanned(Barcode barcode) {
				fail();				
			}
			
    	});
    	  
    	BarcodedItem item = new BarcodedItem(new Barcode(new Numeral[]{Numeral.two, Numeral.five}), 10.0);
    	
    	while(actualFound != 1) {
    		hw.handheldScanner.scan(item);
    	}
    	
    	assertEquals("Observers not notified product is not in the database", 1, actualFound);
    }

    @Test
    public void scanInWrongPhaseHandHeldScanner() {
    	
    	scan.updatePhase(SoftwarePhase.PAYING);
    	
    	scan.attach(new ScanObserver(){

			@Override
			public void barcodeScannedInWrongPhase() {
				actualFound++;
				
			}

			@Override
			public void barcodeScannedNotInDatabase() {
				fail();
				
			}

			@Override
			public void productScanned(Barcode barcode) {
				fail();
			}
			
    	});
    	    	
    	BarcodedItem item = new BarcodedItem(new Barcode(new Numeral[]{Numeral.two, Numeral.five}), 10.0);
    	
    	while(actualFound != 1) {
    		hw.handheldScanner.scan(item);
    	}
    	
    	assertEquals("Observers not notified product tried to be scanned in wrong phase", 1, actualFound);
    }
      
    @Test
    public void successfulScanHandHeldScanner(){
    	
    	scan.attach(new ScanObserver(){

			@Override
			public void barcodeScannedInWrongPhase() {
				fail();
			}

			@Override
			public void barcodeScannedNotInDatabase() {
				fail();
			}

			@Override
			public void productScanned(Barcode barcode) {
				actualFound++;
				assertEquals(TestUtility.normalItemBarcode, barcode);
			}
    	});
    	
    	while(actualFound != 1) {
    		hw.handheldScanner.scan(TestUtility.normalItem);
    	}
    }    
    
}
