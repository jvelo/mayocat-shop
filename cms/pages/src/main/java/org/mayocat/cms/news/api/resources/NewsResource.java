package org.mayocat.cms.news.api.resources;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mayocat.Slugifier;
import org.mayocat.accounts.model.Role;
import org.mayocat.addons.api.representation.AddonRepresentation;
import org.mayocat.authorization.annotation.Authorized;
import org.mayocat.base.Resource;
import org.mayocat.cms.news.api.representations.ArticleRepresentation;
import org.mayocat.cms.news.model.Article;
import org.mayocat.cms.news.store.ArticleStore;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Addon;
import org.mayocat.model.Attachment;
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
@Component("/api/1.0/news/")
@Path("/api/1.0/news/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
public class NewsResource extends AbstractAttachmentResource implements Resource
{
    @Inject
    private Slugifier slugifier;

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
            articleReferences.add(new EntityReferenceRepresentation(article.getTitle(), article.getSlug(),
                    "/api/1.0/news/" + article.getSlug()));
        }

        ResultSetRepresentation<EntityReferenceRepresentation> resultSet =
                new ResultSetRepresentation<EntityReferenceRepresentation>(
                        "/api/1.0/news/",
                        number,
                        offset,
                        articleReferences
                );

        return resultSet;
    }

    @GET
    @Path("{slug}")
    public Object getArticle(@PathParam("slug") String slug)
    {
        Article article = articleStore.get().findBySlug(slug);
        if (article == null) {
            return Response.status(404).build();
        }
        ArticleRepresentation representation = new ArticleRepresentation(article);
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
    @Authorized(roles = { Role.ADMIN })
    public Object createArticle(Article article)
    {
        try {
            if (Strings.isNullOrEmpty(article.getSlug())) {
                article.setSlug(this.slugifier.slugify(article.getTitle()));
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
            Article updatedArticle)
    {
        try {
            Article article = this.articleStore.get().findBySlug(slug);
            if (article == null) {
                return Response.status(404).build();
            } else {
                updatedArticle.setSlug(slug);
                if (isJustBeingPublished(article, updatedArticle) && updatedArticle.getPublicationDate() == null) {
                    // If the article is being published and has no publication date, set it to right now
                    GeneralSettings settings = configurationService.getSettings(GeneralSettings.class);
                    DateTime now = new DateTime().withZone(DateTimeZone.forTimeZone(settings.getTimeZone().getValue()));
                    updatedArticle.setPublicationDate(now.toLocalDateTime().toDate());
                }

                this.articleStore.get().update(updatedArticle);
            }

            return Response.ok().build();
        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No Article with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        }
    }

    @Path("{slug}/image")
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

            result.add(representation);
        }

        return result;
    }

    @Path("{slug}/attachment")
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

        return this.addAttachment(uploadedInputStream, fileDetail.getFileName(), title, description,
                Optional.of(article.getId()));
    }

    private static boolean isJustBeingPublished(Article originalArticle, Article updatedArticle)
    {
        return (originalArticle.getPublished() == null || !originalArticle.getPublished()) &&
                (updatedArticle.getPublished() != null && updatedArticle.getPublished());
    }
}
