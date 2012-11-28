package org.smartly.commons.io.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class FileResource extends AbstractResource {

    File file;

    public FileResource(String path) throws IOException {
        this(new File(path), null);
    }

    public FileResource(File file) throws IOException {
        this(file, null);
    }

    protected FileResource(File file, FileRepository repository) throws IOException {
        // make sure our directory has an absolute path,
        // see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4117557
        file = file.getAbsoluteFile();

        repository = repository == null ?
                new FileRepository(file.getParentFile()) : repository;
        // Make sure path is canonical for all directories, while acutal file may be a symlink
        file = new File(repository.getPath(), file.getName());
        path = file.getPath();
        name = file.getName();
        this.file = file;
        this.repository = repository;
        // base name is short name with extension cut off
        int lastDot = name.lastIndexOf(".");
        baseName = (lastDot == -1) ? name : name.substring(0, lastDot);
    }

    public InputStream getInputStream() throws IOException {
        return stripShebang(new FileInputStream(file));
    }

    public URL getUrl() throws MalformedURLException {
        return new URL("file:" + file.getAbsolutePath());
    }

    public long lastModified() {
        return file.lastModified();
    }

    public long getLength() {
        return file.length();
    }

    public boolean exists() {
        // not a resource if it's a directory
        return file.isFile();
    }

    @Override
    public int hashCode() {
        return 17 + path.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FileResource && path.equals(((FileResource)obj).path);
    }

    @Override
    public String toString() {
        return getPath();
    }
}
