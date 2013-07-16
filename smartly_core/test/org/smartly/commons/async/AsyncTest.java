package org.smartly.commons.async;

import org.junit.Test;
import org.smartly.commons.Delegates;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.MathUtils;

/**
 *
 */
public class AsyncTest {

    @Test
    public void testMaxConcurrent() throws Exception {

        Thread[] threads = AsyncUtils.createArray(20, new Delegates.CreateRunnableCallback() {
            @Override
            public Runnable handle(final int index, final int length) {
                return new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(FormatUtils.format("Running: {0} of {1}", index + 1, length));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
            }
        });
        Async.maxConcurrent(threads, 2, new Delegates.ProgressCallback() {
            @Override
            public void handle(int index, int length, double progress) {
                System.out.println(FormatUtils.format("{0}/{1} {2}%", index + 1, length, (int) (progress * 100)));
            }
        });

        Async.joinAll(threads);

        //-- test jit thread creation --//

        threads = Async.maxConcurrent(20, 2, new Delegates.CreateRunnableCallback() {
            @Override
            public Runnable handle(final int index, final int length) {

                final Runnable result = new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(FormatUtils.format("Running: {0} of {1}", index + 1, length));
                        System.out.println(FormatUtils.format("{0}/{1} {2}%", index + 1, length, (int) (MathUtils.progress(index + 1, length) * 100)));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };

                return result;
            }
        });

        Async.joinAll(threads);
    }

}
