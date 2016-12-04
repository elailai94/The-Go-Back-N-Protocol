//==============================================================================
// CS456 Assignment 02
//
// @description: Module for providing functions to work with timer objects
// @author: Ah Hoe Lai
// @userid: ahlai
// @version: 1.0 01/11/2016
//==============================================================================

import java.util.Timer;

public class timer {
	private static final int DELAY = 150;
   private static final int PERIOD = 150;

	private Timer timer;
   private timeouttask timeOutTask;

   public timer(timeouttask timeOutTask) {
      this.timeOutTask = timeOutTask;
   } // Constructor

   // Starts the timer
   public void start() {
      timer = new Timer();
      timeouttask newTimeOutTask = new timeouttask(timeOutTask);
    	timer.schedule(newTimeOutTask, DELAY, PERIOD);
   } // start

   // Stops the timer
   public void stop() {
    	timer.cancel();
   } // stop

   // Restarts the timer
   public void restart() {
      stop();
      start();
   } // restart
}
