package org.smartly.packages.htmldeployer.impl;


import org.smartly.Smartly;
import org.smartly.commons.jsonrepository.JsonRepository;
import org.smartly.commons.logging.Level;
import org.smartly.commons.repository.deploy.FileDeployer;
import org.smartly.commons.util.ConversionUtils;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.htmldeployer.impl.compressor.Compressor;

public class HtmlDeployer extends FileDeployer {


    public HtmlDeployer(final String startFolder,
                        final String targetFolder,
                        final boolean verbose,
                        final boolean debugApp,
                        final boolean debugJs) {
        super(startFolder, targetFolder, verbose, debugApp, debugJs);
    }

    public HtmlDeployer(final String targetFolder) {
        super("", targetFolder,
                verbose(), debugApp(), debugJs());
    }

    public HtmlDeployer() {
        super("", docRoot(),
                verbose(), debugApp(), debugJs());
    }

    @Override
    public byte[] beforeDeploy(byte[] data, final String filename) {
        return data;
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

    private static String docRoot() {
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

}
