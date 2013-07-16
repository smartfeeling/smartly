/*
 * ExceptionUtils.java
 *
 */
package org.smartly.commons.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author
 */
public abstract class ExceptionUtils {

    /**
     * Finds the causes of an exception, ex, to see whether
     * any of them is the givinge type.
     *
     * @return the cause if found; null if not found
     */
    public static Throwable findCause(Throwable ex, Class cause) {
        while (ex != null) {
            if (cause.isInstance(ex)) {
                return ex;
            }

            ex = getCause(ex);
        }
        return null;
    }

    /**
     * Returns the cause of the given throwable. It is the same as
     * t.getCause, but it solves the compatibility of J2EE that might not
     * support JDK 1.4.
     */
    public static Throwable getCause(final Throwable ex) {
        if (null != ex) {
            final Throwable t = ex.getCause();
            return t;
        }
        return null;
    }

    /**
     * Unveils the real cause. A throwable object is called a real cause,
     * if it doesn't have any cause (or called chained throwable).
     *
     * @param ex the throwable
     * @return the real cause; ex itself if it is already the real cause
     * @see #wrap
     */
    public static Throwable getRealCause(Throwable ex) {
        while (true) {
            final Throwable cause = getCause(ex);
            if (cause == null) {
                return ex;
            }
            ex = cause;
        }
    }

    /**
     * Returns a message of the exception.
     */
    public static String getMessage(final Throwable ex) {
        if (null != ex) {
            String s;
            for (Throwable t = ex; ; ) {
                s = t.getMessage();
                if (StringUtils.hasText(s)) {
                    break; //found
                }
                t = getCause(t);
                if (t == null) {
                    break; //failed
                }
            }
            return s;
        }
        return null;
    }

    /**
     * Retrieve message of real cause.
     *
     * @param ex
     * @return
     */
    public static String getRealMessage(Throwable ex) {
        Throwable t = getRealCause(ex);
        return null != t ? t.toString() : getMessage(ex);
    }

    /**
     * Formats the stack trace and returns the result.
     * Currently, it only adds the prefix to each line.
     *
     * @param prefix the prefix shown in front of each line of the stack trace;
     *               null to denote empty
     */
    public static String formatStackTrace(Throwable t, String prefix) {
        return formatStackTrace(null, t, prefix).toString();
    }

    /**
     * Formats the stack trace and appends it to the specified string buffer.
     *
     * @param sb     the string buffer to append the stack trace. A string buffer
     *               will be created if null.
     * @param prefix the prefix shown in front of each line of the stack trace;
     *               null to denote empty
     */
    public static StringBuffer formatStackTrace(StringBuffer sb, Throwable t, String prefix) {
        return formatStackTrace(sb, t, prefix, 0);
    }

    /**
     * Formats the stack trace and appends it to the specified string buffer,
     * but only display at most maxcnt lines.
     * <p/>
     * <p>The maximal allowed number of lines is controlled by
     * maxcnt. Note: a stack frame is not counted, if it belongs
     * to java.*, javax.* or sun.*.
     *
     * @param sb     the string buffer to append the stack trace. A string buffer
     *               will be created if null.
     * @param prefix the prefix shown in front of each line of the stack trace;
     *               null to denote empty
     * @param maxcnt the maximal allowed number of lines to dump (<=0: no limit)
     */
    public static StringBuffer formatStackTrace(StringBuffer sb,
                                                final Throwable t, String prefix, int maxcnt) {
        final StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        final StringBuffer trace = sw.getBuffer();

        if (prefix == null) {
            prefix = "";
        }
        if (maxcnt > 0 || prefix.length() > 0) {
            final int len = trace.length();
            if (sb == null) {
                sb = new StringBuffer(len + 256);
            }
            if (maxcnt <= 0) {
                maxcnt = Integer.MAX_VALUE;
            }
            boolean ignoreCount = false;
            for (int j = 0; j < len; ) { //for each line
                if (!ignoreCount && --maxcnt < 0) {
                    sb.append(prefix).append("...");
                    break;
                }

                //StringBuffer has no indexOf(char,j), so...
                int k = j;
                while (k < len && trace.charAt(k++) != '\n') {
                    ; //point k to the char after \n
                }
                String frame = trace.substring(j, k);
                sb.append(prefix).append(frame);
                j = k;

                ignoreCount = inStack(frame, "java.") || inStack(frame, "javax.") || inStack(frame, "sun.");
            }
        } else {
            if (sb == null) {
                return trace;
            }
            sb.append(trace);
        }
        return sb;
    }
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static boolean inStack(String frame, String sub) {
        final int j = frame.indexOf(sub);
        if (j < 0) {
            return false;
        }
        if (j == 0) {
            return true;
        }

        final char cc = frame.charAt(j - 1);
        return (cc < 'a' || cc > 'z') && (cc < 'A' || cc > 'Z');
    }
}
