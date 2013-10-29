package org.mayocat.configuration;

import org.mayocat.configuration.reporting.GraphiteSettings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;

/**
 * Settings related to metrics reporting
 *
 * @version $Id$
 */
public class ReportingSettings
{
    @JsonProperty
    private Boolean jmx = Boolean.FALSE;

    @JsonProperty
    private Optional<GraphiteSettings> graphite = Optional.absent();

    public Optional<GraphiteSettings> getGraphiteSettings()
    {
        return graphite;
    }

    public Boolean getJmx()
    {
        return jmx;
    }
}
