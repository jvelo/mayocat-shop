package org.mayocat.cms.pages.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.mayocat.model.AbstractLocalizedEntity;
import org.mayocat.model.Child;
import org.mayocat.model.annotation.LocalizationFieldType;
import org.mayocat.model.annotation.Localized;
import org.mayocat.model.annotation.SearchIndex;

import com.google.common.base.Objects;

/**
 * @version $Id$
 */
public class Page extends AbstractLocalizedEntity implements Child
{
    private Long id;

    private Long parentId = null;

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
    private String content;

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

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    // //////////////////////////////////////////////

    @Override
    public Long getParentId()
    {
        return this.parentId;
    }

    @Override
    public void setParentId(Long id)
    {
        this.parentId = id;
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
        final Page other = (Page) obj;

        return Objects.equal(this.title, other.title) && Objects.equal(this.slug, other.slug)
                && Objects.equal(this.content, other.content);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.slug, this.title, this.content);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).addValue(this.title).addValue(this.slug).toString();
    }
}
