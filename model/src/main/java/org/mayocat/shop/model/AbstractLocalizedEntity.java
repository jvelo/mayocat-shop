package org.mayocat.shop.model;

import java.util.Locale;

public abstract class AbstractLocalizedEntity implements LocalizedEntity
{
    private Translations translations;

    public AbstractLocalizedEntity()
    {
        super();
    }

    public AbstractLocalizedEntity(Translations translations)
    {
        this.translations = translations;
    }

    public String getLocalizedText(String field, Locale locale)
    {
        return this.translations.getLocalizedText(field, locale);
    }

    public Translations getTranslations()
    {
        return this.translations;
    }

    public void setTranslations(Translations translations)
    {
        this.translations = translations;
    }
}
