package org.mayocat.url;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.configuration.SiteSettings;
import org.mayocat.model.Entity;

/**
 * @version $Id$
 */
public abstract class AbstractEntityURLFactory<E extends Entity> implements EntityURLFactory<E>
{
    @Inject
    private SiteSettings siteSettings;

    protected String getDomain(Tenant tenant)
    {
        return StringUtils
                .defaultIfBlank(tenant.getDefaultHost(), tenant.getSlug() + "." + siteSettings.getDomainName());
    }
}
