package org.smartly.commons.io.filetokenizer;


public class FileChunkInfo {

    public static final int MAX_CHUNKS = 500;
    public static final long DEFAULT_CHUNK_SIZE = 1024 * 5 * 1000; //5Mb
    public static final int DEFAULT_CHUNK_COUNT = 0;

    private long _fileSize;
    private long _chunkSize;
    private int _chunkCount;
    private int _maxChunks;


    //-----------------------------------------------
    //             c o n s t r u c t o r
    //-----------------------------------------------

    public FileChunkInfo(long fileSize) throws Exception {
        this(fileSize, DEFAULT_CHUNK_SIZE, DEFAULT_CHUNK_COUNT, MAX_CHUNKS);
    }

    public FileChunkInfo(long fileSize, long chunkSize) throws Exception {
        this(fileSize, chunkSize, DEFAULT_CHUNK_COUNT, MAX_CHUNKS);
    }

    public FileChunkInfo(long fileSize, long chunkSize, int maxChunks) throws Exception {
        this(fileSize, chunkSize, DEFAULT_CHUNK_COUNT, maxChunks);
    }

    public FileChunkInfo(long fileSize, int chunkCount) throws Exception {
        this(fileSize, DEFAULT_CHUNK_SIZE, chunkCount, chunkCount);
    }

    public FileChunkInfo(long fileSize, long chunkSize, int chunkCount, int maxChunks) throws Exception {
        _fileSize = fileSize;
        _chunkSize = chunkSize;
        _maxChunks = maxChunks;
        _chunkCount = chunkCount;
        this.recalculate();
    }

    //-----------------------------------------------
    //             p r o p e r t i e s
    //-----------------------------------------------

    /// <summary>
    /// Size of Chunk.
    /// Last Chunk should be different.
    /// </summary>
    public long getChunkSize() {
        return _chunkSize;
    }

    /// <summary>
    /// Number of Chunks
    /// </summary>
    public int getChunkCount() {
        return _chunkCount;
    }

    //-----------------------------------------------
    //             p r i v a t e
    //-----------------------------------------------

    private void recalculate() throws Exception {
        if (_chunkSize > 0) {
            // calculate by chunk size
            _chunkCount = (int) Math.ceil((double) ((double) _fileSize / (double) _chunkSize));
            if (_chunkCount > _maxChunks) {
                _chunkCount = _maxChunks;
                _chunkSize = (long) Math.ceil((double) ((double) _fileSize / (double) _maxChunks));
            }
        } else if (_chunkCount > 0) {
            // calculate by chunk count
            _chunkSize = (long) Math.floor((double) (_fileSize / _chunkCount));
        }

        // verify
        long check = (_chunkCount * _chunkSize);
        if (_fileSize > check) {
            throw new Exception("Invalid or incoherent parameters!");
        }
    }

}
