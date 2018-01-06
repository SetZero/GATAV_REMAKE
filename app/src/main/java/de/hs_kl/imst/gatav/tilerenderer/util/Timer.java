package de.hs_kl.imst.gatav.tilerenderer.util;

/**
 * Created by Sebastian on 2018-01-02.
 */

public class Timer {
    private volatile boolean runningTimeThread = false;
    private volatile double elapsedTime = 0.0;
    private volatile int totalLevelTime = 0;
    private volatile double snapshotTime = 0.0;

    synchronized public void resetElapsedTime() {
        elapsedTime = 0.0;
    }

    synchronized public double getElapsedTime() {
        return elapsedTime;
    }

    synchronized private void increaseElapsedTime(double increment) {
        elapsedTime += increment;
    }

    public int getTotalLevelTime() {
        return totalLevelTime;
    }

    public void setTotalLevelTime(int totalLevelTime) {
        this.totalLevelTime = totalLevelTime;
    }

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

    public void stopTimeThread() {
        runningTimeThread = false;
    }

    public void snapshotTime() {
        snapshotTime = elapsedTime;
    }

    public double getSnapshotTime() {
        return snapshotTime;
    }
}
