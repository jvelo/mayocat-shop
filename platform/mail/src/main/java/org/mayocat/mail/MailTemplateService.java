/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.mail;

import java.util.Locale;
import java.util.Map;

import org.mayocat.views.Template;
import org.xwiki.component.annotation.Role;

import com.google.common.base.Optional;

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
}
