package org.mayocat.configuration.reporting;

import com.google.common.base.Optional;

/**
 * Reporting configuration for graphite (works with hostedgraphite.com too)
 *
 * @version $Id$
 */
public class GraphiteSettings
{
    private String host;

    private Integer port;

    private Optional<String> apiKey = Optional.absent();

    public String getHost()
    {
        return host;
    }

    public Integer getPort()
    {
        return port;
    }

    public Optional<String> getApiKey()
    {
        return apiKey;
    }
}
