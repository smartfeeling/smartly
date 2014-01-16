package org.smartly.packages.velocity.impl.vtools;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.smartly.commons.util.StringUtils;
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
            String original = null != text ? text.toString() : "";
            if (StringUtils.hasText(original)) {
                final String parsed;
                if (null != _engine) {
                    parsed = VLCManager.getInstance().evaluateText(_engine, _name, original, _context);
                } else {
                    parsed = VLCManager.getInstance().evaluateText(_name, original, _context);
                }
                if(!StringUtils.equalsTrim(original, parsed)){
                    // need another evaluation
                    return eval(parsed);
                } else {
                    return parsed;
                }
            }
            return original;
        } catch (Throwable t) {
            return t.toString();
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
