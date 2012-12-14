package org.smartly.packages.velocity.impl.compilers;

import org.smartly.Smartly;
import org.smartly.commons.lang.compilers.ICompiler;
import org.smartly.commons.util.BeanUtils;
import org.smartly.packages.velocity.impl.VLCManager;

import java.util.Map;

/**
 * Velocity compiler implementing standard Smarty ICompiler interface
 */
public class CompilerVelocity implements ICompiler {

    public static final String ARG_FILE = "file";

    public CompilerVelocity() {

    }

    @Override
    public byte[] compile(byte[] data) throws Exception {
        return compile(data, null);
    }

    @Override
    public byte[] compile(byte[] data, final Map<String, Object> args) throws Exception {
        final String input = new String(data, Smartly.getCharset());
        final String filename = (String) BeanUtils.getValueIfAny(args, ARG_FILE, "UNDEFINED");
        final String output = VLCManager.getInstance().evaluateText(filename, input, args);
        return output.getBytes();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
