package org.mayocat.shop.front.bindings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.mayocat.shop.configuration.general.GeneralConfiguration;
import org.mayocat.shop.context.Execution;
import org.mayocat.shop.front.FrontBindingSupplier;
import org.mayocat.shop.front.annotation.Bindings;
import org.mayocat.shop.front.annotation.FrontBinding;
import org.mayocat.shop.model.Category;
import org.mayocat.shop.service.CatalogService;
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

    @Inject
    private Execution execution;

    @Inject
    private CatalogService catalogService;

    @FrontBinding(path = "/")
    public void contributeRootBindings(@Bindings Map data)
    {
        final GeneralConfiguration config =
                (GeneralConfiguration) execution.getContext().getConfiguration(GeneralConfiguration.class);

        data.put(SITE, new HashMap() {{
            put(SITE_TITLE, config.getName().getValue());
            put(SITE_TAGLINE, config.getTagline().getValue());
        }});

        List<Category> categories = this.catalogService.findAllCategories(20, 0);
        List<Map<String, Object>> categoriesBinding = Lists.newArrayList();

        for (final Category category : categories) {
            categoriesBinding.add(new HashMap<String, Object>(){{
                put("url", "/category/" + category.getSlug());
                put("title", category.getTitle());
                put("description", category.getDescription());
            }});
        }

        data.put(CATEGORIES, categoriesBinding);

        // Put page title and description, mainly for the home page, this will typically get overridden by sub-pages
        data.put(PAGE_TITLE, config.getName().getValue());
        data.put(PAGE_DESCRIPTION, config.getTagline().getValue());
    }
}