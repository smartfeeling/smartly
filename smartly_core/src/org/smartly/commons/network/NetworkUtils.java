/*
 * 
 */
package org.smartly.commons.network;

import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.util.LoggingUtils;

import java.net.*;
import java.rmi.dgc.VMID;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * @author
 */
public class NetworkUtils {

    public static final String IP_WILDCHAR = "*";

    public static boolean ipMatch(final String address1, final String address2) {
        if (address1.equals(address2)) {
            return true;
        }
        final String[] masterIp;
        final String[] childIp;
        if (address1.contains(IP_WILDCHAR)) {
            masterIp = address1.split("\\.");
            childIp = address2.split("\\.");
        } else if (address2.contains(IP_WILDCHAR)) {
            masterIp = address2.split("\\.");
            childIp = address1.split("\\.");
        } else {
            masterIp = address1.split("\\.");
            childIp = address2.split("\\.");
        }

        return ipMatch(masterIp, childIp);
    }

    /**
     * Compare two ip addresses.<br>
     * Returns a negative value if address1 is lower than address2<br>
     * Returns zero if equals<br>
     * Returns a positive value if address1 is greater than address2.
     *
     * @param address1 an ip address
     * @param address2 an ip address
     * @return Returns a negative value if address1 is lower than address2<br>
     *         Returns zero if equals<br>
     *         Returns a positive value if address1 is greater than address2.
     */
    public static int ipCompare(final String address1, final String address2) {
        final String[] masterIp = address1.split("\\.");
        final String[] childIp = address2.split("\\.");

        return ipCompare(masterIp, childIp);
    }

    /**
     * Return tru if 'ipAddress' is a range between 'maxAddress' and 'minAddress'
     *
     * @param ipAddress  IP to check
     * @param maxAddress Left range item
     * @param minAddress Right range item
     * @return Boolean
     */
    public static boolean ipInRange(final String ipAddress,
                                    final String maxAddress, final String minAddress) {
        final String[] ip = ipAddress.split("\\.");
        final String[] maxIp = maxAddress.split("\\.");
        final String[] minIp = minAddress.split("\\.");

        final int check1 = ipCompare(maxIp, ip);   // >0 (fromIp>ip)
        final int check2 = ipCompare(ip, minIp);     // >0 (ip>toIp)
        if (check1 >= 0 && check2 >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public static String getHost() {
        String result = null;
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
            result = inetAddress.toString();
        } catch (Exception ex) {
            result = "localhost/127.0.0.1";
        }
        return result;
    }

    public static String getHostIP() {
        String result = null;
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
            result = inetAddress.getHostAddress();
        } catch (Exception ex) {
            result = "127.0.0.1";
        }
        return result;
    }

    public static String getHostName() {
        String result = null;
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
            result = inetAddress.getHostName();
        } catch (Exception ex) {
            result = "localhost";
        }
        return result;
    }

    /**
     * Get Virtual Machine root id. (persistent)
     */
    public static String getHostVirtualMachineId() {
        VMID vmid = new VMID();
        String[] tokens = vmid.toString().split(":");
        if (tokens.length > 0) {
            return tokens[0];
        } else {
            return vmid.toString();
        }
    }

    /**
     * Get a persistent ID of host. <br>
     * This combine network address with VM id.
     */
    public static String getHostIdentifier() {
        StringBuilder result = new StringBuilder();
        result.append(getHost());
        result.append("/");
        result.append(getHostVirtualMachineId());

        return result.toString();
    }

    /**
     * Get a persistent ID of host, encoded. <br>
     * This combine network address with VM id.
     */
    public static String getHostIdentifierEncoded() {
        String hi = getHostIdentifier();
        StringBuilder result = new StringBuilder();
        char[] hiChars = hi.toCharArray();
        for (char c : hiChars) {
            if (c == '/') {
                result.append("-");
            } else {
                result.append(Integer.toHexString((int) c));
            }
        }

        return result.toString().toUpperCase();
    }

    /**
     * Get a random UUID. (volatile)
     */
    public static String getRandomUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * Get Unique Virtual Machine Id. (volatile)
     */
    public static String getVMID() {
        VMID vmid = new VMID();
        return vmid.toString();
    }

    public static Proxy getProxy() {
        return getDefaultProxy();
    }

    public static Proxy getProxy(final String url, final int port, final String user, final String password) {
        return getDefaultProxy();
    }

    public static boolean ping(final String host, final int timeout) {
        try {
            final InetAddress address = InetAddress.getByName(host);
            return address.isReachable(timeout);
        } catch (Throwable ignored) {
        }
        return false;
    }

    public static boolean hasNetworkAccess() {
        try {
            final InetAddress address = InetAddress.getByName("www.google.com");
            return true;
        } catch (Throwable ignored) {
        }
        return false;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private static boolean ipMatch(final String[] masterIp,
                                   final String[] childIp) {
        if (masterIp.length <= childIp.length) {
            for (int i = 0; i < masterIp.length; i++) {
                final String master = masterIp[i];
                final String child = childIp[i];
                if (!master.equals(IP_WILDCHAR)) {
                    if (!master.equals(child)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static int ipCompare(final String[] masterIp,
                                 final String[] childIp) {
        if (ipMatch(masterIp, childIp)) {
            return 0;
        }
        //-- Compare only if 2 addresses has same length --//
        if (masterIp.length == childIp.length) {
            for (int i = 0; i < masterIp.length; i++) {
                final String master = masterIp[i];
                final String child = childIp[i];
                if (!master.equals(IP_WILDCHAR)) {
                    final Integer im = Integer.parseInt(master);
                    final Integer ic = Integer.parseInt(child);
                    if (im != ic) {
                        return im.compareTo(ic);
                    }
                }
            }
        } else if (masterIp.length > childIp.length) {
            return 1;
        }

        return -1; // masterIp.length < childIp.length
    }

    private static Proxy getDefaultProxy() {
        Proxy result = Proxy.NO_PROXY;

        System.setProperty("java.net.useSystemProxies", "true"); // this should be called in main method of stand alone application

        List list = null;
        try {
            list = ProxySelector.getDefault().select(new URI("http://foo/bar"));
        } catch (URISyntaxException e) {
            LoggingUtils.getLogger().log(Level.SEVERE, null, e);
        }
        if (list != null) {
            final Iterator iter = list.iterator();
            while (iter.hasNext()) {
                final Proxy proxy = (Proxy) iter.next();
                // System.out.println("proxy hostname : " + proxy.type());
                final InetSocketAddress addr = (InetSocketAddress) proxy.address();
                if (addr == null) {
                    // System.out.println("No Proxy");
                } else {
                    // System.out.println("proxy hostname : " + addr.getHostName());
                    // System.setProperty("http.proxyHost", addr.getHostName());
                    // System.out.println("proxy port : " + addr.getPort());
                    // System.setProperty("http.proxyPort", Integer.toString(addr.getPort()));
                    result = proxy;
                }
            }
        }
        return result;
    }
}
