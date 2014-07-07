/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.pages.web

import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.addons.web.AddonsWebObjectBuilder
import org.mayocat.attachment.store.AttachmentStore
import org.mayocat.cms.pages.model.Page
import org.mayocat.cms.pages.store.PageStore
import org.mayocat.cms.pages.web.object.PageWebObject
import org.mayocat.context.WebContext
import org.mayocat.entity.EntityData
import org.mayocat.entity.EntityDataLoader
import org.mayocat.entity.StandardOptions
import org.mayocat.image.model.Image
import org.mayocat.image.model.ImageGallery
import org.mayocat.image.store.ThumbnailStore
import org.mayocat.localization.EntityLocalizationService
import org.mayocat.rest.Resource
import org.mayocat.rest.annotation.ExistingTenant
import org.mayocat.shop.front.views.ErrorWebView
import org.mayocat.shop.front.views.WebView
import org.mayocat.theme.ThemeDefinition
import org.mayocat.theme.ThemeFileResolver
import org.mayocat.url.EntityURLFactory
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.inject.Provider
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Web view of a {@link Page}
 *
 * @version $Id$
 */
@Component("/pages")
@Path("/pages")
@Produces([MediaType.TEXT_HTML, MediaType.APPLICATION_JSON ])
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
@CompileStatic
class PageWebView implements Resource
{
    @Inject
    Provider<PageStore> pageStore

    @Inject
    Provider<AttachmentStore> attachmentStore

    @Inject
    Provider<ThumbnailStore> thumbnailStore

    @Inject
    WebContext context

    @Inject
    EntityLocalizationService entityLocalizationService

    @Inject
    EntityURLFactory urlFactory

    @Inject
    ThemeFileResolver themeFileResolver

    @Inject
    AddonsWebObjectBuilder addonsWebObjectBuilder

    @Inject
    EntityDataLoader dataLoader

    @Path("{slug}")
    @GET
    def getPage(@PathParam("slug") String slug)
    {
        Page page = pageStore.get().findBySlug(slug)
        if (page == null) {
            return new ErrorWebView().status(404)
        }

        def context = new HashMap<String, Object>([
                "title": page.title,
                "content": page.content
        ])

        ThemeDefinition theme = this.context.theme?.definition

        EntityData<Page> data = dataLoader.load(page, StandardOptions.LOCALIZE)

        Optional<ImageGallery> gallery = data.getData(ImageGallery.class);
        List<Image> images = gallery.isPresent() ? gallery.get().images : [] as List<Image>

        PageWebObject pageWebObject = new PageWebObject()
        pageWebObject.withPage(entityLocalizationService.localize(page) as Page, urlFactory
                , themeFileResolver)
        pageWebObject.withAddons(addonsWebObjectBuilder.build(data))
        pageWebObject.withImages(images, page.featuredImageId, Optional.fromNullable(theme))

        context.put("page", pageWebObject)

        return new WebView().template("page.html").model(page.model).data(context)
    }
}
