package org.mayocat.shop.catalog.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.mayocat.model.AbstractLocalizedEntity;
import org.mayocat.model.HasFeaturedImage;
import org.mayocat.model.Translations;
import org.mayocat.model.annotation.LocalizationFieldType;
import org.mayocat.model.annotation.Localized;
import org.mayocat.model.annotation.SearchIndex;

import com.google.common.base.Objects;

public class Collection extends AbstractLocalizedEntity implements HasFeaturedImage
{
    private Long id;

    @SearchIndex
    @NotNull
    @Size(min = 1)
    private String slug;

    @Localized(type = LocalizationFieldType.SMALL)
    @SearchIndex
    @NotNull
    private String title;

    @Localized(type = LocalizationFieldType.MEDIUM)
    @SearchIndex
    private String description;

    private Long featuredImageId;

    public Collection()
    {
        super();
    }

    public Collection(Translations translations)
    {
        super(translations);
    }

    public Collection(Long id)
    {
        super();
        this.id = id;
    }

    public Collection(Long id, Translations translations)
    {
        super(translations);
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public String getSlug()
    {
        return slug;
    }

    public void setSlug(String slug)
    {
        this.slug = slug;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public Long getFeaturedImageId()
    {
        return featuredImageId;
    }

    public void setFeaturedImageId(Long featuredImageId)
    {
        this.featuredImageId = featuredImageId;
    }

    // //////////////////////////////////////////////

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Collection other = (Collection) obj;

        return Objects.equal(this.title, other.title) && Objects.equal(this.slug, other.slug)
            && Objects.equal(this.description, other.description);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.slug, this.title, this.description);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).addValue(this.title).addValue(this.slug).toString();
    }

}
