package org.mayocat.rest.jackson;

import java.util.Locale;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @version $Id$
 */
public class MayocatLocaleBCP47LanguageTagModule extends SimpleModule
{
    public MayocatLocaleBCP47LanguageTagModule()
    {
        super(Version.unknownVersion());
        addDeserializer(Locale.class, new LocaleBCP47LanguageTagDeserializer());
        addSerializer(Locale.class, new LocaleBCP47LanguageTagSerializer());
    }
}
