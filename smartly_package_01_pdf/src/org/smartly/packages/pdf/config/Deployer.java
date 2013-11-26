package org.smartly.packages.pdf.config;


import org.smartly.commons.io.repository.deploy.FileDeployer;

public class Deployer extends FileDeployer {

    public Deployer(final String targetFolder) {
        super("", targetFolder,
                false, false, false);
    }

    @Override
    public byte[] compile(byte[] data, final String filename) {
        return data;
    }

    @Override
    public byte[] compress(byte[] data, final String filename) {
        return null;
    }
}
