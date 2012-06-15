package org.smartly.packages.htmldeployer.impl.compressor;

import compressor.CssCompressor;
import compressor.JavaScriptCompressor;
import old.mozilla.javascript.ErrorReporter;
import old.mozilla.javascript.EvaluatorException;
import org.smartly.Smartly;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.PathUtils;

import java.io.*;

/**
 * User: angelo.geminiani
 */
public class Compressor {

    private static final String ENCODING = Smartly.getCharset();

    private boolean _obfuscate;
    private boolean _preserveAllSemicolons;
    private boolean _disableOptimizations;
    private boolean _verbose;
    private int _linebreakpos;

    public Compressor() {
        _obfuscate = false;
        _preserveAllSemicolons = false;
        _disableOptimizations = false;
        _verbose = false;
        _linebreakpos = -1;
    }

    public boolean isObfuscate() {
        return _obfuscate;
    }

    public void setObfuscate(boolean value) {
        this._obfuscate = value;
    }

    public boolean isPreserveAllSemicolons() {
        return _preserveAllSemicolons;
    }

    public void setPreserveAllSemicolons(boolean value) {
        this._preserveAllSemicolons = value;
    }

    public boolean isDisableOptimizations() {
        return _disableOptimizations;
    }

    public void setDisableOptimizations(boolean value) {
        this._disableOptimizations = value;
    }

    public boolean isVerbose() {
        return _verbose;
    }

    public void setVerbose(boolean value) {
        this._verbose = value;
    }

    public int getLinebreakpos() {
        return _linebreakpos;
    }

    public void setLinebreakpos(int value) {
        this._linebreakpos = value;
    }

    public byte[] compressBytes(final byte[] bytes, final String fileName) throws IOException {
        final String ext = PathUtils.getFilenameExtension(fileName, true);
        final Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        final Writer out = new StringWriter();
        if (".js".equalsIgnoreCase(ext)) {
            this.compressJs(reader, out);
        } else if (".css".equalsIgnoreCase(ext)) {
            this.compressCss(reader, out);
        } else {
            this.compressCss(reader, out);
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

    public void compress(final String text, final String outputFilename) throws IOException {
        final String ext = PathUtils.getFilenameExtension(outputFilename, true);
        final StringReader reader = new StringReader(text);
        Writer out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(outputFilename), ENCODING);
            if (".js".equalsIgnoreCase(ext)) {
                this.compressJs(reader, out);
            } else if (".css".equalsIgnoreCase(ext)) {
                this.compressCss(reader, out);
            } else {
                this.compressCss(reader, out);
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    this.getLogger().log(Level.SEVERE, null, e);
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    this.getLogger().log(Level.SEVERE, null, e);
                }
            }
        }
    }

    public void compressJs(final Reader reader, final Writer out) throws IOException {
        final JavaScriptCompressor compressor = this.getJsCompressor(reader);
        compressor.compress(out, _linebreakpos, _obfuscate, _verbose,
                _preserveAllSemicolons, _disableOptimizations);
    }

    public void compressCss(final Reader reader, final Writer out) throws IOException {
        final CssCompressor compressor = this.getCssCompressor(reader);
        compressor.compress(out, _linebreakpos);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private JavaScriptCompressor getJsCompressor(final Reader reader) throws IOException {
        final Logger logger = this.getLogger();
        JavaScriptCompressor compressor = new JavaScriptCompressor(reader, new ErrorReporter() {

            public void warning(String message, String sourceName,
                                int line, String lineSource, int lineOffset) {
                if (line < 0) {
                    logger.warning("[WARNING] " + message);
                } else {
                    logger.warning("[WARNING] " + line + ':' + lineOffset + ':' + message);
                }
            }

            public void error(String message, String sourceName,
                              int line, String lineSource, int lineOffset) {
                if (line < 0) {
                    logger.severe("[ERROR] " + message);
                } else {
                    logger.severe("[ERROR] " + line + ':' + lineOffset + ':' + message);
                }
            }

            public EvaluatorException runtimeError(String message, String sourceName,
                                                   int line, String lineSource, int lineOffset) {
                error(message, sourceName, line, lineSource, lineOffset);
                return new EvaluatorException(message);
            }
        });
        return compressor;
    }

    private CssCompressor getCssCompressor(final Reader reader) throws IOException {
        final CssCompressor compressor = new CssCompressor(reader);
        return compressor;
    }
}
