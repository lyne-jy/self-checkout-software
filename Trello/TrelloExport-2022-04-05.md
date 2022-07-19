# Iteration3-Tracking

## TODO Test

### [28] Customer does not want to bag a scanned item

**Due:** 4/6/2022 4:00:00 PM

#### Comments
**4/4/2022 10:07:55 PM Jessica Hoang**

Waiting for Delara to finish use case

### [51] Attendant approves a weight discrepancy

**Due:** 4/6/2022 4:00:00 PM

### [52] Attendant looks up a product

**Due:** 4/6/2022 4:00:00 PM

### [30] Customer looks up product

**Due:** 4/6/2022 4:00:00 PM

### [35] Customer enters their membership card information

**Due:** 4/6/2022 4:00:00 PM

### [31] Customer pays with gift card

**Due:** 4/6/2022 4:00:00 PM

### [34] Customer removes purchased items from bagging area

**Due:** 4/6/2022 4:00:00 PM

#### Comments
**4/5/2022 9:05:32 PM Jessica Hoang**

Completed by Rafael?

### [37] Customer enters number of plastic bags used

**Due:** 4/6/2022 4:00:00 PM

### [33] Station detects that the weight in the bagging area does not conform to expectations

**Due:** 4/6/2022 4:00:00 PM

### [46] Attendant blocks a station

**Due:** 4/6/2022 4:00:00 PM

### [40] Attendant logs in to their control console

**Due:** 4/6/2022 4:00:00 PM

### [42] Attendant logs out from their control console

**Due:** 4/6/2022 4:00:00 PM

## Bug Fixes

### [79] Adding own bag use case has dummy functionality



## TODO Documents

### [64] Sequence Diagram for Scan class



## TODO GUI

### [56] Skeletons

**Due:** 4/4/2022 4:00:00 PM

#### Comments
**4/5/2022 9:47:31 PM Delara Shamanian**

Update: still working on it

### [57] UI for It2

**Due:** 4/6/2022 4:00:00 PM

### [58] UI for it3

**Due:** 4/8/2022 4:00:00 PM

## TODO GUI Customer Simulation

### [65] Customer removes purchased items.



### [66] Customer scans an item.



### [67] Customer places item in bagging area



### [68] Customer pays with banknote



### [69] Customer pays with coin



### [70] Customer pays with card



### [71] Customer pays with gift card



## TODO GUI Attendant Simulation

### [72] Attendant adds paper to printer



### [73] Attendant adds ink to printer



### [74] Attendant empties coin storage



### [75] Attendant empties banknote storage



### [76] Attendant refills coin dispenser



### [77] Attendant refills banknote dispenser



## In-Progress

### [50] Attendant removes product from purchases

**Due:** 4/6/2022 4:00:00 PM

#### Comments
**4/5/2022 12:08:53 AM Jessica Hoang**

Testing Classes:
- selfcheckout/communication/SCNotifierUIObserver.java
- selfcheckout/communication/SelfCheckoutNotifier.java
- selfcheckout/coordination/SCCoordinatorObserver.java
- selfcheckout/coordination/SelfCheckoutCoordinator.java
- selfcheckout/product/BaggingArea.java
- selfcheckout/simulations/StoreAttendant.java
- selfcheckout/utility/Cart.java

### [8] Customer pays with gift card

**Due:** 4/6/2022 4:00:00 PM

#### Comments
**4/4/2022 8:00:40 PM Ekhonmu Egbase**

Still a little confused about the functionality of a gift card vs credit/debit cards for this iteration

### [10] Customer enters number of plastic bags used

**Due:** 4/6/2022 4:00:00 PM

#### Comments
**4/4/2022 9:30:19 PM Ekhonmu Egbase**

- Contemplating adding another enum to the SoftwarePhase class
- Bagging area has new selectBagsUsed() method

### [4] Customer does not want to bag a scanned item

**Due:** 4/6/2022 4:00:00 PM

#### Comments
**4/4/2022 7:38:24 PM Delara Shamanian**

Update: this needs to send a notifier to the attendant that the customer will not put their item in the bagging area, then the attendant will approve of this through their station, then the station is ready to scan more items. 

### [5] Customer looks up product

**Due:** 4/6/2022 4:00:00 PM

#### Comments
**4/5/2022 6:09:08 PM Delara Shamanian**

Update: this use case is done on my part. The GUI has to call a notifier that adds PLU items to the bag whenever someone selects an item from the visual catalog

**4/4/2022 7:35:39 PM Delara Shamanian**

Update: The implementation depends on the GUI, most work has to be done there. The logic part is updating the cart once an item has been selected through the visual catalague. 

**4/2/2022 9:49:07 PM Delara Shamanian**

Update: given a string, the function will look for the product PLU in the database. Two cases, notifyexists or notifydoesntexist. 

### [53] Class Diagrams

**Due:** 4/8/2022 4:00:00 PM

#### Comments
**4/2/2022 10:34:37 PM Alizeh**

this will be decided among the members working on sequence diagram

### [54] Sequence  Diagrams

**Due:** 4/8/2022 4:00:00 PM

#### From Iteration 2

[x] BaggingArea 4/5/2022 8:07:19 PM Salman Ahmed

[x] CardPayment 4/5/2022 12:30:48 PM Alizeh

[x] CashPayment 4/5/2022 2:29:52 PM Luke Couture

[ ] Checkout

[ ] Scan

[ ] ScanMembership

#### Added Iteration 3

[ ] PLU

[ ] Attendant interactions

#### New Use Cases That Expand on [Previous] - will be added to code throughout this week 

[ ] Customer returns to adding items [checkout]

[ ] Customer does not want to bag a scanned item [bagging area]

[ ] Customer looks up product from selections [plu]

[ ] Customer pays with gift card [card payment]

[ ] Customer removes purchased items from bagging area [checkout]

#### Comments
**4/3/2022 3:07:21 PM Yanbo Liu**

Partial completion for the Scan class sequence diagram

### [55] State Diagrams

**Due:** 4/8/2022 4:00:00 PM

#### State Diagram v1

[x] state diagram for base code in git 4/4/2022 11:16:49 PM Alizeh

#### State Diagram v2

[ ] state diagram for updated code in git

[ ] ongoing extensions from updated code in git to be added

#### Comments
**4/4/2022 11:15:01 PM Alizeh**

diagram for base code is done. currently under review from management. uploaded to google drive. 

**4/4/2022 5:07:07 PM Alizeh**

the diagram is almost complete, needs to be cleaned a bit. BLOCK and WEIGHING need finishing.

**4/4/2022 11:57:14 AM Samiha Mehrine**

Done with state machine diagram for the start screen: start button.

**4/3/2022 8:27:52 PM Samiha Mehrine**

Started on state machine diagrams for ScanMembershipObserver, BaggingAreaObserver, and PaymentObserver. Added the different states. 

### [12] Station detects that the paperink in a receipt printer is low.

**Due:** 4/6/2022 4:00:00 PM

### [36] Customer enters PLU code for a product

**Due:** 4/6/2022 4:00:00 PM

### [48] Attendant adds ink to receipt printer

**Due:** 4/6/2022 4:00:00 PM

### [49] Attendant adds paper to receipt printer

**Due:** 4/6/2022 4:00:00 PM

### [47] Attendant empties the coin storage unit

**Due:** 4/6/2022 4:00:00 PM

### [44] Attendant empties the banknote storage unit

**Due:** 4/6/2022 4:00:00 PM

### [43] Attendant refills the coin dispenser

**Due:** 4/6/2022 4:00:00 PM

### [39] Attendant starts up a station

**Due:** 4/6/2022 4:00:00 PM

### [38] Attendant shuts down a station

**Due:** 4/6/2022 4:00:00 PM

### [32] Station detects that the paper in a receipt printer is low.

**Due:** 4/6/2022 4:00:00 PM

### [83] Station detects that the paper in a receipt printer is low.

**Due:** 4/6/2022 4:00:00 PM

## Having Problems

### [15] Attendant looks up a product

**Due:** 4/6/2022 4:00:00 PM

#### Comments
**4/2/2022 8:52:22 PM Delara Shamanian**

Update: this will depend purely on UI, waiting for the implementation of UI.

### [41] Attendant refills the banknote dispenser

**Due:** 4/6/2022 4:00:00 PM

#### Comments
**4/5/2022 9:09:13 PM Sumanyu Arya**

In StoreAttendant, refillBanknoteDispenser() incorrectly checks if Capacity < Size. It should check if Capacity > Size.

Also, StoreAttendant.refillBanknoteDispenser only inserts first banknote from the array it's passed. It won't work if array contains banknotes of different denominations.

## Done

### [7] Customer enters their membership card information

**Due:** 4/6/2022 4:00:00 PM

**Completed:** 4/5/2022 9:49:32 PM

**Completed by:** Mackenzie Breithaupt

**Elapse:** 3 days, 9 hours, 47 minutes, 57 seconds

### [29] Customer returns to adding items

**Due:** 4/6/2022 4:00:00 PM

**Completed:** 4/5/2022 9:34:15 PM

**Completed by:** Delara Shamanian

**Elapse:** 3 days, 9 hours, 30 minutes, 29 seconds

#### Comments
**4/4/2022 8:35:25 PM Jessica Hoang**

Testing was done in iteration 2

### [63] Once purchases are done, inventory of items in Database should be updated



**Completed:** 4/5/2022 9:34:37 PM

**Completed by:** Delara Shamanian

**Elapse:** 2 days, 6 hours, 34 minutes, 55 seconds

#### Comments
**4/5/2022 3:59:18 PM Rafael Flores Souza**

Update: We don't have to keep track of the inventory

### [27] Attendant shuts down a station

**Due:** 4/4/2022 4:00:00 PM

**Completed:** 4/5/2022 9:36:16 PM

**Completed by:** Delara Shamanian

**Elapse:** 3 days, 9 hours, 32 minutes, 43 seconds

### [26] Attendant starts up a station

**Due:** 4/4/2022 4:00:00 PM

**Completed:** 4/5/2022 9:36:15 PM

**Completed by:** Delara Shamanian

**Elapse:** 3 days, 9 hours, 32 minutes, 48 seconds

### [16] Attendant adds paper to receipt printer

**Due:** 4/4/2022 4:00:00 PM

**Completed:** 4/5/2022 9:36:14 PM

**Completed by:** Delara Shamanian

**Elapse:** 3 days, 9 hours, 33 minutes, 46 seconds

### [17] Attendant adds ink to receipt printer

**Due:** 4/4/2022 4:00:00 PM

**Completed:** 4/5/2022 9:36:13 PM

**Completed by:** Delara Shamanian

**Elapse:** 3 days, 9 hours, 33 minutes, 39 seconds

### [19] Attendant empties the coin storage unit

**Due:** 4/4/2022 4:00:00 PM

**Completed:** 4/5/2022 9:36:13 PM

**Completed by:** Delara Shamanian

**Elapse:** 3 days, 9 hours, 33 minutes, 27 seconds

### [6] Customer enters PLU code for a product

**Due:** 4/4/2022 4:00:00 PM

**Completed:** 4/5/2022 9:36:10 PM

**Completed by:** Delara Shamanian

**Elapse:** 3 days, 9 hours, 34 minutes, 46 seconds

#### Comments
**4/3/2022 12:30:06 PM Delara Shamanian**

Update: need to add PLU functionality for bagging area and cart 

**4/2/2022 11:17:03 PM Sami Taha**

created PLULogic.java and PLULogicObserver.java, working on 1. Integration with Coordinator and Notifier (UI sends message containing a PLU, Notifier receives messages and forwards it to Coordinator, Coordinator performs logic similar to the one for Scan.java) [You might need to modify Cart.java for this]
2. Integration of adding an item into a single class (ex: AddItem.java would be class that does scanning and PLU similar to how Payment works)

### [61] Membership Card entered not scanned



**Completed:** 4/5/2022 9:36:08 PM

**Completed by:** Delara Shamanian

**Elapse:** 2 days, 6 hours, 37 minutes, 6 seconds

### [25] Attendant logs out from their control console

**Due:** 4/4/2022 4:00:00 PM

**Completed:** 4/5/2022 9:36:07 PM

**Completed by:** Delara Shamanian

**Elapse:** 3 days, 9 hours, 32 minutes, 50 seconds

### [21] Attendant empties the banknote storage unit

**Due:** 4/4/2022 4:00:00 PM

**Completed:** 4/5/2022 9:36:06 PM

**Completed by:** Delara Shamanian

**Elapse:** 3 days, 9 hours, 33 minutes, 8 seconds

### [23] Attendant refills the banknote dispenser

**Due:** 4/4/2022 4:00:00 PM

**Completed:** 4/5/2022 9:36:04 PM

**Completed by:** Delara Shamanian

**Elapse:** 3 days, 9 hours, 32 minutes, 57 seconds

### [24] Attendant logs in to their control console

**Due:** 4/4/2022 4:00:00 PM

**Completed:** 4/5/2022 9:36:03 PM

**Completed by:** Delara Shamanian

**Elapse:** 3 days, 9 hours, 32 minutes, 51 seconds

### [78] Assign simpler "software" identity to Self-Checkout station



**Completed:** 4/5/2022 9:35:59 PM

**Completed by:** Delara Shamanian

**Elapse:** 2 days, 5 hours, 21 minutes, 54 seconds

### [60] Emitting Multiple Banknotes



**Completed:** 4/5/2022 9:35:56 PM

**Completed by:** Delara Shamanian

**Elapse:** 2 days, 6 hours, 37 minutes, 13 seconds

### [18] Attendant blocks a station

**Due:** 4/4/2022 4:00:00 PM

**Completed:** 4/5/2022 9:35:49 PM

**Completed by:** Delara Shamanian

**Elapse:** 3 days, 9 hours, 33 minutes, 10 seconds

### [80] Checkout needs to be notified each time the attendant refills the receipt printer with inkpaper.



**Completed:** 4/5/2022 9:35:39 PM

**Completed by:** Delara Shamanian

**Elapse:** 1 days, 23 hours, 35 minutes, 1 seconds

### [14] Attendant removes product from purchases

**Due:** 4/4/2022 4:00:00 PM

**Completed:** 4/5/2022 9:35:37 PM

**Completed by:** Delara Shamanian

**Elapse:** 3 days, 9 hours, 33 minutes, 21 seconds

#### Comments
**4/2/2022 8:53:08 PM Delara Shamanian**

Update: This will depend on Sami's use case. Waiting on that before starting this.

### [9] Customer removes purchased items from bagging area

**Due:** 4/4/2022 4:00:00 PM

**Completed:** 4/5/2022 9:35:19 PM

**Completed by:** Delara Shamanian

**Elapse:** 3 days, 9 hours, 33 minutes, 33 seconds

#### Comments
**4/3/2022 3:04:17 PM Rafael Flores Souza**

Already implemented in iteration 2. The self-checkout systems detects the customer has removed the appropriate items after paying and it  go back to the READY phase.

### [13] Attendant approves a weight discrepancy

**Due:** 4/4/2022 4:00:00 PM

**Completed:** 4/5/2022 9:35:36 PM

**Completed by:** Delara Shamanian

**Elapse:** 3 days, 9 hours, 33 minutes, 26 seconds

### [3] Customer returns to adding items

**Due:** 4/4/2022 4:00:00 PM

**Completed:** 4/5/2022 9:35:17 PM

**Completed by:** Delara Shamanian

**Elapse:** 3 days, 9 hours, 34 minutes, 23 seconds

#### Comments
**4/3/2022 3:02:44 PM Rafael Flores Souza**

Already implemented in iterations 2. Only when the person partially pays with cash can they go back to adding more items.

### [11] Station detects that the weight in the bagging area does not conform to expectations

**Due:** 4/4/2022 4:00:00 PM

**Completed:** 4/5/2022 9:35:03 PM

**Completed by:** Delara Shamanian

**Elapse:** 3 days, 9 hours, 33 minutes, 4 seconds

#### Comments
**4/3/2022 3:01:25 PM Rafael Flores Souza**

Already implemented in Iteration 2 of the project

### [59] Testsuites Bugs



**Completed:** 4/5/2022 9:34:54 PM

**Completed by:** Delara Shamanian

**Elapse:** 2 days, 8 hours, 14 minutes, 17 seconds

#### Comments
**4/3/2022 1:21:17 PM Delara Shamanian**

Update: fixed and merged to master

### [62] Use CardIssuer instead of Bank for card payments



**Completed:** 4/5/2022 9:34:26 PM

**Completed by:** Delara Shamanian

**Elapse:** 2 days, 6 hours, 35 minutes, 2 seconds

### [81] Partial credit that exists is a little messy



**Completed:** 4/5/2022 9:34:20 PM

**Completed by:** Delara Shamanian

**Elapse:** 1 days, 3 hours, 30 minutes, 52 seconds

### [82] Fix "scan" membership card by adding a type to membership card



**Completed:** 4/5/2022 9:34:18 PM

**Completed by:** Delara Shamanian

**Elapse:** 0 days, 23 hours, 55 minutes, 3 seconds

#### Comments
**4/5/2022 4:55:23 PM Rafael Flores Souza**

Update: Realized that it is not necessary as the membership functionality is minimal.

### [22] Attendant refills the coin dispenser

**Due:** 4/4/2022 4:00:00 PM

**Completed:** 4/5/2022 9:36:06 PM

**Completed by:** Delara Shamanian

**Elapse:** 3 days, 9 hours, 33 minutes, 4 seconds

