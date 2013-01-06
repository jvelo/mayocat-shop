package org.mayocat.shop.model;

import java.util.Locale;

public interface LocalizedEntity extends Entity
{
    String getLocalizedText(String field, Locale locale);
    
    Translations getTranslations();
    
    void setTranslations(Translations translations);
}
