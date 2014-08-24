/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.front.context;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.addons.web.AddonsWebObjectBuilder;
import org.mayocat.attachment.AttachmentLoadingOptions;
import org.mayocat.attachment.store.AttachmentStore;
import org.mayocat.configuration.PlatformSettings;
import org.mayocat.context.WebContext;
import org.mayocat.entity.EntityData;
import org.mayocat.entity.EntityDataLoader;
import org.mayocat.entity.StandardOptions;
import org.mayocat.image.model.Image;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.shop.front.WebDataSupplier;
import org.mayocat.shop.front.builder.ImageContextBuilder;
import org.mayocat.theme.ThemeDefinition;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
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

    @Inject
    private EntityDataLoader dataLoader;

    @Inject
    private AddonsWebObjectBuilder addonsWebObjectBuilder;

    @Override
    public void supply(Map<String, Object> data)
    {
        final Tenant tenant = context.getTenant();
        ThemeDefinition theme = context.getTheme().getDefinition();

        Map site = Maps.newHashMap();
        site.put(SITE_TITLE, tenant.getName());
        site.put(SITE_DESCRIPTION, tenant.getDescription());
        ImageContextBuilder imageContextBuilder = new ImageContextBuilder(theme);

        EntityData<Tenant> tenantData =
                dataLoader.load(tenant, StandardOptions.LOCALIZE, AttachmentLoadingOptions.FEATURED_IMAGE_ONLY);

        List<Image> images = tenantData.getDataList(Image.class);
        Optional<Image> logo = FluentIterable.from(images).firstMatch(new Predicate<Image>()
        {
            public boolean apply(@Nullable Image input)
            {
                return input.getAttachment().getId().equals(tenant.getFeaturedImageId());
            }
        });

        if (logo.isPresent()) {
            site.put("logo", imageContextBuilder.createImageContext(logo.get(), true));
        }

        if (tenant.getAddons().isLoaded()) {
            Map<String, Object> addons = addonsWebObjectBuilder.build(tenantData);
            site.put("addons", addons);

            // For backward compatibility
            site.put("platform_addons", addons);
        }

        data.put(SITE, site);
    }
}
