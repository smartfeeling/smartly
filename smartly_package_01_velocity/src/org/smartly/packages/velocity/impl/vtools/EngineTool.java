package org.smartly.packages.velocity.impl.vtools;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.smartly.packages.velocity.impl.VLCManager;

/**
 * Template engine exposed
 */
public class EngineTool {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------

    public static final String NAME = "engine";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final VelocityEngine _engine;
    private final VelocityContext _context;
    private final String _name;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public EngineTool(final String templateName,
                      final VelocityEngine engine,
                      final VelocityContext context) {
        _engine = engine;
        _context = context;
        _name = templateName;
    }


    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String eval(final Object text) {
        try {
            final String stext = null != text ? text.toString() : "";
            final String result;
            if (null != _engine) {
                result = VLCManager.getInstance().evaluateText(_engine, _name, stext, _context);
            } else {
                result = VLCManager.getInstance().evaluateText(_name, stext, _context);
            }
            return result;
        } catch (Throwable t) {
            return t.toString();
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
