/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.pages.api.v1

import com.google.common.base.Optional
import com.google.common.base.Strings
import com.sun.jersey.core.header.FormDataContentDisposition
import com.sun.jersey.multipart.FormDataParam
import com.yammer.metrics.annotation.Timed
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.mayocat.Slugifier
import org.mayocat.attachment.util.AttachmentUtils
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.cms.pages.api.v1.object.PageApiObject
import org.mayocat.cms.pages.api.v1.object.PageListApiObject
import org.mayocat.cms.pages.model.Page
import org.mayocat.cms.pages.store.PageStore
import org.mayocat.configuration.PlatformSettings
import org.mayocat.context.WebContext
import org.mayocat.image.model.Image
import org.mayocat.image.model.Thumbnail
import org.mayocat.image.store.ThumbnailStore
import org.mayocat.model.Attachment
import org.mayocat.model.Entity
import org.mayocat.rest.Resource
import org.mayocat.rest.annotation.ExistingTenant
import org.mayocat.rest.api.delegate.AttachmentApiDelegate
import org.mayocat.rest.api.delegate.EntityApiDelegateHandler
import org.mayocat.rest.api.delegate.ImageGalleryApiDelegate
import org.mayocat.rest.api.object.ImageApiObject
import org.mayocat.rest.api.object.LinkApiObject
import org.mayocat.rest.api.object.Pagination
import org.mayocat.store.AttachmentStore
import org.mayocat.store.EntityAlreadyExistsException
import org.mayocat.store.EntityDoesNotExistException
import org.mayocat.store.EntityListStore
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
 * API for the pages module
 *
 * @version $Id$
 */
@Component("/api/pages")
@Path("/api/pages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
@CompileStatic
class PageApi implements Resource, Initializable
{
    @Inject
    Provider<ThumbnailStore> thumbnailStore;

    @Inject
    Provider<PageStore> pageStore;

    @Inject
    Provider<EntityListStore> entityListStore

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

    @Delegate(methodAnnotations = true, parameterAnnotations = true)
    AttachmentApiDelegate attachmentApi

    @Delegate(methodAnnotations = true, parameterAnnotations = true)
    ImageGalleryApiDelegate imageGalleryApi

    // Entity handler for delegates

    EntityApiDelegateHandler pageHandler = new EntityApiDelegateHandler() {
        Entity getEntity(String slug)
        {
            return pageStore.get().findBySlug(slug)
        }

        void updateEntity(Entity entity)
        {
            pageStore.get().update(entity)
        }

        String type()
        {
            "page"
        }
    }

    void initialize()
    {
        attachmentApi = new AttachmentApiDelegate([
                attachmentStore: attachmentStore,
                slugifier: slugifier,
                handler: pageHandler,
                doAfterAttachmentAdded: { String target, Entity entity, String fileName, Attachment created ->
                    switch (target) {
                        case "image-gallery":
                            afterImageAddedToGallery(entity as Page, fileName, created)
                            break;
                    }
                }
        ])
        imageGalleryApi = new ImageGalleryApiDelegate([
                attachmentStore: attachmentStore.get(),
                entityListStore: entityListStore.get(),
                thumbnailStore: thumbnailStore.get(),
                handler: pageHandler
        ])
    }

    @GET
    public Object listPages(@QueryParam("number") @DefaultValue("100") Integer number,
            @QueryParam("offset") @DefaultValue("0") Integer offset)
    {
        List<PageApiObject> pageList = [];
        def pages = pageStore.get().findAll(number, offset);
        def totalItems = this.pageStore.get().countAll();

        def imageIds = pages.collect({ Page page -> page.featuredImageId })
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

        pages.each({ Page page ->
            def articleApiObject = new PageApiObject([
                    _href: "/api/pages/${page.slug}"
            ])
            articleApiObject.withPage(page)

            if (page.addons.isLoaded()) {
                articleApiObject.withAddons(page.addons.get())
            }

            def featuredImage = images.find({ Image image -> image.attachment.id == page.featuredImageId })

            if (featuredImage) {
                articleApiObject.withEmbeddedFeaturedImage(featuredImage)
            }

            pageList << articleApiObject
        })

        def pageListApiObject = new PageListApiObject([
                pagination: new Pagination([
                        numberOfItems: number,
                        returnedItems: pageList.size(),
                        offset: offset,
                        totalItems: totalItems,
                        urlTemplate: '/api/pages?number=${numberOfItems}&offset=${offset}&',
                ]),
                pages: pageList
        ])

        pageListApiObject
    }

    @GET
    @Path("{slug}")
    public Object getPage(@PathParam("slug") String slug, @QueryParam("expand") @DefaultValue("") String expand)
    {
        Page page = pageStore.get().findBySlug(slug);
        if (page == null) {
            return Response.status(404).build();
        }

        List<String> expansions = Strings.isNullOrEmpty(expand) ? [] as List<String> : Arrays.asList(expand.split(","));

        def pageApiObject = new PageApiObject([
                _href: "/api/pages/${slug}",
                _links: [
                        self: new LinkApiObject([ href: "/api/pages/${slug}" ]),
                        images: new LinkApiObject([ href: "/api/pages/${slug}/images" ])
                ]
        ])

        pageApiObject.withPage(page);

        if (expansions.contains("images")) {
            def images = this.getImages(slug)
            pageApiObject.withEmbeddedImages(images)
        }

        if (page.addons.isLoaded()) {
            pageApiObject.withAddons(page.addons.get())
        }

        pageApiObject
    }

    @POST
    @Timed
    @Authorized
    public Object createPage(PageApiObject page)
    {
        try {
            if (Strings.isNullOrEmpty(page.slug)) {
                page.setSlug(this.slugifier.slugify(page.title));
            }

            this.pageStore.get().create(page.toPage(platformSettings,
                    Optional.<ThemeDefinition> fromNullable(context.theme?.definition)))

            Page created = pageStore.get().findBySlug(page.slug);

            // Respond with a created URI relative to this API URL.
            // This will add a location header like http://host/api/<version>/page/my-created-product
            return Response.created(new URI(created.slug)).build();
        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        } catch (EntityAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("A product with this slug already exists\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        } catch (URISyntaxException e) {
            throw new WebApplicationException(e);
        }
    }

    @Path("{slug}")
    @POST
    @Timed
    @Authorized
    // Partial update : NOT idempotent
    public Response updatePage(@PathParam("slug") String slug, PageApiObject pageApiObject)
    {
        try {
            Page page = this.pageStore.get().findBySlug(slug);
            if (page == null) {
                return Response.status(404).build();
            } else {

                page = pageApiObject.toPage(platformSettings,
                        Optional.<ThemeDefinition> fromNullable(context.theme?.definition))

                // Slug can't be update this way no matter what
                page.slug = slug

                // Featured image
                if (pageApiObject._embedded && pageApiObject._embedded.get("featuredImage")) {

                    // FIXME:
                    // This should be done via the {slug}/images/ API instead

                    ImageApiObject featured = pageApiObject._embedded.get("featuredImage") as ImageApiObject

                    Attachment featuredImage =
                        this.attachmentStore.get().findBySlugAndExtension(featured.slug, featured.file.extension);

                    if (featuredImage) {
                        page.featuredImageId = featuredImage.id
                    }
                }

                this.pageStore.get().update(page);
            }

            return Response.ok().build();
        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No page with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        }
    }

    @Path("{slug}")
    @DELETE
    @Authorized
    @Consumes(MediaType.WILDCARD)
    public Response deletePage(@PathParam("slug") String slug)
    {
        Page page = this.pageStore.get().findBySlug(slug);

        if (page == null) {
            return Response.status(404).build();
        }

        try {
            this.pageStore.get().delete(page);

            return Response.noContent().build();

        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No page with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        }
    }
}
