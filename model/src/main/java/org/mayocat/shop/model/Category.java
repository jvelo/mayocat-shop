package org.mayocat.shop.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.mayocat.shop.model.annotation.LocalizationFieldType;
import org.mayocat.shop.model.annotation.Localized;
import org.mayocat.shop.model.annotation.SearchIndex;

import com.google.common.base.Objects;

public class Category extends AbstractLocalizedEntity
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

    public Category()
    {
        super();
    }

    public Category(Translations translations)
    {
        super(translations);
    }

    public Category(Long id)
    {
        super();
        this.id = id;
    }

    public Category(Long id, Translations translations)
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
        final Category other = (Category) obj;

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
