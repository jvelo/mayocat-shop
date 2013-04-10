package org.mayocat.cms.news.api.representations;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.List;

import org.mayocat.addons.api.representation.AddonRepresentation;
import org.mayocat.cms.news.model.Article;
import org.mayocat.rest.representations.ImageRepresentation;
import org.mayocat.cms.jackson.DateTimeISO8601Serializer;
import org.mayocat.cms.jackson.DateTimeISO8601Deserializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


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

    @JsonSerialize(using=DateTimeISO8601Serializer.class)
    @JsonDeserialize(using=DateTimeISO8601Deserializer.class)
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
        this.href = "/api/1.0/news/" + article.getSlug();
        this.title = article.getTitle();
        this.content = article.getContent();
        if (article.getPublicationDate() != null) {
            this.publicationDate = new DateTime(article.getPublicationDate().getTime(), tenantZone);
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
