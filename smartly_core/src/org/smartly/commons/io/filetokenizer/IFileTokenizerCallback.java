package org.smartly.commons.io.filetokenizer;


public interface IFileTokenizerCallback {

    public void onProgress(int index, int count, double progress);

}
