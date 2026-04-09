package com.company;

import java.util.Random;

public class ArrClass {
    private final int dim;
    private final int threadNum;
    public final int[] arr;

    private int min = Integer.MAX_VALUE;
    private int minIndex = -1;
    private int finishedThreads = 0; // Counter for synchronized completion

    public ArrClass(int dim, int threadNum) {
        this.dim = dim;
        this.threadNum = threadNum;
        this.arr = new int[dim];
        Random rnd = new Random();

        // Filling array with random values
        for (int i = 0; i < dim; i++) {
            arr[i] = rnd.nextInt(100000);
        }
        // Requirement: ensure at least one negative value exists
        arr[rnd.nextInt(dim)] = -777;
    }

    // Synchronized method acting as a Critical Section
    public synchronized void collectMin(int localMin, int localMinIndex) {
        if (localMin < this.min) {
            this.min = localMin;
            this.minIndex = localMinIndex;
        }

        finishedThreads++;

        // If this is the last thread, wake up the main thread waiting on this monitor
        if (finishedThreads == threadNum) {
            notify();
        }
    }

    public int threadMin() {
        int chunkSize = dim / threadNum;

        // Launch all worker threads
        for (int i = 0; i < threadNum; i++) {
            int start = i * chunkSize;
            int finish = (i == threadNum - 1) ? dim : (start + chunkSize);
            new ThreadMin(start, finish, this).start();
        }

        // Wait for all threads to finish without using Thread.join()
        synchronized (this) {
            while (finishedThreads < threadNum) {
                try {
                    // Main thread goes into waiting state
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // Print the index as required by point 3 of the task
        System.out.println("Result found by monitor signal:");
        System.out.println("Minimum element index: " + minIndex);
        return min;
    }
}