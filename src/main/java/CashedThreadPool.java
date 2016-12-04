//; concurrency/CashedThreadPool.java

import java.util.concurrent.*;

public class CashedThreadPool {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        // альтернатива FixedThreadPool, SingleThreadExecutor
        for (int i = 0; i < 5; i++)
            executorService.execute(new Thread());
        executorService.shutdown();
    }
}
