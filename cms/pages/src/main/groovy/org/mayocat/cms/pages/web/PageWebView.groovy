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
import org.mayocat.cms.pages.model.Page
import org.mayocat.cms.pages.store.PageStore
import org.mayocat.cms.pages.web.object.PageWebObject
import org.mayocat.context.WebContext
import org.mayocat.image.store.ThumbnailStore
import org.mayocat.localization.EntityLocalizationService
import org.mayocat.model.Attachment
import org.mayocat.rest.Resource
import org.mayocat.rest.annotation.ExistingTenant
import org.mayocat.rest.web.delegate.ImageGalleryWebViewDelegate
import org.mayocat.shop.front.views.ErrorWebView
import org.mayocat.shop.front.views.WebView
import org.mayocat.attachment.store.AttachmentStore
import org.mayocat.store.EntityListStore
import org.mayocat.theme.ThemeDefinition
import org.mayocat.theme.ThemeFileResolver
import org.mayocat.url.EntityURLFactory
import org.xwiki.component.annotation.Component
import org.xwiki.component.phase.Initializable

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
class PageWebView implements Resource, Initializable
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
    Provider<EntityListStore> entityListStore

    @Delegate
    ImageGalleryWebViewDelegate imageGalleryDelegate

    void initialize()
    {
        imageGalleryDelegate = new ImageGalleryWebViewDelegate([
                thumbnailStore: thumbnailStore.get(),
                attachmentStore: attachmentStore.get(),
                entityListStore: entityListStore.get(),
                localizationService: entityLocalizationService,
        ])
    }

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

        List<Attachment> attachments = this.attachmentStore.get().findAllChildrenOf(page)

        PageWebObject pageWebObject = new PageWebObject()
        pageWebObject.withPage(entityLocalizationService.localize(page) as Page, urlFactory,
                Optional.fromNullable(theme), themeFileResolver)
        pageWebObject.withImages(getImagesForImageGallery(page, attachments),
                page.featuredImageId, Optional.fromNullable(theme))

        context.put("page", pageWebObject)

        return new WebView().template("page.html").model(page.model).data(context)
    }
}
