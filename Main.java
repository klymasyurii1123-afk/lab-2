package com.company;

public class Main {
    public static void main(String[] args) {
        int arraySize = 1000000;
        int threadsCount = 16; // You can change this to 2, 8, 16 for your report

        ArrClass task = new ArrClass(arraySize, threadsCount);

        System.out.println("Starting multi-threaded search...");
        int finalMin = task.threadMin();

        System.out.println("Global Minimum Value: " + finalMin);
    }
}