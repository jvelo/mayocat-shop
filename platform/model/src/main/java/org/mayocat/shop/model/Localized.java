package org.mayocat.shop.model;

import java.util.Locale;

public interface Localized extends Entity
{
    String getLocalizedText(String field, Locale locale);
    
    Translations getTranslations();
    
    void setTranslations(Translations translations);
}
