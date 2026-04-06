import java.util.Random;

public class Main {
    // Shared global variables for all threads
    private static int globalMin = Integer.MAX_VALUE;
    private static int globalIndex = -1;

    // Monitor method: only one thread can enter here at a time
    public static synchronized void updateMin(int localMin, int localIndex) {
        // Critical section: checking if local result is better than global
        if (localMin < globalMin) {
            globalMin = localMin;
            globalIndex = localIndex;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int size = 1000000;      // Array size
        int threadCount = 16;     // You can change this to 2, 8, 16 for your report
        int[] array = new int[size];
        Random rnd = new Random();

        // 1. Filling the array with random positive numbers
        for (int i = 0; i < size; i++) {
            array[i] = rnd.nextInt(100000);
        }

        // 2. Requirement: Insert a negative value at a random position
        int targetIdx = rnd.nextInt(size);
        array[targetIdx] = -777;

        System.out.println("Starting search with " + threadCount + " threads...");

        Thread[] threads = new Thread[threadCount];
        int chunkSize = size / threadCount;

        // 3. Creating and starting threads
        for (int i = 0; i < threadCount; i++) {
            final int start = i * chunkSize;
            // Handling the remainder for the last thread
            final int end = (i == threadCount - 1) ? size : (start + chunkSize);

            threads[i] = new Thread(() -> {
                int localMin = array[start];
                int localIdx = start;

                // Each thread searches only in its assigned part
                for (int j = start; j < end; j++) {
                    if (array[j] < localMin) {
                        localMin = array[j];
                        localIdx = j;
                    }
                }
                // Update the global result safely
                updateMin(localMin, localIdx);
            });
            threads[i].start();
        }

        // 4. Wait for all threads to complete (Barrier)
        for (Thread t : threads) {
            t.join();
        }

        // 5. Final output for the laboratory report
        System.out.println("\n--- Results ---");
        System.out.println("Threads used: " + threadCount);
        System.out.println("Global Minimum found: " + globalMin);
        System.out.println("Found at index: " + globalIndex);

        // Verification (optional)
        if (array[globalIndex] == globalMin) {
            System.out.println("Verification: SUCCESS (Value matches index)");
        } else {
            System.out.println("Verification: FAILED");
        }
    }
}