package com.example.demo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Scheduler { //round-robin schedular
    private final Queue<rocket> rocketQueue = new LinkedList<>();
    private final int timeQuantum; // Time slice in milliseconds
    private boolean isRunning = true;

    public Scheduler(int timeQuantum) {
        this.timeQuantum = timeQuantum;
    }

    public void addRocket(rocket rocket) {
        rocketQueue.add(rocket);
    }
    public void addRockets(ArrayList<rocket> rocketList) {
        for (rocket rocket : rocketList) {
            addRocket(rocket);
        }
    }

    public void start() {
        new Thread(() -> {
            while (isRunning) {
                if (!rocketQueue.isEmpty()) {
                    rocket rocket = rocketQueue.poll(); // Get the next rocket
                    synchronized (rocket) {
                        rocket.notify(); // Activate the rocket thread
                    }
                    try {
                        Thread.sleep(timeQuantum); // allow the car to rocket for the time slice
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    rocketQueue.offer(rocket); // Re-add the rocket to the end of the queue
                }
            }
        }).start();
    }

}
