package org.smartly.commons.io;

/**
 * Listen to FileObserver events
 */
public interface IFileObserverListener {

    void onEvent(int event, final String path);

}
