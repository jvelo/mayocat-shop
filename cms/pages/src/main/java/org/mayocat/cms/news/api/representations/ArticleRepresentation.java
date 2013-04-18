package org.mayocat.cms.news.api.representations;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.List;

import org.mayocat.addons.api.representation.AddonRepresentation;
import org.mayocat.cms.news.model.Article;
import org.mayocat.model.Addon;
import org.mayocat.rest.representations.ImageRepresentation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Lists;

/**
 * @version $Id$
 */
public class ArticleRepresentation
{
    private String slug;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String model;

    private Boolean published;

    private String href;

    private String title;

    private String content;

    private DateTime publicationDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ImageRepresentation featuredImage = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ImageRepresentation> images = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AddonRepresentation> addons = null;

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ArticleRepresentation()
    {
        // No-arg constructor required for Jackson de-serialization
    }

    public ArticleRepresentation(DateTimeZone tenantZone, Article article)
    {
        this.slug = article.getSlug();
        this.model = article.getModel().orNull();
        this.published = article.getPublished();
        this.href = "/api/news/" + article.getSlug();
        this.title = article.getTitle();
        this.content = article.getContent();
        if (article.getPublicationDate() != null) {
            this.publicationDate = new DateTime(article.getPublicationDate().getTime(), tenantZone);
        }

        if (article.getAddons().isLoaded()) {
            List<AddonRepresentation> addons = Lists.newArrayList();
            for (Addon a : article.getAddons().get()) {
                addons.add(new AddonRepresentation(a));
            }
            this.addons = addons;
        }
    }

    public ArticleRepresentation(DateTimeZone tenantZone, Article article, List<ImageRepresentation> images)
    {
        this(tenantZone, article);
        this.images = images;
    }

    public String getSlug()
    {
        return slug;
    }

    public String getTitle()
    {
        return title;
    }

    public String getContent()
    {
        return content;
    }

    public String getHref()
    {
        return href;
    }

    public Boolean getPublished()
    {
        return published;
    }

    public List<ImageRepresentation> getImages()
    {
        return images;
    }

    public List<AddonRepresentation> getAddons()
    {
        return addons;
    }

    public void setAddons(List<AddonRepresentation> addons)
    {
        this.addons = addons;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public DateTime getPublicationDate()
    {
        return publicationDate;
    }

    public void setPublicationDate(DateTime publicationDate)
    {
        this.publicationDate = publicationDate;
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
