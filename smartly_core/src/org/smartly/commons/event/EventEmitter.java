/*
 * 
 */
package org.smartly.commons.event;

import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.ClassLoaderUtils;

import java.io.Serializable;

/**
 * Generic Events notifier.<br>
 * Add here listeners for runtime events.<br>
 * <p/>
 * <p>
 * <b>LISTENERS</b><br>
 * Event listeners are added in a list, for each "sender".
 * You can have many "senders" as you need. A "sender" is an object that fire events.
 * If you don't specify any sender, a default sender will be provided (this class is default sender).<br>
 * <p/>
 * </p>
 * <p>
 * <b>CONFIGURATION</b><br>
 * Listeners declared in configuration file must have
 * a constructor without parameters.<br>
 * All listeners declared are automatically created after
 * <code>EventEmitter.initialize()</code> is called.<br>
 * You should declare here only listeners that must be created automatically,
 * for example because you need a particular action after initialization
 * (or when a certain event is fired).
 * </p>
 *
 * @author
 */
public class EventEmitter
        implements IEventEmitter, Serializable {

    private EventListeners _listeners;

    public EventEmitter() {
        _listeners = new EventListeners();
    }

    @Override
    protected void finalize() throws Throwable {
        if (null != _listeners) {
            _listeners.clear();
            _listeners = null;
        }
        super.finalize();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.getClass().getName());
        result.append(" [");
        result.append("Size=").append(this.size());
        result.append("]");

        return result.toString();
    }

    public String getEventListenerSnapshot() {
        return _listeners.getListenerSnapshot();
    }

    public IEventListener[] getEventListeners() {
        return _listeners.toArray();
    }

    @Override
    public void addEventListener(final IEventListener listener) {
        if (null == listener) {
            this.getLogger().warning(String.format("Attempting to load a null listener"));
        }
        _listeners.add(listener);
    }

    public void addEventListener(final String filePath, final String listenerClass) {
        try {
            // JarClassLoader.getInstance().addFile(filePath);
            this.addEventListener(listenerClass);
        } catch (Exception ex) {
            this.getLogger().log(Level.WARNING, String.format("Unable to add listener [%s]: [%s]",
                    listenerClass, ex.toString()), ex);
        }
    }

    public void addEventListener(final String listener) {
        try {
            //Class aclass = JarClassLoader.getInstance().loadClass(listener);
            final Class aclass = ClassLoaderUtils.forName(listener);
            if (null == aclass) {
                throw new ClassNotFoundException(String.format("Unable to retrieve [%s] class.", listener));
            }
            final Object instance = aclass.newInstance();
            final boolean assignable = IEventListener.class.isAssignableFrom(instance.getClass());
            if (assignable) {
                this.addEventListener((IEventListener) instance);
            } else {
                throw new Exception(String.format("Listener [%s] does not implement interface IEventListener.", listener));
            }
        } catch (Exception ex) {
            this.getLogger().log(Level.WARNING, String.format("Unable to add listener [%s]: [%s]",
                    listener, ex.toString()), ex);
        }
    }

    public void addEventListeners(final IEventListener[] listeners) {
        if (null != listeners) {
            for (IEventListener listener : listeners) {
                this.addEventListener(listener);
            }
        }
    }

    public void clearEventListeners() {
        _listeners.clear();
    }

    @Override
    public void removeEventListener(IEventListener listener) {
        _listeners.remove(listener);
    }

    public int size() {
        return _listeners.size();
    }

    public int doEvent(final String eventName, Object... args) {
        Event event = new Event(this, eventName);
        if (null != args && args.length > 0) {
            event.setData(args);
        }
        return this.emit(event);
    }

    public int doEvent(Object eventSender, final String eventName) {
        Event event = new Event(eventSender, eventName);
        return this.emit(event);
    }

    @Override
    public int emit(Event event) {
        Logger logger = this.getLogger();
        //logger.info(String.format("KEY=[%s] Fire %s ", key, event));
        int result = 0;
        IEventListener[] listeners = _listeners.toArray();
        if (listeners.length > 0) {
            for (IEventListener listener : listeners) {
                try {
                    if (null != listener) {
                        listener.on(event);
                        result++;
                        if (logger.isLoggable(Level.FINE)) {
                            final String msg = String.format("Fired event {%s}", event);
                            logger.log(Level.FINE, msg);
                        }
                    } else {
                        if (logger.isLoggable(Level.FINE)) {
                            final String msg = String.format("NULL Listener: Firing event {%s}", event);
                            logger.log(Level.FINE, msg);
                        }
                    }
                } catch (NullPointerException t) {
                    if (logger.isLoggable(Level.FINE)) {
                        final String msg = String.format("Catched a NullPointerException firing event {%s}", event);
                        logger.log(Level.FINE, msg);
                    }
                } catch (Throwable t) {
                    final String msg = String.format("An error occurred in event listener [%s]: {%s}" +
                            "\nEvent is: [%s]",
                            listener, t, event);
                    logger.log(Level.SEVERE, msg, t);
                }
            }
        } else {
            if (logger.isLoggable(Level.FINE)) {
                final String msg = String.format("No listeners for Event: {%s}", event);
                logger.log(Level.FINE, msg);
            }
        }
        return result;
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }


}
