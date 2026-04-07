namespace Lab_2__ThreadMinFinder
{
    using System;
    using System.Threading;

    namespace ThreadMinApp
    {
        class Program
        {
            // Global variables for synchronization
            private static int globalMin = int.MaxValue;
            private static int globalIndex = -1;
            private static readonly object locker = new object();

            static void Main(string[] args)
            {
                int size = 1000000;      // Array size
                int threadCount = 16;     // Change to 2, 8, 16 for your report
                int[] array = new int[size];
                Random rnd = new Random();

                // 1. Filling the array with random positive numbers
                for (int i = 0; i < size; i++)
                {
                    array[i] = rnd.Next(0, 100000);
                }

                // 2. Requirement: Insert a negative value at a random position
                int targetIdx = rnd.Next(0, size);
                array[targetIdx] = -888;

                Console.WriteLine($"Starting C# search with {threadCount} threads...");

                Thread[] threads = new Thread[threadCount];
                int chunkSize = size / threadCount;

                // 3. Creating and starting threads
                for (int i = 0; i < threadCount; i++)
                {
                    int start = i * chunkSize;
                    // Handling the remainder for the last thread
                    int end = (i == threadCount - 1) ? size : (start + chunkSize);

                    threads[i] = new Thread(() =>
                    {
                        int localMin = array[start];
                        int localIdx = start;

                        // Each thread searches only its assigned part
                        for (int j = start; j < end; j++)
                        {
                            if (array[j] < localMin)
                            {
                                localMin = array[j];
                                localIdx = j;
                            }
                        }

                        // 4. Critical Section: Sync access to global variables
                        lock (locker)
                        {
                            if (localMin < globalMin)
                            {
                                globalMin = localMin;
                                globalIndex = localIdx;
                            }
                        }
                    });
                    threads[i].Start();
                }

                // 5. Wait for all threads to finish (Join)
                foreach (var t in threads)
                {
                    t.Join();
                }

                // 6. Final output for the report
                Console.WriteLine("\n--- C# Results ---");
                Console.WriteLine($"Threads used: {threadCount}");
                Console.WriteLine($"Global Minimum: {globalMin}");
                Console.WriteLine($"Found at index: {globalIndex}");

                // Verification
                if (array[globalIndex] == globalMin)
                {
                    Console.WriteLine("Verification: SUCCESS");
                }

                Console.WriteLine("\nPress any key to exit...");
                Console.ReadKey();
            }
        }
    }
}
