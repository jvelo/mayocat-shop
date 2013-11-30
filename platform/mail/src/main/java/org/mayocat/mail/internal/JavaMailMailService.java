/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.mail.internal;

import java.util.Objects;
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

import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.context.WebContext;
import org.mayocat.mail.Mail;
import org.mayocat.mail.MailException;
import org.mayocat.mail.MailService;
import org.mayocat.mail.SmtpSettings;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

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

    @Override
    public Mail emailToTenant()
    {
        return new Mail().from(generalSettings.getNotificationsEmail()).to(context.getTenant().getContactEmail());
    }

    @Override
    public void sendEmail(Mail mail) throws MailException
    {
        Preconditions.checkNotNull(mail.getFrom(), "Cannot send a message without a from address");

        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", smtpSettings.getServer());
        properties.put("mail.smtp.port", smtpSettings.getPort());
        properties.put("mail.smtp.timeout", 1000);
        properties.put("mail.smtp.connectiontimeout", 1000);

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
            if (mail.getText() != null) {
                message.setText(mail.getText());
            }

            // Boom goes the dynamite
            Transport.send(message);
        } catch (MessagingException e) {
            throw new MailException("Failed to send email", e);
        }
    }
}
