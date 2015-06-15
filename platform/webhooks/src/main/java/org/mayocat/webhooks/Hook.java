/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.webhooks;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;
import org.mayocat.jackson.OptionalStringDeserializer;

/**
 * @version $Id$
 *
 * Registered hook : a URL that is called when an event occurs. An optional secret can be given for
 * computing a signature that attests the hook HTTP call authenticity.
 */
public class Hook
{
    // And I don't need no hook for this shiiiit

    private String event;

    private String url;

    @JsonDeserialize(using = OptionalStringDeserializer.class)
    private Optional<String> secret = Optional.absent();

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
