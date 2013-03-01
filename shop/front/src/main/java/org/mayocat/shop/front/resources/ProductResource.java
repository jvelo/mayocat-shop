package org.mayocat.shop.front.resources;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mayocat.configuration.ConfigurationSource;
import org.mayocat.configuration.general.GeneralConfiguration;
import org.mayocat.context.Execution;
import org.mayocat.shop.catalog.CatalogService;
import org.mayocat.shop.catalog.configuration.shop.CatalogConfiguration;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.front.FrontBindingManager;
import org.mayocat.shop.front.bindings.BindingsContants;
import org.mayocat.shop.rest.annotation.ExistingTenant;
import org.mayocat.base.Resource;
import org.mayocat.shop.rest.views.FrontView;
import org.mayocat.theme.Breakpoint;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
@Component("/product/")
@Path("/product/")
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class ProductResource implements Resource, BindingsContants
{
    @Inject
    @Named("catalog")
    private Map<String, ConfigurationSource> configurationSources;

    @Inject
    private CatalogService catalogService;

    @Inject
    private FrontBindingManager bindingManager;

    @Path("{slug}")
    @GET
    public FrontView getProduct(@PathParam("slug") String slug, @Context Breakpoint breakpoint,
            @Context UriInfo uriInfo)
    {
        final Product product = this.catalogService.findProductBySlug(slug);
        if (product == null) {
            return new FrontView("404", breakpoint);
        }

        FrontView result = new FrontView("product", breakpoint);

        Map<String, Object> bindings = bindingManager.getBindings(uriInfo.getPathSegments());

        bindings.put(PAGE_TITLE, product.getTitle());
        bindings.put(PAGE_DESCRIPTION, product.getDescription());

        // TODO Introduce a notion of "Front representation"
        Map<String, Object> productContext = Maps.newHashMap();
        productContext.put("title", product.getTitle());
        productContext.put("description", product.getDescription());

        // Prices
        if (product.getPrice() != null) {
            final CatalogConfiguration configuration = (CatalogConfiguration) configurationSources.get("catalog").get();
            final GeneralConfiguration generalConfiguration =
                    (GeneralConfiguration) configurationSources.get("general").get();

            final Locale locale = generalConfiguration.getLocales().getMainLocale().getValue();
            final Currency currency = configuration.getCurrencies().getMainCurrency().getValue();
            productContext.put("price", new HashMap<String, Object>()
            {{
                    put("amount", product.getPrice());
                    put("currency", new HashMap<String, Object>()
                    {{
                            put("code", currency.getCurrencyCode());
                            put("symbol", currency.getSymbol(locale));
                    }});
            }});

            // TODO
            // - distinguish between two symbols : "absolute" and "internationalized" (i.e. "$" vs. "US$")
            // - look into amount formatting.
            // Check http://joda-money.sourceforge.net/apidocs/org/joda/money/format/MoneyFormatter.html
            // - handle multiple prices (unit, discounts, etc.)
        }

        // Images
        productContext.put("images", new HashMap<String, Object>()
        {{
            put("featured", new HashMap<String, Object>() {{
                put("theme_small_url", "http://placehold.it/150x150");
                put("theme_large_url", "http://placehold.it/450x450");
            }});
            put("all", new ArrayList<Map<String, Object>>(){{
                add(new HashMap<String, Object>(){{
                    put("theme_small_url", "http://placehold.it/150x150");
                    put("theme_large_url", "http://placehold.it/450x450");
                }});
                add(new HashMap<String, Object>(){{
                    put("theme_small_url", "http://placehold.it/150x150");
                    put("theme_large_url", "http://placehold.it/450x450");
                }});
                add(new HashMap<String, Object>(){{
                    put("theme_small_url", "http://placehold.it/150x150");
                    put("theme_large_url", "http://placehold.it/450x450");
                }});
                add(new HashMap<String, Object>(){{
                    put("theme_small_url", "http://placehold.it/150x150");
                    put("theme_large_url", "http://placehold.it/450x450");
                }});
            }});
        }});

        bindings.put("product", productContext);
        result.putBindings(bindings);

        return result;
    }
}
