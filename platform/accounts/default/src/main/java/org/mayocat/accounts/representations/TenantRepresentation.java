package org.mayocat.accounts.representations;

import java.util.List;

import org.mayocat.accounts.meta.TenantEntity;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.addons.api.representation.AddonRepresentation;
import org.mayocat.rest.Resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Preconditions;

/**
 * @version $Id$
 */
public class TenantRepresentation
{
    private String slug;

    private String title;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AddonRepresentation> addons = null;

    private String href;

    public TenantRepresentation()
    {
    }

    public TenantRepresentation(Tenant tenant)
    {
        Preconditions.checkNotNull(tenant);

        this.slug = tenant.getSlug();

        this.href = Resource.API_ROOT_PATH + TenantEntity.PATH + "/" + this.slug;
    }

    public String getSlug()
    {
        return slug;
    }

    public String getTitle()
    {
        return title;
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
