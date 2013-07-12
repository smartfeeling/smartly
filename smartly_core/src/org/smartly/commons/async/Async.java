package org.smartly.commons.async;

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
}
