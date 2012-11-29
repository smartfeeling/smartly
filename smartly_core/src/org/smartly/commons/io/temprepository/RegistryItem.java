package org.smartly.commons.io.temprepository;

import org.json.JSONObject;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.PathUtils;

/**
 *
 */
public class RegistryItem {

    private static final String PATH = "path";
    private static final String TIMESTAMP = "timestamp";

    private final JsonWrapper _data;
    private String _path;

    public RegistryItem() {
        _data = new JsonWrapper(new JSONObject());
    }

    public RegistryItem(final String path) {
        _data = new JsonWrapper(new JSONObject());
        this.setPath(path);
    }

    public RegistryItem(final JSONObject item) {
        _data = new JsonWrapper(item);
    }

    public JSONObject getData() {
        return _data.getJSONObject();
    }

    public void setPath(final String path) {
        _data.putSilent(PATH, PathUtils.toUnixPath(path));
        _data.putSilent(TIMESTAMP, System.currentTimeMillis());
    }

    public String getPath() {
        return _data.optString(PATH);
    }

    public long getTimestamp() {
        return _data.optLong(TIMESTAMP);
    }

    public boolean expired(final long duration) {
        final long now = System.currentTimeMillis();
        return now - this.getTimestamp() > duration;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
