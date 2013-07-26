package org.smartly.commons.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * LinkedList of JSONObjects
 */
public class JsonList
        extends LinkedList<JSONObject> {

    public JsonList() {

    }

    public JsonList(final Collection<JSONObject> items) {
        super(items);
    }

    public JsonList(final JSONObject[] items) {
        super(Arrays.asList(items));
    }

    public JsonList(final JSONArray items) {
        final int length = items.length();
        for (int i = 0; i < length; i++) {
            try {
                final Object item = items.opt(i);
                super.add((JSONObject)item);
            } catch (Throwable ignored) {
            }
        }
    }

    @Override
    public String toString() {
        final JSONArray array = new JSONArray(this);
        return array.toString();
    }

    public JSONArray toJSONArray() {
        return new JSONArray(this);
    }
}
