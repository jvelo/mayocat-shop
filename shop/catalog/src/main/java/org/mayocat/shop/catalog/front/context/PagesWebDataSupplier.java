/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.front.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.attachment.store.AttachmentStore;
import org.mayocat.cms.pages.model.Page;
import org.mayocat.cms.pages.store.PageStore;
import org.mayocat.cms.pages.web.object.PageWebObject;
import org.mayocat.context.WebContext;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.localization.EntityLocalizationService;
import org.mayocat.model.Attachment;
import org.mayocat.shop.front.WebDataSupplier;
import org.mayocat.shop.front.util.WebDataHelper;
import org.mayocat.theme.ThemeDefinition;
import org.mayocat.theme.ThemeFileResolver;
import org.mayocat.url.EntityURLFactory;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;

/**
 * Data supplier for the list of root pages :
 *
 * {{#pages}}
 *    [[...]]
 * {{/pages}}
 *
 * @version $Id$
 */
@Component("pages")
public class PagesWebDataSupplier implements WebDataSupplier
{
    @Inject
    private Provider<PageStore> pageStore;

    @Inject
    private Provider<AttachmentStore> attachmentStore;

    @Inject
    private Provider<ThumbnailStore> thumbnailStore;

    @Inject
    private EntityURLFactory urlFactory;

    @Inject
    private EntityLocalizationService entityLocalizationService;

    @Inject
    private WebContext context;

    @Inject
    private ThemeFileResolver themeFileResolver;

    @Override
    public void supply(Map<String, Object> data)
    {
        ThemeDefinition theme = context.getTheme().getDefinition();

        // Pages
        List<Page> rootPages = this.pageStore.get().findAllRootPages();

        java.util.Collection<UUID> featuredImageIds =
                Collections2.transform(rootPages, WebDataHelper.ENTITY_FEATURED_IMAGE);
        List<UUID> ids = new ArrayList<>(Collections2.filter(featuredImageIds, Predicates.notNull()));
        List<Attachment> allImages;
        List<Thumbnail> allThumbnails;
        if (ids.isEmpty()) {
            allImages = Collections.emptyList();
            allThumbnails = Collections.emptyList();
        } else {
            allImages = this.attachmentStore.get().findByIds(ids);
            allThumbnails = this.thumbnailStore.get().findAllForIds(ids);
        }

        List<PageWebObject> pageWebObjectList = new ArrayList<>();

        for (final Page page : rootPages) {
            PageWebObject pageWebObject = new PageWebObject();
            pageWebObject.withPage(entityLocalizationService.localize(page), urlFactory,
                    themeFileResolver);
            java.util.Collection<Attachment> attachments = Collections2.filter(allImages,
                    WebDataHelper.isEntityFeaturedImage(page));
            List<Image> images = new ArrayList<>();
            for (final Attachment attachment : attachments) {
                java.util.Collection<Thumbnail> thumbnails =
                        Collections2.filter(allThumbnails, WebDataHelper.isThumbnailOfAttachment(attachment));
                Image image = new Image(attachment, new ArrayList<>(thumbnails));
                images.add(image);
            }
            pageWebObject.withImages(images, page.getFeaturedImageId(),
                    Optional.fromNullable(context.getTheme() != null ? context.getTheme().getDefinition() : null));

            pageWebObjectList.add(pageWebObject);
        }
        data.put("pages", pageWebObjectList);
    }
}
