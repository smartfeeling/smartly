package org.smartly.commons.network.socket.client;

import org.smartly.commons.Delegates;
import org.smartly.commons.network.socket.messages.multipart.MultipartInfo;
import org.smartly.commons.network.socket.messages.multipart.MultipartMessagePart;
import org.smartly.commons.util.FileUtils;

import java.io.File;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Runnable Upload job
 */
public class UploadRunnable
        extends Thread {

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final String _transactionId;
    private final SocketAddress _address;
    private final Socket _socket;
    private final String _fileName;
    private final String _userToken;
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

    public UploadRunnable(final String transactionId,
                          final SocketAddress address,
                          final Socket socket,
                          final String filename,
                          final String userToken,
                          final String[] chunks,
                          final boolean useMultipleConnections,
                          final int index,
                          final Delegates.ExceptionCallback errorHandler) {
        _transactionId = transactionId;
        _address = address;
        _socket = socket;
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
    public String toString(){
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append("{");
        sb.append("Index: ").append(_index);
        sb.append(", ");
        sb.append("Error: ").append(this.hasError()?_error:"");
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

        final int len = _chunks.length;
        try {
            final String chunk = _chunks[_index];
            final MultipartInfo info = new MultipartInfo(_fileName,
                    MultipartInfo.MultipartInfoType.File, chunk, _index, len);

            final MultipartMessagePart part = new MultipartMessagePart();
            part.setUserToken(_userToken);
            part.setInfo(info);
            part.setUid(_transactionId);
            part.setData(FileUtils.copyToByteArray(new File(chunk)));

            _dataLength = part.getData().length;

            //-- send part --//
            if (_useMultipleConnections) {
                Client.send(_address, part);
            } else {
                Client.send(_socket, part);
            }
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

    public int getIndex(){
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

    public long getDataLength(){
        return _dataLength;
    }

}
