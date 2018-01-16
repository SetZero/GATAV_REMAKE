package de.hs_kl.imst.gatav.tilerenderer.util;

/**
 * A Timer Thread for the Level
 * Created by Sebastian on 2018-01-02.
 */

public class Timer {
    private volatile boolean runningTimeThread = false;
    private volatile double elapsedTime = 0.0;
    private volatile int totalLevelTime = 0;
    private volatile double snapshotTime = 0.0;

    /**
     * Sets the timer back to 0
     */
    synchronized public void resetElapsedTime() {
        elapsedTime = 0.0;
    }

    /**
     * gets the current elapsed time
     * @return the time in seconds
     */
    synchronized public double getElapsedTime() {
        return elapsedTime;
    }

    /**
     * add time to timer
     * @param increment time in seconds
     */
    synchronized public void increaseElapsedTime(double increment) {
        elapsedTime += increment;
    }

    /**
     * returns the total time a player has for the current level
     * @return
     */
    public int getTotalLevelTime() {
        return totalLevelTime;
    }

    /**
     * sets the total time a player has to finish a level
     * @param totalLevelTime time in seconds
     */
    public void setTotalLevelTime(int totalLevelTime) {
        this.totalLevelTime = totalLevelTime;
    }

    /**
     * Starts the Time threads, which will update all 10ms
     */
    public void startTimeThread() {
        if (runningTimeThread) return;
        runningTimeThread = true;
        resetElapsedTime();
        Thread timeThread = new Thread(() -> {
            while (runningTimeThread) {
                increaseElapsedTime(0.01);

                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    runningTimeThread = false;
                }
            }
        });
        timeThread.start();
    }

    /**
     * stops the timer thread and closed the thread
     */
    public void stopTimeThread() {
        runningTimeThread = false;
    }

    /**
     * saves the current time in a snapshot (only one snapshot available)
     */
    public void snapshotTime() {
        snapshotTime = elapsedTime;
    }

    /**
     * @return the time when a snapshot was taken, if there is none it'll return 0
     */
    public double getSnapshotTime() {
        return snapshotTime;
    }
}
