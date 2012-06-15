/*
 * 
 */

package org.smartly.packages.velocity.impl.engine.loaders;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

import java.io.InputStream;

/**
 *
 * @author angelo.geminiani
 */
public class VLCFileLoader extends FileResourceLoader {

    @Override
    public boolean resourceExists(String name) {
        return super.resourceExists(name);
    }

    @Override
    public InputStream getResourceStream(String templateName) throws ResourceNotFoundException {
        return super.getResourceStream(templateName);
    }

    @Override
    public void commonInit(RuntimeServices rs, ExtendedProperties configuration) {
        super.commonInit(rs, configuration);
    }

    @Override
    public void init(ExtendedProperties configuration) {
        super.init(configuration);
    }

    

}
