package org.smartly.commons.network.socket.client;

import org.smartly.commons.Delegates;
import org.smartly.commons.network.socket.messages.UserToken;
import org.smartly.commons.network.socket.messages.multipart.MultipartInfo;
import org.smartly.commons.network.socket.messages.multipart.MultipartMessagePart;
import org.smartly.commons.util.FileUtils;

import java.io.File;

/**
 * Runnable Upload job
 */
public class UploadRunnable
        extends Thread {

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final String _transactionId;
    private final Client _client;
    private final String _fileName;
    private final UserToken _userToken;
    private final String[] _chunks;
    private final boolean _useMultipleConnections;
    private final int _index;
    private final Delegates.ExceptionCallback _errorHandler;

    private Throwable _error;
    private long _elapsedTime;
    private long _dataLength;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public UploadRunnable(final int index,
                          final String transactionId,
                          final Client client,
                          final String filename,
                          final UserToken userToken,
                          final String[] chunks,
                          final boolean useMultipleConnections,
                          final Delegates.ExceptionCallback errorHandler) {
        _transactionId = transactionId;
        _client = client;
        _fileName = filename;
        _userToken = userToken;
        _chunks = chunks;
        _useMultipleConnections = useMultipleConnections;
        _index = index;

        _errorHandler = errorHandler;
    }

    // --------------------------------------------------------------------
    //               o v e r r i d e
    // --------------------------------------------------------------------

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append("{");
        sb.append("Index: ").append(_index);
        sb.append(", ");
        sb.append("Error: ").append(this.hasError() ? _error : "");
        sb.append(", ");
        sb.append("ElapsedTime: ").append(_elapsedTime);
        sb.append(", ");
        sb.append("DataLength: ").append(_dataLength);
        sb.append("}");
        return sb.toString();
    }

    @Override
    public void run() {
        _elapsedTime = System.currentTimeMillis();
        try {
            this.runTask();
        } catch (Throwable t) {
            if (null != _errorHandler) {
                _errorHandler.handle(null, t);
            }
            _error = t;
        }
        _elapsedTime = System.currentTimeMillis() - _elapsedTime;
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public int getIndex() {
        return _index;
    }

    public boolean hasError() {
        return null != _error;
    }

    public Throwable getError() {
        return _error;
    }

    public long getElapsedTime() {
        return _elapsedTime;
    }

    public long getDataLength() {
        return _dataLength;
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private void runTask() throws Exception {
        final int len = _chunks.length;
        final String chunk = _chunks[_index];
        final MultipartInfo info = new MultipartInfo(
                _fileName,
                MultipartInfo.MultipartInfoType.File,
                MultipartInfo.MultipartInfoDirection.Upload,
                chunk,
                0,
                _index,
                len);

        final MultipartMessagePart part = new MultipartMessagePart();
        part.setUserToken(_userToken.toString());
        part.setInfo(info);
        part.setUid(_transactionId);
        part.setData(FileUtils.copyToByteArray(new File(chunk)));

        _dataLength = part.getDataLength();

        //-- send part --//
        if (_useMultipleConnections) {
            Client.send(_client.getAddress(), part);
        } else {
            Client.send(_client.getSocket(), part);
        }
    }


}
