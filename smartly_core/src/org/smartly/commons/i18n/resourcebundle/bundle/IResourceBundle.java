/*
 * IResourceBundle.java
 */

package org.smartly.commons.i18n.resourcebundle.bundle;

import java.util.Properties;

/**
 * Common interface for all XPoint resource bundles.
 * All bundles must implement this class. 
 *
 * @author
 */
public interface IResourceBundle {
    
    public abstract boolean isActive();
    
    public abstract String getString(String key);
    
    public abstract Properties getProperties();
    
    public abstract Throwable getError();
    
}
