package org.mayocat.mail;

import org.xwiki.component.annotation.Role;

/**
 * A service for sending emails through the configured SMTP gateway.
 *
 * @version $Id$
 */
@Role
public interface MailService
{
    Mail emailToTenant();

    void sendEmail(Mail mail) throws MailException;
}
