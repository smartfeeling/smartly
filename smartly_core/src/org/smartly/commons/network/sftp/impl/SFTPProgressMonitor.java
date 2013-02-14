package org.smartly.commons.network.sftp.impl;

import com.jcraft.jsch.SftpProgressMonitor;

import javax.swing.*;

/**
 * User: angelo.geminiani
 */
public class SFTPProgressMonitor implements SftpProgressMonitor {

    private long _percent = -1;
    private ProgressMonitor _monitor;
    private long _count = 0;
    private long _max = 0;

    public SFTPProgressMonitor() {

    }

    public void init(int op, String src, String dest, long max) {
        _max = max;
        _monitor = new ProgressMonitor(null,
                ((op == SftpProgressMonitor.PUT) ?
                        "put" : "get") + ": " + src,
                "", 0, (int) max);
        _count = 0;
        _percent = -1;
        _monitor.setProgress((int) this._count);
        _monitor.setMillisToDecideToPopup(1000);
    }

    public boolean count(long count) {
        _count += count;

        if (_percent >= this._count * 100 / _max) {
            return true;
        }
        _percent = this._count * 100 / _max;

        _monitor.setNote("Completed " + this._count + "(" + _percent + "%) out of " + _max + ".");
        _monitor.setProgress((int) this._count);

        return !(_monitor.isCanceled());
    }

    public void end() {
        _monitor.close();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
