package org.mayocat.cms.news;

import javax.validation.Valid;

import org.mayocat.configuration.Configurable;
import org.mayocat.configuration.ExposedSettings;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @version $Id$
 */
public class NewsSettings implements ExposedSettings
{
    @Valid
    @JsonProperty
    private Configurable<String> newsPageTitle = new Configurable<String>("{{siteName}} \u2014 News");

    @Valid
    @JsonProperty
    private Configurable<String> articlePageTitle = new Configurable<String>("{{siteName}} \u2014 {{articleTitle}}");

    public Configurable<String> getNewsPageTitle()
    {
        return newsPageTitle;
    }

    public Configurable<String> getArticlePageTitle()
    {
        return articlePageTitle;
    }

    @Override
    public String getKey()
    {
        return "news";
    }
}
