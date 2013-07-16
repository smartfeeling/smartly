package org.smartly.commons.network.socket.messages.multipart;

import org.junit.Test;
import org.smartly.commons.network.socket.messages.multipart.util.MultipartPoolEvents;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class MultipartPoolTest {


    @Test
    public void testMain() throws Exception {

        final MultipartPool pool = new MultipartPool(1000);
        this.handleEvents(pool);

        final MultipartMessagePart part1 = new MultipartMessagePart();
        part1.setUid("FULL-PART");
        part1.setInfo(new MultipartInfo("FULL", MultipartInfo.MultipartInfoType.File, "chunk_01", 0, 2));
        final MultipartMessagePart part2 = new MultipartMessagePart();
        part2.setUid("FULL-PART");
        part2.setInfo(new MultipartInfo("FULL", MultipartInfo.MultipartInfoType.File, "chunk_02", 1, 2));

        pool.add(part1);
        pool.add(part2);

        System.out.println("ITEMS in POOL: " + pool.size());

        Thread.sleep(1000);

        System.out.println("ITEMS in POOL: " + pool.size());

        //-- time out --//
        System.out.println("Testing timeout.... ");
        final MultipartMessagePart part3 = new MultipartMessagePart();
        part3.setUid("INCOMPLETE-PART");
        part3.setInfo(new MultipartInfo("INCOMPLETE", MultipartInfo.MultipartInfoType.File, "chunk_01", 0, 2));

        pool.add(part3);

        System.out.println("ITEMS in POOL: " + pool.size());
        System.out.println("Wait 3 seconds....");

        Thread.sleep(3000);

        System.out.println("ITEMS in POOL: " + pool.size());

        assertTrue(pool.size() == 0);
    }

    private void handleEvents(final MultipartPool pool) {
        // handle
        pool.onFull(new MultipartPoolEvents.OnFullListener() {
            @Override
            public void handle(Multipart sender) {
                System.out.println("FULL: " + sender.toString());
            }
        });
        // handle timeout
        pool.onTimeOut(new MultipartPoolEvents.OnTimeOutListener() {
            @Override
            public void handle(Multipart sender) {
                System.out.println("TIME OUT: " + sender.toString());
            }
        });
    }

}
