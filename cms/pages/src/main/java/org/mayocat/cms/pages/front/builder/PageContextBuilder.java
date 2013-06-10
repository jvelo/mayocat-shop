package org.mayocat.cms.pages.front.builder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.mayocat.addons.model.AddonGroup;
import org.mayocat.cms.pages.front.resource.PageResource;
import org.mayocat.cms.pages.model.Page;
import org.mayocat.image.model.Image;
import org.mayocat.shop.front.builder.AddonContextBuilderHelper;
import org.mayocat.shop.front.builder.ImageContextBuilder;
import org.mayocat.shop.front.context.ContextConstants;
import org.mayocat.theme.Theme;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class PageContextBuilder
{
    private Theme theme;

    private ImageContextBuilder imageContextBuilder;

    public PageContextBuilder(Theme theme)
    {
        this.theme = theme;

        imageContextBuilder = new ImageContextBuilder(theme);
    }

    public Map<String, Object> build(final Page page, List<Image> images)
    {
        Map<String, Object> pageContext = Maps.newHashMap();

        pageContext.put("title", page.getTitle());
        pageContext.put("content", page.getContent());
        pageContext.put("published", page.getPublished());
        pageContext.put(ContextConstants.URL, PageResource.PATH + PageResource.SLASH + page.getSlug());
        pageContext.put(ContextConstants.SLUG, page.getSlug());

        Map<String, Object> imagesContext = Maps.newHashMap();
        List<Map<String, String>> allImages = Lists.newArrayList();

        if (images.size() > 0) {
            Image featuredImage = null;
            for (Image image : images) {
                if (featuredImage == null && image.getAttachment().getId().equals(page.getFeaturedImageId())) {
                    featuredImage = image;
                }
                allImages.add(imageContextBuilder.createImageContext(image, image == featuredImage));
            }
            if (featuredImage == null) {
                // If no featured image has been set, we use the first image in the array.
                featuredImage = images.get(0);
            }
            imagesContext.put("featured", imageContextBuilder.createImageContext(featuredImage, true));
        } else {
            Map<String, String> placeholder = imageContextBuilder.createPlaceholderImageContext();
            imagesContext.put("featured", placeholder);
            allImages = Arrays.asList(placeholder);
        }

        imagesContext.put("all", allImages);
        pageContext.put("images", imagesContext);

        // Addons

        if (page.getAddons().isLoaded()) {
            Map<String, Object> themeAddonsContext = Maps.newHashMap();
            Map<String, AddonGroup> themeAddons = theme.getAddons();
            for (String groupKey : themeAddons.keySet()) {

                AddonGroup group = themeAddons.get(groupKey);
                Map<String, Object> groupContext = Maps.newHashMap();

                for (String field : group.getFields().keySet()) {
                    Optional<org.mayocat.model.Addon> addon =
                            AddonContextBuilderHelper.findAddon(groupKey, field, page.getAddons().get());
                    if (addon.isPresent()) {
                        groupContext.put(field, addon.get().getValue());
                    } else {
                        groupContext.put(field, null);
                    }
                }

                themeAddonsContext.put(groupKey, groupContext);
            }
            pageContext.put("theme_addons", themeAddonsContext);
        }

        return pageContext;
    }
}
