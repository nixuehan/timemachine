import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CommonTest {

    private int corePoolSize = 5;
    private int maxPoolSize = 10;
    private long keepAliveTime = 5L;
    private int threadWorkQueueSize = 20;

    @Test
    public void threadPool() {
       ExecutorService pool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<>(threadWorkQueueSize),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {

                        System.out.println("thread pool start.....");

                        Thread thread = new Thread(r,"thread - " + r.hashCode());

                        return thread;
                    }
                },new ThreadPoolExecutor.CallerRunsPolicy());

        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName());
            }
        });

        System.out.println(Thread.currentThread().getName() + " niubi...");
    }

    @Test
    public void futureThreadPool() throws ExecutionException, InterruptedException {

        long startTime = System.currentTimeMillis();

        List<Future> futures = new ArrayList<>();

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                1,
                10,
                30L,
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>()
        );

        for (int i = 0; i <= 9; i++) {
            futures.add(threadPoolExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName());

                    System.out.println(Thread.currentThread().getName() + " done");
                }
            }));
        }

        for (Future future : futures) {
            future.get();
        }

        System.out.println(System.currentTimeMillis() - startTime);
    }

    @Test
    public void tt() {
        Runnable runnable = new Runnable() {
            /**
             * When an object implementing interface <code>Runnable</code> is used
             * to create a thread, starting the thread causes the object's
             * <code>run</code> method to be called in that separately executing
             * thread.
             * <p>
             * The general contract of the method <code>run</code> is that it may
             * take any action whatsoever.
             *
             * @see Thread#run()
             */
            @Override
            public void run() {
                System.out.println("start.....");
                throw new IllegalStateException("hahah");
            }
        };

        try {
            Thread thread = new Thread(runnable);
            thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    System.out.println(e.getMessage());
                }
            });
            thread.start();
            System.out.println("end.....");
        }catch (Throwable e) {
            System.out.println("xxx");
        }
    }

    @Test
    public void haha() throws InterruptedException {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("hahahah " + System.currentTimeMillis());
            }
        },0,100,TimeUnit.MILLISECONDS);

        Thread.sleep(10 * 1000);
    }
}
