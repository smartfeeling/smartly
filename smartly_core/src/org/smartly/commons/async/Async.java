package org.smartly.commons.async;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class to run async methods
 */
public abstract class Async {

    public static interface AsyncActionHandler {
        public void handle(Object... args);
    }

    public static void Action(final AsyncActionHandler handler, final Object... args) {
        if (null != handler) {
            final Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    handler.handle(args);
                }
            });
            t.setDaemon(true);
            t.setPriority(Thread.NORM_PRIORITY);
            t.start();
        }
    }

    public static void Delay(final AsyncActionHandler handler, final int delay, final Object... args) {
        if (null != handler) {
            final Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(delay);
                        handler.handle(args);
                    } catch (Throwable ignored) {
                    }
                }
            });
            t.setDaemon(true);
            t.setPriority(Thread.NORM_PRIORITY);
            t.start();
        }
    }

    public static void maxConcurrent(final Thread[] threads, final int maxConcurrentThreads) {
        //-- Async execution --//
        Action(new AsyncActionHandler() {
            @Override
            public void handle(Object... args) {
                final int max = maxConcurrentThreads<=threads.length?maxConcurrentThreads:threads.length;
                final Thread[] concurrent = new Thread[max];
                int count = 0;
                for (int i=0;i<threads.length;i++) {
                    concurrent[count] = threads[i];
                    count++;
                    if(count==max){
                        startAll(concurrent);
                        joinAll(concurrent);
                        count = 0;
                    }
                }
            }
        });
    }

    public static void startAll(final Thread[] threads) {
        for (final Thread thread : threads) {
            try {
                if (!thread.isAlive() && !thread.isInterrupted()) {
                    thread.start();
                }
            } catch (Throwable ignored) {
            }
        }
    }

    public static void joinAll(final Thread[] threads) {
        final int length = threads.length;
        final Set<Long> terminated = new HashSet<Long>();
        while(length>terminated.size()){
            for (final Thread thread : threads) {
                try {
                    final Thread.State state = thread.getState();
                    if(Thread.State.RUNNABLE.equals(state)){
                        thread.join();
                    } else if(Thread.State.TERMINATED.equals(state)){
                        terminated.add(thread.getId());
                    }
                } catch (Throwable ignored) {
                }
            }
        }

    }

}
