package org.mayocat.shop.model;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.mayocat.shop.model.reference.EntityReference;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

public class Tenant implements Identifiable, Slug
{
    @JsonIgnore
    Long id;

    @NotNull
    @Pattern(message = "Only word characters or hyphens", regexp = "\\w[\\w-]*\\w")
    String slug;

    @JsonIgnore
    private TenantConfiguration configuration;

    ///////////////////////////////////////////////////

    public Tenant(String slug, TenantConfiguration configuration)
    {
        setSlug(slug);
        this.configuration = configuration;
    }

    public Tenant(Long id, String slug, TenantConfiguration configuration)
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

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
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
