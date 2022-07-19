package ca.ucalgary.seng300.testing.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.InvalidArgumentSimulationException;
import org.lsmr.selfcheckout.SimulationException;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.BanknoteStorageUnit;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.CoinStorageUnit;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.BanknoteDispenserObserver;

import ca.ucalgary.seng300.selfcheckout.SelfCheckoutControlSoftware;
import ca.ucalgary.seng300.selfcheckout.communication.SelfCheckoutNotifier;
import ca.ucalgary.seng300.simulations.StoreAttendant;
import ca.ucalgary.seng300.testing.utility.BanknoteStorageUnitObserverStub;
import ca.ucalgary.seng300.testing.utility.CoinDispenserObserverStub;
import ca.ucalgary.seng300.testing.utility.CoinStorageUnitObserverStub;
import ca.ucalgary.seng300.testing.utility.TestUtility;

/*
 * Test suite tests wether the simulation of physical actions taken by the store attendant are correctly being registered
 */
public class StoreAttendantTest {

	private StoreAttendant attendant;
	private SelfCheckoutStation hw;
	private SelfCheckoutControlSoftware sw;
	private CoinStorageUnit csu;
	private BanknoteStorageUnit bsu;
	private CoinDispenser cd;
	private BanknoteDispenser bd;
	private boolean coinsLoaded;
	private boolean banknotesLoaded;
	private int actualFound;
	
	@Before
	public void setup() {
		
		hw = TestUtility.getHarwareInstance1();
		sw = new SelfCheckoutControlSoftware(hw);
		
		attendant = new StoreAttendant(new SelfCheckoutControlSoftware[] {sw});
	
		csu = hw.coinStorage;
		bsu = hw.banknoteStorage;
		coinsLoaded = false;
		banknotesLoaded = false;
		
		actualFound = 0;
	}
	
	/**
	 * attendant adds ink to printer
	 * @throws OverloadException
	 */
	@Test
	public void addInkToPrinter() throws OverloadException {
		
		attendant.attach(new SelfCheckoutNotifier(sw.ID){
	
			@Override
			public void paperAddedToPrinter(int units) {
				fail();
			}

			@Override
			public void inkAddedToPrinter(int quantity) {
				actualFound++;
				assertEquals(10, quantity);
			}
			
		});
		
		attendant.addInkToPrinter(hw, 10, sw.ID);
		
		assertEquals(1, actualFound);	
	}
	
	/**
	 * attendant adds paper to printer
	 * @throws OverloadException
	 */
	@Test
	public void addPaperToPrinter() throws OverloadException {
	
		attendant.attach(new SelfCheckoutNotifier(sw.ID){
			
			@Override
			public void paperAddedToPrinter(int units) {
				actualFound++;
				assertEquals(10, units);	
			}

			@Override
			public void inkAddedToPrinter(int quantity) {
				fail();
			}
		});
		
		
		attendant.addPaperToPrinter(hw, 10, sw.ID);
		
		assertEquals(1, actualFound);	
		
	}
	
	/**
	 * attendant adds to much paper in printer and goes over capacity
	 * @throws OverloadException
	 */
	@Test(expected=OverloadException.class)
	public void addedToMuchPaper() throws OverloadException {
		attendant.attach(new SelfCheckoutNotifier(sw.ID){
		
		public void paperAddedToPrinter(int units) {
			assertEquals((int)Math.pow(2, 21), units);	
		}

		public void inkAddedToPrinter(int quantity) {
			fail();
		}
	});
		attendant.addPaperToPrinter(hw, (int)Math.pow(2, 21), sw.ID);

		
	}
	/**
	 * attendant adds to much ink in printer and goes over capacity
	 * @throws OverloadException
	 */
	@Test(expected=OverloadException.class)
	public void addedToMuchInk() throws OverloadException {
		attendant.attach(new SelfCheckoutNotifier(sw.ID){
		
		public void paperAddedToPrinter(int units) {
			fail();
		}

		public void inkAddedToPrinter(int quantity) {
			assertEquals((int)Math.pow(2, 21), quantity);
		}
	});
		attendant.addInkToPrinter(hw, (int)Math.pow(2, 21), sw.ID);
	}
	
	/**
	 * Attendant adds both paper and ink 
	 * @throws OverloadException
	 */
	@Test
	public void addedPaperAndInk() throws OverloadException {
		attendant.attach(new SelfCheckoutNotifier(sw.ID){
			@Override
			public void paperAddedToPrinter(int units) {
				actualFound++;
				assertEquals(10, units);	
			}
			@Override
			public void inkAddedToPrinter(int quantity) {
				actualFound++;
				assertEquals(10, quantity);	
			}
		});
			
		attendant.addPaperToPrinter(hw, 10, sw.ID);
		attendant.addInkToPrinter(hw, 10, sw.ID);
			
		assertEquals(2, actualFound);	

	}
	
	/**
	 * Adding Zero paper to the printer
	 * @throws OverloadException
	 */
	@Test
	public void addedZeroPaper() throws OverloadException {
	attendant.attach(new SelfCheckoutNotifier(sw.ID){
		@Override
		public void paperAddedToPrinter(int units) {
			actualFound++;
			assertEquals(0, units);	
		}
		@Override
		public void inkAddedToPrinter(int quantity) {
			fail();
		}
	});
		
	attendant.addPaperToPrinter(hw, 0, sw.ID);
	assertEquals(1, actualFound);	
	}
	
	/**
	 * Adding Zero ink to the printer
	 * @throws OverloadException
	 */
	@Test
	public void addedZeroInk() throws OverloadException {
	attendant.attach(new SelfCheckoutNotifier(sw.ID){
		@Override
		public void paperAddedToPrinter(int units) {
				fail();			
		}
		@Override
		public void inkAddedToPrinter(int quantity) {
			actualFound++;
			assertEquals(0, quantity);	
			}
	});
		
	attendant.addInkToPrinter(hw, 0, sw.ID);
	assertEquals(1, actualFound);	
	}
	
	/**
	 * Negative paper is being added to printer
	 * @throws OverloadException
	 */	
	@Test(expected=InvalidArgumentSimulationException.class)
	public void addedNegativePaper() throws OverloadException {
	attendant.attach(new SelfCheckoutNotifier(sw.ID){
		@Override
		public void paperAddedToPrinter(int units) {
			assertEquals(-1, units);	
		}
		@Override
		public void inkAddedToPrinter(int quantity) {
			fail();
		}
	});
	
	attendant.addPaperToPrinter(hw, -1, sw.ID);	
	}
	
	/**
	 * Negative Ink is being added to printer
	 * @throws OverloadException
	 */
	@Test(expected=InvalidArgumentSimulationException.class)
	public void addedNegativeInk() throws OverloadException {
	attendant.attach(new SelfCheckoutNotifier(sw.ID){
		@Override
		public void paperAddedToPrinter(int units) {
			fail();

		}
		@Override
		public void inkAddedToPrinter(int quantity) {
			assertEquals(-1, quantity);	
			}
	});
	
	attendant.addInkToPrinter(hw, -1, sw.ID);	
	}
	
	/**
	 * Attendant empties Coin Storage Unit
	 * @throws SimulationException, Overload Exception
	 */
	@Test
	public void emptyCoinStorageTest() throws SimulationException, OverloadException {
		csu.attach(new CoinStorageUnitObserverStub() {
			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
				
			}

			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
				
			}

			@Override
			public void coinsFull(CoinStorageUnit unit) {
				fail();
				
			}

			@Override
			public void coinAdded(CoinStorageUnit unit) {
				fail();
				
			}

			@Override
			public void coinsLoaded(CoinStorageUnit unit) {
				coinsLoaded = true;
			}

			@Override
			public void coinsUnloaded(CoinStorageUnit unit) {
				coinsLoaded = false;
				actualFound++;
				
			}	
		});
		csu.load(TestUtility.oneDollar);
		csu.load(TestUtility.twoDollars);
		
		attendant.emptyCoinStorage(hw);
		Assert.assertFalse("Store attendant did not properly empty coin storage", coinsLoaded);
		Assert.assertEquals("Store attendant did not properly empty coin storage", 1, actualFound);
	}
	
	/**
	 * Attendant empties Banknote Storage Unit
	 * @throws SimulationException, OverloadException
	 */
	@Test
	public void emptyBanknoteStorageTest() throws SimulationException, OverloadException {
		bsu.attach(new BanknoteStorageUnitObserverStub() {
			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
				
			}

			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
				
			}

			@Override
			public void banknotesFull(BanknoteStorageUnit unit) {
				fail();
				
			}

			@Override
			public void banknoteAdded(BanknoteStorageUnit unit) {
				fail();
				
			}

			@Override
			public void banknotesLoaded(BanknoteStorageUnit unit) {
				banknotesLoaded = true;
				
			}

			@Override
			public void banknotesUnloaded(BanknoteStorageUnit unit) {
				banknotesLoaded = false;
				actualFound++;
				
			}
		});
		
		bsu.load(TestUtility.fiveDollarBill);
		bsu.load(TestUtility.tenDollarBill);
		
		attendant.emptyBanknoteStorage(hw, bsu);
		Assert.assertFalse("Store attendant did not properly empty banknote storage", banknotesLoaded);
		Assert.assertEquals("Store attendant did not properly empty banknote storage", 1, actualFound);
	}
	
	/**
	 * Refills one dollar coin dispenser with an empty array
	 * @throws SimulationException, OverloadException
	 */
	@Test
	public void refillCoinDispenserNoCoin() throws SimulationException, OverloadException {
		cd = hw.coinDispensers.get(TestUtility.oneDollar.getValue());
		cd.attach(new CoinDispenserObserverStub() {
			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
				
			}

			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
				
			}

			@Override
			public void coinsFull(CoinDispenser dispenser) {
				fail();
				
			}

			@Override
			public void coinsEmpty(CoinDispenser dispenser) {
				fail();
				
			}

			@Override
			public void coinAdded(CoinDispenser dispenser, Coin coin) {
				fail();
				
			}

			@Override
			public void coinRemoved(CoinDispenser dispenser, Coin coin) {
				fail();
				
			}

			@Override
			public void coinsLoaded(CoinDispenser dispenser, Coin... coins) {
				fail("coinsLoaded should not be called");
			}

			@Override
			public void coinsUnloaded(CoinDispenser dispenser, Coin... coins) {
				fail();
				
			}
		});
		
		Coin[] coins = new Coin[0];
		attendant.refiilsCoinDispenser(hw,coins);
	}
	
	/**
	 * Refills one dollar coin dispenser with one dollar
	 * @throws SimulationException, OverloadException
	 */
	@Test
	public void refillCoinDispenserOneCoin() throws SimulationException, OverloadException {
		cd = hw.coinDispensers.get(TestUtility.oneDollar.getValue());
		cd.attach(new CoinDispenserObserverStub() {
			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
				
			}

			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
				
			}

			@Override
			public void coinsFull(CoinDispenser dispenser) {
				fail();
				
			}

			@Override
			public void coinsEmpty(CoinDispenser dispenser) {
				fail();
				
			}

			@Override
			public void coinAdded(CoinDispenser dispenser, Coin coin) {
				fail();
				
			}

			@Override
			public void coinRemoved(CoinDispenser dispenser, Coin coin) {
				fail();
				
			}

			@Override
			public void coinsLoaded(CoinDispenser dispenser, Coin... coins) {
				coinsLoaded = true;
				actualFound++;
				
			}

			@Override
			public void coinsUnloaded(CoinDispenser dispenser, Coin... coins) {
				fail();
				
			}
		});
		
		
		attendant.refiilsCoinDispenser(hw,TestUtility.oneDollar);
		Assert.assertTrue("Store attendant did not properly fill one dollar coin dispenser", coinsLoaded);
		Assert.assertEquals("Store attendant did not properly fill one dollar coin dispenser", 1, actualFound);
	}
	
	/**
	 * Refills one dollar coin dispenser with two dollar coin
	 * @throws SimulationException, OverloadException
	 */
	@Test
	public void refillCoinDispenserWrongCoin() throws SimulationException, OverloadException {
		cd = hw.coinDispensers.get(TestUtility.oneDollar.getValue());
		cd.attach(new CoinDispenserObserverStub() {
			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
				
			}

			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
				
			}

			@Override
			public void coinsFull(CoinDispenser dispenser) {
				fail();
				
			}

			@Override
			public void coinsEmpty(CoinDispenser dispenser) {
				fail();
				
			}

			@Override
			public void coinAdded(CoinDispenser dispenser, Coin coin) {
				fail();
				
			}

			@Override
			public void coinRemoved(CoinDispenser dispenser, Coin coin) {
				fail();
				
			}

			@Override
			public void coinsLoaded(CoinDispenser dispenser, Coin... coins) {
					coinsLoaded = true;
					actualFound++;
				
			}

			@Override
			public void coinsUnloaded(CoinDispenser dispenser, Coin... coins) {
				fail();
				
			}
		});
		
		
		attendant.refiilsCoinDispenser(hw,TestUtility.twoDollars);
		Assert.assertFalse("Store attendant properly filled the one dollar coin dispenser", coinsLoaded);
		Assert.assertEquals("Store attendant properly filled the one dollar coin dispenser", 0, actualFound);
	}
	
	/**
	 * Refills one dollar coin dispenser with 100 one dollar coins
	 * @throws SimulationException, OverloadException
	 */
	@Test
	public void refillCoinDispenserMultipleCoins() throws SimulationException, OverloadException {
		cd = hw.coinDispensers.get(TestUtility.oneDollar.getValue());
		cd.attach(new CoinDispenserObserverStub() {
			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
				
			}

			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
				
			}

			@Override
			public void coinsFull(CoinDispenser dispenser) {
				fail();
				
			}

			@Override
			public void coinsEmpty(CoinDispenser dispenser) {
				fail();
				
			}

			@Override
			public void coinAdded(CoinDispenser dispenser, Coin coin) {
				fail();
				
			}

			@Override
			public void coinRemoved(CoinDispenser dispenser, Coin coin) {
				fail();
				
			}

			@Override
			public void coinsLoaded(CoinDispenser dispenser, Coin... coins) {
				coinsLoaded = true;
				actualFound++;
				
			}

			@Override
			public void coinsUnloaded(CoinDispenser dispenser, Coin... coins) {
				fail();
				
			}
		});
		
		for (int i = 0; i < 100; i++) {
			coinsLoaded = false;
			attendant.refiilsCoinDispenser(hw,TestUtility.oneDollar);
		}
			
		
		Assert.assertTrue("Store attendant did not properly fill one dollar coin dispenser", coinsLoaded);
		Assert.assertEquals("Store attendant did not properly fill 100 one dollar coins in dispenser", 100, actualFound);
	}
	
	/**
	 * Refills all coin dispensers with their respective denomination coins
	 * @throws SimulationException, OverloadException
	 */
	@Test
	public void refillAllCoinDispensers() throws SimulationException, OverloadException {
		for (BigDecimal denom: TestUtility.getCoinDenominations()) {
			cd = hw.coinDispensers.get(denom);
			cd.attach(new CoinDispenserObserverStub() {
				@Override
				public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
					fail();
				
				}

				@Override
				public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
					fail();
				
				}

				@Override
				public void coinsFull(CoinDispenser dispenser) {
					fail();
				
				}
				
				@Override
				public void coinsEmpty(CoinDispenser dispenser) {
					fail();
				
				}

				@Override
				public void coinAdded(CoinDispenser dispenser, Coin coin) {
					fail();
				
				}

				@Override
				public void coinRemoved(CoinDispenser dispenser, Coin coin) {
					fail();
				
				}

				@Override
				public void coinsLoaded(CoinDispenser dispenser, Coin... coins) {
					coinsLoaded = true;
					actualFound++;
				
				}

				@Override
				public void coinsUnloaded(CoinDispenser dispenser, Coin... coins) {
					fail();
				
				}
			});
		
	
			coinsLoaded = false;
			attendant.refiilsCoinDispenser(hw, new Coin(TestUtility.getCurrency(), denom));
		
		}
		
		Assert.assertTrue("Store attendant did not properly fill all coin dispensers", coinsLoaded);
		Assert.assertEquals("Store attendant properly filled the one dollar coin dispenser", TestUtility.getCoinDenominations().length , actualFound);
	}
	
	/**
	 * Overloads one dollar coin dispenser with one dollar coins
	 * @throws SimulationException, OverloadException
	 */
	@Test
	public void overloadCoinDispenserMultipleCoins() throws SimulationException, OverloadException {
		cd = hw.coinDispensers.get(TestUtility.oneDollar.getValue());
		cd.attach(new CoinDispenserObserverStub() {
			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
				
			}

			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
				
			}

			@Override
			public void coinsFull(CoinDispenser dispenser) {
				fail();
				
			}

			@Override
			public void coinsEmpty(CoinDispenser dispenser) {
				fail();
				
			}

			@Override
			public void coinAdded(CoinDispenser dispenser, Coin coin) {
				fail();
				
			}

			@Override
			public void coinRemoved(CoinDispenser dispenser, Coin coin) {
				fail();
				
			}

			@Override
			public void coinsLoaded(CoinDispenser dispenser, Coin... coins) {
				coinsLoaded = true;
				actualFound++;
				
			}

			@Override
			public void coinsUnloaded(CoinDispenser dispenser, Coin... coins) {
				fail();
				
			}
		});
		
		for (int i = 0; i < cd.getCapacity() + 10; i++) {
			coinsLoaded = false;
			attendant.refiilsCoinDispenser(hw,TestUtility.oneDollar);
		}
			
		
		Assert.assertTrue("Last Coin did go to One Dollar Dispenser", coinsLoaded == false);
		Assert.assertTrue("All coins was filled into One Dollar Dispenser", cd.getCapacity() + 10 != actualFound);
	}
	
	/**
	 * Refills Five Dollar Banknote Dispenser with an empty array
	 * @throws OverloadException
	 */
	@Test
	public void refillBanknoteDispenserNoBanknote() throws OverloadException {
		bd = hw.banknoteDispensers.get(TestUtility.fiveDollarBill.getValue());
		bd.attach(new BanknoteDispenserObserver() {
			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
			}
	
			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
			}
	
			@Override
			public void moneyFull(BanknoteDispenser dispenser) {
				fail();
			}
	
			@Override
			public void banknotesEmpty(BanknoteDispenser dispenser) {
				fail();
			}
	
			@Override
			public void billAdded(BanknoteDispenser dispenser, Banknote banknote) {
				fail();
			}
	
			@Override
			public void banknoteRemoved(BanknoteDispenser dispenser, Banknote banknote) {
				fail();
			}
	
			@Override
			public void banknotesLoaded(BanknoteDispenser dispenser, Banknote... banknotes) {
				fail("coinsLoaded should not be called");
			}
	
			@Override
			public void banknotesUnloaded(BanknoteDispenser dispenser, Banknote... banknotes) {
				fail();
			}
		});
		
		Banknote[] banknotes = new Banknote[0];
		attendant.refillBanknoteDispenser(hw, banknotes);
	}


	/**
	 * Refills Five Dollar Banknote Dispenser with five dollar banknote
	 * @throws OverloadException
	 */	
	@Test
	public void refillBanknoteDispenserOneBanknote() throws OverloadException {
		bd = hw.banknoteDispensers.get(TestUtility.fiveDollarBill.getValue());
		bd.attach(new BanknoteDispenserObserver() {
			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
			}
	
			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
			}
	
			@Override
			public void moneyFull(BanknoteDispenser dispenser) {
				fail();
			}
	
			@Override
			public void banknotesEmpty(BanknoteDispenser dispenser) {
				fail();
			}
	
			@Override
			public void billAdded(BanknoteDispenser dispenser, Banknote banknote) {
				fail();
			}
	
			@Override
			public void banknoteRemoved(BanknoteDispenser dispenser, Banknote banknote) {
				fail();
			}
	
			@Override
			public void banknotesLoaded(BanknoteDispenser dispenser, Banknote... banknotes) {
				actualFound++;
				banknotesLoaded = true;
			}
	
			@Override
			public void banknotesUnloaded(BanknoteDispenser dispenser, Banknote... banknotes) {
				fail();
			}
		});

		attendant.refillBanknoteDispenser(hw, TestUtility.fiveDollarBill);
		Assert.assertTrue("Last Banknote did not go to its Dispenser", banknotesLoaded);
		Assert.assertEquals("1 Five Dollar banknote was not filled into its Dispenser", 1, actualFound);
	}

	/**
	 * Refills Five Dollar Banknote Dispenser with fifty dollar banknote
	 * @throws OverloadException
	 */	
	@Test
	public void refillBanknoteDispenserWrongBanknote() throws OverloadException {
		bd = hw.banknoteDispensers.get(TestUtility.fiveDollarBill.getValue());
		bd.attach(new BanknoteDispenserObserver() {
			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
			}
	
			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
			}
	
			@Override
			public void moneyFull(BanknoteDispenser dispenser) {
				fail();
			}
	
			@Override
			public void banknotesEmpty(BanknoteDispenser dispenser) {
				fail();
			}
	
			@Override
			public void billAdded(BanknoteDispenser dispenser, Banknote banknote) {
				fail();
			}
	
			@Override
			public void banknoteRemoved(BanknoteDispenser dispenser, Banknote banknote) {
				fail();
			}
	
			@Override
			public void banknotesLoaded(BanknoteDispenser dispenser, Banknote... banknotes) {
				actualFound++;
				banknotesLoaded = true;
			}
	
			@Override
			public void banknotesUnloaded(BanknoteDispenser dispenser, Banknote... banknotes) {
				fail();
			}
		});

		attendant.refillBanknoteDispenser(hw, TestUtility.fiftyDollarBill);
		Assert.assertTrue("Five dollar dispenser was filled", banknotesLoaded == false);
		Assert.assertEquals("Five dollar dispenser was filled", 0, actualFound);
	}

	
	/**
	 * Refills Five Dollar Banknote Dispenser with 100 five dollar banknotes
	 * @throws OverloadException
	 */	
	@Test
	public void refillBanknoteDispenserMultipleBanknotes() throws OverloadException {
		bd = hw.banknoteDispensers.get(TestUtility.fiveDollarBill.getValue());
		bd.attach(new BanknoteDispenserObserver() {
			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
			}
	
			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
			}
	
			@Override
			public void moneyFull(BanknoteDispenser dispenser) {
				fail();
			}
	
			@Override
			public void banknotesEmpty(BanknoteDispenser dispenser) {
				fail();
			}
	
			@Override
			public void billAdded(BanknoteDispenser dispenser, Banknote banknote) {
				fail();
			}
	
			@Override
			public void banknoteRemoved(BanknoteDispenser dispenser, Banknote banknote) {
				fail();
			}
	
			@Override
			public void banknotesLoaded(BanknoteDispenser dispenser, Banknote... banknotes) {
				actualFound++;
				banknotesLoaded = true;
			}
	
			@Override
			public void banknotesUnloaded(BanknoteDispenser dispenser, Banknote... banknotes) {
				fail();
			}
		});

		for (int i = 0; i < 100; i++) {
			banknotesLoaded = false;
			attendant.refillBanknoteDispenser(hw, TestUtility.fiveDollarBill);
		}
		Assert.assertTrue("Last Banknote did not go to its Dispenser", banknotesLoaded);
		Assert.assertEquals("100 Five Dollar banknotes was not filled into its Dispenser", 100, actualFound);
	}

	/**
	 * Refills All Banknote Dispensers with 100 of their respective denomination banknotes
	 * @throws OverloadException
	 */	
	@Test
	public void refillAllBanknoteDispensers() throws OverloadException {
		for (int denom : TestUtility.getBanknoteDenominations()) {
			bd = hw.banknoteDispensers.get(denom);
			bd.attach(new BanknoteDispenserObserver() {
				@Override
				public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
					fail();
				}
		
				@Override
				public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
					fail();
				}
		
				@Override
				public void moneyFull(BanknoteDispenser dispenser) {
					fail();
				}
		
				@Override
				public void banknotesEmpty(BanknoteDispenser dispenser) {
					fail();
				}
		
				@Override
				public void billAdded(BanknoteDispenser dispenser, Banknote banknote) {
					fail();
				}
		
				@Override
				public void banknoteRemoved(BanknoteDispenser dispenser, Banknote banknote) {
					fail();
				}
		
				@Override
				public void banknotesLoaded(BanknoteDispenser dispenser, Banknote... banknotes) {
					actualFound++;
					banknotesLoaded = true;
				}
		
				@Override
				public void banknotesUnloaded(BanknoteDispenser dispenser, Banknote... banknotes) {
					fail();
				}
			});

			for (int i = 0; i < 100; i++) {
				banknotesLoaded = false;
				attendant.refillBanknoteDispenser(hw, new Banknote(TestUtility.getCurrency(), denom));
			}
		}
		Assert.assertTrue("Last Banknote did not go to its Dispenser", banknotesLoaded);
		Assert.assertEquals("100 banknotes was not filled into each Dispenser", 100 * TestUtility.getBanknoteDenominations().length, actualFound);
	}

	/**
	 * Overloads Five Dollar Banknote Dispenser with five dollar banknotes
	 * @throws OverloadException
	 */	
	@Test
	public void OverloadBanknoteDispenserMultipleBanknotes() throws OverloadException {
		bd = hw.banknoteDispensers.get(TestUtility.fiveDollarBill.getValue());
		bd.attach(new BanknoteDispenserObserver() {
			@Override
			public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
			}
	
			@Override
			public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
				fail();
			}
	
			@Override
			public void moneyFull(BanknoteDispenser dispenser) {
				fail();
			}
	
			@Override
			public void banknotesEmpty(BanknoteDispenser dispenser) {
				fail();
			}
	
			@Override
			public void billAdded(BanknoteDispenser dispenser, Banknote banknote) {
				fail();
			}
	
			@Override
			public void banknoteRemoved(BanknoteDispenser dispenser, Banknote banknote) {
				fail();
			}
	
			@Override
			public void banknotesLoaded(BanknoteDispenser dispenser, Banknote... banknotes) {
				actualFound++;
				banknotesLoaded = true;
			}
	
			@Override
			public void banknotesUnloaded(BanknoteDispenser dispenser, Banknote... banknotes) {
				fail();
			}
		});

		for (int i = 0; i < bd.getCapacity() + 10; i++) {
			banknotesLoaded = false;
			attendant.refillBanknoteDispenser(hw, TestUtility.fiveDollarBill);
		}

		Assert.assertTrue("Last Banknote did go to Five Dollar Dispenser", banknotesLoaded == false);
		Assert.assertTrue("All banknotes was filled into Five Dollar Dispenser", bd.getCapacity() + 10 != actualFound);
	}
}
