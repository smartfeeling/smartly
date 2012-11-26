package org.smartly.commons.util;

import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Simple file observer.
 * <p/>
 * This FileObserver works like Android FileObserver
 * <p/>
 * Monitors files to fire an event after files are accessed or changed
 * by any process on the device (including this one).
 * FileObserver is an abstract class; subclasses must implement the event handler onEvent(int, String).
 * Each FileObserver instance monitors a single file or directory.
 * If a directory is monitored as 'recursive', events will be triggered for all files and subdirectories (recursively)
 * inside the monitored directory.
 * An event mask is used to specify which changes or actions to report.
 * Event type constants are used to describe the possible changes in the event mask as well as what actually
 * happened in event callbacks.
 */
public abstract class FileObserver {

    public static final int EVENT_MODIFY = 0x00000002; /* File was modified */
    public static final int EVENT_CREATE = 0x00000100; /* Subfile was created */
    public static final int EVENT_DELETE = 0x00000200; /* Subfile was deleted */

    public static final int ALL_EVENTS = EVENT_MODIFY | EVENT_DELETE | EVENT_CREATE;


    // instance
    private final Path _path;
    private final boolean _recursive;
    private final boolean _verbose;
    private final int _mask;
    private final Set<WatchKey> _keys;

    public FileObserver(final String path) {
        this(path, false, false, ALL_EVENTS);
    }

    public FileObserver(final String path,
                        final boolean recursive,
                        final boolean verbose) {
        this(path, recursive, verbose, ALL_EVENTS);
    }

    public FileObserver(final String path,
                        final boolean recursive,
                        final boolean verbose,
                        final int mask) {
        _recursive = recursive;
        _verbose = verbose;
        _path = Paths.get(path);
        _mask = mask;
        _keys = new HashSet<WatchKey>();
    }

    protected void finalize() throws Throwable {
        try {
            stopWatching();
        } finally {
            super.finalize();
        }
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append(this.getClass().getName()).append("{");
        result.append("path: ").append(_path);
        result.append(", ");
        result.append("recursive: ").append(_recursive);
        result.append(", ");
        result.append("verbose: ").append(_verbose);
        // events
        result.append("events: [");
        if ((_mask & EVENT_CREATE) == EVENT_CREATE) {
            result.append(this.eventToString(EVENT_CREATE));
        }
        if ((_mask & EVENT_DELETE) == EVENT_DELETE) {
            result.append(this.eventToString(EVENT_DELETE));
        }
        if ((_mask & EVENT_MODIFY) == EVENT_MODIFY) {
            result.append(this.eventToString(EVENT_MODIFY));
        }
        result.append("]");
        result.append("}");
        return result.toString();
    }

    public final boolean isRecursive() {
        return _recursive;
    }

    public final boolean isVerbose() {
        return _verbose;
    }

    public final int getMask() {
        return _mask;
    }

    public final String eventToString(final int event) {
        if (EVENT_CREATE == event) {
            return "CREATE";
        } else if (EVENT_DELETE == event) {
            return "DELETE";
        } else if (EVENT_MODIFY == event) {
            return "MODIFY";
        }
        return "UNKNOWN";
    }

    public String startWatching() throws IOException {
        return getObserverThread().startWatching(_path, _mask, this);
    }

    public void stopWatching() {
        try {
            getObserverThread().stopWatching(_path);
        } catch (Throwable ignored) {
        }
    }

    /**
     * Join main thread until interrupt is called
     */
    public void join() {
        try {
            joinObserverThread();
        } catch (Throwable ignored) {
        }
    }

    public void interrupt() {
        interruptObserverThread();
    }

    protected abstract void onEvent(int event, final String path);

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private void addKey(final WatchKey key) {
        _keys.add(key);
    }

    private void addKeys(final Collection<WatchKey> keys) {
        _keys.addAll(keys);
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    // late initialized observer thread
    private static ObserverThread __observerThread;

    private static ObserverThread getObserverThread() throws IOException {
        if (null == __observerThread) {
            __observerThread = new ObserverThread();
            __observerThread.start();
        }
        return __observerThread;
    }

    private static void joinObserverThread() throws InterruptedException {
        if (null != __observerThread) {
            __observerThread.join();
        }
    }

    private static void interruptObserverThread() {
        if (null != __observerThread) {
            __observerThread.interrupt();
            __observerThread = null;
        }
    }

    private static class ObserverThread extends Thread {

        private final WatchService _watcher;
        private final Map<WatchKey, Path> _keys;
        private final Map<String, WeakReference<FileObserver>> _observers;

        public ObserverThread() throws IOException {
            super("FileObserver");
            super.setPriority(Thread.NORM_PRIORITY);
            super.setDaemon(true);
            _observers = Collections.synchronizedMap(new HashMap<String, WeakReference<FileObserver>>());
            _watcher = FileSystems.getDefault().newWatchService();
            _keys = new HashMap<WatchKey, Path>();
        }

        public void run() {
            this.observe();
        }

        public String startWatching(final Path path,
                                    final int mask,
                                    final FileObserver observer) throws IOException {
            final String key = this.getKey(path);
            synchronized (_observers) {
                if (!_observers.containsKey(key)) {
                    //-- register watch --//
                    final WatchKey wkey = this.registerWatch(path, observer.isVerbose(), observer.getMask());
                    if (null != wkey) {
                        observer.addKey(wkey);
                    }
                    //-- add observer for main loop --//
                    final WeakReference<FileObserver> ref = new WeakReference<FileObserver>(observer);
                    _observers.put(key, ref);
                }
            }

            return key;
        }

        public void stopWatching(final Path path) {
            final String key = this.getKey(path);
            synchronized (_observers) {
                if (_observers.containsKey(key)) {
                    //-- remove observer from main loop --//
                    final WeakReference<FileObserver> ref = _observers.remove(key);
                    //-- remove watch --//
                    if (null != ref && null != ref.get()) {
                        this.removeWatch(ref.get());
                    }
                }
            }
        }

        // --------------------------------------------------------------------
        //               p r i v a t e
        // --------------------------------------------------------------------

        private Logger getLogger() {
            return LoggingUtils.getLogger(this);
        }

        /**
         * Main loop to keep thread alive
         */
        private void observe() {
            try {
                while (!super.isInterrupted()) {
                    Thread.sleep(200);
                    final Collection<WeakReference<FileObserver>> references = _observers.values();
                    for (final WeakReference reference : references) {
                        final FileObserver observer = (FileObserver) reference.get();
                        if (null != observer) {
                            this.watch(observer);
                        }
                    }
                }
            } catch (Throwable ignored) {

            }
        }

        private void log(final FileObserver observer, final String msg) {
            if (null != observer && observer.isVerbose()) {
                this.getLogger().log(Level.INFO, msg);
            }
        }

        private void log(final String msg) {
            this.getLogger().log(Level.INFO, msg);
        }

        private void error(final FileObserver observer, final String msg, final Throwable t) {
            if (null != observer && observer.isVerbose()) {
                this.getLogger().log(Level.SEVERE, msg, t);
            }
        }

        private String getKey(final Path path) {
            return path.toString();
        }

        private void watch(final FileObserver observer) {
            // wait for key to be signalled
            WatchKey key;
            try {
                key = _watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            final Path dir = _keys.get(key);
            if (dir == null) {
                this.log(observer, "WatchKey not recognized!!");
                return;
            }

            for (final WatchEvent<?> event : key.pollEvents()) {
                final WatchEvent.Kind kind = event.kind();

                // OVERFLOW event does nothing
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                final WatchEvent<Path> ev = cast(event);
                final Path name = ev.context();
                final Path child = dir.resolve(name);

                // print out event
                this.log(observer, FormatUtils.format("{0}: {1}", event.kind().name(), child));

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (observer.isRecursive() && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            final Set<WatchKey> keys = this.registerAll(child, observer.isVerbose(), observer.getMask());
                            if (!keys.isEmpty()) {
                                observer.addKeys(keys);
                            }
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readbale
                    }
                }

                //-- call onEvent --//
                try {
                    observer.onEvent(this.convert(event), child.toString());
                } catch (Throwable throwable) {
                    this.error(observer, "Unhandled throwable " + throwable.toString() +
                            " (returned by observer " + observer + ")", throwable);
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                _keys.remove(key);

                // all directories are inaccessible
                if (_keys.isEmpty()) {
                    return;
                }
            }
        }

        private Set<WatchKey> registerAll(final Path start,
                                          final boolean verbose,
                                          final int mask) throws IOException {
            final Set<WatchKey> keys = new HashSet<WatchKey>();
            // register directory and sub-directories
            Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                        throws IOException {
                    keys.add(registerWatch(dir, verbose, mask));
                    return FileVisitResult.CONTINUE;
                }
            });
            return keys;
        }

        /**
         * Register the given directory with the WatchService
         */
        private WatchKey registerWatch(final Path dir,
                                       final boolean verbose,
                                       final int mask) throws IOException {
            // prepare flags
            final Set<WatchEvent.Kind<Path>> flags = new HashSet<WatchEvent.Kind<Path>>();
            if ((mask & EVENT_CREATE) == EVENT_CREATE) {
                flags.add(ENTRY_CREATE);
            }
            if ((mask & EVENT_DELETE) == EVENT_DELETE) {
                flags.add(ENTRY_DELETE);
            }
            if ((mask & EVENT_MODIFY) == EVENT_MODIFY) {
                flags.add(ENTRY_MODIFY);
            }
            final WatchEvent.Kind<Path>[] array = new WatchEvent.Kind[flags.size()];
            final WatchKey key = dir.register(_watcher, flags.toArray(array));
            if (verbose) {
                final Path prev = _keys.get(key);
                if (prev == null) {
                    this.log(FormatUtils.format("register: {0}", dir));
                } else {
                    if (!dir.equals(prev)) {
                        this.log(FormatUtils.format("update: {0} -> {1}", prev, dir));
                    }
                }
            }
            _keys.put(key, dir);
            return key;
        }

        private void removeWatch(final FileObserver observer) {
            if (null != observer) {
                final Set<WatchKey> keys = observer._keys;
                for (final WatchKey key : keys) {
                    key.cancel();
                }
            }
        }

        private int convert(final WatchEvent event) {
            return convert(event.kind());
        }

        private int convert(final WatchEvent.Kind kind) {
            if (null != kind) {
                if (kind.equals(ENTRY_CREATE)) {
                    return EVENT_CREATE;
                } else if (kind.equals(ENTRY_DELETE)) {
                    return EVENT_DELETE;
                } else if (kind.equals(ENTRY_MODIFY)) {
                    return EVENT_MODIFY;
                }
            }
            return 0;
        }


    }


}
