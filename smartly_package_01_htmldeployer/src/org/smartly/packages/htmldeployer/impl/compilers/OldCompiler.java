package org.smartly.packages.htmldeployer.impl.compilers;


import org.lesscss.LessCompiler;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.PathUtils;

import java.io.*;

public class OldCompiler {

    public OldCompiler() {

    }

    public byte[] compileBytes(final byte[] bytes, final String fileName) throws Exception {
        final String ext = PathUtils.getFilenameExtension(fileName, true);
        final Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        final Writer out = new StringWriter();
        if (".less".equalsIgnoreCase(ext)) {
            this.compileLess(reader, out);
        } else {
            // unsupported
        }

        final byte[] result = out.toString().getBytes();
        try {
            reader.close();
        } catch (Throwable ignored) {
        }
        try {
            out.close();
        } catch (Throwable ignored) {
        }
        return result;
    }



    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private LessCompiler getLessCompiler() {
        final LessCompiler lessCompiler = new LessCompiler();

        return lessCompiler;
    }

    private void compileLess(final Reader reader, final Writer out) throws Exception {
        final LessCompiler less = this.getLessCompiler();
        final String source = FileUtils.copyToString(reader);
        final String sout = less.compile(source);
        out.write(sout);
    }
}
