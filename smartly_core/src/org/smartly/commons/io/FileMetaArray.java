package org.smartly.commons.io;

import org.json.JSONArray;

import java.util.LinkedList;

/**
 *
 */
public class FileMetaArray extends LinkedList<FileMeta> {

    public FileMetaArray() {

    }

    @Override
    public FileMeta[] toArray() {
        return this.toArray(new FileMeta[this.size()]);
    }

    public JSONArray toJSONArray() {
        return new JSONArray(this.toString());
    }
}
