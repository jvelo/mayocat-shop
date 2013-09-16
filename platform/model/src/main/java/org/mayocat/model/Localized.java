package org.mayocat.model;

import java.util.Locale;
import java.util.Map;

public interface Localized extends Entity
{
    Map<Locale, Object> getLocalizedVersions();
}
