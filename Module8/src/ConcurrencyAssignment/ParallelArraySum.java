package ConcurrencyAssignment;
/*
  Concurrency Assignment
  @author Nick Zambri
 * 4/18/2023
 */
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelArraySum {
/**
 * ParallelArraySum. This class demonstrates how to use multi-threading to sum the values of an array in parallel, and compares the performance of the parallel approach with a single-threaded approach.
 */
    private static final int ARRAY_SIZE = 200_000_000;
    private static final int NUM_THREADS = 8;

    public static void main(String[] args) throws InterruptedException {
        /**
         *
         */
        int[] array = new int[ARRAY_SIZE];
        Random rand = new Random();
        for (int i = 0; i < ARRAY_SIZE; i++) {
            array[i] = rand.nextInt(10) + 1;
        }
        /**
         *The main() method creates an array of random integers and then calls the parallelSum() and singleThreadSum() methods to sum the array using both approaches.
         */
        long startTime = System.nanoTime();
        int sumParallel = parallelSum(array);
        long endTime = System.nanoTime();

        long parallelTime = endTime - startTime;

        startTime = System.nanoTime();
        int sumSingle = singleThreadSum(array);
        endTime = System.nanoTime();

        long singleTime = endTime - startTime;

        System.out.println("Parallel sum: " + sumParallel);
        System.out.println("Parallel time (ms): " + TimeUnit.NANOSECONDS.toMillis(parallelTime));
        System.out.println("Single thread sum: " + sumSingle);
        System.out.println("Single thread time (ms): " + TimeUnit.NANOSECONDS.toMillis(singleTime));
    }

    private static int parallelSum(int[] array) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        int[] partialSums = new int[NUM_THREADS];
        int chunkSize = ARRAY_SIZE / NUM_THREADS;
        for (int i = 0; i < NUM_THREADS; i++) {
            int start = i * chunkSize;
            int end = (i == NUM_THREADS - 1) ? ARRAY_SIZE : (i + 1) * chunkSize;
            executor.submit(new ArraySumTask(array, start, end, partialSums, i));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        int sum = 0;
        for (int partialSum : partialSums) {
            sum += partialSum;
        }

        return sum;
    }

    private static int singleThreadSum(int[] array) {
        int sum = 0;
        for (int i = 0; i < ARRAY_SIZE; i++) {
            sum += array[i];
        }
        return sum;
    }

    private record ArraySumTask(int[] array, int start, int end, int[] partialSums, int index) implements Runnable {
        /**
         * The ArraySumTask class is a private inner class that implements the Runnable interface.
         * Each ArraySumTask instance is responsible for summing a portion of the input array and storing the result in the partialSums array at the appropriate index.
         */
        @Override
            public void run() {
                int sum = 0;
                for (int i = start; i < end; i++) {
                    sum += array[i];
                }
                partialSums[index] = sum;
            /**
             * ParallelArraySum class provides a simple example of how to use multi-threading to improve the performance of a computationally intensive task.
             */
        }
        }
}
