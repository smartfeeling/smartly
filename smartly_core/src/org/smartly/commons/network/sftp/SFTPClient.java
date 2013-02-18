package org.smartly.commons.network.sftp;


import com.jcraft.jsch.*;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.network.sftp.impl.SFTPProgressMonitor;
import org.smartly.commons.network.sftp.impl.SFTPUserInfo;
import org.smartly.commons.util.StringUtils;

import java.io.File;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * SFTP client implementation.
 */
public class SFTPClient {

    private final JSch _jsch;

    private String _username;
    private String _password;
    private String _host;
    private int _port;

    private Session _session;
    private ChannelSftp _channel;

    public SFTPClient() {
        _jsch = new JSch();
    }

    public SFTPClient(final String host, final int port) {
        this();
        _host = host;
        _port = port;
    }

    public SFTPClient(final String host, final int port, final String username, final String password) {
        this();
        _host = host;
        _port = port;
        _username = username;
        _password = password;
    }

    public String getUsername() {
        return _username;
    }

    public void setUsername(final String username) {
        this._username = username;
    }

    public String getPassword() {
        return _password;
    }

    public void setPassword(final String password) {
        this._password = password;
    }

    public String getHost() {
        return _host;
    }

    public void setHost(final String host) {
        this._host = host;
    }

    public int getPort() {
        return _port;
    }

    public void setPort(final int port) {
        this._port = port;
    }

    public boolean isConnected() {
        return null != _session
                && _session.isConnected()
                && null != _channel
                && _channel.isConnected();
    }

    public void disconnect() {
        if (this.isConnected()) {
            _session.disconnect();
            _channel.disconnect();
        }
    }

    public SFTPClient connect() throws Exception {
        // disconnect if already connected
        this.disconnect();
        // connect
        if (StringUtils.hasText(_host) && _port > 0) {
            final SFTPUserInfo ui = new SFTPUserInfo(_password);
            _session = this.getSession();
            _session.setUserInfo(ui);
            _session.connect();

            _channel = (ChannelSftp) _session.openChannel("sftp");
            _channel.connect();
        }
        return this;
    }

    // --------------------------------------------------------------------
    //               c o m m a n d s
    // --------------------------------------------------------------------

    public Set<String> list() {
        return this.list(null);
    }

    /**
     * Returns current Remote directory
     *
     * @return current Remote dir
     */
    public String pwd() {
        try {
            return _channel.pwd();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns current Local directory
     *
     * @return current Local dir
     */
    public String lpwd() {
        return _channel.lpwd();
    }

    /**
     * List remote path
     *
     * @param opt_path
     * @return
     */
    public Set<String> list(final String opt_path) {
        final Set<String> result = new HashSet<String>();
        final String path = StringUtils.hasText(opt_path) ? opt_path : ".";
        try {
            final Vector vv = _channel.ls(path);
            if (vv != null) {
                for (int ii = 0; ii < vv.size(); ii++) {
                    Object obj = vv.elementAt(ii);
                    if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
                        result.add(((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getFilename());
                    }
                }
            }
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, null, e);
        }
        return result;
    }

    /**
     * List local path
     *
     * @param opt_path
     * @return
     */
    public Set<String> llist(final String opt_path) {
        final Set<String> result = new HashSet<String>();
        final String path = StringUtils.hasText(opt_path) ? opt_path : ".";
        try {
            final File file = new File(path);
            if (file.exists() && file.isDirectory()) {
                String[] list = file.list();
                for (int ii = 0; ii < list.length; ii++) {
                    result.add(list[ii]);
                }
            }
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, null, e);
        }
        return result;
    }

    /**
     * Change Remote directory
     *
     * @param path
     */
    public void cd(final String path) {
        if (!StringUtils.hasText(path)) return;
        try {
            _channel.cd(path);
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, null, e);
        }
    }

    /**
     * Change Local directory
     *
     * @param path
     */
    public void lcd(final String path) {
        if (!StringUtils.hasText(path)) return;
        try {
            _channel.lcd(path);
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, null, e);
        }
    }

    /**
     *
     * @param p1 Source
     * @param opt_p2 Destination
     * @return
     */
    public File get(final String p1, final String opt_p2) {
        final String p2 = StringUtils.hasText(opt_p2) ? opt_p2 : ".";
        try {
            final int mode = ChannelSftp.OVERWRITE;
            _channel.get(p1, p2, new SFTPProgressMonitor(), mode);
            return this.toFile(opt_p2);
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, null, e);
        }
        return null;
    }

    public File getResume(final String p1, final String opt_p2) {
        final String p2 = StringUtils.hasText(opt_p2) ? opt_p2 : ".";
        try {
            final int mode = ChannelSftp.RESUME;
            _channel.get(p1, p2, new SFTPProgressMonitor(), mode);
            return this.toFile(opt_p2);
        } catch (SftpException e) {
            this.getLogger().log(Level.SEVERE, null, e);
        }
        return null;
    }

    public File getAppend(final String p1, final String opt_p2) {
        final String p2 = StringUtils.hasText(opt_p2) ? opt_p2 : ".";
        try {
            final int mode = ChannelSftp.APPEND;
            _channel.get(p1, p2, new SFTPProgressMonitor(), mode);
            return this.toFile(opt_p2);
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, null, e);
        }
        return null;
    }

    public void put(final String p1, final String opt_p2) {
        final String p2 = StringUtils.hasText(opt_p2) ? opt_p2 : ".";
        try {
            final int mode = ChannelSftp.OVERWRITE;
            _channel.put(p1, p2, new SFTPProgressMonitor(), mode);
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, null, e);
        }
    }

    public void putResume(final String p1, final String opt_p2) {
        final String p2 = StringUtils.hasText(opt_p2) ? opt_p2 : ".";
        try {
            final int mode = ChannelSftp.RESUME;
            _channel.put(p1, p2, new SFTPProgressMonitor(), mode);
        } catch (SftpException e) {
            this.getLogger().log(Level.SEVERE, null, e);
        }
    }

    public void putAppend(final String p1, final String opt_p2) {
        final String p2 = StringUtils.hasText(opt_p2) ? opt_p2 : ".";
        try {
            final int mode = ChannelSftp.APPEND;
            _channel.put(p1, p2, new SFTPProgressMonitor(), mode);
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, null, e);
        }
    }

    /**
     * Remove file
     *
     * @param path
     * @throws Exception
     */
    public void rm(final String path) throws Exception {
        if (StringUtils.hasText(path)) {
            _channel.rm(path);
        }
    }

    /**
     * Remove directory
     *
     * @param path
     * @throws Exception
     */
    public void rmdir(final String path) throws Exception {
        if (StringUtils.hasText(path)) {
            _channel.rmdir(path);
        }
    }

    /**
     * Create directory
     *
     * @param path
     * @throws Exception
     */
    public void mkdir(final String path) throws Exception {
        if (StringUtils.hasText(path)) {
            _channel.mkdir(path);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private Session getSession() throws JSchException {
        return _jsch.getSession(_username, _host, _port);
    }

    private File toFile(final String path) {
        final File result = new File(path);
        return result.exists() ? result : null;
    }

    private void parseCommand(final String cmd, final Vector<String> cmds) throws Exception {
        int level = 0;
        String str = "";
        PrintStream out = System.out;
        if (cmd.equals("quit")) {
            _channel.quit();
            return;
        }
        if (cmd.equals("exit")) {
            _channel.exit();
            return;
        }
        if (cmd.equals("rekey")) {
            _session.rekey();
        }
        if (cmd.equals("compression")) {
            if (cmds.size() < 2) {
                out.println("compression level: " + level);
                return;
            }
            try {
                level = Integer.parseInt((String) cmds.elementAt(1));
                if (level == 0) {
                    _session.setConfig("compression.s2c", "none");
                    _session.setConfig("compression.c2s", "none");
                } else {
                    _session.setConfig("compression.s2c", "zlib@openssh.com,zlib,none");
                    _session.setConfig("compression.c2s", "zlib@openssh.com,zlib,none");
                }
            } catch (Exception e) {
            }
            _session.rekey();
            return;
        }
        if (cmd.equals("cd") || cmd.equals("lcd")) {
            if (cmds.size() < 2) return;
            String path = (String) cmds.elementAt(1);
            try {
                if (cmd.equals("cd")) _channel.cd(path);
                else _channel.lcd(path);
            } catch (SftpException e) {
                this.getLogger().log(Level.SEVERE, null, e);
            }
            return;
        }
        if (cmd.equals("rm") || cmd.equals("rmdir") || cmd.equals("mkdir")) {
            if (cmds.size() < 2) return;
            String path = (String) cmds.elementAt(1);
            try {
                if (cmd.equals("rm"))
                    _channel.rm(path);
                else if (cmd.equals("rmdir"))
                    _channel.rmdir(path);
                else
                    _channel.mkdir(path);
            } catch (SftpException e) {
                System.out.println(e.toString());
            }
            return;
        }
        if (cmd.equals("chgrp") || cmd.equals("chown") || cmd.equals("chmod")) {
            if (cmds.size() != 3) return;
            String path = (String) cmds.elementAt(2);
            int foo = 0;
            if (cmd.equals("chmod")) {
                byte[] bar = ((String) cmds.elementAt(1)).getBytes();
                int k;
                for (int j = 0; j < bar.length; j++) {
                    k = bar[j];
                    if (k < '0' || k > '7') {
                        foo = -1;
                        break;
                    }
                    foo <<= 3;
                    foo |= (k - '0');
                }
                if (foo == -1) return;
            } else {
                try {
                    foo = Integer.parseInt((String) cmds.elementAt(1));
                } catch (Exception e) {
                    return;
                }
            }
            try {
                if (cmd.equals("chgrp")) {
                    _channel.chgrp(foo, path);
                } else if (cmd.equals("chown")) {
                    _channel.chown(foo, path);
                } else if (cmd.equals("chmod")) {
                    _channel.chmod(foo, path);
                }
            } catch (SftpException e) {
                System.out.println(e.toString());
            }
            return;
        }
        if (cmd.equals("pwd") || cmd.equals("lpwd")) {
            str = (cmd.equals("pwd") ? "Remote" : "Local");
            str += " working directory: ";
            if (cmd.equals("pwd")) str += _channel.pwd();
            else str += _channel.lpwd();
            out.println(str);
            return;
        }

        if (cmd.equals("ls") || cmd.equals("dir")) {
            String path = ".";
            if (cmds.size() == 2) path = (String) cmds.elementAt(1);
            try {
                java.util.Vector vv = _channel.ls(path);
                if (vv != null) {
                    for (int ii = 0; ii < vv.size(); ii++) {
//		out.println(vv.elementAt(ii).toString());

                        Object obj = vv.elementAt(ii);
                        if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
                            out.println(((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getLongname());
                        }

                    }
                }
            } catch (SftpException e) {
                System.out.println(e.toString());
            }
            return;
        }

        if (cmd.equals("lls") || cmd.equals("ldir")) {
            String path = ".";
            if (cmds.size() == 2) path = (String) cmds.elementAt(1);
            try {
                java.io.File file = new java.io.File(path);
                if (!file.exists()) {
                    out.println(path + ": No such file or directory");
                    return;
                }
                if (file.isDirectory()) {
                    String[] list = file.list();
                    for (int ii = 0; ii < list.length; ii++) {
                        out.println(list[ii]);
                    }
                    return;
                }
                out.println(path);
            } catch (Exception e) {
                System.out.println(e);
            }
            return;
        }
        if (cmd.equals("get") ||
                cmd.equals("get-resume") || cmd.equals("get-append") ||
                cmd.equals("put") ||
                cmd.equals("put-resume") || cmd.equals("put-append")
                ) {
            if (cmds.size() != 2 && cmds.size() != 3) return;
            String p1 = (String) cmds.elementAt(1);
//	  String p2=p1;
            String p2 = ".";
            if (cmds.size() == 3) p2 = (String) cmds.elementAt(2);
            try {
                final SftpProgressMonitor monitor = new SFTPProgressMonitor();
                if (cmd.startsWith("get")) {
                    int mode = ChannelSftp.OVERWRITE;
                    if (cmd.equals("get-resume")) {
                        mode = ChannelSftp.RESUME;
                    } else if (cmd.equals("get-append")) {
                        mode = ChannelSftp.APPEND;
                    }
                    _channel.get(p1, p2, monitor, mode);
                } else {
                    int mode = ChannelSftp.OVERWRITE;
                    if (cmd.equals("put-resume")) {
                        mode = ChannelSftp.RESUME;
                    } else if (cmd.equals("put-append")) {
                        mode = ChannelSftp.APPEND;
                    }
                    _channel.put(p1, p2, monitor, mode);
                }
            } catch (SftpException e) {
                System.out.println(e.toString());
            }
            return;
        }
        if (cmd.equals("ln") || cmd.equals("symlink") || cmd.equals("rename")) {
            if (cmds.size() != 3) return;
            String p1 = (String) cmds.elementAt(1);
            String p2 = (String) cmds.elementAt(2);
            try {
                if (cmd.equals("rename")) _channel.rename(p1, p2);
                else _channel.symlink(p1, p2);
            } catch (SftpException e) {
                System.out.println(e.toString());
            }
            return;
        }
        if (cmd.equals("stat") || cmd.equals("lstat")) {
            if (cmds.size() != 2) return;
            String p1 = (String) cmds.elementAt(1);
            SftpATTRS attrs = null;
            try {
                if (cmd.equals("stat")) attrs = _channel.stat(p1);
                else attrs = _channel.lstat(p1);
            } catch (SftpException e) {
                System.out.println(e.toString());
            }
            if (attrs != null) {
                out.println(attrs);
            } else {
            }
            return;
        }
        if (cmd.equals("readlink")) {
            if (cmds.size() != 2) return;
            String p1 = (String) cmds.elementAt(1);
            String filename = null;
            try {
                filename = _channel.readlink(p1);
                out.println(filename);
            } catch (SftpException e) {
                System.out.println(e.toString());
            }
            return;
        }
        if (cmd.equals("realpath")) {
            if (cmds.size() != 2) return;
            String p1 = (String) cmds.elementAt(1);
            String filename = null;
            try {
                filename = _channel.realpath(p1);
                out.println(filename);
            } catch (SftpException e) {
                System.out.println(e.toString());
            }
        }
        if (cmd.equals("version")) {
            out.println("SFTP protocol version " + _channel.version());
        }
    }

    private static String help =
            "      Available commands:\n" +
                    "      * means unimplemented command.\n" +
                    "cd path                       Change remote directory to 'path'\n" +
                    "lcd path                      Change local directory to 'path'\n" +
                    "chgrp grp path                Change group of file 'path' to 'grp'\n" +
                    "chmod mode path               Change permissions of file 'path' to 'mode'\n" +
                    "chown own path                Change owner of file 'path' to 'own'\n" +
                    "help                          Display this help text\n" +
                    "get remote-path [local-path]  Download file\n" +
                    "get-resume remote-path [local-path]  Resume to download file.\n" +
                    "get-append remote-path [local-path]  Append remote file to local file\n" +
                    "*lls [ls-options [path]]      Display local directory listing\n" +
                    "ln oldpath newpath            Symlink remote file\n" +
                    "*lmkdir path                  Create local directory\n" +
                    "lpwd                          Print local working directory\n" +
                    "ls [path]                     Display remote directory listing\n" +
                    "*lumask umask                 Set local umask to 'umask'\n" +
                    "mkdir path                    Create remote directory\n" +
                    "put local-path [remote-path]  Upload file\n" +
                    "put-resume local-path [remote-path]  Resume to upload file\n" +
                    "put-append local-path [remote-path]  Append local file to remote file.\n" +
                    "pwd                           Display remote working directory\n" +
                    "stat path                     Display info about path\n" +
                    "exit                          Quit sftp\n" +
                    "quit                          Quit sftp\n" +
                    "rename oldpath newpath        Rename remote file\n" +
                    "rmdir path                    Remove remote directory\n" +
                    "rm path                       Delete remote file\n" +
                    "symlink oldpath newpath       Symlink remote file\n" +
                    "readlink path                 Check the target of a symbolic link\n" +
                    "realpath path                 Canonicalize the path\n" +
                    "rekey                         Key re-exchanging\n" +
                    "compression level             Packet compression will be enabled\n" +
                    "version                       Show SFTP version\n" +
                    "?                             Synonym for help";
}
