package org.smartly.commons.async;

import org.junit.Test;
import org.smartly.commons.util.FormatUtils;

/**
 *
 */
public class AsyncTest {

    @Test
    public void testMaxConcurrent() throws Exception {

        Thread[] threads = AsyncUtils.createArray(20, new AsyncUtils.CreateRunnableCallback() {
            @Override
            public Runnable handle(final int index, final int length) {
                return new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(FormatUtils.format("Running: {0} of {1}", index+1, length));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
            }
        });
        Async.maxConcurrent(threads, 2);

        Async.joinAll(threads);
    }

}
