package org.mayocat.accounts.model;

import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.mayocat.model.Identifiable;
import org.mayocat.model.Slug;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;

public class Tenant implements Identifiable, Slug
{
    @JsonIgnore
    private UUID id;

    @NotNull
    @Pattern(message = "Only word characters or hyphens", regexp = "\\w[\\w-]*\\w")
    private String slug;

    // FIXME
    // Implement a @Hostname constraint that verifies a hostname is valid
    private String defaultHost;

    @JsonIgnore
    private TenantConfiguration configuration;

    ///////////////////////////////////////////////////

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

    public String getDefaultHost()
    {
        return defaultHost;
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
