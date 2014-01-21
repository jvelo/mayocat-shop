/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.pages.front.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mayocat.cms.pages.front.builder.PageContextBuilder;
import org.mayocat.cms.pages.meta.PageEntity;
import org.mayocat.context.WebContext;
import org.mayocat.localization.EntityLocalizationService;
import org.mayocat.rest.Resource;
import org.mayocat.cms.pages.model.Page;
import org.mayocat.cms.pages.store.PageStore;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Attachment;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.shop.front.resources.AbstractWebViewResource;
import org.mayocat.shop.front.views.ErrorWebView;
import org.mayocat.shop.front.views.WebView;
import org.mayocat.shop.front.context.ContextConstants;
import org.mayocat.store.AttachmentStore;
import org.mayocat.theme.ThemeDefinition;
import org.mayocat.theme.ThemeFileResolver;
import org.mayocat.url.EntityURLFactory;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component(PageResource.PATH)
@Path(PageResource.PATH)
@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class PageResource extends AbstractWebViewResource implements Resource
{
    public static final String PATH = ROOT_PATH + PageEntity.PATH;

    @Inject
    private Provider<PageStore> pageStore;

    @Inject
    private Provider<AttachmentStore> attachmentStore;

    @Inject
    private Provider<ThumbnailStore> thumbnailStore;

    @Inject
    private WebContext context;

    @Inject
    private EntityLocalizationService entityLocalizationService;

    @Inject
    private EntityURLFactory entityURLFactory;

    @Inject
    private ThemeFileResolver themeFileResolver;

    @Path("{slug}")
    @GET
    public WebView getPage(@PathParam("slug") String slug, @Context UriInfo uriInfo)
    {
        final Page page = pageStore.get().findBySlug(slug);
        if (page == null) {
            return new ErrorWebView().status(404);
        }

        Map<String, Object> context = new HashMap<>();

        context.put(ContextConstants.PAGE_TITLE, page.getTitle());
        context.put(ContextConstants.PAGE_DESCRIPTION, page.getContent());

        ThemeDefinition theme = this.context.getTheme().getDefinition();

        List<Attachment> attachments = this.attachmentStore.get().findAllChildrenOf(page, Arrays
                .asList("png", "jpg", "jpeg", "gif"));
        List<Image> images = new ArrayList<>();
        for (Attachment attachment : attachments) {
            if (AbstractWebViewResource.isImage(attachment)) {
                List<Thumbnail> thumbnails = thumbnailStore.get().findAll(attachment);
                Image image = new Image(entityLocalizationService.localize(attachment), thumbnails);
                images.add(image);
            }
        }

        PageContextBuilder builder = new PageContextBuilder(themeFileResolver, entityURLFactory, theme);
        Map<String, Object> pageContext = builder.build(entityLocalizationService.localize(page), images);

        context.put("page", pageContext);

        return new WebView().template("page.html").model(page.getModel()).data(context);
    }
}
