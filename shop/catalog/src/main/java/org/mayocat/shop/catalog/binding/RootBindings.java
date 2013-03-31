package org.mayocat.shop.catalog.binding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.shop.catalog.CatalogService;
import org.mayocat.shop.catalog.meta.CollectionEntity;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.context.Execution;
import org.mayocat.shop.front.FrontBindingSupplier;
import org.mayocat.shop.front.annotation.Bindings;
import org.mayocat.shop.front.annotation.FrontBinding;
import org.mayocat.shop.front.bindings.BindingsContants;
import org.mayocat.shop.front.resources.ResourceResource;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.Lists;

/**
 * @version $Id$
 */
@Component("home")
public class RootBindings implements FrontBindingSupplier, BindingsContants
{
    public final static String SITE = "site";

    public final static String SITE_TITLE = "title";

    public final static String SITE_TAGLINE = "tagline";

    public static final String THEME_PATH = "THEME_PATH";

    @Inject
    private Execution execution;

    @Inject
    private CatalogService catalogService;

    @FrontBinding(path = "/")
    public void contributeRootBindings(@Bindings Map data)
    {
        final GeneralSettings config =  execution.getContext().getSettings(GeneralSettings.class);

        data.put(THEME_PATH, ResourceResource.PATH);

        data.put(SITE, new HashMap() {{
            put(SITE_TITLE, config.getName().getValue());
            put(SITE_TAGLINE, config.getTagline().getValue());
        }});

        List<Collection> collections = this.catalogService.findAllCollections(20, 0);
        List<Map<String, Object>> collectionsBinding = Lists.newArrayList();

        for (final Collection collection : collections) {
            collectionsBinding.add(new HashMap<String, Object>(){{
                put("url", "/" + CollectionEntity.PATH + "/" + collection.getSlug());
                put("title", collection.getTitle());
                put("description", collection.getDescription());
            }});
        }

        data.put(COLLECTIONS, collectionsBinding);

        // Put page title and description, mainly for the home page, this will typically get overridden by sub-pages
        data.put(PAGE_TITLE, config.getName().getValue());
        data.put(PAGE_DESCRIPTION, config.getTagline().getValue());
    }
}
