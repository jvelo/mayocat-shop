package org.mayocat.configuration.general;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.mayocat.configuration.Configurable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @version $Id$
 */
public class LocalesConfiguration
{
    @Valid
    @JsonProperty("main")
    private Configurable<Locale> mainLocale = new Configurable(Locale.ENGLISH);

    @Valid
    @JsonProperty("others")
    private Configurable<List<Locale>> otherLocales = new Configurable<List<Locale>>(Collections.<Locale>emptyList());

    public Configurable<Locale> getMainLocale()
    {
        return mainLocale;
    }

    public Configurable<List<Locale>> getOtherLocales()
    {
        return otherLocales;
    }
}
