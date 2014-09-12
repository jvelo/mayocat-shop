package org.mayocat.shop.marketplace.web

import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.entity.EntityData
import org.mayocat.entity.EntityDataLoader
import org.mayocat.entity.StandardOptions
import org.mayocat.image.model.Image
import org.mayocat.image.model.ImageGallery
import org.mayocat.rest.Resource
import org.mayocat.shop.catalog.store.CollectionStore
import org.mayocat.shop.catalog.web.object.CollectionWebObject
import org.mayocat.shop.front.views.ErrorWebView
import org.mayocat.shop.front.views.WebView
import org.mayocat.url.EntityURLFactory
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.inject.Provider
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * @version $Id$
 */
@Component("/marketplace/collections")
@Path("/marketplace/collections")
@Produces([MediaType.TEXT_HTML, MediaType.APPLICATION_JSON])
@CompileStatic
class MarketplaceCollectionWebView implements Resource
{
    @Inject
    EntityDataLoader dataLoader

    @Inject
    Provider<CollectionStore> collectionStore

    @Inject
    EntityURLFactory urlFactory

    @GET
    @Path("{slug}")
    def getCollection(@PathParam("slug") String slug, @QueryParam("page") @DefaultValue("1") Integer page)
    {
        def context = [:]

        final org.mayocat.shop.catalog.model.Collection collection = collectionStore.get().findBySlug(slug)

        if (collection == null) {
            return new ErrorWebView().status(404)
        }

        // Add category

        EntityData<org.mayocat.shop.catalog.model.Collection> data = dataLoader.
                load(collection, StandardOptions.LOCALIZE)

        Optional<ImageGallery> gallery = data.getData(ImageGallery.class)
        List<Image> images = gallery.isPresent() ? gallery.get().images : [] as List<Image>

        CollectionWebObject collectionWebObject = new CollectionWebObject()
        collectionWebObject.withCollection(data.entity, urlFactory)

        collectionWebObject.withImages(images, collection.featuredImageId, Optional.absent())

        context.put("collection", collectionWebObject)

        new WebView().data(context)
    }
}
