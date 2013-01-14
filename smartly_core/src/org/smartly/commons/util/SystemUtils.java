/*
 * 
 */

package org.smartly.commons.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author angelo.geminiani
 */
public abstract class SystemUtils {

    private static Boolean _iswindow = null;
    private static Boolean _islinux = null;
    private static Boolean _ismac = null;

    public static String getOperatingSystem(){
        return System.getProperty("os.name");
    }

    public static String getOSVersion(){
        return System.getProperty("os.version");
    }

    public static String getOSAchitecture(){
        return System.getProperty("os.arch");
    }

    public static boolean isWindows(){
        if(null==_iswindow){
            final String os = getOperatingSystem();
            _iswindow = os.toLowerCase().startsWith("win");
        }
        return _iswindow;
    }

    public static boolean isLinux(){
        if(null==_islinux){
            final String os = getOperatingSystem();
            _islinux = os.toLowerCase().startsWith("linux");
        }
        return _islinux;
    }

    public static boolean isMac(){
        if(null==_ismac){
            final String os = getOperatingSystem();
            _ismac = os.toLowerCase().startsWith("mac");
        }
        return _ismac;
    }

    public enum FileType {

        file("file://"),
        zip("zip://"),
        jar("jar://"),
        tar("tar://"),
        tgz("tgz://"),
        tbz2("tbz2://"),
        gz("gz://"),
        bz2("bz2://"),
        http("http://"),
        https("https://"),
        webdav("webdav://"),
        ftp("ftp://"),
        sftp("sftp://"),
        smb("smb://"),
        tmp("tmp://"),
        res("res://"),
        ram("ram://");
        private final String _value;

        FileType(String value) {
            _value = value;
        }

        @Override
        public String toString() {
            return super.toString();
        }

        public String getValue() {
            return _value;
        }

        /**
         * Retrieve a File System Type value
         * @param path a path. i.e. "http://folder/file.txt"
         * @return
         */
        public static FileType getType(final String path) {
            final FileType[] values = FileType.values();
            for (final FileType sfs : values) {
                final String value = sfs.toString().concat(":");
                if (path.startsWith(value)) {
                    return sfs;
                }
            }
            return null;
        }

        public static String[] getValues() {
            final List<String> result = new ArrayList<String>();
            final FileType[] values = FileType.values();
            for (final FileType sfs : values) {
                result.add(sfs.getValue());
            }
            return result.toArray(new String[result.size()]);
        }
    }

}
