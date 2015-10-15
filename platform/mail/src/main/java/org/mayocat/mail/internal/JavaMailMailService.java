/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.mail.internal;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.inject.Inject;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.context.WebContext;
import org.mayocat.mail.Mail;
import org.mayocat.mail.MailAttachment;
import org.mayocat.mail.MailException;
import org.mayocat.mail.MailService;
import org.mayocat.mail.SmtpSettings;
import org.slf4j.Logger;
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

    @Inject
    private GeneralSettings generalSettings;

    @Inject
    private WebContext context;

    @Inject
    private Logger logger;

    @Override
    public Mail emailToTenant() {
        return new Mail().from(generalSettings.getNotificationsEmail()).to(context.getTenant().getContactEmail());
    }

    @Override
    public void sendEmail(Mail mail) throws MailException {
        Preconditions.checkNotNull(mail.getFrom(), "Cannot send a message without a from address");

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
                protected PasswordAuthentication getPasswordAuthentication() {
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
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            }

            // Cc:
            for (String cc : mail.getCc()) {
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
            }

            // Bcc:
            for (String bcc : mail.getBcc()) {
                message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
            }

            // Subject:
            message.setSubject(mail.getSubject());

            if (mail.getAttachments().size() == 0) {
                // Body text
                if (mail.getText() != null) {
                    message.setText(mail.getText());
                }

                if (mail.getHtml() != null) {
                    message.setContent(mail.getHtml(), "text/html; charset=utf-8");
                }
            } else {
                // Multipart message

                Multipart multipart = new MimeMultipart();

                BodyPart messageBodyPart = new MimeBodyPart();
                if (mail.getText() != null) {
                    messageBodyPart.setText(mail.getText());
                }

                if (mail.getHtml() != null) {
                    messageBodyPart.setContent(mail.getHtml(), "text/html; charset=utf-8");
                }

                multipart.addBodyPart(messageBodyPart);

                List<Optional<BodyPart>> attachments = FluentIterable.from(mail.getAttachments()).transform(new Function<MailAttachment, Optional<BodyPart>>()
                {
                    public Optional<BodyPart> apply(MailAttachment mailAttachment) {
                        BodyPart attachment = new MimeBodyPart();
                        try {
                            attachment.setDataHandler(new DataHandler(mailAttachment.getDataSource()));
                            attachment.setFileName(mailAttachment.getFileName());
                        } catch (MessagingException e) {
                            logger.warn("Failed to add attachment to mail", e);
                            return Optional.absent();
                        }
                        return Optional.of(attachment);
                    }
                }).toList();

                for (Optional<BodyPart> attachment : attachments) {
                    if (attachment.isPresent()) {
                        multipart.addBodyPart(attachment.get());
                    }
                }

                message.setContent(multipart);
            }

            // Boom goes the dynamite
            Transport.send(message);
        } catch (MessagingException e) {
            throw new MailException("Failed to send email", e);
        }
    }
}
