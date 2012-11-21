package org.smartly.config;


import org.smartly.commons.repository.deploy.FileDeployer;

public class Deployer extends FileDeployer {

    public Deployer(final String targetFolder, final boolean silent) {
        super("", targetFolder,
                silent, false, false, false);
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
