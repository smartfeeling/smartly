/*
 * MailSender.java
 *
 *
 */
package org.smartly.packages.mail.impl;

import org.smartly.commons.util.MimeTypeUtils;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.mail.SmartlyMail;


public final class MailUtils {

    private MailUtils() {
    }

    public static Thread sendMailTo(final String[] to,
                                    final String subject,
                                    final String body) throws Exception {
        final String from = SmartlyMail.getFrom();
        return sendMailTo(from, to, subject, body);
    }

    public static Thread sendMailHTMLTo(final String[] to,
                                        final String subject,
                                        final String body) throws Exception {
        final String from = SmartlyMail.getFrom();
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
            final String smtpHost = SmartlyMail.getHost();
            final int smtpPort = SmartlyMail.getPort();
            final String user = SmartlyMail.getUsername();
            final String password = SmartlyMail.getPassword();
            final boolean TLS = SmartlyMail.getTLS();
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
            sender.setDebug(SmartlyMail.isDebug());
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


}
