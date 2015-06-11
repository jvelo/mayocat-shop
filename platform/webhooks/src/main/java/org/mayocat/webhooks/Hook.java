/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.webhooks;

import com.google.common.base.Optional;

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
