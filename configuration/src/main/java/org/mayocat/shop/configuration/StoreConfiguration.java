package org.mayocat.shop.configuration;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.codehaus.jackson.annotate.JsonProperty;

public class StoreConfiguration
{

    public static class LocalizationConfiguration
    {
        @Valid
        @JsonProperty
        private boolean enabled = false;

        @Valid
        @JsonProperty
        private boolean configurable = true;

        @Valid
        @JsonProperty
        private List<String> languages = Collections.emptyList();

        @Valid
        @JsonProperty
        private String defaultLanguage = "en";

        public boolean isEnabled()
        {
            return enabled;
        }

        public void setEnabled(boolean enabled)
        {
            this.enabled = enabled;
        }

        public boolean isConfigurable()
        {
            return configurable;
        }

        public void setConfigurable(boolean configurable)
        {
            this.configurable = configurable;
        }

        public List<String> getLanguages()
        {
            return languages;
        }

        public void setLanguages(List<String> languages)
        {
            this.languages = languages;
        }

        public String getDefaultLanguage()
        {
            return defaultLanguage;
        }

        public void setDefaultLanguage(String defaultLanguage)
        {
            this.defaultLanguage = defaultLanguage;
        }
    }

    @Valid
    @JsonProperty
    private LocalizationConfiguration localization = new LocalizationConfiguration();

    public LocalizationConfiguration getLocalizaation()
    {
        return localization;
    }
}
