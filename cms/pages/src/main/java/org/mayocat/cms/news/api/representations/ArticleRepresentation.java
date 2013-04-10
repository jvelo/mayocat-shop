package org.mayocat.cms.news.api.representations;

import java.util.Date;
import java.util.List;

import org.mayocat.addons.api.representation.AddonRepresentation;
import org.mayocat.cms.news.model.Article;
import org.mayocat.model.Addon;
import org.mayocat.rest.representations.ImageRepresentation;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    private Date publicationDate;

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

    public ArticleRepresentation(Article article)
    {
        this.slug = article.getSlug();
        this.model = article.getModel().orNull();
        this.published = article.getPublished();
        this.href = "/api/1.0/news/" + article.getSlug();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.publicationDate = article.getPublicationDate();

        if (article.getAddons().isLoaded()) {
            List<AddonRepresentation> addons = Lists.newArrayList();
            for (Addon a : article.getAddons().get()) {
                addons.add(new AddonRepresentation(a));
            }
            this.addons = addons;
        }
    }

    public ArticleRepresentation(Article article, List<ImageRepresentation> images)
    {
        this(article);
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

    public Date getPublicationDate()
    {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate)
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
