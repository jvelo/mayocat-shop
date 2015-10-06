/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.mail;

import java.util.Map;
import org.mayocat.accounts.model.Tenant;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface MailTemplateService
{
    /**
     * Sends a mail template against a context
     *
     * @param mail the template mail
     * @param context the context to send the template against
     */
    void sendTemplateMail(MailTemplate mail, Map<String, Object> context) throws MailException;

    /**
     * Sends a mail template against a context
     *
     * @param mail the template mail
     * @param context the context to send the template against
     */
    void sendTemplateMail(MailTemplate mail, Map<String, Object> context, Tenant tenant) throws MailException;
}
