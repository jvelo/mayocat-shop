/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.web

import com.google.common.base.Predicates
import com.google.common.collect.Collections2
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.common.math.IntMath
import org.mayocat.cms.news.front.resource.ArticleContextBuilder
import org.mayocat.cms.news.model.Article
import org.mayocat.cms.news.store.ArticleStore
import org.mayocat.cms.pages.front.builder.PageContextBuilder
import org.mayocat.cms.pages.model.Page
import org.mayocat.cms.pages.store.PageStore
import org.mayocat.image.model.Image
import org.mayocat.image.model.Thumbnail
import org.mayocat.model.Attachment
import org.mayocat.model.EntityList
import org.mayocat.rest.Resource
import org.mayocat.rest.annotation.ExistingTenant
import org.mayocat.shop.catalog.front.resource.AbstractProductListWebViewResource
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.catalog.store.ProductStore
import org.mayocat.shop.front.builder.PaginationContextBuilder
import org.mayocat.shop.front.context.ContextConstants
import org.mayocat.shop.front.resources.AbstractWebViewResource
import org.mayocat.shop.front.util.WebDataHelper
import org.mayocat.shop.front.views.WebView
import org.mayocat.store.EntityListStore
import org.mayocat.theme.ThemeDefinition
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

import static org.mayocat.shop.front.util.WebDataHelper.isEntityFeaturedImage
import static org.mayocat.shop.front.util.WebDataHelper.isThumbnailOfAttachment

/**
 * @version $Id: c73b626db3d0cda9da6d8f9b4b0d674667913958 $
 */
@Component("/")
@Path("/")
@Produces([MediaType.TEXT_HTML, MediaType.APPLICATION_JSON])
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
class HomeWebView extends AbstractProductListWebViewResource implements Resource
{
    @Inject
    Provider<ProductStore> productStore;

    @Inject
    Provider<PageStore> pageStore;

    @Inject
    Provider<ArticleStore> articleStore;

    @Inject
    Provider<EntityListStore> entityListStore

    @GET
    def getHomePage()
    {
        Map<String, Object> context = new HashMap<>();

        // Featured products

        def List<EntityList> lists = entityListStore.get().findListsByHint("home_featured_products");
        if (!lists.isEmpty() && !lists.first().entities.isEmpty()) {
            List<Product> products = productStore.get().findByIds(lists.first().entities)
            List<Product> sorted = lists.first().entities.collect({ UUID id ->
                products.find({ Product product -> product.id == id })
            })
            context.put("featuredProducts", createProductListContextList(sorted));

        }

        // All products

        Integer numberOfProducts =
                this.context.getTheme().getDefinition().getPaginationDefinition("home").getItemsPerPage();
        List<Product> products = this.productStore.get().findAllOnShelf(numberOfProducts, 0);
        Integer totalCount = this.productStore.get().countAllOnShelf();
        Integer totalPages = IntMath.divide(totalCount, numberOfProducts, RoundingMode.UP);
        context.put("products", createProductListContext(1, totalPages, products,
                new PaginationContextBuilder.UrlBuilder() {
                    public String build(int page)
                    {
                        return MessageFormat.format("/products/?page={0}", page);
                    }
                }));

        // Home page content

        final Page page = pageStore.get().findBySlug("home");
        if (page != null) {
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

            PageContextBuilder builder = new PageContextBuilder(themeFileResolver, urlFactory, theme);
            Map<String, Object> pageContext = builder.build(entityLocalizationService.localize(page), images);
            context.put("home", pageContext);
        }

        // News articles

        Integer numberOfArticlesPerPAge = this.context.getTheme().getDefinition()
                .getPaginationDefinition("home").getOtherDefinition("articles").or(6);

        List<Article> articles = articleStore.get().findAllPublished(0, numberOfArticlesPerPAge);

        Collection<UUID> featuredImageIds = Collections2.transform(articles,
                WebDataHelper.ENTITY_FEATURED_IMAGE);
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

        Map<String, Object> articlesContext = Maps.newHashMap();
        List<Map<String, Object>> articleListContext = Lists.newArrayList();
        ArticleContextBuilder articleContextBuilder = new ArticleContextBuilder(this.context.getTheme().getDefinition(),
                this.configurationService, this.urlFactory, themeFileResolver);

        for (final Article article : articles) {
            Collection<Attachment> attachments =
                    Collections2.filter(allImages, isEntityFeaturedImage(article));
            List<Image> images = new ArrayList<>();
            for (final Attachment attachment : attachments) {
                Collection<Thumbnail> thumbnails =
                        Collections2.filter(allThumbnails, isThumbnailOfAttachment(attachment));
                Image image = new Image(entityLocalizationService.localize(attachment), new ArrayList<>(thumbnails));
                images.add(image);
            }

            Map<String, Object> articleContext = articleContextBuilder.build(article, images);
            articleListContext.add(articleContext);
        }

        articlesContext.put("list", articleListContext);
        context.put("articles", articlesContext);

        return new WebView().template("home.html").data(context);
    }
}
