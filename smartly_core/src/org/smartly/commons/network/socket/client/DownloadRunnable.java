package org.smartly.commons.network.socket.client;

import org.smartly.commons.Delegates;
import org.smartly.commons.io.filetokenizer.FileChunkInfo;
import org.smartly.commons.io.filetokenizer.FileTokenizer;
import org.smartly.commons.network.socket.messages.MessageResponse;
import org.smartly.commons.network.socket.messages.UserToken;
import org.smartly.commons.network.socket.messages.multipart.MultipartInfo;
import org.smartly.commons.network.socket.messages.multipart.MultipartMessagePart;
import org.smartly.commons.network.socket.messages.tools.MultipartMessageUtils;
import org.smartly.commons.util.PathUtils;

/**
 *
 */
public class DownloadRunnable
        extends Thread {

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final int _index;
    private final String _transactionId;
    private final Client _client;
    private final UserToken _userToken;
    private final FileChunkInfo _chunkInfo;
    private final boolean _useMultipleConnections;
    private final Delegates.ExceptionCallback _errorHandler;


    private Throwable _error;
    private long _elapsedTime;
    private long _dataLength;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public DownloadRunnable(final int index,
                            final String transactionId,
                            final Client client,
                            final UserToken userToken,
                            final FileChunkInfo chunkInfo,
                            final Delegates.ExceptionCallback errorHandler) {
        _index = index;
        _transactionId = transactionId;
        _client = client;
        _userToken = userToken;
        _chunkInfo = chunkInfo;
        _useMultipleConnections = true;
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
        final int len = _chunkInfo.getChunkCount();
        _dataLength = _chunkInfo.getChunkSize();
        final String source = _userToken.getSourceAbsolutePath();
        final String fileName = PathUtils.getFilename(source, true);
        final String chunkName = FileTokenizer.getChunkName(fileName, _index);
        final MultipartInfo info = new MultipartInfo(
                fileName,
                MultipartInfo.MultipartInfoType.File,
                MultipartInfo.MultipartInfoDirection.Download,
                chunkName,
                _dataLength,
                _index,
                len);

        final MultipartMessagePart part = new MultipartMessagePart();
        part.setUserToken(_userToken.toString());
        part.setInfo(info);
        part.setUid(_transactionId);

        // part.setData();

        //-- send part --//
        final Object response;
        if (_useMultipleConnections) {
            response = Client.send(_client.getAddress(), part);
        } else {
            response = Client.send(_client.getSocket(), part);
        }

        if (response instanceof MessageResponse) {
            final MessageResponse msg = (MessageResponse) response;
            if (msg.getData() instanceof MultipartMessagePart) {
                final MultipartMessagePart response_part = (MultipartMessagePart) msg.getData();
                MultipartMessageUtils.saveOnDisk(response_part);
                _client.addMultipartMessagePart(response_part);
            }
        }
    }

}
