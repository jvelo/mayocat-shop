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
import groovy.transform.CompileStatic
import org.joda.time.DateTimeZone
import org.mayocat.attachment.AttachmentLoadingOptions
import org.mayocat.attachment.model.Attachment
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.cms.news.api.v1.object.ArticleApiObject
import org.mayocat.cms.news.api.v1.object.ArticleListApiObject
import org.mayocat.cms.news.model.Article
import org.mayocat.cms.news.store.ArticleStore
import org.mayocat.configuration.ConfigurationService
import org.mayocat.configuration.PlatformSettings
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.entity.EntityData
import org.mayocat.image.model.Image
import org.mayocat.image.model.ImageGallery
import org.mayocat.model.Entity
import org.mayocat.rest.Resource
import org.mayocat.rest.annotation.ExistingTenant
import org.mayocat.rest.api.delegate.AttachmentApiDelegate
import org.mayocat.rest.api.delegate.EntityApiDelegateHandler
import org.mayocat.rest.api.delegate.ImageGalleryApiDelegate
import org.mayocat.rest.api.object.LinkApiObject
import org.mayocat.rest.api.object.Pagination
import org.mayocat.store.EntityAlreadyExistsException
import org.mayocat.store.EntityDoesNotExistException
import org.mayocat.store.EntityListStore
import org.mayocat.store.InvalidEntityException
import org.mayocat.theme.ThemeDefinition
import org.slf4j.Logger
import org.xwiki.component.annotation.Component

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
@Component("/tenant/{tenant}/api/news")
@Path("/tenant/{tenant}/api/news")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
@CompileStatic
class NewsApi implements Resource, AttachmentApiDelegate, ImageGalleryApiDelegate
{
    @Inject
    Provider<ArticleStore> articleStore

    @Inject
    Provider<EntityListStore> entityListStore

    @Inject
    ConfigurationService configurationService

    @Inject
    PlatformSettings platformSettings

    @Inject
    Logger logger

    EntityApiDelegateHandler handler = new EntityApiDelegateHandler() {
        Entity getEntity(String slug)
        {
            return articleStore.get().findBySlug(slug)
        }

        void updateEntity(Entity entity)
        {
            articleStore.get().update(entity as Article)
        }

        String type()
        {
            "article"
        }
    }

    Closure doAfterAttachmentAdded = { String target, Entity entity, String fileName, Attachment created ->
        switch (target) {
            case "image-gallery":
                afterImageAddedToGallery(entity as Article, fileName, created)
                break
        }
    }

    @GET
    def listArticles(@QueryParam("number") @DefaultValue("100") Integer number,
            @QueryParam("offset") @DefaultValue("0") Integer offset)
    {
        List<ArticleApiObject> articleList = []
        def articles = articleStore.get().findAll(number, offset)
        def totalItems = this.articleStore.get().countAll()

        List<EntityData<Article>> articlesData = dataLoader.load(articles, AttachmentLoadingOptions.FEATURED_IMAGE_ONLY)

        GeneralSettings settings = configurationService.getSettings(GeneralSettings.class)
        DateTimeZone tenantTz = DateTimeZone.forTimeZone(settings.getTime().getTimeZone().getValue())

        articlesData.each({ EntityData<Article> articleData ->
            def article = articleData.entity
            def articleApiObject = new ArticleApiObject([
                    _href: "${context.request.tenantPrefix}/api/news/${article.slug}"
            ])
            articleApiObject.withArticle(article, tenantTz)

            if (article.addons.isLoaded()) {
                articleApiObject.withAddons(article.addons.get())
            }

            def images = articleData.getDataList(Image.class)
            def featuredImage = images.find({ Image image -> image.attachment.id == article.featuredImageId })

            if (featuredImage) {
                articleApiObject.withEmbeddedFeaturedImage(featuredImage, context.request.tenantPrefix)
            }

            articleList << articleApiObject
        })

        def articleListApiObject = new ArticleListApiObject([
                _pagination: new Pagination([
                        numberOfItems: number,
                        returnedItems: articleList.size(),
                        offset       : offset,
                        totalItems   : totalItems,
                        urlTemplate  : '${tenantPrefix}/api/products?number=${numberOfItems}&offset=${offset}&',
                        urlArguments : [
                                tenantPrefix: context.request.tenantPrefix
                        ]
                ]),
                articles   : articleList
        ])

        articleListApiObject
    }

    @GET
    @Path("{slug}")
    def getArticle(@PathParam("slug") String slug, @QueryParam("expand") @DefaultValue("") String expand)
    {
        Article article = articleStore.get().findBySlug(slug)
        if (article == null) {
            return Response.status(404).build()
        }

        EntityData<Article> articleData = dataLoader.load(article)

        def gallery = articleData.getData(ImageGallery)
        List<Image> images = gallery.isPresent() ? gallery.get().images : [] as List<Image>

        List<String> expansions = Strings.isNullOrEmpty(expand) ? [] as List<String> : expand.split(",") as List<String>

        GeneralSettings settings = configurationService.getSettings(GeneralSettings.class)
        DateTimeZone tenantTz = DateTimeZone.forTimeZone(settings.getTime().getTimeZone().getValue())

        def articleApiObject = new ArticleApiObject([
                _href : "${context.request.tenantPrefix}/api/news/${slug}",
                _links: [
                        self  : new LinkApiObject([href: "${context.request.tenantPrefix}/api/news/${slug}"]),
                        images: new LinkApiObject([href: "${context.request.tenantPrefix}/api/news/${slug}/images"])
                ]
        ])

        articleApiObject.withArticle(article, tenantTz)

        if (expansions.contains("images")) {
            articleApiObject.withEmbeddedImages(images, article.featuredImageId, context.request.tenantPrefix)
        }

        if (article.addons.isLoaded()) {
            articleApiObject.withAddons(article.addons.get())
        }

        articleApiObject
    }

    @POST
    @Authorized
    def createArticle(ArticleApiObject articleApiObject)
    {
        try {
            def article = articleApiObject.toArticle(platformSettings,
                    Optional.<ThemeDefinition> fromNullable(context.theme?.definition))

            if (Strings.isNullOrEmpty(article.slug)) {
                article.setSlug(this.getSlugifier().slugify(article.title))
            }

            this.articleStore.get().create(article)

            Article created = articleStore.get().findBySlug(article.slug)

            // Respond with a created URI relative to this API URL.
            // This will add a location header like http://host/api/<version>/news/my-article
            return Response.created(new URI(created.slug)).build()
        } catch (InvalidEntityException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid article\n").type(MediaType.TEXT_PLAIN_TYPE).build()
        } catch (EntityAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("An article with this slug already exists\n").type(MediaType.TEXT_PLAIN_TYPE).build()
        } catch (URISyntaxException e) {
            throw new WebApplicationException(e)
        }
    }

    @Path("{slug}")
    @POST
    @Authorized
    // Partial update : NOT idempotent
    def updateArticle(@PathParam("slug") String slug,
            ArticleApiObject articleApiObject)
    {
        try {
            Article article = this.articleStore.get().findBySlug(slug)
            if (article == null) {
                return Response.status(404).build()
            } else {
                def featuredImageId = article.featuredImageId

                article = articleApiObject.
                        toArticle(platformSettings, Optional.<ThemeDefinition> fromNullable(context.theme?.definition))

                // Slug can't be update this way no matter what
                article.slug = slug

                // Article featured image is updated via the /images API only, set it back
                article.featuredImageId = featuredImageId

                if (isJustBeingPublished(article, articleApiObject) &&
                        articleApiObject.getPublicationDate() == null)
                {
                    // If the article is being published and has no publication date, set it to right now
                    article.setPublicationDate(new Date())
                } else if (articleApiObject.getPublicationDate() != null) {
                    article.setPublicationDate(articleApiObject.getPublicationDate().toDate())
                }

                article.setPublished(articleApiObject.published)

                this.articleStore.get().update(article)
            }

            return Response.ok().build()
        } catch (InvalidEntityException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid article\n").type(MediaType.TEXT_PLAIN_TYPE).build()
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No Article with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build()
        }
    }

    @Path("{slug}")
    @DELETE
    @Consumes(MediaType.WILDCARD)
    @Authorized
    def deleteArticle(@PathParam("slug") String slug)
    {
        Article page = this.articleStore.get().findBySlug(slug)

        if (page == null) {
            return Response.status(404).build()
        }

        try {
            this.articleStore.get().delete(page)

            return Response.noContent().build()
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No article with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build()
        }
    }

    static boolean isJustBeingPublished(Article originalArticle, ArticleApiObject updatedArticle)
    {
        return (originalArticle.published == null || !originalArticle.published) &&
                (updatedArticle.published != null && updatedArticle.published)
    }
}
