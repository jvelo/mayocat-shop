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
     * Retrieves a notification mail template localized in the context's locale (with fallback on the default locale)
     *
     * @param templateName the name of the template to get
     * @return an optional template, present if found (might not be in the context's locale, fallback on the default
     * locale) ; absent otherwise
     */
    Optional<Template> getTemplate(String templateName);

    /**
     * Retrieves a notification mail template
     *
     * @param templateName the name of the template to get
     * @param locale the locale to get it in
     * @return an optional template, present if found (might not be in the asked locale, fallback on the default locale)
     * ; absent otherwise
     */
    Optional<Template> getTemplate(String templateName, Locale locale);

    /**
     * Sends a mail template against a context
     *
     * @param mailTemplate the template mail
     * @param context the context to send the template against
     */
    void sendMailTemplate(MailTemplate mailTemplate, Map<String, Object> context) throws MailException;
}
