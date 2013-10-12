/**
 *
 */
package org.smartly.commons.image.resize;

import org.smartly.commons.image.resize.Resize.Method;
import org.smartly.commons.image.resize.Resize.Mode;
import org.smartly.commons.image.resize.Resize.Rotation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImagingOpException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 */
@SuppressWarnings("javadoc")
public class AsyncResize {
    /**
     * System property name used to set the number of threads the default
     * underlying {@link ExecutorService} will use to process async image
     * operations.
     * <p/>
     * Value is "<code>imgscalr.async.threadCount</code>".
     */
    public static final String THREAD_COUNT_PROPERTY_NAME = "imgscalr.async.threadCount";

    /**
     * Number of threads the internal {@link ExecutorService} will use to
     * simultaneously execute scale requests.
     * <p/>
     * This value can be changed by setting the
     * <code>imgscalr.async.threadCount</code> system property (see
     * {@link #THREAD_COUNT_PROPERTY_NAME}) to a valid integer value &gt; 0.
     * <p/>
     * Default value is <code>2</code>.
     */
    public static final int THREAD_COUNT = Integer.getInteger(
            THREAD_COUNT_PROPERTY_NAME, 2);

    /**
     * Initializer used to verify the THREAD_COUNT system property.
     */
    static {
        if (THREAD_COUNT < 1)
            throw new RuntimeException("System property '"
                    + THREAD_COUNT_PROPERTY_NAME + "' set THREAD_COUNT to "
                    + THREAD_COUNT + ", but THREAD_COUNT must be > 0.");
    }

    protected static ExecutorService service;

    /**
     * Used to get access to the internal {@link ExecutorService} used by this
     * class to process scale operations.
     * <p/>
     * <strong>NOTE</strong>: You will need to explicitly shutdown any service
     * currently set on this class before the host JVM exits.
     * <p/>
     * You can call {@link ExecutorService#shutdown()} to wait for all scaling
     * operations to complete first or call
     * {@link ExecutorService#shutdownNow()} to kill any in-process operations
     * and purge all pending operations before exiting.
     * <p/>
     * Additionally you can use
     * {@link ExecutorService#awaitTermination(long, TimeUnit)} after issuing a
     * shutdown command to try and wait until the service has finished all
     * tasks.
     *
     * @return the current {@link ExecutorService} used by this class to process
     *         scale operations.
     */
    public static ExecutorService getService() {
        return service;
    }

    /**
     * @see Resize#apply(BufferedImage, BufferedImageOp...)
     */
    public static Future<BufferedImage> apply(final BufferedImage src,
                                              final BufferedImageOp... ops) throws IllegalArgumentException,
            ImagingOpException {
        checkService();

        return service.submit(new Callable<BufferedImage>() {
            public BufferedImage call() throws Exception {
                return Resize.apply(src, ops);
            }
        });
    }

    /**
     * @see Resize#crop(BufferedImage, int, int, BufferedImageOp...)
     */
    public static Future<BufferedImage> crop(final BufferedImage src,
                                             final int width, final int height, final BufferedImageOp... ops)
            throws IllegalArgumentException, ImagingOpException {
        checkService();

        return service.submit(new Callable<BufferedImage>() {
            public BufferedImage call() throws Exception {
                return Resize.crop(src, width, height, ops);
            }
        });
    }

    /**
     * @see Resize#crop(BufferedImage, int, int, int, int, BufferedImageOp...)
     */
    public static Future<BufferedImage> crop(final BufferedImage src,
                                             final int x, final int y, final int width, final int height,
                                             final BufferedImageOp... ops) throws IllegalArgumentException,
            ImagingOpException {
        checkService();

        return service.submit(new Callable<BufferedImage>() {
            public BufferedImage call() throws Exception {
                return Resize.crop(src, x, y, width, height, ops);
            }
        });
    }

    /**
     * @see Resize#pad(BufferedImage, int, BufferedImageOp...)
     */
    public static Future<BufferedImage> pad(final BufferedImage src,
                                            final int padding, final BufferedImageOp... ops)
            throws IllegalArgumentException, ImagingOpException {
        checkService();

        return service.submit(new Callable<BufferedImage>() {
            public BufferedImage call() throws Exception {
                return Resize.pad(src, padding, ops);
            }
        });
    }

    /**
     * @see Resize#pad(BufferedImage, int, Color, BufferedImageOp...)
     */
    public static Future<BufferedImage> pad(final BufferedImage src,
                                            final int padding, final Color color, final BufferedImageOp... ops)
            throws IllegalArgumentException, ImagingOpException {
        checkService();

        return service.submit(new Callable<BufferedImage>() {
            public BufferedImage call() throws Exception {
                return Resize.pad(src, padding, color, ops);
            }
        });
    }

    /**
     * @see Resize#resize(BufferedImage, int, BufferedImageOp...)
     */
    public static Future<BufferedImage> resize(final BufferedImage src,
                                               final int targetSize, final BufferedImageOp... ops)
            throws IllegalArgumentException, ImagingOpException {
        checkService();

        return service.submit(new Callable<BufferedImage>() {
            public BufferedImage call() throws Exception {
                return Resize.resize(src, targetSize, ops);
            }
        });
    }

    /**
     * @see Resize#resize(BufferedImage, Method, int, BufferedImageOp...)
     */
    public static Future<BufferedImage> resize(final BufferedImage src,
                                               final Method scalingMethod, final int targetSize,
                                               final BufferedImageOp... ops) throws IllegalArgumentException,
            ImagingOpException {
        checkService();

        return service.submit(new Callable<BufferedImage>() {
            public BufferedImage call() throws Exception {
                return Resize.resize(src, scalingMethod, targetSize, ops);
            }
        });
    }


    public static Future<BufferedImage> resize(final BufferedImage src,
                                               final Resize.Mode resizeMode, final int targetSize,
                                               final BufferedImageOp... ops) throws IllegalArgumentException,
            ImagingOpException {
        checkService();

        return service.submit(new Callable<BufferedImage>() {
            public BufferedImage call() throws Exception {
                return Resize.resize(src, resizeMode, targetSize, ops);
            }
        });
    }


    public static Future<BufferedImage> resize(final BufferedImage src,
                                               final Method scalingMethod, final Resize.Mode resizeMode,
                                               final int targetSize, final BufferedImageOp... ops)
            throws IllegalArgumentException, ImagingOpException {
        checkService();

        return service.submit(new Callable<BufferedImage>() {
            public BufferedImage call() throws Exception {
                return Resize.resize(src, scalingMethod, resizeMode, targetSize,
                        ops);
            }
        });
    }

    /**
     * @see Resize#resize(BufferedImage, int, int, BufferedImageOp...)
     */
    public static Future<BufferedImage> resize(final BufferedImage src,
                                               final int targetWidth, final int targetHeight,
                                               final BufferedImageOp... ops) throws IllegalArgumentException,
            ImagingOpException {
        checkService();

        return service.submit(new Callable<BufferedImage>() {
            public BufferedImage call() throws Exception {
                return Resize.resize(src, targetWidth, targetHeight, ops);
            }
        });
    }

    /**
     * @see Resize#resize(BufferedImage, Method, int, int, BufferedImageOp...)
     */
    public static Future<BufferedImage> resize(final BufferedImage src,
                                               final Method scalingMethod, final int targetWidth,
                                               final int targetHeight, final BufferedImageOp... ops) {
        checkService();

        return service.submit(new Callable<BufferedImage>() {
            public BufferedImage call() throws Exception {
                return Resize.resize(src, scalingMethod, targetWidth,
                        targetHeight, ops);
            }
        });
    }

    /**
     * @see Resize#resize(BufferedImage, Mode, int, int, BufferedImageOp...)
     */
    public static Future<BufferedImage> resize(final BufferedImage src,
                                               final Mode resizeMode, final int targetWidth,
                                               final int targetHeight, final BufferedImageOp... ops)
            throws IllegalArgumentException, ImagingOpException {
        checkService();

        return service.submit(new Callable<BufferedImage>() {
            public BufferedImage call() throws Exception {
                return Resize.resize(src, resizeMode, targetWidth, targetHeight,
                        ops);
            }
        });
    }

    /**
     * @see Resize#resize(BufferedImage, Method, Mode, int, int,
     *      BufferedImageOp...)
     */
    public static Future<BufferedImage> resize(final BufferedImage src,
                                               final Method scalingMethod, final Mode resizeMode,
                                               final int targetWidth, final int targetHeight,
                                               final BufferedImageOp... ops) throws IllegalArgumentException,
            ImagingOpException {
        checkService();

        return service.submit(new Callable<BufferedImage>() {
            public BufferedImage call() throws Exception {
                return Resize.resize(src, scalingMethod, resizeMode,
                        targetWidth, targetHeight, ops);
            }
        });
    }

    /**
     * @see Resize#rotate(BufferedImage, Rotation, BufferedImageOp...)
     */
    public static Future<BufferedImage> rotate(final BufferedImage src,
                                               final Resize.Rotation rotation, final BufferedImageOp... ops)
            throws IllegalArgumentException, ImagingOpException {
        checkService();

        return service.submit(new Callable<BufferedImage>() {
            public BufferedImage call() throws Exception {
                return Resize.rotate(src, rotation, ops);
            }
        });
    }

    protected static ExecutorService createService() {
        return createService(new DefaultThreadFactory());
    }

    protected static ExecutorService createService(ThreadFactory factory)
            throws IllegalArgumentException {
        if (factory == null)
            throw new IllegalArgumentException("factory cannot be null");

        return Executors.newFixedThreadPool(THREAD_COUNT, factory);
    }

    /**
     * Used to verify that the underlying <code>service</code> points at an
     * active {@link ExecutorService} instance that can be used by this class.
     * <p/>
     * If <code>service</code> is <code>null</code>, has been shutdown or
     * terminated then this method will replace it with a new
     * {@link ExecutorService} by calling the {@link #createService()} method
     * and assigning the returned value to <code>service</code>.
     * <p/>
     * Any subclass that wants to customize the {@link ExecutorService} or
     * {@link ThreadFactory} used internally by this class should override the
     * {@link #createService()}.
     */
    protected static void checkService() {
        if (service == null || service.isShutdown() || service.isTerminated()) {
            /*
             * If service was shutdown or terminated, assigning a new value will
			 * free the reference to the instance, allowing it to be GC'ed when
			 * it is done shutting down (assuming it hadn't already).
			 */
            service = createService();
        }
    }

    /**
     * Default {@link ThreadFactory} used by the internal
     * {@link ExecutorService} to creates execution {@link Thread}s for image
     * scaling.
     * <p/>
     * More or less a copy of the hidden class backing the
     * {@link Executors#defaultThreadFactory()} method, but exposed here to make
     * it easier for implementors to extend and customize.
     *
     * @author Doug Lea
     * @author Riyad Kalla (software@thebuzzmedia.com)
     * @since 4.0
     */
    protected static class DefaultThreadFactory implements ThreadFactory {
        protected static final AtomicInteger poolNumber = new AtomicInteger(1);

        protected final ThreadGroup group;
        protected final AtomicInteger threadNumber = new AtomicInteger(1);
        protected final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager manager = System.getSecurityManager();

			/*
			 * Determine the group that threads created by this factory will be
			 * in.
			 */
            group = (manager == null ? Thread.currentThread().getThreadGroup()
                    : manager.getThreadGroup());

			/*
			 * Define a common name prefix for the threads created by this
			 * factory.
			 */
            namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
        }

        /**
         * Used to create a {@link Thread} capable of executing the given
         * {@link Runnable}.
         * <p/>
         * Thread created by this factory are utilized by the parent
         * {@link ExecutorService} when processing queued up scale operations.
         */
        public Thread newThread(Runnable r) {
			/*
			 * Create a new thread in our specified group with a meaningful
			 * thread name so it is easy to identify.
			 */
            Thread thread = new Thread(group, r, namePrefix
                    + threadNumber.getAndIncrement(), 0);

            // Configure thread according to class or subclass
            thread.setDaemon(false);
            thread.setPriority(Thread.NORM_PRIORITY);

            return thread;
        }
    }

    /**
     * An extension of the {@link DefaultThreadFactory} class that makes two
     * changes to the execution {@link Thread}s it generations:
     * <ol>
     * <li>Threads are set to be daemon threads instead of user threads.</li>
     * <li>Threads execute with a priority of {@link Thread#MIN_PRIORITY} to
     * make them more compatible with server environment deployments.</li>
     * </ol>
     * This class is provided as a convenience for subclasses to use if they
     * want this (common) customization to the {@link Thread}s used internally
     * by {@link AsyncResize} to process images, but don't want to have to write
     * the implementation.
     *
     * @author Riyad Kalla (software@thebuzzmedia.com)
     * @since 4.0
     */
    protected static class ServerThreadFactory extends DefaultThreadFactory {
        /**
         * Overridden to set <code>daemon</code> property to <code>true</code>
         * and decrease the priority of the new thread to
         * {@link Thread#MIN_PRIORITY} before returning it.
         */
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = super.newThread(r);

            thread.setDaemon(true);
            thread.setPriority(Thread.MIN_PRIORITY);

            return thread;
        }
    }
}