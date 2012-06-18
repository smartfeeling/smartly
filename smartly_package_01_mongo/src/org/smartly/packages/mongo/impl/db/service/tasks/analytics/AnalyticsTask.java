package org.smartly.packages.mongo.impl.db.service.tasks.analytics;

import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.packages.mongo.impl.db.service.MongoAnalyticsService;

/**
 * User: angelo.geminiani
 */
public class AnalyticsTask implements Runnable {

    private final AnalyticsData _data;

    public AnalyticsTask(final AnalyticsData data) {
        _data = data;
    }

    @Override
    public void run() {
        try {
            MongoAnalyticsService.insertNew(_data);
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static void insertNew(final String jsondata) {
        final AnalyticsData data = new AnalyticsData(jsondata);
        AnalyticsTask.insertNew(data);
    }

    public static void insertNew(final AnalyticsData data) {
        final Runnable runnable = new AnalyticsTask(data);
        final Thread thread = new Thread(runnable);
        thread.start();
    }
}
