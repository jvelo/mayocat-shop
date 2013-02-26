package org.mayocat.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Translations implements Map<String, Map<Locale, String>>
{
    private Map<String, Map<Locale, String>> translations;

    public Translations()
    {
        this.translations = new HashMap<String, Map<Locale, String>>();
    }
    
    public Translations(Map<String, Map<Locale, String>> translations)
    {
        this.translations = translations;
    }

    public String getLocalizedText(String field, Locale locale)
    {
        return this.getLocalizedText(field, locale, new String());
    }

    public String getLocalizedText(String field, Locale locale, String defaultText)
    {
        if (this.translations.containsKey(field)) {
            if (this.translations.get(field).containsKey(locale)) {
                return this.translations.get(field).get(locale);
            }
        }
        return defaultText;
    }

    public void clear()
    {
        this.translations.clear();
    }

    public boolean containsKey(Object key)
    {
        return this.translations.containsKey(key);
    }

    public boolean containsValue(Object value)
    {
        return this.translations.containsValue(value);
    }

    public Set<java.util.Map.Entry<String, Map<Locale, String>>> entrySet()
    {
        return this.translations.entrySet();
    }

    public Map<Locale, String> get(Object key)
    {
        return this.translations.get(key);
    }

    public boolean isEmpty()
    {
        return this.translations.isEmpty();
    }

    public Set<String> keySet()
    {
        return this.translations.keySet();
    }

    public Map<Locale, String> put(String key, Map<Locale, String> value)
    {
        return this.translations.put(key, value);
    }

    public void putAll(Map< ? extends String, ? extends Map<Locale, String>> m)
    {
        this.translations.putAll(m);
    }

    public Map<Locale, String> remove(Object key)
    {
        return this.translations.remove(key);
    }

    public int size()
    {
        return this.translations.size();
    }

    public Collection<Map<Locale, String>> values()
    {
        return this.translations.values();
    }

}
