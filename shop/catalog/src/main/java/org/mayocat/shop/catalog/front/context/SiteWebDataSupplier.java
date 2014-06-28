/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.front.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.addons.front.builder.AddonContextBuilder;
import org.mayocat.addons.model.AddonGroupDefinition;
import org.mayocat.configuration.PlatformSettings;
import org.mayocat.context.WebContext;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Attachment;
import org.mayocat.shop.front.WebDataSupplier;
import org.mayocat.shop.front.builder.ImageContextBuilder;
import org.mayocat.shop.front.resources.AbstractWebViewResource;
import org.mayocat.store.AttachmentStore;
import org.mayocat.theme.ThemeDefinition;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.Maps;

/**
 * Data supplier that brings site related data into the map (site title, site logo, addons, etc.)
 *
 * @version $Id$
 */
@Component("site")
public class SiteWebDataSupplier implements WebDataSupplier
{
    public final static String SITE = "site";

    public final static String SITE_TITLE = "title";

    public final static String SITE_DESCRIPTION = "description";

    @Inject
    private WebContext context;

    @Inject
    private Provider<AttachmentStore> attachmentStore;

    @Inject
    private Provider<ThumbnailStore> thumbnailStore;

    @Inject
    private PlatformSettings platformSettings;

    @Override
    public void supply(Map<String, Object> data)
    {
        Tenant tenant = context.getTenant();
        ThemeDefinition theme = context.getTheme().getDefinition();

        Map site = Maps.newHashMap();
        site.put(SITE_TITLE, tenant.getName());
        site.put(SITE_DESCRIPTION, tenant.getDescription());
        ImageContextBuilder imageContextBuilder = new ImageContextBuilder(theme);

        List<Attachment> siteAttachments = this.attachmentStore.get().findAllChildrenOf(tenant);
        List<Image> siteImages = new ArrayList<Image>();
        for (Attachment attachment : siteAttachments) {
            if (AbstractWebViewResource.isImage(attachment)) {
                if (attachment.getId().equals(tenant.getFeaturedImageId())) {
                    List<Thumbnail> thumbnails = thumbnailStore.get().findAll(attachment);
                    Image image = new Image(attachment, thumbnails);
                    siteImages.add(image);
                    site.put("logo", imageContextBuilder.createImageContext(image, true));
                }
            }
        }

        if (tenant.getAddons().isLoaded()) {
            AddonContextBuilder addonContextBuilder = new AddonContextBuilder();
            Map<String, AddonGroupDefinition> platformAddons = platformSettings.getAddons();
            site.put("platform_addons",
                    addonContextBuilder.build(platformAddons, tenant.getAddons().get(), "platform"));
        }

        data.put(SITE, site);

    }
}
