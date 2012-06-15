/*
 *
 */

package org.smartly.packages.velocity.impl.engine.loaders;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.InputStream;

/**
 *
 * @author angelo.geminiani
 */
public class VLCClasspathLoader extends ClasspathResourceLoader {

    @Override
    public void commonInit(RuntimeServices rs, ExtendedProperties configuration) {
        super.commonInit(rs, configuration);
    }

    @Override
    public void init(ExtendedProperties configuration) {
        super.init(configuration);
    }

    @Override
    public InputStream getResourceStream(String name) throws ResourceNotFoundException {
        return super.getResourceStream(name);
    }

    @Override
    public boolean resourceExists(String resourceName) {
        return super.resourceExists(resourceName);
    }

    

}
