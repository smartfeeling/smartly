package org.smartly.commons.io.filetokenizer;

import org.junit.Test;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.PathUtils;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class FileTokenizerTest {

    @Test
    public void testSplitFromChunkSize() throws Exception {

        final String filename = PathUtils.concat(PathUtils.getTemporaryDirectory(), "ARCHIVIO.zip");
        final String[] chunks = FileTokenizer.splitFromChunkSize(filename, 1024 * 5 * 1000, new IFileTokenizerCallback() {
            @Override
            public void onProgress(int index, int count, double progress) {
                System.out.println(FormatUtils.format("Chunking... {0}/{1} {2}%", index, count, progress * 100));
            }
        });

        final String out_filename = PathUtils.concat(PathUtils.getTemporaryDirectory(), "ARCHIVIO_out.zip");
        FileTokenizer.join(chunks, out_filename, new IFileTokenizerCallback() {
            @Override
            public void onProgress(int index, int count, double progress) {
                System.out.println(FormatUtils.format("Composing... {0}/{1} {2}%", index, count, progress * 100));
            }
        });

        assertTrue(FileUtils.exists(out_filename));
        FileUtils.delete(out_filename);
        FileUtils.delete(PathUtils.concat(PathUtils.getTemporaryDirectory(), "TOKENIZER/"));
    }

}
