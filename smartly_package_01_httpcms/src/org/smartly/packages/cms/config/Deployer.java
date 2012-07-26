package org.smartly.packages.cms.config;

import org.smartly.commons.repository.deploy.FileDeployer;

public class Deployer extends FileDeployer {

    public Deployer(final String targetFolder) {
        super("", targetFolder,
                false, false, false);
        super.setOverwrite(true);
    }

    @Override
    public byte[] beforeDeploy(byte[] data, String filename) {
        return data;
    }

    @Override
    public byte[] compress(byte[] data, final String filename) {
        return null;
    }
}

