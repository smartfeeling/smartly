
package org.smartly.commons.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Resource represents a pointer to some kind of information
 * from which the content can be fetched
 */
public interface Resource extends Trackable {


    /**
     * Returns the length of the resource's content
     * @return content length
     */
    public long getLength();

    /**
     * Returns an input stream to the content of the resource
     * @throws java.io.IOException if a I/O related error occurs
     * @return content input stream
     */
    public InputStream getInputStream() throws IOException;

    /**
     * Returns a reader for the resource using the given character encoding
     * @param encoding the character encoding
     * @return the reader
     * @throws java.io.IOException if a I/O related error occurs
     */
    public Reader getReader(String encoding) throws IOException;

    /**
     * Returns a reader for the resource
     * @return the reader
     * @throws java.io.IOException if a I/O related error occurs
     */
    public Reader getReader() throws IOException;

    /**
     * Returns the content of the resource in array of bytes
     * @return content
     */
    public byte[] getBytes() throws IOException;

    /**
     * Returns the content of the resource in a given encoding
     * @param encoding
     * @return content
     */
    public String getContent(String encoding) throws IOException;

    /**
     * Returns the content of the resource
     * @return content
     */
    public String getContent() throws IOException;

    /**
     * Returns the short name of the resource with the file extension
     * (everything following the last dot character) cut off.
     * @return the file name without the file extension
     */
    public String getBaseName();

    /**
     * Get the path of this resource relative to its root repository.
     * @return the relative resource path
     */
    public String getRelativePath();


    /**
     * Returns true if the input stream for this resource will look for a
     * first line starting with the characters #! and suppress it if found
     * @return true if Hashbang stripping is enabled
     */
    public boolean getStripHashbang();

    /**
     * Switch shebang stripping on or off
     * @param value true to enable Hashbang stripping
     */
    public void setStripHashbang(boolean value);

}
