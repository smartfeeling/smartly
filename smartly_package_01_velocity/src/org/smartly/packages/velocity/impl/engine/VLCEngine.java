/*
 * 
 */

package org.smartly.packages.velocity.impl.engine;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.InvalidReferenceEventHandler;
import org.apache.velocity.app.event.MethodExceptionEventHandler;
import org.apache.velocity.runtime.RuntimeConstants;
import org.smartly.commons.lang.CharEncoding;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.velocity.impl.engine.eventhandlers.VLCEventHandlerInvalidReference;
import org.smartly.packages.velocity.impl.engine.eventhandlers.VLCEventHandlerMethodException;
import org.smartly.packages.velocity.impl.engine.loaders.VLCClasspathLoader;
import org.smartly.packages.velocity.impl.engine.loaders.VLCFileLoader;
import org.smartly.packages.velocity.impl.engine.loggers.VLCLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author
 */
public final class VLCEngine {

    private final Map<String, Object> _properties;

    private final String PATHS = ".";
    private final Long CHECK_INTERVAL = 60L; // every 60 sec.

    private final String LOADER_FILE = "file";
    private final String FILE_RESOURCE_LOADER_PATH = LOADER_FILE + "." + RuntimeConstants.RESOURCE_LOADER + ".path";
    private final String FILE_RESOURCE_LOADER_CLASS = LOADER_FILE + "." + RuntimeConstants.RESOURCE_LOADER + ".class";
    private final String FILE_RESOURCE_LOADER_CACHE = LOADER_FILE + "." + RuntimeConstants.RESOURCE_LOADER + ".cache";
    private final String FILE_RESOURCE_LOADER_CHECKINT = LOADER_FILE + "." + RuntimeConstants.RESOURCE_LOADER + ".modificationCheckInterval";
    /*
    private final String LOADER_JAR= "jar";
    private final String JAR_RESOURCE_LOADER_PATH = LOADER_JAR + "." + RuntimeConstants.RESOURCE_LOADER + ".path";
    private final String JAR_RESOURCE_LOADER_CLASS = LOADER_JAR + "." + RuntimeConstants.RESOURCE_LOADER + ".class";
    private final String JAR_RESOURCE_LOADER_CACHE = LOADER_JAR + "." + RuntimeConstants.RESOURCE_LOADER + ".cache";
    private final String JAR_RESOURCE_LOADER_CHECKINT = LOADER_JAR + "." + RuntimeConstants.RESOURCE_LOADER + ".modificationCheckInterval";
     */
    private final String LOADER_CLASSPATH = "classpath";
    private final String CLASSPATH_RESOURCE_LOADER_PATH = LOADER_CLASSPATH + "." + RuntimeConstants.RESOURCE_LOADER + ".path";
    private final String CLASSPATH_RESOURCE_LOADER_CLASS = LOADER_CLASSPATH + "." + RuntimeConstants.RESOURCE_LOADER + ".class";
    private final String CLASSPATH_RESOURCE_LOADER_CACHE = LOADER_CLASSPATH + "." + RuntimeConstants.RESOURCE_LOADER + ".cache";
    private final String CLASSPATH_RESOURCE_LOADER_CHECKINT = LOADER_CLASSPATH + "." + RuntimeConstants.RESOURCE_LOADER + ".modificationCheckInterval";
    //-- EVENT HANDLER --//
    private final String EVENTHANDLER_METHODEXCEPTION = RuntimeConstants.EVENTHANDLER_METHODEXCEPTION;
    private final String EVENTHANDLER_INVALIDREFERENCES = RuntimeConstants.EVENTHANDLER_INVALIDREFERENCES;

    //-- ENCODING --//
    private static final String INPUT_ENCODIG = "input.encoding";
    private static final String OUTPUT_ENCODIG = "output.encoding";

    public VLCEngine() {
        _properties = new HashMap<String, Object>();
        // init defaults
        this.initProperties();
    }

    public VelocityEngine getNativeEngine() throws Exception {
        return this.createEngine();
    }

    public void setProperty(final String name, final Object value) {
        if (StringUtils.hasText(name) && null != value) {
            _properties.put(name, value);
        }
    }

    public Object getProperty(final String name) {
        if (StringUtils.hasText(name)) {
            return _properties.get(name);
        }
        return null;
    }

    public void setEventHandlerInvalidReferences(final Class<? extends InvalidReferenceEventHandler> aclass) {
        this.setProperty(EVENTHANDLER_INVALIDREFERENCES, aclass.getName());
    }

    public void setEventHandlerMethodException(final Class<? extends MethodExceptionEventHandler> aclass) {
        this.setProperty(EVENTHANDLER_METHODEXCEPTION, aclass.getName());
    }

    public void setFileResourceLoaderPath(final String path) {
        this.setProperty(FILE_RESOURCE_LOADER_PATH, path);
    }
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void initProperties() {
        //-- Resource Loaders --//
        this.initLoaders();

        //-- Event Handlers --//
        this.initPropertiesEventHandlers();

        //-- LOG properties --//
        this.initPropertiesLog();

        //-- encoding --//
        this.initPropertiesEncoding();
    }

    private void initPropertiesEventHandlers() {
        this.setEventHandlerMethodException(
                VLCEventHandlerMethodException.class);

        this.setEventHandlerInvalidReferences(
                VLCEventHandlerInvalidReference.class);
    }

    private void initPropertiesLog() {
        _properties.put(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
                VLCLogger.class);
    }

    private void initPropertiesEncoding() {
        _properties.put(INPUT_ENCODIG, CharEncoding.getDefault());
        _properties.put(OUTPUT_ENCODIG, CharEncoding.getDefault());
    }

    private void initLoaders() {
        // this.setProperty(RuntimeConstants.RESOURCE_LOADER, LOADER_FILE + "," + LOADER_CLASSPATH);

        //-- File loader --//
        this.setProperty(RuntimeConstants.RESOURCE_LOADER, LOADER_FILE);
        this.initFileLoader();

        //-- Class loader --//
        // this.setProperty(RuntimeConstants.RESOURCE_LOADER, LOADER_CLASSPATH);
        //this.initClassLoader();
    }

    private void initFileLoader() {
        this.setProperty(FILE_RESOURCE_LOADER_CLASS, VLCFileLoader.class.getName());
        this.setProperty(FILE_RESOURCE_LOADER_PATH, this.getPaths());
        this.setProperty(FILE_RESOURCE_LOADER_CACHE, "true");
        this.setProperty(FILE_RESOURCE_LOADER_CHECKINT, CHECK_INTERVAL);
    }

    private void initClassLoader() {
        this.setProperty(CLASSPATH_RESOURCE_LOADER_CLASS, VLCClasspathLoader.class.getName());
        this.setProperty(CLASSPATH_RESOURCE_LOADER_PATH, this.getPaths());
        this.setProperty(CLASSPATH_RESOURCE_LOADER_CACHE, "true");
        this.setProperty(CLASSPATH_RESOURCE_LOADER_CHECKINT, CHECK_INTERVAL);
    }

    private VelocityEngine createEngine() throws Exception {
        final VelocityEngine ve = new VelocityEngine();

        // set properties
        final Set<String> names = _properties.keySet();
        for (final String name : names) {
            ve.setProperty(name, _properties.get(name));
        }

        ve.init();
        return ve;
    }

    private String getPaths() {
        return PATHS;
    }


}
