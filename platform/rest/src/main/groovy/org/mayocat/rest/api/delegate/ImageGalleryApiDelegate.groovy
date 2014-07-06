/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.api.delegate

import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.attachment.store.AttachmentStore
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.entity.EntityData
import org.mayocat.entity.EntityDataLoader
import org.mayocat.image.model.Image
import org.mayocat.image.model.ImageGallery
import org.mayocat.model.Attachment
import org.mayocat.model.EntityList
import org.mayocat.model.HasFeaturedImage
import org.mayocat.rest.api.object.ImageApiObject
import org.mayocat.rest.api.object.ImageGalleryApiObject
import org.mayocat.store.EntityDoesNotExistException
import org.mayocat.store.EntityListStore
import org.mayocat.store.InvalidEntityException

import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

import static org.mayocat.attachment.util.AttachmentUtils.isImage

/**
 * Delegate used to implement API for entities that have a standard image gallery
 *
 * @version $Id$
 */
@CompileStatic
class ImageGalleryApiDelegate
{
    EntityDataLoader dataLoader

    EntityListStore entityListStore

    AttachmentStore attachmentStore

    EntityApiDelegateHandler handler

    @Path("{slug}/images")
    @GET
    List<ImageApiObject> getImages(@PathParam("slug") String slug)
    {
        def images = [] as List<ImageApiObject>;
        HasFeaturedImage entity = handler.getEntity(slug) as HasFeaturedImage;
        if (entity == null) {
            throw new WebApplicationException(Response.status(404).build());
        }

        EntityData<?> data = dataLoader.load(entity)

        Optional<ImageGallery> gallery = data.getData(ImageGallery.class)

        if (!gallery.isPresent()) {
            return Collections.emptyList()
        }

        gallery.get().images.each({ Image image ->
            def imageApiObject = new ImageApiObject()
            imageApiObject.withImage(image)

            if (entity.featuredImageId != null && entity.featuredImageId.equals(image.attachment.id)) {
                imageApiObject.featured = true
            }
            images << imageApiObject
        })

        images;
    }


    @Path("{slug}/images/{imageSlug}")
    @Authorized
    @POST
    @Consumes(MediaType.WILDCARD)
    def updateImage(@PathParam("slug") String slug,
            @PathParam("imageSlug") String imageSlug, ImageApiObject image)
    {
        def attachment = attachmentStore.findBySlug(imageSlug);
        if (attachment == null) {
            return Response.status(404).build();
        }
        try {
            attachment.with {
                setTitle image.title
                setDescription image.description
                setLocalizedVersions image._localized
            }
            attachmentStore.update(attachment);
            return Response.noContent().build();
        } catch (EntityDoesNotExistException e) {
            return Response.status(404).build();
        } catch (InvalidEntityException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @Path("{slug}/images/{imageSlug}")
    @Authorized
    @DELETE
    @Consumes(MediaType.WILDCARD)
    def detachImage(@PathParam("slug") String slug,
            @PathParam("imageSlug") String imageSlug)
    {
        def attachment = attachmentStore.findBySlug(imageSlug);

        def entity = handler.getEntity(slug)
        if (entity == null) {
            return Response.status(404).entity("Entity not found").build();
        }

        def entityList = entityListStore.findListByHintAndParentId("image_gallery", entity.id)
        if (entityList != null && entityList.entities.indexOf(attachment.id) >= 0) {
            // remove from main gallery if necessary
            entityListStore.removeEntityFromList(entityList, attachment.id)
        }

        if (attachment == null) {
            return Response.status(404).build();
        }
        try {
            attachmentStore.detach(attachment);

            // Now check if the image was the featured image, in which case, we find another (first one on the list
            // that's not this one)
            if (HasFeaturedImage.class.isAssignableFrom(entity.class) &&
                    ((HasFeaturedImage) entity).featuredImageId == attachment.id)
            {
                // Detaching the featured image -> we need to find another
                ((HasFeaturedImage) entity).featuredImageId = entityList.entities.find({ UUID id ->
                    id != attachment.id
                })
                handler.updateEntity(entity)
            }

            return Response.noContent().build();
        } catch (EntityDoesNotExistException e) {
            return Response.status(404).build();
        }
    }

    @Path("{slug}/images/")
    @Authorized
    @POST
    def updateImageGallery(@PathParam("slug") String slug, ImageGalleryApiObject gallery)
    {
        def entity = handler.getEntity(slug) as HasFeaturedImage
        def allAttachments = attachmentStore.findAllChildrenOf(entity);
        def EntityList list = entityListStore.findListByHintAndParentId("image_gallery", entity.id);

        // 1. Update image gallery entity list

        if (list == null) {
            // This is not supposed to happen but who knows...
            // (the list is expected to be created as soon as an image is uploaded)
            list = entityListStore.getOrCreate(new EntityList([
                    slug: "image-gallery",
                    hint: "image_gallery",
                    type: handler.type(),
                    parentId: entity.id
            ]))
        }

        Collection<UUID> ids = gallery.images.collect({ ImageApiObject image ->
            allAttachments.find({ Attachment attachment -> attachment.slug == image.slug })?.id
        });

        list.entities = ids.findAll({ UUID id -> id != null }).toList();

        entityListStore.update(list)

        // 2. Set featured image

        ImageApiObject featured = gallery.images.find({ ImageApiObject object -> object.featured })

        if (!featured) {
            // there must always be a featured image
            featured = gallery.images.first()
        }

        def featuredImage = allAttachments.find({ Attachment attachment -> attachment.slug == featured.slug })

        if (entity.featuredImageId != featuredImage.id) {
            // Update if the featured image has changed
            entity.featuredImageId = featuredImage.id
            handler.updateEntity(entity)
        }
    }

    def afterImageAddedToGallery(HasFeaturedImage entity, String filename, Attachment attachment)
    {
        def EntityList list = entityListStore.getOrCreate(new EntityList([
                slug: "image-gallery",
                hint: "image_gallery",
                type: handler.type(),
                parentId: entity.id
        ]))

        entityListStore.addEntityToList(list, attachment.id)

        if (entity.featuredImageId == null && isImage(filename)) {

            // If this is an image and the entity doesn't have a featured image yet, and the attachment was
            // successful, the we set this image as featured image.
            entity.featuredImageId = attachment.id;

            handler.updateEntity(entity);
        }
    }
}
