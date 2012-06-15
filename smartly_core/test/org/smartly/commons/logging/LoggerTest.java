package org.smartly.commons.logging;

import org.junit.Test;
import org.smartly.commons.logging.util.LoggingUtils;


public class LoggerTest {

    @Test
    public void testLog() throws Exception {

        LoggingRepository.getInstance().setFilePath("z:/_test/LOGTEST/logging.log");
        LoggingRepository.getInstance().setLogFileName(this.getClass(), "TEST-LOG.log");

        final Logger logger = LoggingUtils.getLogger(this);

        logger.log(Level.INFO, "This is a test log");
    }
}
