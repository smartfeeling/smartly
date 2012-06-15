/*
 * 
 */
package org.smartly.packages.velocity.impl;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.smartly.commons.event.EventEmitter;
import org.smartly.commons.lang.CharEncoding;
import org.smartly.commons.util.RegExUtils;
import org.smartly.packages.velocity.impl.engine.VLCContextFactory;
import org.smartly.packages.velocity.impl.engine.VLCEngine;
import org.smartly.packages.velocity.impl.events.OnBeforeEvaluate;
import org.smartly.packages.velocity.impl.tools.VLCToolbox;
import org.smartly.packages.velocity.impl.tools.VLCToolboxItem;

import java.io.StringWriter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Singleton helper for velocity engine.
 *
 * @author
 */
public class VLCManager extends EventEmitter
        implements IVLCCostants {

    private VLCEngine _engine;

    private VLCManager() {
    }

    public String evaluateText(final String vlcText,
                               final Map<String, Object> contextData) throws Exception {
        return this.evaluateText("VLC-TEXT", vlcText, contextData);
    }

    public String evaluateText(final String templateName,
                               final String vlcText,
                               final Map<String, Object> contextData) throws Exception {
        final VelocityContext context = VLCContextFactory.getContext(contextData);
        return this.evaluateTemplate(templateName, vlcText, context);
    }

    public String evaluateText(final String templateName,
                               final String vlcText,
                               final VelocityContext contextData) throws Exception {
        final VelocityContext context = VLCContextFactory.getContext(contextData);
        return this.evaluateTemplate(templateName, vlcText, context);
    }

    public String evaluate(final String templateName,
                           final VelocityContext contextData) throws Exception {
        final VelocityContext context = VLCContextFactory.getContext(contextData);
        return this.evaluateTemplate(templateName, context);
    }


    public String evaluate(final String templateName,
                           final Map<String, Object> contextData) throws Exception {
        final VelocityContext context = VLCContextFactory.getContext(contextData);
        return this.evaluateTemplate(templateName, context);
    }

    public void addTool(final VLCToolboxItem item) {
        VLCToolbox.getInstance().add(item);
    }

    public void addTool(final String id, final Class toolClass,
                        final Object[] args, final boolean isSingleton) {
        VLCToolbox.getInstance().add(id, toolClass, args, isSingleton);
    }

    public VLCEngine getEngine() {
        if (null == _engine) {
            _engine = new VLCEngine();
        }
        return _engine;
    }

    public VLCToolbox getToolbox() {
        return VLCToolbox.getInstance();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private VelocityEngine getNativeEngine() throws Exception {
        if (null == _engine) {
            _engine = new VLCEngine();
        }
        return _engine.getNativeEngine();
    }

    private String replaceUnsolvedVariables(final String text) {
        final StringBuffer sb = new StringBuffer();
        final Pattern p = Pattern.compile(RegExUtils.VELOCITY_VARIABLES);
        final Matcher m = p.matcher(text);
        boolean result = m.find();
        // Loop through and add mathes
        while (result) {
            final String key = m.group();
            m.appendReplacement(sb, key.replaceAll("\\$", "#").
                    replaceAll("\\{", "").
                    replaceAll("\\}", ""));
            result = m.find();
        }
        // Add the last segment of input to 
        // the new String
        m.appendTail(sb);
        return sb.toString();
    }

    private String evaluateTemplate(final String templateName,
                                    final String vlcText,
                                    final VelocityContext context) throws Exception {
        //-- raise event --//
        super.emit(new OnBeforeEvaluate(this, context));
        //-- evaluate template --//
        final VelocityEngine engine = this.getNativeEngine();
        final StringWriter writer = new StringWriter();
        engine.evaluate(context, writer, templateName, vlcText);

        return this.replaceUnsolvedVariables(writer.toString());
    }

    private String evaluateTemplate(final String templateName,
                                    final VelocityContext context) throws Exception {
        //-- raise event --//
        super.emit(new OnBeforeEvaluate(this, context));
        //-- evaluate template --//
        final VelocityEngine engine = this.getNativeEngine();
        final StringWriter writer = new StringWriter();

        final Template template = engine.getTemplate(templateName, CharEncoding.getDefault());
        template.merge(context, writer);

        return this.replaceUnsolvedVariables(writer.toString());
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------
    private static VLCManager _instance;

    public static VLCManager getInstance() {
        if (null == _instance) {
            _instance = new VLCManager();
        }
        return _instance;
    }

    public static String mergeText(final String vlcText,
                                   final Map<String, Object> contextData) throws Exception {
        final VLCManager instance = getInstance();
        return instance.evaluateText(vlcText, contextData);
    }

    public static String mergeText(final String templateName,
                                   final String vlcText,
                                   final Map<String, Object> contextData) throws Exception {
        final VLCManager instance = getInstance();
        return instance.evaluateText(templateName, vlcText, contextData);
    }

    public static String mergeTemplate(final String templateName,
                                       final Map<String, Object> contextData) throws Exception {
        final VLCManager instance = getInstance();
        return instance.evaluate(templateName, contextData);
    }
}
