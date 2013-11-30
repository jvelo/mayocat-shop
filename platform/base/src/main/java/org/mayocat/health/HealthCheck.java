/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.health;

import org.xwiki.component.annotation.Role;

/**
 * Tag interface for Metrics Health Checks implemented as XWiki components and automatically registered when the
 * application starts. Such health checks MUST also extends the {@link com.yammer.metrics.core.HealthCheck} abstract
 * class, otherwise they will be ignored.
 */
@Role
public interface HealthCheck
{
}
