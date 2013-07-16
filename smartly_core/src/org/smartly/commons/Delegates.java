package org.smartly.commons;

/**
 * Common listeners repository.
 */
public class Delegates {

    public static interface ExceptionCallback {
        public void handle(final Throwable exception);
    }

    public static interface CreateRunnableCallback {
        public Runnable handle(final int index, final int length);
    }

    /**
     * Simple handler for Async Action
     */
    public static interface AsyncActionHandler {
        public void handle(Object... args);
    }

    /**
     * Callback for progress indicators.
     */
    public static interface ProgressCallback {
        void handle(final int index, final int length, final double progress);
    }

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    private Delegates() {
    }

}
