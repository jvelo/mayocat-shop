/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.web

import com.google.common.base.Optional
import com.google.common.math.IntMath
import groovy.transform.CompileStatic
import org.mayocat.addons.web.AddonsWebObjectBuilder
import org.mayocat.attachment.AttachmentLoadingOptions
import org.mayocat.cms.news.model.Article
import org.mayocat.cms.news.store.ArticleStore
import org.mayocat.cms.news.web.object.ArticleListWebObject
import org.mayocat.cms.news.web.object.ArticleWebObject
import org.mayocat.cms.pages.model.Page
import org.mayocat.cms.pages.store.PageStore
import org.mayocat.cms.pages.web.object.PageWebObject
import org.mayocat.configuration.ConfigurationService
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.context.WebContext
import org.mayocat.entity.EntityData
import org.mayocat.entity.EntityDataLoader
import org.mayocat.entity.StandardOptions
import org.mayocat.image.model.Image
import org.mayocat.localization.EntityLocalizationService
import org.mayocat.model.EntityList
import org.mayocat.rest.Resource
import org.mayocat.rest.annotation.ExistingTenant
import org.mayocat.rest.web.object.PaginationWebObject
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.catalog.model.ProductCollection
import org.mayocat.shop.catalog.store.CollectionStore
import org.mayocat.shop.catalog.store.ProductStore
import org.mayocat.shop.front.context.ContextConstants
import org.mayocat.shop.front.views.WebView
import org.mayocat.store.EntityListStore
import org.mayocat.theme.ThemeDefinition
import org.mayocat.theme.ThemeFileResolver
import org.mayocat.url.EntityURLFactory
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.inject.Provider
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import java.math.RoundingMode
import java.text.MessageFormat

/**
 * @version $Id: c73b626db3d0cda9da6d8f9b4b0d674667913958 $
 */
@Component("/")
@Path("/")
@Produces([MediaType.TEXT_HTML, MediaType.APPLICATION_JSON])
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
@CompileStatic
class HomeWebView implements Resource
{
    @Inject
    Provider<ProductStore> productStore;

    @Inject
    Provider<PageStore> pageStore;

    @Inject
    Provider<ArticleStore> articleStore;

    @Inject
    Provider<EntityListStore> entityListStore

    @Inject
    EntityDataLoader dataLoader

    @Inject
    WebContext webContext

    @Inject
    Provider<CollectionStore> collectionStoreProvider

    @Inject
    EntityURLFactory urlFactory

    @Inject
    ThemeFileResolver themeFileResolver

    @Inject
    EntityLocalizationService entityLocalizationService

    @Inject
    ConfigurationService configurationService

    @Inject
    AddonsWebObjectBuilder addonsWebObjectBuilder

    @Inject
    @Delegate
    ProductListWebViewDelegate listWebViewDelegate

    @GET
    def getHomePage()
    {
        Map<String, Object> context = new HashMap<>();

        ThemeDefinition theme = this.webContext.theme?.definition

        // Featured products

        def List<EntityList> lists = entityListStore.get().findListsByHint("home_featured_products");
        if (!lists.isEmpty() && !lists.first().entities.isEmpty()) {
            List<Product> products = productStore.get().findByIds(lists.first().entities)
            List<Product> sorted = lists.first().entities.collect({ UUID id ->
                products.find({ Product product -> product.id == id })
            })

            List<EntityData<Product>> productsData = dataLoader.
                    load(sorted, AttachmentLoadingOptions.FEATURED_IMAGE_ONLY, StandardOptions.LOCALIZE)
            context.put("featuredProducts", buildProductListListWebObject(productsData, Optional.absent()));
        }

        // All products

        Integer numberOfProducts =
                this.webContext.getTheme().definition.getPaginationDefinition("home").itemsPerPage;
        List<Product> products = this.productStore.get().findAllOnShelf(numberOfProducts, 0)
        List<EntityData> productsData = dataLoader.load(products,
                AttachmentLoadingOptions.FEATURED_IMAGE_ONLY,
                StandardOptions.LOCALIZE
        )
        List<UUID> productIds = products.collect { Product product -> product.id }
        List<org.mayocat.shop.catalog.model.Collection> collections = collectionStoreProvider.get().
                findAllForProductIds(productIds)
        List<ProductCollection> productsCollections = collectionStoreProvider.get().
                findAllProductsCollectionsForIds(productIds)

        products.each({ Product product ->
            def productCollections = productsCollections.findAll { ProductCollection productCollection ->
                productCollection.productId == product.id
            }
            productCollections = productCollections.collect({ ProductCollection pc ->
                collections.find({ org.mayocat.shop.catalog.model.Collection c -> pc.collectionId == c.id })
            })
            product.setCollections(productCollections)
        })

        Integer totalCount = this.productStore.get().countAllOnShelf();
        Integer totalPages = IntMath.divide(totalCount, numberOfProducts, RoundingMode.UP);
        context.put("products", buildProductListWebObject(1, totalPages, productsData, {
            Integer page -> MessageFormat.format("/products/?page={0}", page);
        }))

        // Home page content

        final Page page = pageStore.get().findBySlug("home");
        if (page != null) {
            context.put(ContextConstants.PAGE_TITLE, page.getTitle());
            context.put(ContextConstants.PAGE_DESCRIPTION, page.getContent());

            EntityData<Page> pageData = dataLoader.load(page, StandardOptions.LOCALIZE)
            List<Image> images = pageData.getDataList(Image.class)

            PageWebObject pageWebObject = new PageWebObject()
            pageWebObject.withPage(entityLocalizationService.localize(page) as Page, urlFactory
                    , themeFileResolver)
            pageWebObject.withAddons(addonsWebObjectBuilder.build(pageData))
            pageWebObject.withImages(images, page.featuredImageId, Optional.fromNullable(theme))
            context.put("home", pageWebObject);
        }

        // News articles

        GeneralSettings generalSettings = configurationService.getSettings(GeneralSettings.class) // <o

        Integer numberOfArticlesPerPAge = theme ? theme.getPaginationDefinition("home")
                .getOtherDefinition("articles").or(6) : 6;

        List<Article> articles = articleStore.get().findAllPublished(0, numberOfArticlesPerPAge);
        List<EntityData<Article>> articlesData = dataLoader.load(articles,
                StandardOptions.LOCALIZE,
                AttachmentLoadingOptions.FEATURED_IMAGE_ONLY
        )

        List<ArticleWebObject> articleList = []

        articlesData.each({ EntityData<Article> articleData ->
            Article article = articleData.entity
            List<Image> images = articleData.getDataList(Image.class)

            ArticleWebObject articleWebObject = new ArticleWebObject()
            articleWebObject.withArticle(article, urlFactory, generalSettings.locales.mainLocale.value);
            articleWebObject.withAddons(addonsWebObjectBuilder.build(articleData))
            articleWebObject.withImages(images, article.featuredImageId, Optional.fromNullable(theme))

            articleList << articleWebObject
        })

        PaginationWebObject pagination = new PaginationWebObject()
        pagination.withPages(1, totalPages, { Integer p -> MessageFormat.format("/news/{0}", p) });

        context.put("articles", new ArticleListWebObject(
                pagination: pagination,
                list: articleList
        ));

        return new WebView().template("home.html").data(context);
    }
}
