package org.smartly.commons.mutex;

import org.smartly.commons.util.DateUtils;

/**
 *
 */
public class Mutex {

    private final long _timeout;
    private long _lastAccessTime;

    public Mutex(final long timeout) {
        _timeout = timeout;
        _lastAccessTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append("id: ").append(this.hashCode());
        result.append(", ");
        result.append("timeout: ").append(_timeout);
        result.append(", ");
        result.append("last_access: ").append(_lastAccessTime);

        return result.toString();
    }

    Mutex wakeUp() {
        _lastAccessTime = System.currentTimeMillis();
        return this;
    }

    boolean isExpired() {
        final long now = System.currentTimeMillis();
        return (now - _lastAccessTime) > _timeout;
    }
}
