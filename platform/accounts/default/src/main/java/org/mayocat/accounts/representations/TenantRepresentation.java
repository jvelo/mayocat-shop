package org.mayocat.accounts.representations;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mayocat.accounts.meta.TenantEntity;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.addons.api.representation.AddonRepresentation;
import org.mayocat.model.Addon;
import org.mayocat.rest.Resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * @version $Id$
 */
public class TenantRepresentation
{
    private String slug;

    private String name;

    private DateTime creationDate;

    private String defaultHost;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AddonRepresentation> addons = null;

    private String href;

    public TenantRepresentation()
    {
    }

    public TenantRepresentation(DateTimeZone globalTimeZone, Tenant tenant)
    {
        Preconditions.checkNotNull(tenant);

        this.slug = tenant.getSlug();
        this.name = tenant.getName();
        this.defaultHost = tenant.getDefaultHost();

        if (tenant.getAddons().isLoaded()) {
            this.addons = Lists.newArrayList();
            for (Addon a : tenant.getAddons().get()) {
                addons.add(new AddonRepresentation(a));
            }
        }

        if (tenant.getCreationDate() != null) {
            this.creationDate = new DateTime(tenant.getCreationDate().getTime(), globalTimeZone);
        }

        this.href = Resource.API_ROOT_PATH + TenantEntity.PATH + "/" + this.slug;
    }

    public String getSlug()
    {
        return slug;
    }

    public String getName()
    {
        return name;
    }

    public DateTime getCreationDate()
    {
        return creationDate;
    }

    public String getDefaultHost()
    {
        return defaultHost;
    }

    public List<AddonRepresentation> getAddons()
    {
        return addons;
    }

    public String getHref()
    {
        return href;
    }
}
