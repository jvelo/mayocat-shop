package org.mayocat.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.mayocat.configuration.general.FilesSettings;
import org.mayocat.mail.SmtpSettings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

/**
 * @version $Id$
 */
public class AbstractSettings extends Configuration
{
    @Valid
    @NotNull
    @JsonProperty
    private PlatformSettings platform = new PlatformSettings();

    @Valid
    @NotNull
    @JsonProperty
    private MultitenancySettings multitenancy = new MultitenancySettings();

    @Valid
    @NotNull
    @JsonProperty
    private SecuritySettings security = new SecuritySettings();

    @Valid
    @NotNull
    @JsonProperty
    private FilesSettings files = new FilesSettings();

    @Valid
    @NotNull
    @JsonProperty
    private SiteSettings site = new SiteSettings();

    @Valid
    @NotNull
    @JsonProperty
    private SearchEngineSettings searchEngine = new SearchEngineSettings();

    @Valid
    @NotNull
    @JsonProperty
    private SmtpSettings smtp = new SmtpSettings();

    @Valid
    @JsonProperty
    private ReportingSettings reporting = new ReportingSettings();

    public MultitenancySettings getMultitenancySettings()
    {
        return multitenancy;
    }

    public SecuritySettings getAuthenticationSettings()
    {
        return security;
    }

    public SearchEngineSettings getSearchEngineSettings()
    {
        return searchEngine;
    }

    public PlatformSettings getPlatformSettings()
    {
        return platform;
    }

    public FilesSettings getFilesSettings()
    {
        return files;
    }

    public SiteSettings getSiteSettings()
    {
        return site;
    }

    public SmtpSettings getSmtpSettings()
    {
        return smtp;
    }

    public ReportingSettings getReportingSettings()
    {
        return reporting;
    }
}
