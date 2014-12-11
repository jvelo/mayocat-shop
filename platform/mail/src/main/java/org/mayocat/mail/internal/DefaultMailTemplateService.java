/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.mail.internal;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.mayocat.files.FileManager;
import org.mayocat.mail.MailException;
import org.mayocat.mail.MailService;
import org.mayocat.mail.MailTemplate;
import org.mayocat.mail.MailTemplateService;
import org.mayocat.templating.TemplateRenderer;
import org.mayocat.templating.TemplateRenderingException;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * @version $Id$
 */
@Component
public class DefaultMailTemplateService implements MailTemplateService
{
    @Inject
    private FileManager fileManager;

    @Inject
    private MailService mailService;

    @Inject
    private TemplateRenderer templateRenderer;

    private static final String DELIMITER =
            "------------------------------------------------------------------------------------------------------------------------";

    @Override
    public void sendTemplateMail(MailTemplate mail, Map<String, Object> context) throws MailException
    {
        Path templatePath = null;
        if (mail.locale().isPresent()) {
            templatePath =
                    fileManager.resolvePermanentFilePath(Paths.get("emails")).resolve("localized")
                            .resolve(mail.locale().get().toLanguageTag())
                            .resolve(mail.template() + ".twig");
            if (!templatePath.toFile().isFile()) {
                templatePath =
                        fileManager.resolvePermanentFilePath(Paths.get("emails")).resolve("localized")
                                .resolve(mail.locale().get().getLanguage())
                                .resolve(mail.template() + ".twig");
            }
        }

        if (templatePath == null || !templatePath.toFile().isFile()) {
            templatePath =
                    fileManager.resolvePermanentFilePath(Paths.get("emails")).resolve(mail.template() + ".twig");
        }

        if (templatePath == null || !templatePath.toFile().isFile()) {
            throw new MailException("Mail template not found");
        }

        try {
            String result = templateRenderer.renderAsString(templatePath, context);

            List<String> parts = Lists.newArrayList(Splitter.on(DELIMITER).trimResults().split(result));

            if (parts.size() != 3) {
                throw new MailException("Failed to render email. Is the mail extending mail.twig ?");
            }

            mail.subject(parts.get(0));
            if (!Strings.isNullOrEmpty(parts.get(1))) {
                mail.text(parts.get(1));
            }
            if (!Strings.isNullOrEmpty(parts.get(2))) {
                mail.html(parts.get(2));
            }

            mailService.sendEmail(mail);
        } catch (TemplateRenderingException e) {
            throw new MailException("Failed to render mail template", e);
        }
    }
}
