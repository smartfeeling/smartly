package org.smartly.commons.io;

import java.io.InputStream;

/**
 * Simple data model to hold file meta info and content.
 */
public class FileMeta {

    private String _fileName;
    private long _fileSize;
    private String _fileType;

    private InputStream _content;

    public String getFileName() {
        return _fileName;
    }

    public void setFileName(String fileName) {
        this._fileName = fileName;
    }

    public long getFileSize() {
        return _fileSize;
    }

    public void setFileSize(long fileSize) {
        _fileSize = fileSize;
    }

    public String getFileType() {
        return _fileType;
    }

    public void setFileType(String fileType) {
        this._fileType = fileType;
    }

    public InputStream getContent() {
        return _content;
    }

    public void setContent(InputStream content) {
        _content = content;
    }


}
