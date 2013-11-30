/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.front.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mayocat.cms.pages.front.builder.PageContextBuilder;
import org.mayocat.cms.pages.model.Page;
import org.mayocat.cms.pages.store.PageStore;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.model.Attachment;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.views.FrontView;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.shop.front.context.ContextConstants;
import org.mayocat.shop.front.resources.AbstractFrontResource;
import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.ThemeDefinition;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component(HomeResource.PATH)
@Path(HomeResource.PATH)
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class HomeResource extends AbstractProductListFrontResource implements Resource
{
    @Inject
    private Provider<ProductStore> productStore;

    public static final String PATH = ROOT_PATH;

    @Inject
    private Provider<PageStore> pageStore;

    @GET
    public FrontView getHomePage(@Context Breakpoint breakpoint, @Context UriInfo uriInfo)
    {
        FrontView result = new FrontView("home", breakpoint);
        Map<String, Object> context = getContext(uriInfo);

        Integer numberOfProducts =
                this.context.getTheme().getDefinition().getPaginationDefinition("home").getItemsPerPage();
        List<Product> products = this.productStore.get().findAllOnShelf(numberOfProducts, 0);
        context.put("products", createProductListContext(products));
        result.putContext(context);

        final Page page = pageStore.get().findBySlug("home");
        if (page != null) {
            context.put(ContextConstants.PAGE_TITLE, page.getTitle());
            context.put(ContextConstants.PAGE_DESCRIPTION, page.getContent());

            ThemeDefinition theme = this.context.getTheme().getDefinition();

            List<Attachment> attachments = this.attachmentStore.get().findAllChildrenOf(page, Arrays
                    .asList("png", "jpg", "jpeg", "gif"));
            List<Image> images = new ArrayList<>();
            for (Attachment attachment : attachments) {
                if (AbstractFrontResource.isImage(attachment)) {
                    List<Thumbnail> thumbnails = thumbnailStore.get().findAll(attachment);
                    Image image = new Image(entityLocalizationService.localize(attachment), thumbnails);
                    images.add(image);
                }
            }

            PageContextBuilder builder = new PageContextBuilder(urlFactory, theme);
            Map<String, Object> pageContext = builder.build(entityLocalizationService.localize(page), images);
            context.put("home", pageContext);
        }
        result.putContext(context);
        return result;
    }
}
