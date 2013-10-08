package org.mayocat.cms.news.api.resources;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTimeZone;
import org.mayocat.addons.api.representation.AddonRepresentation;
import org.mayocat.authorization.annotation.Authorized;
import org.mayocat.cms.news.api.representations.ArticleRepresentation;
import org.mayocat.cms.news.meta.ArticleEntity;
import org.mayocat.cms.news.model.Article;
import org.mayocat.cms.news.store.ArticleStore;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Addon;
import org.mayocat.model.AddonFieldType;
import org.mayocat.model.AddonSource;
import org.mayocat.model.Attachment;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.representations.EntityReferenceRepresentation;
import org.mayocat.rest.representations.ImageRepresentation;
import org.mayocat.rest.representations.ResultSetRepresentation;
import org.mayocat.rest.resources.AbstractAttachmentResource;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import com.yammer.metrics.annotation.Timed;

/**
 * @version $Id$
 */
@Component(NewsResource.PATH)
@Path(NewsResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
public class NewsResource extends AbstractAttachmentResource implements Resource
{
    public static final String PATH = API_ROOT_PATH + ArticleEntity.PATH;

    @Inject
    private Provider<ThumbnailStore> thumbnailStore;

    @Inject
    private Provider<ArticleStore> articleStore;

    @Inject
    private ConfigurationService configurationService;

    @GET
    public Object listArticles(@QueryParam("number") @DefaultValue("100") Integer number,
            @QueryParam("offset") @DefaultValue("0") Integer offset)
    {
        List<EntityReferenceRepresentation> articleReferences = Lists.newArrayList();
        List<Article> articles = articleStore.get().findAll(number, offset);

        for (Article article : articles) {
            articleReferences.add(new EntityReferenceRepresentation(PATH + "/" + article.getSlug(), article.getSlug(),
                    article.getTitle()
            ));
        }

        Integer total = this.articleStore.get().countAll();
        ResultSetRepresentation<EntityReferenceRepresentation> resultSet =
                new ResultSetRepresentation<EntityReferenceRepresentation>(
                        PATH,
                        offset, number,
                        articleReferences,
                        total
                );

        return resultSet;
    }

    @GET
    @Path("{slug}")
    public Object getArticle(@PathParam("slug") String slug, @QueryParam("expand") @DefaultValue("") String expand)
    {
        Article article = articleStore.get().findBySlug(slug);
        if (article == null) {
            return Response.status(404).build();
        }

        List<String> expansions = Strings.isNullOrEmpty(expand)
                ? Collections.<String>emptyList()
                : Arrays.asList(expand.split(","));

        GeneralSettings settings = configurationService.getSettings(GeneralSettings.class);
        DateTimeZone tenantTz = DateTimeZone.forTimeZone(settings.getTime().getTimeZone().getValue());
        ArticleRepresentation representation;
        if (expansions.contains("images")) {
            List<ImageRepresentation> images = getImages(slug);
            representation = new ArticleRepresentation(tenantTz, article, images);
            if (images != null) {
                for (ImageRepresentation image : images) {
                    if (image.isFeaturedImage()) {
                        representation.setFeaturedImage(image);
                    }
                }
            }
        } else {
            representation = new ArticleRepresentation(tenantTz, article);
        }

        if (article.getAddons().isLoaded()) {
            List<AddonRepresentation> addons = Lists.newArrayList();
            for (Addon a : article.getAddons().get()) {
                addons.add(new AddonRepresentation(a));
            }
            representation.setAddons(addons);
        }
        return representation;
    }

    @POST
    @Timed
    @Authorized // (roles = { Role.ADMIN })
    public Object createArticle(Article article)
    {
        try {
            if (Strings.isNullOrEmpty(article.getSlug())) {
                article.setSlug(this.getSlugifier().slugify(article.getTitle()));
            }
            this.articleStore.get().create(article);

            Article created = articleStore.get().findBySlug(article.getSlug());

            // Respond with a created URI relative to this API URL.
            // This will add a location header like http://host/api/<version>/news/my-article
            return Response.created(new URI(created.getSlug())).build();
        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        } catch (EntityAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("An article with this slug already exists\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        } catch (URISyntaxException e) {
            throw new WebApplicationException(e);
        }
    }

    @Path("{slug}")
    @POST
    @Timed
    @Authorized
    // Partial update : NOT idempotent
    public Response updateArticle(@PathParam("slug") String slug,
            ArticleRepresentation updatedArticleRepresentation)
    {
        try {
            Article article = this.articleStore.get().findBySlug(slug);
            if (article == null) {
                return Response.status(404).build();
            } else {

                article.setTitle(updatedArticleRepresentation.getTitle());
                article.setContent(updatedArticleRepresentation.getContent());

                if (isJustBeingPublished(article, updatedArticleRepresentation) &&
                        updatedArticleRepresentation.getPublicationDate() == null)
                {
                    // If the article is being published and has no publication date, set it to right now
                    article.setPublicationDate(new Date());
                } else if (updatedArticleRepresentation.getPublicationDate() != null) {
                    article.setPublicationDate(updatedArticleRepresentation.getPublicationDate().toDate());
                }

                article.setPublished(updatedArticleRepresentation.getPublished());

                // Addons
                if (updatedArticleRepresentation.getAddons() != null) {
                    List<Addon> addons = Lists.newArrayList();
                    for (AddonRepresentation addonRepresentation : updatedArticleRepresentation.getAddons()) {
                        Addon addon = new Addon();
                        addon.setSource(AddonSource.fromJson(addonRepresentation.getSource()));
                        addon.setType(AddonFieldType.fromJson(addonRepresentation.getType()));
                        addon.setValue(addonRepresentation.getValue());
                        addon.setKey(addonRepresentation.getKey());
                        addon.setGroup(addonRepresentation.getGroup());
                        addons.add(addon);
                    }
                    article.setAddons(addons);
                }

                // Featured image
                if (updatedArticleRepresentation.getFeaturedImage() != null) {
                    ImageRepresentation representation = updatedArticleRepresentation.getFeaturedImage();

                    Attachment featuredImage =
                            this.getAttachmentStore().findBySlugAndExtension(representation.getSlug(),
                                    representation.getFile().getExtension());
                    if (featuredImage != null) {
                        article.setFeaturedImageId(featuredImage.getId());
                    }
                }

                this.articleStore.get().update(article);
            }

            return Response.ok().build();
        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No Article with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        }
    }

    @Path("{slug}")
    @DELETE
    @Consumes(MediaType.WILDCARD)
    @Authorized
    public Response deleteArticle(@PathParam("slug") String slug)
    {
        Article page = this.articleStore.get().findBySlug(slug);

        if (page == null) {
            return Response.status(404).build();
        }

        try {
            this.articleStore.get().delete(page);

            return Response.noContent().build();
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No article with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        }
    }

    @Path("{slug}/images")
    @GET
    public List<ImageRepresentation> getImages(@PathParam("slug") String slug)
    {
        List<ImageRepresentation> result = new ArrayList();
        Article article = this.articleStore.get().findBySlug(slug);
        if (article == null) {
            throw new WebApplicationException(Response.status(404).build());
        }

        for (Attachment attachment : this.getAttachmentStore().findAllChildrenOf(article,
                Arrays.asList("png", "jpg", "jpeg", "gif")))
        {
            List<Thumbnail> thumbnails = thumbnailStore.get().findAll(attachment);
            Image image = new Image(attachment, thumbnails);
            ImageRepresentation representation = new ImageRepresentation(image);

            if (article.getFeaturedImageId() != null) {
                if (article.getFeaturedImageId().equals(attachment.getId())) {
                    representation.setFeatured(true);
                }
            }

            result.add(representation);
        }

        return result;
    }


    @Path("{slug}/images/{imageSlug}")
    @POST
    @Consumes(MediaType.WILDCARD)
    public Response updateImage(@PathParam("slug") String slug, @PathParam("imageSlug") String imageSlug,
            ImageRepresentation image)
    {
        Attachment attachment = getAttachmentStore().findBySlug(imageSlug);
        if (attachment == null) {
            return Response.status(404).build();
        }
        try {
            attachment.setTitle(image.getTitle());
            attachment.setDescription(image.getDescription());
            getAttachmentStore().update(attachment);
            return Response.noContent().build();
        } catch (EntityDoesNotExistException e) {
            return Response.status(404).build();
        } catch (InvalidEntityException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }


    @Path("{slug}/images/{imageSlug}")
    @DELETE
    @Consumes(MediaType.WILDCARD)
    public Response detachImage(@PathParam("slug") String slug, @PathParam("imageSlug") String imageSlug)
    {
        Attachment attachment = getAttachmentStore().findBySlug(imageSlug);
        if (attachment == null) {
            return Response.status(404).build();
        }
        try {
            getAttachmentStore().detach(attachment);
            return Response.noContent().build();

        } catch (EntityDoesNotExistException e) {
            return Response.status(404).build();
        }
    }

    @Path("{slug}/attachments")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addAttachment(@PathParam("slug") String slug,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("title") String title, @FormDataParam("description") String description)
    {
        Article article = this.articleStore.get().findBySlug(slug);
        if (article == null) {
            return Response.status(404).build();
        }

        this.addAttachment(uploadedInputStream, fileDetail.getFileName(), title, description,
                Optional.of(article.getId()));
        return Response.noContent().build();
    }

    private static boolean isJustBeingPublished(Article originalArticle, ArticleRepresentation updatedArticle)
    {
        return (originalArticle.getPublished() == null || !originalArticle.getPublished()) &&
                (updatedArticle.getPublished() != null && updatedArticle.getPublished());
    }
}
