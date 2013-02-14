package org.smartly.commons.network.sftp.impl;

import com.jcraft.jsch.UserInfo;

/**
 *
 */
public class SFTPUserInfo implements UserInfo {

    private final String _password;

    public SFTPUserInfo(final String password) {
        _password = password;
    }

    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public String getPassword() {
        return _password;
    }

    @Override
    public boolean promptPassword(String message) {
        return true;
    }

    @Override
    public boolean promptPassphrase(String message) {
        return true;
    }

    @Override
    public boolean promptYesNo(String message) {
        return true;
    }

    @Override
    public void showMessage(String message) {

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
