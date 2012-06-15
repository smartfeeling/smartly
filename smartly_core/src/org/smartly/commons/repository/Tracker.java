
package org.smartly.commons.repository;

import java.io.IOException;

/**
 * A utility class that allows Resource consumers to track changes
 * on resources.
 */
public class Tracker {

    Trackable source;
    long lastModified;

    public Tracker(Trackable source) throws IOException {
        this.source = source;
        markClean();
    }

    public boolean hasChanged() throws IOException {
        return lastModified != source.lastModified();
    }

    public void markClean() throws IOException {
        lastModified = source.lastModified();
    }

    public Trackable getSource() {
        return source;
    }
}
