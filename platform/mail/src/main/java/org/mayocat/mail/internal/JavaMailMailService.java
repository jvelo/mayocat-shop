package org.mayocat.mail.internal;

import java.util.Properties;

import javax.inject.Inject;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.mayocat.mail.Mail;
import org.mayocat.mail.MailException;
import org.mayocat.mail.MailService;
import org.mayocat.mail.SmtpSettings;
import org.xwiki.component.annotation.Component;

/**
 * Implementation of {@link org.mayocat.mail.MailService} based on JavaMail/SMTP
 *
 * @version $Id$
 */
@Component
public class JavaMailMailService implements MailService
{
    @Inject
    private SmtpSettings smtpSettings;

    @Override
    public void sendEmail(Mail mail) throws MailException
    {
        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", smtpSettings.getServer());
        properties.put("mail.smtp.port", smtpSettings.getPort());

        for (String key : smtpSettings.getProperties().keySet()) {
            properties.put(key, smtpSettings.getProperties().get(key));
        }

        Session mailSession;

        if (smtpSettings.getUsername().isPresent() && smtpSettings.getPassword().isPresent()) {
            properties.put("mail.smtp.auth", "true");
            final String username = smtpSettings.getUsername().get();
            final String password = smtpSettings.getPassword().get();
            Authenticator authenticator = new Authenticator()
            {
                @Override
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(username, password);
                }
            };
            mailSession = Session.getDefaultInstance(properties, authenticator);
        } else {
            mailSession = Session.getDefaultInstance(properties);
        }

        try {
            MimeMessage message = new MimeMessage(mailSession);

            // From:
            message.setFrom(new InternetAddress(mail.getFrom()));

            // To:
            for (String to : mail.getTo()) {
                message.addRecipient(Message.RecipientType.TO,
                        new InternetAddress(to));
            }

            // Cc:
            for (String cc : mail.getCc()) {
                message.addRecipient(Message.RecipientType.CC,
                        new InternetAddress(cc));
            }

            // Bcc:
            for (String bcc : mail.getBcc()) {
                message.addRecipient(Message.RecipientType.BCC,
                        new InternetAddress(bcc));
            }

            // Subject:
            message.setSubject(mail.getSubject());

            // Body text
            message.setText(mail.getText());

            // Boom goes the dynamite
            Transport.send(message);
        } catch (MessagingException e) {
            throw new MailException("Failed to send email", e);
        }
    }
}
