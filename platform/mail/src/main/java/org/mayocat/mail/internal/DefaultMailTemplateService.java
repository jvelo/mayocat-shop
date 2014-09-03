/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.mail.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.mayocat.context.WebContext;
import org.mayocat.files.FileManager;
import org.mayocat.mail.Mail;
import org.mayocat.mail.MailException;
import org.mayocat.mail.MailService;
import org.mayocat.mail.MailTemplate;
import org.mayocat.mail.MailTemplateService;
import org.mayocat.views.Template;
import org.mayocat.views.TemplateEngine;
import org.mayocat.views.TemplateEngineException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

/**
 * @version $Id$
 */
@Component
public class DefaultMailTemplateService implements MailTemplateService
{
    @Inject
    private FileManager fileManager;

    @Inject
    private WebContext context;

    @Inject
    private Provider<TemplateEngine> engine;

    @Inject
    private MailService emailService;

    @Inject
    private Logger logger;

    @Override
    public Optional<Template> getTemplate(String templateName)
    {
        return getTemplate(templateName, context != null ? context.getLocale() : Locale.ENGLISH);
    }

    @Override
    public Optional<Template> getTemplate(String templateName, Locale locale)
    {
        // Localized as country + language
        Path templatePath = fileManager.resolvePermanentFilePath(Paths.get("emails").resolve(locale.toLanguageTag()))
                .resolve(templateName);

        // Localized as language
        if (!templatePath.toFile().isFile()) {
            templatePath = fileManager.resolvePermanentFilePath(Paths.get("emails"))
                    .resolve(locale.getLanguage()).resolve(templateName);
        }

        // Global fallback
        if (!templatePath.toFile().isFile()) {
            templatePath =
                    fileManager.resolvePermanentFilePath(Paths.get("emails")).resolve(templateName);
        }

        if (templatePath.toFile().isFile()) {
            try {
                String content = Files.toString(templatePath.toFile(), Charsets.UTF_8);
                Template template = new Template(templateName, content);
                return Optional.of(template);
            } catch (IOException e) {
                // Fail below
            }
        }
        return Optional.absent();
    }

    @Override
    public void sendMailTemplate(MailTemplate mailTemplate, Map<String, Object> context) throws MailException
    {
        Preconditions.checkNotNull(mailTemplate);
        Preconditions.checkNotNull(mailTemplate.getTemplate());
        Preconditions.checkNotNull(context);

        try {
            ObjectMapper mapper = new ObjectMapper();
            engine.get().register(mailTemplate.getTemplate());
            String jsonContext = mapper.writeValueAsString(context);
            String html = engine.get().render(mailTemplate.getTemplate().getId(), jsonContext);
            List<String> lines = IOUtils.readLines(new ByteArrayInputStream(html.getBytes()), Charsets.UTF_8);
            String subject = StringUtils.substringAfter(lines.remove(0), "Subject:").trim();
            String body = StringUtils.join(lines, "\n");
            Mail mail = new Mail().from(mailTemplate.getFrom())
                    .to((String[]) mailTemplate.getTo().toArray(new String[mailTemplate.getTo().size()]))
                    .cc((String[]) mailTemplate.getCc().toArray(new String[mailTemplate.getCc().size()]))
                    .bcc((String[]) mailTemplate.getBcc().toArray(new String[mailTemplate.getBcc().size()]))
                    .text(body)
                    .subject(subject);

            emailService.sendEmail(mail);
        } catch (TemplateEngineException | IOException | MailException e) {
            logger.error("Failed to send email from template", ExceptionUtils.getRootCause(e));
            throw new MailException(e);
        }
    }
}
