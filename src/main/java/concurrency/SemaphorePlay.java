package concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * With semaphores one can configure how many threads are allowed to enter in the critical section.
 * This can be used as barrier.
 */
public class SemaphorePlay {

    private Semaphore semaphore = new Semaphore(1);
    private class DisplayText {
        public void displayThreadName() throws InterruptedException {
            semaphore.acquire();
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + " started.");
            // Some business logic
            System.out.println(threadName + " ended.");
            semaphore.release();
            wait();
        }
    }

    public void threadPlayGround(int threadsCount) {
        DisplayText displayText = new DisplayText();
        ExecutorService executorService = Executors.newFixedThreadPool(threadsCount);
        for (int i = 0; i < 1000; i++) {
            executorService.submit(() -> {
                try {
                    displayText.displayThreadName();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SemaphorePlay semaphorePlay = new SemaphorePlay();
        semaphorePlay.threadPlayGround(5);
    }
}


