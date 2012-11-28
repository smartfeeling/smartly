package org.smartly.packages.htmldeployer.impl;


import org.smartly.Smartly;
import org.smartly.commons.io.jsonrepository.JsonRepository;
import org.smartly.commons.io.repository.deploy.FileDeployer;
import org.smartly.commons.logging.Level;
import org.smartly.commons.util.ConversionUtils;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.htmldeployer.impl.compiler.Compiler;
import org.smartly.packages.htmldeployer.impl.compressor.Compressor;

public class HtmlDeployer extends FileDeployer {


    public HtmlDeployer(final String startFolder,
                        final String targetFolder,
                        final boolean verbose,
                        final boolean debugApp,
                        final boolean debugJs) {
        super(startFolder, targetFolder, verbose, debugApp, debugJs);
        this.init();
    }

    public HtmlDeployer(final String targetFolder) {
        super("", targetFolder,
                verbose(), debugApp(), debugJs());
        this.init();
    }

    public HtmlDeployer() {
        super("", docRoot(),
                verbose(), debugApp(), debugJs());
        this.init();
    }

    @Override
    public byte[] compile(byte[] data, final String filename) {
        try {
            final Compiler compiler = new Compiler();
            return compiler.compileBytes(data, filename);
        } catch (Throwable t) {
            super.getLogger().log(Level.SEVERE,
                    FormatUtils.format("ERROR COMPILING '{0}': {1}", filename, t), t);
        }
        return null;
    }

    @Override
    public final byte[] compress(byte[] data, final String filename) {
        try {
            final Compressor compressor = new Compressor();
            return compressor.compressBytes(data, filename);
        } catch (Throwable t) {
            super.getLogger().log(Level.SEVERE,
                    FormatUtils.format("ERROR COMPRESSING '{0}': {1}", filename, t), t);
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        // pre-process
        FileDeployer.getPreProcessorFiles().add(".less");
        FileDeployer.getPreProcessorFiles().add(".js");
        FileDeployer.getPreProcessorFiles().add(".css");
        FileDeployer.getPreProcessorFiles().add(".vm");
        // compile
        FileDeployer.getCompileFiles().put(".less", ".css");
        // compress
        FileDeployer.getCompressFiles().add(".js");
        FileDeployer.getCompressFiles().add(".css");
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static JsonRepository getConfiguration() {
        try {
            return Smartly.getConfiguration(true);
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static boolean verbose() {
        final JsonRepository config = getConfiguration();
        if (null != config) {
            return ConversionUtils.toBoolean(config.get("htmldeployer.verbose"));
        }
        return false;
    }

    private static boolean debugApp() {
        final JsonRepository config = getConfiguration();
        if (null != config) {
            return ConversionUtils.toBoolean(config.get("htmldeployer.debugApp"));
        }
        return false;
    }

    private static boolean debugJs() {
        final JsonRepository config = getConfiguration();
        if (null != config) {
            return ConversionUtils.toBoolean(config.get("htmldeployer.debugJs"));
        }
        return false;
    }

    public static String docRoot() {
        final JsonRepository config = getConfiguration();
        if (null != config) {
            final String path = config.getString("http.webserver.root");
            if (!StringUtils.hasText(path)) {
                return null;
            }
            return Smartly.getAbsolutePath(path);
        }
        return "";
    }

    public static String docRoot(final String subFolder) {
        return PathUtils.merge(docRoot(), subFolder);
    }

}
