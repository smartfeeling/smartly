/*
 * MailSender.java
 *
 *
 */
package org.smartly.packages.mail.impl;

import org.smartly.Smartly;
import org.smartly.commons.jsonrepository.JsonRepository;
import org.smartly.commons.util.MimeTypeUtils;
import org.smartly.commons.util.StringUtils;


public final class MailUtils {

    private MailUtils() {
    }

    public static Thread sendMailTo(final String[] to,
                                    final String subject,
                                    final String body) throws Exception {
        final String from = getFrom();
        return sendMailTo(from, to, subject, body);
    }

    public static Thread sendMailHTMLTo(final String[] to,
                                        final String subject,
                                        final String body) throws Exception {
        final String from = getFrom();
        return sendMailHTMLTo(from, to, subject, body);
    }

    public static Thread sendMailHTMLTo(final String from,
                                        final String[] to,
                                        final String subject,
                                        final String body) throws Exception {
        final String mimeType = MimeTypeUtils.MIME_HTML;
        return sendMailTo(from, to, subject, body, mimeType);
    }

    public static Thread sendMailTo(final String from,
                                    final String[] to,
                                    final String subject,
                                    final String body) throws Exception {
        final String mimeType = MimeTypeUtils.MIME_PLAINTEXT;
        return sendMailTo(from, to, subject, body, mimeType);
    }

    public static Thread sendMailTo(final String from,
                                    final String to,
                                    final String subject,
                                    final String body,
                                    final String mimeType) throws Exception {
        final String[] addresses = parseAddresses(to);
        return sendMailTo(from, addresses, subject, body, mimeType);
    }

    public static Thread sendMailTo(final String from,
                                    final String[] addresses,
                                    final String subject,
                                    final String body,
                                    final String mimeType) throws Exception {
        if (null != addresses && addresses.length > 0 && !StringUtils.isNULL(from)) {
            final String smtpHost = getHost();
            final int smtpPort = getPort();
            final String user = getUsername();
            final String password = getPassword();
            final boolean TLS = getTLS();
            return sendMail(smtpHost,
                    smtpPort,
                    user,
                    password,
                    TLS,
                    from,
                    addresses,
                    subject,
                    body,
                    mimeType);
        }
        throw new Exception("WRONG PARAMETERS EXCEPTION: Address and Sender cannot be null or empty.");
    }

    // ------------------------------------------------------------------------
    //                  p r i v a t e
    // ------------------------------------------------------------------------

    private static String[] parseAddresses(final String addresses) {
        if (StringUtils.hasText(addresses)) {
            if (addresses.contains(";")) {
                return StringUtils.split(addresses, ";", true, true);
            } else if (addresses.contains(",")) {
                return StringUtils.split(addresses, ",", true, true);
            } else {
                return new String[]{addresses};
            }
        }
        return null;
    }

    private static Thread sendMail(final String smtpHost,
                                   final int smtpPort,
                                   final String user,
                                   final String password, final boolean TLS,
                                   final String from,
                                   final String[] addresses,
                                   final String subject,
                                   final String content,
                                   final String mimeType) throws Exception {
        if (!StringUtils.isNULL(addresses)) {
            //-- creates message --//
            final RunnablePostman sender = new RunnablePostman();
            //-- fill message --//
            sender.setDebug(isDebug());
            sender.setSmtpHost(smtpHost);
            sender.setSmtpPort(smtpPort);
            sender.setUser(user);
            sender.setPassword(password);
            sender.setTLS(TLS);
            sender.setFrom(from);
            sender.addAddresses(addresses);
            sender.setSubject(subject);
            sender.setMailFormat(mimeType);
            sender.setMessage(content);
            final Thread starter = new Thread(sender);
            starter.start();
            return starter;
        }
        throw new Exception("NULL ADDRESS EXCEPTION: Addresses cannot be null object.");
    }

    // ------------------------------------------------------------------------
    //                      config
    // ------------------------------------------------------------------------

    private static JsonRepository __config;

    private static JsonRepository getConfiguration() throws Exception {
        if (null == __config) {
            __config = Smartly.getConfiguration(true);
        }
        return __config;
    }

    private static String getFrom() throws Exception {
        return (String) getConfiguration().get("mail.smtp.reply_to");
    }

    private static String getHost() throws Exception {
        return (String) getConfiguration().get("mail.smtp.host");
    }

    private static int getPort() throws Exception {
        return (Integer) getConfiguration().get("mail.smtp.port");
    }

    private static String getUsername() throws Exception {
        return (String) getConfiguration().get("mail.smtp.username");
    }

    private static String getPassword() throws Exception {
        return (String) getConfiguration().get("mail.smtp.password");
    }

    private static boolean getTLS() throws Exception {
        return (Boolean) getConfiguration().get("mail.smtp.TLS");
    }

    private static boolean isDebug() throws Exception {
        return (Boolean) getConfiguration().get("mail.smtp.debug");
    }

}
