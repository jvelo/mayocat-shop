package org.mayocat.base;

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
