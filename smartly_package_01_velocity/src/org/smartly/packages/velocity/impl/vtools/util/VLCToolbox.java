/*
 * 
 */
package org.smartly.packages.velocity.impl.vtools.util;

import org.apache.velocity.VelocityContext;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.BeanUtils;
import org.smartly.commons.util.ExceptionUtils;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.velocity.impl.vtools.*;
import org.smartly.packages.velocity.impl.vtools.Formatter;
import org.smartly.packages.velocity.impl.vtools.Math;
import org.smartly.packages.velocity.impl.vtools.System;

import java.util.*;

/**
 * Toolbox is a container of 'static' vtools that are passed to context.<br/>
 * "Static" vtools are no-context vtools (usually singleton objects).
 * Use VLCToolbox metods to add/remove tool references.<br/>
 * NOTE: If vtools are not singleton, they are created from scratch
 * before each execution.
 *
 * @author angelo.geminiani
 */
public class VLCToolbox {

    private final List<VLCToolboxItem> _tools = new LinkedList<VLCToolboxItem>();
    // lazy inizialide from _tools and keep synchronized with _tools.
    private VelocityContext __vcontext;
    private Map<String, Object> __mcontext;

    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------
    private VLCToolbox() {
        // add default 'no-context' vtools
        this.initTools();
    }
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Create a tool reference and add it to Toolbox.
     *
     * @param id        Tool id (usually the Tool NAME)
     * @param toolClass Class reference of tool
     * @param args      Arguments to pass at tool constructor
     */
    public void add(final String id,
                    final Class toolClass,
                    final Object[] args,
                    final boolean isSingleton) {
        final VLCToolboxItem item = new VLCToolboxItem(id, toolClass, args, isSingleton);
        this.add(item);
    }

    /**
     * Add tool reference
     *
     * @param item Tool reference
     */
    public void add(final VLCToolboxItem item) {
        // add tool to list
        if (!_tools.contains(item)) {
            _tools.add(item);
        }
        // add tool to vcontext
        if (null != __vcontext) {
            try {
                if (!__vcontext.containsKey(item.getId())) {
                    final Object tool = item.getInstance();
                    __vcontext.put(getName(tool), tool);
                }
            } catch (Throwable ignored) {
            }
        }
        // add tool to mcontext
        if (null != __mcontext) {
            try {
                if (!__mcontext.containsKey(item.getId())) {
                    final Object tool = item.getInstance();
                    __mcontext.put(getName(tool), tool);
                }
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * Replace tool if exists, or add if does not exists.
     *
     * @param id          Tool id
     * @param toolClass   Class
     * @param args        Array
     * @param isSingleton boolean
     */
    public void replace(final String id,
                        final Class toolClass,
                        final Object[] args,
                        final boolean isSingleton) {
        final VLCToolboxItem item = new VLCToolboxItem(id, toolClass, args, isSingleton);
        this.replace(item);
    }

    /**
     * Replace tool if exists, or add if does not exists.
     *
     * @param item Tool to replace
     */
    public void replace(final VLCToolboxItem item) {
        this.remove(item.getId());
        this.add(item);
    }

    /**
     * Remove tool reference from toolbox
     *
     * @param id Tool id
     * @return VLCToolboxItem
     */
    public VLCToolboxItem remove(final String id) {
        // remove tool from vcontext
        if (null != __vcontext) {
            __vcontext.remove(id);
        }
        // remove tool from mcontext
        if (null != __mcontext) {
            __mcontext.remove(id);
        }
        // remove tool from list
        final VLCToolboxItem item = new VLCToolboxItem(id, null, null, false);
        final int index = _tools.indexOf(item);
        if (index > -1) {
            return _tools.remove(index);
        }
        return null;
    }

    /**
     * Return output string with a log of all installed vtools.<br/>
     * This method is useful for log or debug.
     *
     * @return String
     */
    public String enumTools() {
        final StringBuilder result = new StringBuilder();
        try {
            for (final VLCToolboxItem item : _tools) {
                StringUtils.append("\t" + item.toString(), result, "\n");
            }
        } catch (Throwable t) {
            result.append(ExceptionUtils.getRealMessage(t));
        }
        return result.toString();
    }

    public VelocityContext getToolsContext() {
        if (null == __vcontext) {
            __vcontext = this.createVContext();
        }

        return __vcontext;
    }

    public Map<String, Object> getToolsContextAsMap() {
        if (null == __mcontext) {
            __mcontext = this.createMContext();
        }

        return __mcontext;
    }

    // ------------------------------------------------------------------------
    //                  p r i v a t e
    // ------------------------------------------------------------------------
    private void initTools() {
        // add Formatter for date and number
        add(Formatter.NAME, Formatter.class, null, true);

        // add Math helper.
        add(Math.NAME, Math.class, null, true);

        // add System helper
        add(System.NAME, System.class, null, true);

        // add Js helper
        add(Script.NAME, Script.class, null, true);

        // add App helper
        add(App.NAME, App.class, null, true);

        // more vtools can be added using add command
    }

    private VelocityContext createVContext() {
        final VelocityContext vcontext = new VelocityContext();
        //-- add static (no-context) vtools --//
        for (final VLCToolboxItem item : _tools) {
            try {
                final Object tool = item.getInstance();
                vcontext.put(item.getId(), tool);
            } catch (Throwable ignored) {
            }
        }
        return vcontext;
    }

    private Map<String, Object> createMContext() {
        final Map<String, Object> mcontext = Collections.synchronizedMap(new HashMap<String, Object>());
        //-- add static (no-context) vtools --//
        for (final VLCToolboxItem item : _tools) {
            try {
                final Object tool = item.getInstance();
                mcontext.put(item.getId(), tool);
            } catch (Throwable ignored) {
            }
        }
        return mcontext;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------
    private static VLCToolbox __instance;

    public static VLCToolbox getInstance() {
        if (null == __instance) {
            __instance = new VLCToolbox();
        }
        return __instance;
    }

    private static String getName(final Object instance){
        final String result = (String)BeanUtils.getValueIfAny(instance, "name");
        if(StringUtils.hasText(result)){
             return result;
        }
        LoggingUtils.getLogger(VLCToolbox.class).log(Level.SEVERE, FormatUtils.format("INVALID TOOL IN TOOLBOX. Missing 'name' property, so a default name 'undefined' will be assigned: {0}", instance));
        return "undefined";
    }
}
