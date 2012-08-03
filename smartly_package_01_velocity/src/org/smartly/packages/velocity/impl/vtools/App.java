package org.smartly.packages.velocity.impl.vtools;


import org.smartly.Smartly;
import org.smartly.commons.jsonrepository.JsonRepository;
import org.smartly.packages.velocity.impl.vtools.IVLCTool;

public class App implements IVLCTool {

    public static final String NAME = "app";

    @Override
    public String getName() {
        return NAME;
    }

    public JsonRepository getConfiguration() {
        return Smartly.getConfiguration();
    }

    public String[] getLangArray() {
        return Smartly.getLanguages();
    }

    public boolean isDebug() {
        return Smartly.isDebugMode();
    }

    public String getLang() {
        return Smartly.getLang();
    }

}
