package org.smartly.commons.util;

import org.junit.Test;

/**
 *
 */
public class FileObserverTest {

    @Test
    public void testStartWatching() throws Exception {

        final FileObserver fo = new FileObserver("c:/_test", true, true,
                FileObserver.EVENT_DELETE | FileObserver.EVENT_MODIFY | FileObserver.EVENT_CREATE) {
            @Override
            protected void onEvent(int event, final String path) {
                System.out.println(this.eventToString(event) + ": " + path);
            }
        };

        final String path = fo.startWatching();
        System.out.println(path);
        fo.join();
    }

}
