using System;
using System.Threading;

namespace Lab_2_MonitorApp
{
    class Program
    {
        // Shared resources
        private static int[] array;
        private static int globalMin = int.MaxValue;
        private static int globalIndex = -1;
        private static int finishedThreads = 0;

        // Monitor object for synchronization
        private static readonly object monitorLock = new object();

        static void Main(string[] args)
        {
            int size = 1000000;
            int threadCount = 16; // Change to 2, 8, 16 for your report
            array = new int[size];
            Random rnd = new Random();

            // 1. Initializing array with random values
            for (int i = 0; i < size; i++)
            {
                array[i] = rnd.Next(0, 100000);
            }

            // 2. Requirement: Insert a negative value at a random position
            int targetIdx = rnd.Next(0, size);
            array[targetIdx] = -888;

            Console.WriteLine($"Starting C# Monitor search with {threadCount} threads...");

            int chunkSize = size / threadCount;

            // 3. Launching threads
            for (int i = 0; i < threadCount; i++)
            {
                // Capture boundaries for the thread (Closure fix)
                int start = i * chunkSize;
                int end = (i == threadCount - 1) ? size : (start + chunkSize);

                Thread t = new Thread(() =>
                {
                    // Local search (No synchronization needed here)
                    int localMin = array[start];
                    int localIdx = start;

                    for (int j = start; j < end; j++)
                    {
                        if (array[j] < localMin)
                        {
                            localMin = array[j];
                            localIdx = j;
                        }
                    }

                    // 4. Critical Section: Update global results and signal the monitor
                    lock (monitorLock)
                    {
                        if (localMin < globalMin)
                        {
                            globalMin = localMin;
                            globalIndex = localIdx;
                        }

                        finishedThreads++;

                        // If all threads are done, pulse the monitor to wake the main thread
                        if (finishedThreads == threadCount)
                        {
                            Monitor.Pulse(monitorLock);
                        }
                    }
                });
                t.Start();
            }

            // 5. Main thread enters wait state (Instead of Join)
            lock (monitorLock)
            {
                while (finishedThreads < threadCount)
                {
                    // Releases monitorLock and waits for Pulse()
                    Monitor.Wait(monitorLock);
                }
            }

            // 6. Final report
            Console.WriteLine("\n--- C# Monitor Results ---");
            Console.WriteLine($"Global Minimum: {globalMin}");
            Console.WriteLine($"Found at Index: {globalIndex}");
            Console.WriteLine("Verification: " + (array[globalIndex] == globalMin ? "SUCCESS" : "FAILED"));

            Console.WriteLine("\nPress any key to exit...");
            Console.ReadKey();
        }
    }
}