package org.smartly.commons.util;

import org.junit.Test;
import org.smartly.commons.io.FileObserver;
import org.smartly.commons.io.IFileObserverListener;

/**
 *
 */
public class FileObserverTest {

    @Test
    public void testStartWatching() throws Exception {

        final FileObserver fo = new FileObserver("c:/_test", true, true,
                FileObserver.EVENT_DELETE | FileObserver.EVENT_MODIFY | FileObserver.EVENT_CREATE, new IFileObserverListener() {
            @Override
            public void onEvent(int event, String path) {
                System.out.println(FileObserver.eventToString(event) + ": " + path);
            }
        });

        final String path = fo.startWatching();
        System.out.println(path);
        fo.join();
    }

}
