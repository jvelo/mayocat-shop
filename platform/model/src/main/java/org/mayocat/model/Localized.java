package org.mayocat.model;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

public interface Localized extends Entity, Serializable
{
    Map<Locale, Map<String, Object>> getLocalizedVersions();
}
