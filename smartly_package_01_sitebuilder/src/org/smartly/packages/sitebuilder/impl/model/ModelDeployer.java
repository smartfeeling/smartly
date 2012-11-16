package org.smartly.packages.sitebuilder.impl.model;

import org.smartly.commons.repository.deploy.FileDeployer;

public class ModelDeployer extends FileDeployer {


    public ModelDeployer(final String targetFolder) {
        super("", targetFolder,
                false, false, false);
        super.setOverwrite(true);
    }

    public ModelDeployer(final String modelName, final String targetFolder) {
        super(modelName, targetFolder,
                false, false, false);
        super.setOverwrite(true);
    }

    @Override
    public byte[] compile(byte[] data, final String filename) {
        return data;
    }

    @Override
    public byte[] compress(byte[] data, final String filename) {
        return null;
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------


}

