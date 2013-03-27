package org.mayocat.cms.pages.model;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.mayocat.model.AbstractLocalizedEntity;
import org.mayocat.model.Addon;
import org.mayocat.model.Child;
import org.mayocat.model.HasAddons;
import org.mayocat.model.HasFeaturedImage;
import org.mayocat.model.HasModel;
import org.mayocat.model.PerhapsLoaded;
import org.mayocat.model.annotation.LocalizationFieldType;
import org.mayocat.model.annotation.Localized;
import org.mayocat.model.annotation.SearchIndex;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class Page extends AbstractLocalizedEntity implements Child, HasAddons, HasModel, HasFeaturedImage
{
    private Long id;

    private Long parentId = null;

    @SearchIndex
    @NotNull
    @Size(min = 1)
    private String slug;

    @SearchIndex
    private Boolean published;

    @Localized(type = LocalizationFieldType.SMALL)
    @SearchIndex
    @NotNull
    private String title;

    @Localized(type = LocalizationFieldType.MEDIUM)
    @SearchIndex
    private String content;

    private PerhapsLoaded<List<Addon>> addons = PerhapsLoaded.notLoaded();

    private Optional<String> model = Optional.absent();

    private Long featuredImageId;

    public Page()
    {
    }

    public Page(Long id)
    {
        setId(id);
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

    public Boolean getPublished()
    {
        return published;
    }

    public void setPublished(Boolean published)
    {
        this.published = published;
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

    @Override
    public PerhapsLoaded<List<Addon>> getAddons()
    {
        return addons;
    }

    public void setAddons(List<Addon> addons)
    {
        this.addons = new PerhapsLoaded<List<Addon>>(addons);
    }

    public void setModel(String model)
    {
        this.model = Optional.fromNullable(model);
    }

    @Override
    public Optional<String> getModel()
    {
        return this.model;
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
        final Page other = (Page) obj;

        return Objects.equal(this.title, other.title)
                && Objects.equal(this.slug, other.slug)
                && Objects.equal(this.content, other.content)
                && Objects.equal(this.published, other.published)
                && Objects.equal(this.addons, other.addons);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(
                this.slug,
                this.title,
                this.content,
                this.published,
                this.addons
        );
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).addValue(this.title).addValue(this.slug).toString();
    }
}
