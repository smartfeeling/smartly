package org.smartly.packages.sitebuilder.impl.model;

import org.smartly.commons.repository.deploy.FileDeployer;
import org.smartly.commons.util.PathUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ModelDeployer extends FileDeployer {

    private static final Set<String> _ext = new HashSet<String>(Arrays.asList(new String[]{".json"}));

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
    public byte[] beforeDeploy(byte[] data, final String filename) {
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

