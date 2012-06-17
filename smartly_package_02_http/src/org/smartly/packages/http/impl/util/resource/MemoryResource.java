package org.smartly.packages.http.impl.util.resource;


import org.eclipse.jetty.util.resource.Resource;
import org.smartly.commons.util.DateUtils;
import org.smartly.commons.util.FileUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class MemoryResource extends Resource {

    private final String _name;
    private final byte[] _content;

    public MemoryResource(final String name, final byte[] content) {
        _name = name;
        _content = content;
    }

    @Override
    public boolean isContainedIn(Resource r) throws MalformedURLException {
        return false;
    }

    @Override
    public void release() {

    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public long lastModified() {
        return DateUtils.now().getTime();
    }

    @Override
    public long length() {
        return _content.length;
    }

    @Override
    public URL getURL() {
        return null;
    }

    @Override
    public File getFile() throws IOException {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(_content);
    }

    @Override
    public OutputStream getOutputStream() throws IOException, SecurityException {
        return null;
    }

    @Override
    public boolean delete() throws SecurityException {
        return false;
    }

    @Override
    public boolean renameTo(Resource dest) throws SecurityException {
        return false;
    }

    @Override
    public String[] list() {
        return new String[0];
    }

    @Override
    public Resource addPath(String path) throws IOException, MalformedURLException {
        return null;
    }

}
