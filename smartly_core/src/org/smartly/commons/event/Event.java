/*
 *
 */

package org.smartly.commons.event;

/**
 * @author
 */
public class Event {

    private final Object _sender;
    private final String _name;

    private Object _data;


    public Event(final Object sender, final String name) {
        this._sender = sender;
        this._name = name;
    }

    public Event(final Object sender, final String name, final Object data) {
        this._sender = sender;
        this._name = name;
        this._data = data;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.getClass().getName());
        result.append("[");
        result.append("Name: '").append(_name).append("'");
        result.append(", ");
        result.append("Sender: {").append(_sender).append("}");
        result.append(", ");
        result.append("Data: {").append(_data).append("}");
        result.append("]");

        return result.toString();
    }


    public Object getSender() {
        return _sender;
    }

    /*
    public void setSender(Object sender) {
        _sender = sender;
    }
    */
    public String getName() {
        return _name;
    }

    public Object getData() {
        return _data;
    }

    public void setData(Object data) {
        this._data = data;
    }


}
