package com.company;

public class ThreadMin extends Thread {
    private final int startIndex;
    private final int finishIndex;
    private final ArrClass arrClass;

    public ThreadMin(int startIndex, int finishIndex, ArrClass arrClass) {
        this.startIndex = startIndex;
        this.finishIndex = finishIndex;
        this.arrClass = arrClass;
    }

    @Override
    public void run() {
        // Find local minimum and its index within the assigned range
        int localMin = arrClass.arr[startIndex];
        int localMinIndex = startIndex;

        for (int i = startIndex + 1; i < finishIndex; i++) {
            if (arrClass.arr[i] < localMin) {
                localMin = arrClass.arr[i];
                localMinIndex = i;
            }
        }

        // Send results to the shared ArrClass object
        // This method handles synchronization and notification
        arrClass.collectMin(localMin, localMinIndex);
    }
}