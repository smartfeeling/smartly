
package org.smartly.commons.io.repository;

import java.io.File;
import java.io.IOException;

/**
 * Repository represents an abstract container of resources.
 * In addition to resources, repositories may contain other repositories, building
 * a hierarchical structure.
 */
public interface Repository extends Trackable {

    /**
     * String containing file separator characters. Always include slash character,
     * plus the native separator char if it isn't the slash.
     */
    final public static String SEPARATOR = File.separatorChar == '/' ? "/" : File.separator + "/";

    /**
     * Returns a specific direct resource of the repository
     *
     * @param resourceName name of the child resource to return
     * @return specified child resource
     */
    public Resource getResource(String resourceName) throws IOException;

    /**
     * Get a list of resources contained in this repository identified by the
     * given local name.
     *
     * @return a list of all direct child resources
     */
    public Resource[] getResources() throws IOException;

    /**
     * Get a list of resources contained in this repository identified by the
     * given local name.
     *
     * @param recursive whether to include nested resources
     * @return a list of all nested child resources
     */
    public Resource[] getResources(boolean recursive) throws IOException;

    /**
     * Get a list of resources contained in this repository identified by the
     * given local name.
     *
     * @param resourcePath the repository path
     * @param recursive    whether to include nested resources
     * @return a list of all nested child resources
     */
    public Resource[] getResources(String resourcePath, boolean recursive) throws IOException;

    /**
     * Returns this repository's direct child repositories
     *
     * @return direct repositories
     * @throws java.io.IOException an I/O error occurred
     */
    public Repository[] getRepositories() throws IOException;

    /**
     * Get a child repository with the given path
     *
     * @param path the path of the repository
     * @return the child repository
     * @throws java.io.IOException an IOException occurred
     */
    public Repository getChildRepository(String path) throws IOException;

    /**
     * Mark this repository as root repository, disabling any parent access.
     */
    public void setRoot();

    /**
     * Get the path of this repository relative to its root repository.
     *
     * @return the repository path
     */
    public String getRelativePath();

}