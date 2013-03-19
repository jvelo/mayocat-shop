package org.mayocat.cms.pages.api.representations;

import java.util.List;

import org.mayocat.addons.api.representation.AddonRepresentation;
import org.mayocat.cms.pages.model.Page;
import org.mayocat.rest.representations.ImageRepresentation;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @version $Id$
 */
public class PageRepresentation
{
    private String slug;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String model;

    private Boolean published;

    private String href;

    private String title;

    private String content;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ImageRepresentation> images = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AddonRepresentation> addons = null;

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public PageRepresentation(Page page)
    {
        this.slug = page.getSlug();
        this.model = page.getModel().orNull();
        this.published = page.getPublished();
        this.href = "/api/1.0/page/" + page.getSlug();
        this.title = page.getTitle();
        this.content = page.getContent();
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
}
