/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
