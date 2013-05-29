package org.mayocat.accounts.model;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;
import org.mayocat.image.model.Image;
import org.mayocat.model.Addon;
import org.mayocat.model.Entity;
import org.mayocat.model.HasAddons;
import org.mayocat.model.HasFeaturedImage;
import org.mayocat.model.PerhapsLoaded;
import org.mayocat.model.annotation.Index;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;

public class Tenant implements Entity, HasAddons, HasFeaturedImage
{
    @JsonIgnore
    private UUID id;

    @JsonIgnore
    private UUID featuredImageId;

    @NotNull
    @Pattern(message = "Only word characters or hyphens", regexp = "\\w[\\w-]*\\w")
    private String slug;

    @Index
    @NotBlank
    private String name;

    private Date creationDate;

    // FIXME
    // Implement a @Hostname constraint that verifies a hostname is valid
    @Index
    private String defaultHost;

    @JsonIgnore
    private TenantConfiguration configuration;

    private PerhapsLoaded<List<Addon>> addons = PerhapsLoaded.notLoaded();

    @JsonIgnore
    private PerhapsLoaded<Image> featuredImage = PerhapsLoaded.notLoaded();

    ///////////////////////////////////////////////////

    public Tenant()
    {
        this.configuration = new TenantConfiguration();
    }

    public Tenant(String slug, TenantConfiguration configuration)
    {
        setSlug(slug);
        this.configuration = configuration;
    }

    public Tenant(UUID id, String slug, TenantConfiguration configuration)
    {
        setId(id);
        setSlug(slug);
        this.configuration = configuration;
    }

    ///////////////////////////////////////////////////

    public TenantConfiguration getConfiguration()
    {
        return configuration;
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public String getSlug()
    {
        return slug;
    }

    public void setSlug(String slug)
    {
        this.slug = slug;
    }

    public void setDefaultHost(String defaultHost)
    {
        this.defaultHost = defaultHost;
    }

    public String getDefaultHost()
    {
        return defaultHost;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Date getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(Date creationDate)
    {
        this.creationDate = creationDate;
    }

    @Override
    public PerhapsLoaded<List<Addon>> getAddons()
    {
        return this.addons;
    }

    @Override
    public void setAddons(List<Addon> addons)
    {
        this.addons = new PerhapsLoaded<List<Addon>>(addons);
    }

    public UUID getFeaturedImageId()
    {
        return featuredImageId;
    }

    public void setFeaturedImageId(UUID featuredImageId)
    {
        this.featuredImageId = featuredImageId;
    }

    public PerhapsLoaded<Image> getFeaturedImage()
    {
        return featuredImage;
    }

    public void setFeaturedImage(Image featuredImage)
    {
        this.featuredImage = new PerhapsLoaded<Image>(featuredImage);
    }

    // ///////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tenant other = (Tenant) obj;

        return Objects.equal(this.slug, other.slug);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.slug);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).addValue(this.slug).toString();
    }
}
