package org.mayocat.accounts.representations;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mayocat.accounts.meta.TenantEntity;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.addons.api.representation.AddonRepresentation;
import org.mayocat.image.model.Image;
import org.mayocat.model.Addon;
import org.mayocat.rest.Resource;
import org.mayocat.rest.representations.ImageRepresentation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * @version $Id$
 */
public class TenantRepresentation
{
    private String slug;

    private String name;

    @JsonIgnore
    // Ignored on de-serialization only. See getter and setter
    private DateTime creationDate;

    private String defaultHost;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AddonRepresentation> addons = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ImageRepresentation featuredImage = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String href;

    public TenantRepresentation()
    {
    }

    public TenantRepresentation(DateTimeZone globalTimeZone, Tenant tenant)
    {
        this(globalTimeZone, tenant, null);
    }

    public TenantRepresentation(DateTimeZone globalTimeZone, Tenant tenant, String href)
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

        if (tenant.getFeaturedImage().isLoaded()) {
            this.setFeaturedImage(new ImageRepresentation(tenant.getFeaturedImage().get()));
        }

        this.href = href;
    }

    public String getSlug()
    {
        return slug;
    }

    public String getName()
    {
        return name;
    }

    @JsonProperty("creationDate")
    public DateTime getCreationDate()
    {
        return creationDate;
    }

    @JsonIgnore
    public void setCreationDate(DateTime creationDate)
    {
        this.creationDate = creationDate;
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

    public ImageRepresentation getFeaturedImage()
    {
        return featuredImage;
    }

    public void setFeaturedImage(ImageRepresentation featuredImage)
    {
        this.featuredImage = featuredImage;
    }
}
