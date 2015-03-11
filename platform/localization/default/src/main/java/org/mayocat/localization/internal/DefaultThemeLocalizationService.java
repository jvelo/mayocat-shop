/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.localization.internal;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.context.WebContext;
import org.mayocat.files.PermanentFileEvent;
import org.mayocat.theme.Theme;
import org.mayocat.localization.ThemeLocalizationService;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.ObservationManager;
import org.xwiki.observation.event.Event;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.ibm.icu.text.MessageFormat;

import asia.redact.bracket.properties.Properties;

/**
 * @version $Id$
 */
@Component
public class DefaultThemeLocalizationService implements ThemeLocalizationService, Initializable
{
    public static final String LOCALIZATION_DIRECTORY = "localization";

    public static final String PROPERTIES_FILE_EXTENSION = ".properties";

    @Inject
    private WebContext context;

    @Inject
    private ObservationManager observationManager;

    @Override
    public void initialize() throws InitializationException
    {
        this.observationManager.addListener(new FileEventListener());
    }

    private class FileEventListener implements EventListener
    {
        @Override
        public String getName()
        {
            return "themeLocalizationService";
        }

        @Override
        public List<Event> getEvents()
        {
            return Arrays.<Event>asList(new PermanentFileEvent());
        }

        @Override
        public void onEvent(Event event, Object source, Object memo)
        {
            PermanentFileEvent.Data data = (PermanentFileEvent.Data) memo;
            if (propertiesFilesCache.containsKey(data.getPath())) {
                propertiesFilesCache.remove(data.getPath());
            }
        }
    }

    private Map<Path, Optional<Properties>> propertiesFilesCache = Maps.newConcurrentMap();

    @Override
    public String getMessageTemplate(String key)
    {
        return getMessageTemplate(key, context.getLocale());
    }

    @Override
    public String getMessageTemplate(String key, Locale locale)
    {
        Theme theme = context.getTheme();

        Path propertiesPath = theme.getPath()
                .resolve(LOCALIZATION_DIRECTORY)
                .resolve(locale.toLanguageTag() + PROPERTIES_FILE_EXTENSION);

        if (!propertiesFilesCache.containsKey(propertiesPath)) {
            // Find properties.
            Properties properties = null;
            switch (theme.getType()) {
                case FILE_SYSTEM:
                    File file = propertiesPath.toFile();
                    if (file.exists()) {
                        properties = Properties.Factory.getInstance(file, Charsets.UTF_8);
                    }
                    break;
                case CLASSPATH:
                    InputStream in = getClass().getResourceAsStream('/' + propertiesPath.toString());
                    if (in != null) {
                        properties = Properties.Factory.getInstance(in);
                    }
                    break;

            }

            propertiesFilesCache.put(propertiesPath, Optional.fromNullable(properties));
        }

        Optional<Properties> props = propertiesFilesCache.get(propertiesPath);

        if (props.isPresent() && props.get().containsKey(key)) {
            // A property file exists for this theme & locale and has this key
            return props.get().get(key);
        } else {
            // No property file exists for theme & locale, or it exists but doesn't have this key -> ignore
            return null;
        }
    }

    @Override
    public String getMessage(String key, Map<String, Object> arguments)
    {
        return getMessage(key, context.getLocale(), arguments);
    }

    @Override
    public String getMessage(String key, Locale locale, Map<String, Object> arguments)
    {
        String template = getMessageTemplate(key, locale);
        if (template == null && !locale.getCountry().equals("")) {
            // if the message has not been found and the locale is a country variant of a language, try its base
            // language (like english for the american english variant).
            template = getMessageTemplate(key, new Locale(locale.getLanguage()));
        }
        if (template == null) {
            template = getMessageTemplate(key, getTenantDefaultLocale());
        }
        if (template == null) {
            template = getMessageTemplate(key, getThemeDefaultLocale());
        }
        if (template == null) {
            return null;
        }
        return MessageFormat.format(template, arguments);
    }

    private Locale getThemeDefaultLocale()
    {
        // FIXME: let this be defined in the theme.yml file.
        return Locale.ENGLISH;
    }

    private Locale getTenantDefaultLocale()
    {
        return context.getSettings(GeneralSettings.class).getLocales().getMainLocale().getValue();
    }
}
