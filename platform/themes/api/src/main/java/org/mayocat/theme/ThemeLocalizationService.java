package org.mayocat.theme;

import org.xwiki.component.annotation.Role;

import java.util.Locale;
import java.util.Map;

@Role
public interface ThemeLocalizationService {

    String getMessage(String key, Map<String, Object> namedArguments);

    String getMessage(String key, Locale locale, Map<String, Object> namedArguments);

    String getMessageTemplate(String key);

    String getMessageTemplate(String key, Locale locale);
}
