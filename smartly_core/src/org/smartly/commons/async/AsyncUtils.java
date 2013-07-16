package org.smartly.commons.async;

import org.smartly.commons.Delegates;

/**
 *
 */
public class AsyncUtils {

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    /**
     * Creates array of Async actions
     *
     * @param length
     * @param callback
     * @return
     */
    public static Thread[] createArray(final int length, final Delegates.CreateRunnableCallback callback) {
        final Thread[] result = new Thread[length];
        for (int i = 0; i < length; i++) {
            final Runnable action = null != callback ? callback.handle(i, length) : getEmptyAction();
            result[i] = new Thread(action);
            result[i].setDaemon(true);
        }
        return result;
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private static Runnable getEmptyAction() {
        return new Runnable() {
            @Override
            public void run() {
                // nothing to to, just a stub
            }
        };
    }

}
