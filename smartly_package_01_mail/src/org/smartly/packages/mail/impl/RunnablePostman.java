package org.smartly.packages.mail.impl;

import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.ExceptionUtils;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.MimeTypeUtils;
import org.smartly.commons.util.StringUtils;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

public class RunnablePostman
        implements Runnable {

    private String _smtpHost;
    private int _smtpPort = 25;
    private String _from;
    private String _to;
    private String _subject;
    private String _message;
    private String _user = null;
    private String _password = null;
    private boolean _TLS = false;
    private String _mailFormat = MimeTypeUtils.getMimeType(".txt");
    private boolean _debug = false;
    private final List<String> _addresses;
    private final List<File> _fileAttachments;
    private final Map<String, InputStream> _streamAttachments;
    private Exception _exception;

    public RunnablePostman() {
        _addresses = new ArrayList<String>();
        _fileAttachments = new ArrayList<File>();
        _streamAttachments = new HashMap<String, InputStream>();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            _addresses.clear();
            _fileAttachments.clear();
            _streamAttachments.clear();
        } catch (Exception e) {
        }
        super.finalize();
    }


    @Override
    public void run() {
        try {
            this.sendMail();
        } catch (Exception e) {
            _exception = new Exception(ExceptionUtils.getRealMessage(e), e);
        }
    }

    public Exception getException() {
        return _exception;
    }

    public boolean isTLS() {
        return _TLS;
    }

    public void setTLS(boolean TLS) {
        this._TLS = TLS;
    }

    public boolean isDebug() {
        return _debug;
    }

    public void setDebug(boolean debug) {
        this._debug = debug;
    }

    public String getSmtpHost() {
        return _smtpHost;
    }

    public void setSmtpHost(String value) {
        _smtpHost = value;
    }

    public int getSmtpPort() {
        return _smtpPort;
    }

    public void setSmtpPort(int value) {
        _smtpPort = value;
    }

    public String getFrom() {
        return _from;
    }

    public void setFrom(String value) {
        _from = this.checkAddress(value);
    }

    public String getTo() {
        return _to;
    }

    public void setTo(String value) {
        _to = this.checkAddress(value);
    }

    public String getSubject() {
        return _subject;
    }

    public void setSubject(String value) {
        _subject = value;
    }

    public String getMessage() {
        return _message;
    }

    public void setMessage(String value) {
        _message = value;
    }

    public String getUser() {
        return _user;
    }

    public void setUser(String value) {
        _user = value;
    }

    public String getPassword() {
        return _password;
    }

    public void setPassword(String value) {
        _password = value;
    }

    public String getMailFormat() {
        return _mailFormat;
    }

    public void setMailFormat(final String value) {
        _mailFormat = value;
    }

    public void addAddress(final String address) {
        if(StringUtils.hasText(address)){
            if (address.indexOf(";") > 0) {
                final String[] addresses = address.split(";");
                addAddresses(addresses);
            } else {
                _addresses.add(checkAddress(address));
            }
        }
    }

    public void addAddresses(String[] addresses) {
        for (final String address : addresses) {
            if (null != address && address.length() > 0) {
                _addresses.add(checkAddress(address));
            }
        }
    }

    public String[] getAddresses() {
        return _addresses.toArray(new String[_addresses.size()]);
    }

    public void clearAddresses() {
        _addresses.clear();
    }

    public void addFileAttachment(final String path) {
        _fileAttachments.add(new File(path));
    }

    public void addFileAttachment(final File file) {
        _fileAttachments.add(file);
    }

    /**
     * Add an attachment as output stream.<br>
     * In most cases you can take "other content types" to mean file attachments,
     * such as Word documents, but for something a bit more interesting,
     * for example sending a Java serialized object.<br>
     *
     * @param name Name
     * @param stream InputStream
     */
    public void addStreamAttachment(final String name, final InputStream stream) {
        try {
            _streamAttachments.put(name, stream);
        } catch (Throwable ignored) {
        }
    }

    public boolean sendMail() {
        try {
            // create addresses
            if (null != _to && _to.length() > 0) {
                final String[] addresses = _to.split(";");
                this.addAddresses(addresses);
            }

            // Create a mail session
            final Authenticator auth;
            final Properties props = new Properties();
            props.put("mail.smtp.host", _smtpHost);
            props.put("mail.smtp.port", "" + _smtpPort);
            if (null != _user && _user.length() > 0) {
                props.put("mail.smtp.auth", "true");
                if (this.isTLS()) {
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.smtp.ssl.protocols", "SSLv3 TLSv1");
                }
                auth = new SMTPAuthenticator(_user, _password);
            } else {
                props.put("mail.smtp.auth", "false");
                auth = null;
            }

            final Session session = Session.getInstance(props, auth);
            session.setDebug(isDebug());
            final Transport transport = session.getTransport("smtp");
            try {
                transport.connect();
                int count = _addresses.size();
                for (int i = 0; i < count; i++) {
                    final String mailAddress = _addresses.get(i);
                    final InternetAddress[] addressTo = new InternetAddress[1];
                    addressTo[0] = new InternetAddress(mailAddress);
                    final InternetAddress addressFrom = new InternetAddress(_from);
                    // Construct the message
                    final Message msg = new MimeMessage(session);
                    msg.setSentDate(new Date());
                    msg.setDescription("this is a smartly message");
                    msg.setFrom(addressFrom);
                    msg.setReplyTo(new InternetAddress[]{addressFrom});
                    msg.setRecipients(Message.RecipientType.TO, addressTo);

                    msg.setSubject(_subject);

                    //msg.setContent(_message, _mailFormat.toString());
                    final Multipart mp = this.getMailMultiPart();

                    msg.setContent(mp);

                    // Send the message
                    Transport.send(msg);
                }
                return true;
            } finally {
                try {
                    transport.close();
                } catch (MessagingException ex) {
                }
            }
        } catch (Throwable t) {
            final String msg = FormatUtils.format("SEND EMAIL ERROR: " +
                    "'%s' error sending mail: [%s].",
                    t.getClass().getSimpleName(),
                    ExceptionUtils.getRealMessage(t));
            _exception = new Exception(msg, t);
            LoggingUtils.getLogger(this).severe(msg);
        }
        return false;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private Multipart getMailMultiPart() throws MessagingException {
        // simple body
        final MimeBodyPart body = new MimeBodyPart();
        body.setContent(_message, _mailFormat);
        //msg.setContent(_message, _mailFormat.toString());

        // attachments
        final MimeBodyPart[] attachments = this.getAttachments();

        final Multipart result = new MimeMultipart();
        // add body
        result.addBodyPart(body);
        // add attachments
        for (MimeBodyPart part : attachments) {
            result.addBodyPart(part);
        }

        return result;
    }

    private MimeBodyPart[] getAttachments() throws MessagingException {
        final List<MimeBodyPart> result = new ArrayList<MimeBodyPart>();

        // file attachments
        for (final File file : _fileAttachments) {
            final DataHandler dh = new DataHandler(new FileDataSource(file));
            final MimeBodyPart part = new MimeBodyPart();
            part.setDataHandler(dh);
            part.setFileName(file.getName());
            result.add(part);
        }

        // stream attachments
        final Set<Entry<String, InputStream>> entries = _streamAttachments.entrySet();
        for (final Entry<String, InputStream> entry : entries) {
            try {
                final String name = entry.getKey();
                final InputStream stream = entry.getValue();
                final DataHandler dh = new DataHandler(new ByteArrayDataSource(stream, name));
                final MimeBodyPart body = new MimeBodyPart();
                body.setDataHandler(dh);
                result.add(body);
            } catch (Throwable t) {
            }
        }

        return result.toArray(new MimeBodyPart[result.size()]);
    }

    private String checkAddress(final String address) {
        final String result = address.replace("[", "<").replace("]", ">");
        return result;
    }

    // ------------------------------------------------------------------------
    //                      e m b e d d e d
    // ------------------------------------------------------------------------

    /**
     * SimpleAuthenticator is used to do simple authentication
     * when the SMTP server requires it.
     */
    private class SMTPAuthenticator extends Authenticator {

        private String _usr = null;
        private String _psw = null;

        public SMTPAuthenticator(final String user,
                                 final String password) {
            _usr = user;
            _psw = password;
        }

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(_usr, _psw);
        }
    }
}
