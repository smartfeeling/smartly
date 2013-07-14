package org.smartly.commons.async;

/**
 *
 */
public class AsyncUtils {

    public static interface CreateRunnableCallback {
        public Runnable handle(final int index, final int length);
    }

    /**
     * Creates array of Async actions
     * @param length
     * @param callback
     * @return
     */
    public static Thread[] createArray(final int length, final CreateRunnableCallback callback) {
        final Thread[] result = new Thread[length];
        for (int i = 0; i < length; i++) {
            final Runnable action = null != callback ? callback.handle(i, length) : getEmptyAction();
            result[i] = new Thread(action);
            result[i].setDaemon(true);
        }
        return result;
    }

    private static Runnable getEmptyAction() {
        return new Runnable() {
            @Override
            public void run() {
                // nothing to to, just a stub
            }
        };
    }

}
