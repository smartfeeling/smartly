package org.smartly.commons.async;

import org.smartly.commons.Delegates;
import org.smartly.commons.util.MathUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class to run async methods
 */
public abstract class Async {


    public static Thread Action(final Delegates.Action handler, final Object... args) {
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
            return t;
        }
        return null;
    }

    public static void Delay(final Delegates.Action handler, final int delay, final Object... args) {
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

    public static void maxConcurrent(final Thread[] threads,
                                     final int maxConcurrentThreads) {
        maxConcurrent(threads, maxConcurrentThreads, null);
    }

    public static void maxConcurrent(final Thread[] threads,
                                     final int maxConcurrentThreads,
                                     final Delegates.ProgressCallback onProgress) {
        final int len = threads.length;
        //-- Async execution --//
        Action(new Delegates.Action() {
            @Override
            public void handle(Object... args) {
                final int max = maxConcurrentThreads <= len ? maxConcurrentThreads : len;
                final Thread[] concurrent = new Thread[max];
                int count = 0;
                for (int i = 0; i < len; i++) {
                    concurrent[count] = threads[i];
                    count++;
                    if (count == max) {
                        startAll(concurrent);
                        joinAll(concurrent);
                        count = 0;
                    }
                    if (null != onProgress) {
                        onProgress.handle(i, len, MathUtils.progress(i + 1, len, 2));
                    }
                }
            }
        });
    }

    public static Thread[] maxConcurrent(final int length,
                                         final int maxConcurrentThreads,
                                         final Delegates.CreateRunnableCallback runnableFunction) {
        if (null == runnableFunction) {
            return new Thread[0];
        }
        final Thread[] result = new Thread[length];
        final int max = maxConcurrentThreads <= length ? maxConcurrentThreads : length;
        final Thread[] concurrent = new Thread[max];
        int count = 0;
        for (int i = 0; i < length; i++) {
            final Runnable runnable = runnableFunction.handle(i, length);
            final Thread t = (runnable instanceof Thread) ? (Thread) runnable : new Thread(runnable);
            result[i] = t;
            concurrent[count] = t;
            count++;
            if (count == max || (i + 1) == length) {
                startAll(concurrent);
                joinAll(concurrent);
                count = 0;
            }
        }
        return result;
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
        while (length > terminated.size()) {
            for (final Thread thread : threads) {
                try {
                    final Thread.State state = thread.getState();
                    if (Thread.State.RUNNABLE.equals(state)) {
                        thread.join();
                    } else if (Thread.State.TERMINATED.equals(state)) {
                        terminated.add(thread.getId());
                    } else {
                        System.out.println(state);
                    }
                } catch (Throwable ignored) {
                }
            }
        }

    }

}
