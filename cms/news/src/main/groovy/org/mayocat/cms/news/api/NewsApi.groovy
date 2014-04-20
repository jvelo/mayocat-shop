/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.news.api

import com.google.common.base.Optional
import com.google.common.base.Strings
import com.sun.jersey.core.header.FormDataContentDisposition
import com.sun.jersey.multipart.FormDataParam
import com.yammer.metrics.annotation.Timed
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTimeZone
import org.mayocat.Slugifier
import org.mayocat.attachment.util.AttachmentUtils
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.cms.news.api.v1.object.ArticleApiObject
import org.mayocat.cms.news.api.v1.object.ArticleListApiObject
import org.mayocat.cms.news.model.Article
import org.mayocat.cms.news.store.ArticleStore
import org.mayocat.configuration.ConfigurationService
import org.mayocat.configuration.PlatformSettings
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.context.WebContext
import org.mayocat.image.model.Image
import org.mayocat.image.model.Thumbnail
import org.mayocat.image.store.ThumbnailStore
import org.mayocat.model.Attachment
import org.mayocat.rest.Resource
import org.mayocat.rest.annotation.ExistingTenant
import org.mayocat.rest.api.delegate.AttachmentApiDelegate
import org.mayocat.rest.api.object.ImageApiObject
import org.mayocat.rest.api.object.LinkApiObject
import org.mayocat.rest.api.object.Pagination
import org.mayocat.store.AttachmentStore
import org.mayocat.store.EntityAlreadyExistsException
import org.mayocat.store.EntityDoesNotExistException
import org.mayocat.store.InvalidEntityException
import org.mayocat.theme.ThemeDefinition
import org.slf4j.Logger
import org.xwiki.component.annotation.Component
import org.xwiki.component.phase.Initializable

import javax.inject.Inject
import javax.inject.Provider
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * API for {@link Article} news
 *
 * @version $Id$
 */
@Component("/api/news")
@Path("/api/news")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
@CompileStatic
class NewsApi implements Resource, Initializable
{
    @Inject
    Provider<ThumbnailStore> thumbnailStore;

    @Inject
    Provider<ArticleStore> articleStore;

    @Inject
    ConfigurationService configurationService;

    @Inject
    PlatformSettings platformSettings

    @Inject
    Provider<AttachmentStore> attachmentStore

    @Inject
    WebContext context;

    @Inject
    Slugifier slugifier

    @Inject
    Logger logger

    @Delegate
    AttachmentApiDelegate attachmentApi

    void initialize() {
        attachmentApi = new AttachmentApiDelegate([
                attachmentStore: attachmentStore,
                slugifier: slugifier
        ])
    }

    @GET
    def listArticles(@QueryParam("number") @DefaultValue("100") Integer number,
            @QueryParam("offset") @DefaultValue("0") Integer offset)
    {
        List<ArticleApiObject> articleList = [];
        def articles = articleStore.get().findAll(number, offset);
        def totalItems = this.articleStore.get().countAll();

        def imageIds = articles.collect({ Article article -> article.featuredImageId })
                .findAll({ UUID id -> id != null })

        List<Image> images;
        if (imageIds.size() > 0) {
            List<Attachment> attachments = this.attachmentStore.get().findByIds(imageIds.toList());
            List<Thumbnail> thumbnails = this.thumbnailStore.get().findAllForIds(imageIds.toList());
            images = attachments.collect({ Attachment attachment ->
                def thumbs = thumbnails.findAll({ Thumbnail thumbnail -> thumbnail.attachmentId = attachment.id })
                return new Image(attachment, thumbs.toList())
            });
        } else {
            images = []
        }

        GeneralSettings settings = configurationService.getSettings(GeneralSettings.class);
        DateTimeZone tenantTz = DateTimeZone.forTimeZone(settings.getTime().getTimeZone().getValue());

        articles.each({ Article article ->
            def articleApiObject = new ArticleApiObject([
                    _href: "/api/news/${article.slug}"
            ])
            articleApiObject.withArticle(article, tenantTz)

            if (article.addons.isLoaded()) {
                articleApiObject.withAddons(article.addons.get())
            }

            def featuredImage = images.find({ Image image -> image.attachment.id == article.featuredImageId })

            if (featuredImage) {
                articleApiObject.withEmbeddedFeaturedImage(featuredImage)
            }

            articleList << articleApiObject
        })

        def articleListApiObject = new ArticleListApiObject([
                pagination: new Pagination([
                        numberOfItems: number,
                        returnedItems: articleList.size(),
                        offset: offset,
                        totalItems: totalItems,
                        urlTemplate: '/api/products?number=${numberOfItems}&offset=${offset}&',
                ]),
                articles: articleList
        ])

        articleListApiObject
    }

    @GET
    @Path("{slug}")
    def getArticle(@PathParam("slug") String slug, @QueryParam("expand") @DefaultValue("") String expand)
    {
        Article article = articleStore.get().findBySlug(slug);
        if (article == null) {
            return Response.status(404).build();
        }

        List<String> expansions = Strings.isNullOrEmpty(expand) ? [] as List<String> : expand.split(",") as List<String>;

        GeneralSettings settings = configurationService.getSettings(GeneralSettings.class);
        DateTimeZone tenantTz = DateTimeZone.forTimeZone(settings.getTime().getTimeZone().getValue());

        def articleApiObject = new ArticleApiObject([
                _href: "/api/news/${slug}",
                _links: [
                        self: new LinkApiObject([ href: "/api/news/${slug}" ]),
                        images: new LinkApiObject([ href: "/api/news/${slug}/images" ])
                ]
        ])

        articleApiObject.withArticle(article, tenantTz);

        if (expansions.contains("images")) {
            def images = this.getImages(slug)
            articleApiObject.withEmbeddedImages(images)
        }

        if (article.addons.isLoaded()) {
            articleApiObject.withAddons(article.addons.get())
        }

        articleApiObject
    }

    @POST
    @Timed
    @Authorized
    def createArticle(ArticleApiObject articleApiObject)
    {
        try {
            def article = articleApiObject.toArticle(platformSettings,
                    Optional.<ThemeDefinition> fromNullable(context.theme?.definition))

            if (Strings.isNullOrEmpty(article.slug)) {
                article.setSlug(this.getSlugifier().slugify(article.title));
            }

            this.articleStore.get().create(article);

            Article created = articleStore.get().findBySlug(article.slug);

            // Respond with a created URI relative to this API URL.
            // This will add a location header like http://host/api/<version>/news/my-article
            return Response.created(new URI(created.slug)).build();
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
    def updateArticle(@PathParam("slug") String slug,
            ArticleApiObject articleApiObject)
    {
        try {
            Article article = this.articleStore.get().findBySlug(slug);
            if (article == null) {
                return Response.status(404).build();
            } else {

                article = articleApiObject.toArticle(platformSettings, Optional.<ThemeDefinition> fromNullable(context.theme?.definition))

                // Slug can't be update this way no matter what
                article.slug = slug

                if (isJustBeingPublished(article, articleApiObject) &&
                        articleApiObject.getPublicationDate() == null)
                {
                    // If the article is being published and has no publication date, set it to right now
                    article.setPublicationDate(new Date());
                } else if (articleApiObject.getPublicationDate() != null) {
                    article.setPublicationDate(articleApiObject.getPublicationDate().toDate());
                }

                article.setPublished(articleApiObject.published);

                // Featured image
                if (articleApiObject._embedded && articleApiObject._embedded.get("featuredImage")) {

                    // FIXME:
                    // This should be done via the {slug}/images/ API instead

                    ImageApiObject featured = articleApiObject._embedded.get("featuredImage") as ImageApiObject

                    Attachment featuredImage =
                        this.attachmentStore.get().findBySlugAndExtension(featured.slug, featured.file.extension);

                    if (featuredImage) {
                        article.featuredImageId = featuredImage.id
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
    def deleteArticle(@PathParam("slug") String slug)
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
    def getImages(@PathParam("slug") String slug)
    {
        def images = [];
        def article = this.articleStore.get().findBySlug(slug);
        if (article == null) {
            throw new WebApplicationException(Response.status(404).build());
        }

        for (Attachment attachment : this.attachmentStore.get().findAllChildrenOf(article,
                ["png", "jpg", "jpeg", "gif"] as List))
        {
            def thumbnails = thumbnailStore.get().findAll(attachment);
            def image = new Image(attachment, thumbnails);

            def imageApiObject = new ImageApiObject()
            imageApiObject.withImage(image)

            if (article.featuredImageId != null && article.featuredImageId.equals(attachment.id)) {
                imageApiObject.featured = true
            }

            images << imageApiObject
        }

        images;
    }

    @Path("{slug}/images/{imageSlug}")
    @Authorized
    @DELETE
    @Consumes(MediaType.WILDCARD)
    def detachImage(@PathParam("slug") String slug,
            @PathParam("imageSlug") String imageSlug)
    {
        def attachment = attachmentStore.get().findBySlug(imageSlug);
        if (attachment == null) {
            return Response.status(404).build();
        }
        try {
            attachmentStore.get().detach(attachment);
            return Response.noContent().build();
        } catch (EntityDoesNotExistException e) {
            return Response.status(404).build();
        }
    }

    @Path("{slug}/images/{imageSlug}")
    @Authorized
    @POST
    @Consumes(MediaType.WILDCARD)
    def updateImage(@PathParam("slug") String slug,
            @PathParam("imageSlug") String imageSlug, ImageApiObject image)
    {
        def attachment = attachmentStore.get().findBySlug(imageSlug);
        if (attachment == null) {
            return Response.status(404).build();
        }
        try {
            attachment.with {
                setTitle image.title
                setDescription image.description
                setLocalizedVersions image._localized
            }
            attachmentStore.get().update(attachment);
            return Response.noContent().build();
        } catch (EntityDoesNotExistException e) {
            return Response.status(404).build();
        } catch (InvalidEntityException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }


    @Path("{slug}/attachments")
    @Authorized
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    def addAttachment(@PathParam("slug") String slug,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("filename") String sentFilename,
            @FormDataParam("title") String title,
            @FormDataParam("description") String description)
    {
        def article = this.articleStore.get().findBySlug(slug);
        if (article == null) {
            return Response.status(404).build();
        }

        def filename = StringUtils.defaultIfBlank(fileDetail.fileName, sentFilename) as String;
        def created = this.addAttachment(uploadedInputStream, filename, title, description,
                Optional.of(article.id));

        if (article.featuredImageId == null && AttachmentUtils.isImage(filename) && created != null) {

            // If this is an image and the article doesn't have a featured image yet, and the attachment was
            // successful, the we set this image as featured image.
            article.featuredImageId = created.id;

            try {
                this.articleStore.get().update(article);
            } catch (EntityDoesNotExistException | InvalidEntityException e) {
                // Fail silently. The attachment has been added successfully, that's what matter
                this.logger.warn("Failed to set first image as featured image for entity {} with id", article.id);
            }
        }

        return Response.noContent().build();
    }

    static boolean isJustBeingPublished(Article originalArticle, ArticleApiObject updatedArticle)
    {
        return (originalArticle.published == null || !originalArticle.published) &&
                (updatedArticle.published != null && updatedArticle.published);
    }

}
