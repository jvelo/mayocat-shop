package org.mayocat.webhooks;

import java.util.Optional;

/**
 * @version $Id$
 */
public class Hook
{
    private String event;

    private String url;

    private Optional<String> secret;

    public String getEvent() {
        return event;
    }

    public String getUrl() {
        return url;
    }

    public Optional<String> getSecret() {
        return secret;
    }
}
