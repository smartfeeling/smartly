package org.smartly.commons.async.future;

import org.smartly.commons.Delegates;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Act as a timer
 */
public class Timed {

    // --------------------------------------------------------------------
    //               c o n s t a n t s
    // --------------------------------------------------------------------

    /**
     * If invocations might overlap, you can specify more than a single thread.
     */
    private static final int MAX_THREADS = 1;

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final List<ScheduledFuture<?>> _tasks;
    private ScheduledFuture<?> _stop_task;
    private ScheduledExecutorService __scheduler;
    private int _count;

    //-- properties --//
    private int _max_threads;
    private TimeUnit _time_unit;
    private long _initial_delay;
    private long _interval;
    private long _stop_after_time;
    private int _stop_after_count;
    private boolean _daemon;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public Timed() {
        this(TimeUnit.MILLISECONDS, 0, 0, 0, 0); // one shot task
    }

    public Timed(final long delayBetweenRuns) {
        this(TimeUnit.SECONDS, 0, delayBetweenRuns, 0, 0);
    }

    public Timed(final TimeUnit timeUnit,
                 final long initialDelay,
                 final long delayBetweenRuns,
                 final long stopAfterTime,
                 final int stopAfterCount) {
        _time_unit = timeUnit;
        _initial_delay = initialDelay;
        _interval = delayBetweenRuns;
        _stop_after_time = stopAfterTime;
        _stop_after_count = stopAfterCount;

        _tasks = new LinkedList<ScheduledFuture<?>>();
    }

    // --------------------------------------------------------------------
    //               p r o p e r t i e s
    // --------------------------------------------------------------------

    public int getMaxThreads() {
        return _max_threads > 0 ? _max_threads : MAX_THREADS;
    }

    /**
     * Set max number of concurrent threads in pool
     * @param value int
     */
    public Timed setMaxThreads(final int value) {
        _max_threads = value > 0 ? value : MAX_THREADS;
        return this;
    }

    public boolean isDaemon() {
        return _daemon;
    }

    /**
     * Set to true if you want that tasks works as Daemon
     * @param value boolean
     */
    public Timed setDaemon(final boolean value) {
        _daemon = value;
        return this;
    }

    public TimeUnit getTimeUnit() {
        return _time_unit;
    }

    public Timed setTimeUnit(final TimeUnit value) {
        _time_unit = value;
        return this;
    }

    public long getInitialDelay() {
        return _initial_delay;
    }

    public Timed setInitialDelay(final long value) {
        _initial_delay = value;
        return this;
    }

    public long getInterval() {
        return _interval;
    }

    public Timed setInterval(final long value) {
        _interval = value;
        return this;
    }

    public long getShutdownAfterTime() {
        return _stop_after_time;
    }

    public Timed setShutdownAfterTime(final long value) {
        _stop_after_time = value;
        return this;
    }

    public int getShutdownAfterCount() {
        return _stop_after_count;
    }

    public Timed setShutdownAfterCount(final int value) {
        _stop_after_count = value;
        return this;
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    /**
     * Sound the alarm for a few seconds, then stop.
     */
    public ScheduledFuture<?> start(final Delegates.Function<?> callback) {
        return startTask(callback);
    }

    public void stop(final boolean interrupt_if_running) {
        getScheduler().schedule(new StopTask(this, interrupt_if_running),
                0, TimeUnit.MILLISECONDS);
    }

    public void join() {
        synchronized (this) {
            for (final ScheduledFuture<?> task : _tasks) {
                try {
                    if (!task.isDone() && !task.isCancelled()) {
                        //task.get();
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private ScheduledExecutorService getScheduler() {
        this.init();
        return __scheduler;
    }

    private void init() {
        if (null == __scheduler) {
            __scheduler = Executors.newScheduledThreadPool(this.getMaxThreads(), new Factory(this));
        }
    }

    private void finish(final boolean interrupt_if_running) {
        try {
            for (final ScheduledFuture<?> task : _tasks) {
                if (!task.isCancelled() || !task.isDone()) {
                    task.cancel(interrupt_if_running);
                }
            }
        } catch (Throwable ignored) {
        } finally {
            _tasks.clear();
        }
        //-- stop all tasks--//
        try {
            if (null != __scheduler) {
                if (interrupt_if_running) {
                    __scheduler.shutdownNow();
                } else {
                    __scheduler.shutdown();
                }
            }
        } catch (Throwable ignored) {
        } finally {
            __scheduler = null;
            _stop_task = null;
        }
    }

    private ScheduledFuture<?> startTask(final Delegates.Function<?> callback) {
        if(_initial_delay==0 && _interval==0){
            _stop_after_count = 1; // avoid infinite loop at no interval
        }
        final ScheduledFuture<?> future = getScheduler().scheduleWithFixedDelay(
                new RunTask(this, callback), _initial_delay, _interval, _time_unit
        );
        // add to internal list
        _tasks.add(future);

        //-- eval if run count-down--//
        if (_stop_after_time > 0 && null == _stop_task) {
            _stop_task = getScheduler().schedule(new StopTask(this, false), _stop_after_time, _time_unit);
        }

        return future;
    }

    private int incCount() {
        if (_count == Integer.MAX_VALUE) {
            _count = 0; // reset counter to avoid errors
        }
        _count++;
        return _count;
    }

    private void onTaskFinish() {

    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    /**
     * To start at a specific date in the future, the initial delay
     * needs to be calculated relative to the current time, as in :
     * Date futureDate = ...
     * long startTime = Timed.getRelativeToCurrentTime(futureDate);
     * Timed alarm = new Timed(startTime, ...);
     * This works only if the system clock isn't reset.
     *
     * @param date Specific Date in the future
     * @return Millisecond from now to specific Date.
     */
    public static long getRelativeToCurrentTime(final Date date) {
        return date.getTime() - System.currentTimeMillis();
    }

    // --------------------------------------------------------------------
    //               M A I N   (sample usage)
    // --------------------------------------------------------------------

    /**
     * Run the example.
     */
    public static void main(String... aArgs) throws InterruptedException {
        Timed alarmClock = new Timed(TimeUnit.SECONDS,
                3, // start after 3 seconds
                1, // run each 1 second
                0, 0);
        alarmClock.setMaxThreads(2);
        // start first thread
        alarmClock.start(new Delegates.Function<Object>() {
            @Override
            public Object handle(Object... args) {
                final int count = (Integer) args[0];
                try {
                    System.out.println("STOPPED BY CODE (sleeping): " + count);
                    Thread.sleep((long) (Math.random() * 1000 * 3));
                } catch (Throwable ignored) {

                }
                return count < 10; // stop after 10 loop
            }
        });
        // start second thread
        alarmClock.start(new Delegates.Function<Object>() {
            @Override
            public Object handle(Object... args) {
                final int count = (Integer) args[0];
                System.out.println("STOPPED BY CODE: " + count);
                return count < 10; // stop after 10 loop
            }
        });


        Timed alarmClock2 = new Timed(TimeUnit.SECONDS,
                0, // start after 3 seconds
                1, // run each 1 second
                0, 3);
        alarmClock2.start(new Delegates.Function<Object>() {

            @Override
            public Object handle(final Object... args) {
                final int count = (Integer) args[0];
                System.out.println("STOPPED AFTER 3 LOOPS: " + count);

                return null;
            }
        });
    }

    // --------------------------------------------------------------------
    //               E M B E D D E D
    // --------------------------------------------------------------------

    /**
     * Thread Factory
     */
    private static final class Factory
            implements ThreadFactory {

        private final Timed _sender;

        public Factory(final Timed sender) {
            _sender = sender;
        }

        @Override
        public Thread newThread(final Runnable r) {
            final Thread t = new Thread(r);
            t.setPriority(Thread.NORM_PRIORITY);
            t.setDaemon(_sender.isDaemon());
            return t;
        }
    }

    /**
     * Task Runner
     */
    private static final class RunTask
            implements Runnable {

        private final Delegates.Function<?> _callback;
        private final Timed _sender;

        public RunTask(final Timed sender,
                       final Delegates.Function<?> callback) {
            _sender = sender;
            _callback = callback;
        }

        @Override
        public void run() {
            this.invoke();
        }

        private void invoke() {
            try {
                if (null != _sender && null != _callback) {
                    final int count = _sender.incCount();
                    final Object response = _callback.handle(count, _sender);
                    boolean stop = false;
                    // if false, stop execution of all tasks
                    if (response instanceof Boolean) {
                        if (!((Boolean) response)) {
                            stop = true;
                        }
                    }
                    // if reached max number of iterations, stop
                    final int max_iterations = _sender.getShutdownAfterCount();
                    if (max_iterations > 0 && count >= max_iterations) {
                        stop = true;
                    }

                    if (stop) {
                        _sender.stop(true);
                    }
                }
            } catch (Throwable ignored) {
            }
        }
    } //-- start task

    /**
     * Tasks Stopper
     */
    private static final class StopTask
            implements Runnable {

        private final Timed _sender;
        private final boolean _interrupt_if_running;

        public StopTask(final Timed sender, final boolean interrupt_if_running) {
            _sender = sender;
            _interrupt_if_running = interrupt_if_running;
        }

        @Override
        public void run() {
            if (null != _sender) {
                _sender.finish(_interrupt_if_running);
            }
        }
    } // stop-task


}
