package concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CounterPlay {

    public class Counter {
        private AtomicInteger count;
        public Counter() {
            this(0);
        }
        public Counter(int count) {
            this.count = new AtomicInteger(count);
        }

        public void inc() {
            count.incrementAndGet();
        }

        public int getCount() {
            return count.get();
        }
        public void setCount(int count) {
            this.count = new AtomicInteger(count);
        }
    }

    public void threadsPlayGround(int threadsCount) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadsCount);
        Counter counter = new Counter();
        for (int i = 0; i < 1000; i++) {
            executorService.submit(() -> counter.inc());
        }
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);

        // This will not be thousand as this is not thread safe yet.
        // Solution:
        // 1. Make inc function synchronized
        // 2. Make counter variable inside Counter object as AtomicInteger. This is implemented above.
        System.out.println(counter.getCount());
    }

    public static void main(String[] args) {
        CounterPlay counterPlay = new CounterPlay();
        try {
            counterPlay.threadsPlayGround(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


