package org.mayocat.cms.news.model;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.mayocat.model.AbstractLocalizedEntity;
import org.mayocat.model.Addon;
import org.mayocat.model.HasAddons;
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
public class Article extends AbstractLocalizedEntity implements HasAddons, HasModel
{
    private Long id;

    @SearchIndex
    @NotNull
    @Size(min = 1)
    private String slug;

    @Localized(type = LocalizationFieldType.SMALL)
    @SearchIndex
    @NotNull
    public String title;

    public String content;

    @SearchIndex
    private Boolean published;

    private Date publicationDate;

    private PerhapsLoaded<List<Addon>> addons = PerhapsLoaded.notLoaded();

    private Optional<String> model = Optional.absent();

    public Article()
    {
    }
    public Article(Long id)
    {
        this.setId(id);
    }

    @Override
    public PerhapsLoaded<List<Addon>> getAddons()
    {
        return addons;
    }

    @Override
    public Optional<String> getModel()
    {
        return model;
    }

    @Override
    public Long getId()
    {
        return id;
    }

    @Override
    public void setId(Long id)
    {
        this.id = id;
    }

    @Override
    public String getSlug()
    {
        return slug;
    }

    @Override
    public void setSlug(String slug)
    {
        this.slug = slug;
    }

    public void setAddons(List<Addon> addons)
    {
        this.addons = new PerhapsLoaded(addons);
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

    public Boolean getPublished()
    {
        return published;
    }

    public void setPublished(Boolean published)
    {
        this.published = published;
    }

    public Date getPublicationDate()
    {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate)
    {
        this.publicationDate = publicationDate;
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
        final Article other = (Article) obj;

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
