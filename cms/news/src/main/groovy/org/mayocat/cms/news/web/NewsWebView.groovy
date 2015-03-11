/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.news.web

import com.google.common.base.Optional
import com.google.common.collect.Maps
import com.google.common.math.IntMath
import com.googlecode.flyway.core.util.StringUtils
import groovy.transform.CompileStatic
import org.apache.commons.lang3.text.StrSubstitutor
import org.mayocat.addons.web.AddonsWebObjectBuilder
import org.mayocat.attachment.AttachmentLoadingOptions
import org.mayocat.attachment.store.AttachmentStore
import org.mayocat.cms.news.NewsSettings
import org.mayocat.cms.news.model.Article
import org.mayocat.cms.news.store.ArticleStore
import org.mayocat.cms.news.web.object.ArticleListWebObject
import org.mayocat.cms.news.web.object.ArticleWebObject
import org.mayocat.configuration.ConfigurationService
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.context.WebContext
import org.mayocat.entity.EntityData
import org.mayocat.entity.EntityDataLoader
import org.mayocat.entity.StandardOptions
import org.mayocat.image.model.Image
import org.mayocat.image.model.ImageGallery
import org.mayocat.image.store.ThumbnailStore
import org.mayocat.rest.Resource
import org.mayocat.rest.annotation.ExistingTenant
import org.mayocat.rest.web.object.PaginationWebObject
import org.mayocat.shop.front.context.ContextConstants
import org.mayocat.shop.front.views.ErrorWebView
import org.mayocat.shop.front.views.WebView
import org.mayocat.theme.ThemeDefinition
import org.mayocat.url.EntityURLFactory
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.inject.Provider
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import java.math.RoundingMode
import java.text.MessageFormat

/**
 * Web view for {@link Article}
 *
 * @version $Id$
 */
@Component("/news")
@Path("/news")
@Produces([MediaType.TEXT_HTML, MediaType.APPLICATION_JSON])
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
@CompileStatic
class NewsWebView implements Resource
{
    @Inject
    Provider<ArticleStore> articleStore

    @Inject
    Provider<AttachmentStore> attachmentStore

    @Inject
    Provider<ThumbnailStore> thumbnailStore

    @Inject
    WebContext context

    @Inject
    EntityURLFactory urlFactory

    @Inject
    ConfigurationService configurationService

    @Inject
    AddonsWebObjectBuilder addonsWebObjectBuilder

    @Inject
    EntityDataLoader dataLoader

    @GET
    def getNews(@QueryParam("page") @DefaultValue("1") Integer page)
    {
        final int currentPage = page < 1 ? 1 : page
        Integer numberOfArticlesPerPAge =
                context.theme.definition.getPaginationDefinition("news").getItemsPerPage()

        Integer offset = (page - 1) * numberOfArticlesPerPAge
        Integer totalCount = this.articleStore.get().countAllPublished()
        Integer totalPages = IntMath.divide(totalCount, numberOfArticlesPerPAge, RoundingMode.UP);

        Map<String, Object> context = new HashMap<>();

        // Compute news page name
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("siteName", this.context.getTenant().getName());
        StrSubstitutor substitutor = new StrSubstitutor(parameters, "{{", "}}");
        NewsSettings settings = configurationService.getSettings(NewsSettings.class);
        context.put(ContextConstants.PAGE_TITLE, substitutor.replace(settings.newsPageTitle.value))

        List<Article> articles = articleStore.get().findAllPublished(offset, numberOfArticlesPerPAge)

        List<ArticleWebObject> list = []
        ThemeDefinition theme = this.context.theme?.definition;
        GeneralSettings generalSettings = configurationService.getSettings(GeneralSettings.class) // <o

        List<EntityData<Article>> articlesData = dataLoader.
                load(articles, StandardOptions.LOCALIZE, AttachmentLoadingOptions.FEATURED_IMAGE_ONLY)

        articlesData.each({ EntityData<Article> articleData ->

            Article article = articleData.entity
            List<Image> images = articleData.getDataList(Image.class)

            ArticleWebObject articleWebObject = new ArticleWebObject()
            articleWebObject.withArticle(article, urlFactory, generalSettings.locales.mainLocale.getValue())
            articleWebObject.withAddons(addonsWebObjectBuilder.build(articleData))
            articleWebObject.withImages(images as List<Image>, article.featuredImageId, Optional.fromNullable(theme))

            list << articleWebObject
        })

        PaginationWebObject pagination = new PaginationWebObject()
        pagination.withPages(currentPage, totalPages, {
            Integer p -> MessageFormat.format("/news/{0}", p);
        });

        context.put("articles", new ArticleListWebObject([
                pagination: pagination,
                list      : list
        ]));

        return new WebView().template("news.html").data(context);
    }

    @Path("{slug}")
    @GET
    def getArticle(@PathParam("slug") String slug)
    {
        if (StringUtils.isNumeric(slug)) {
            // Treat /articles/17 as paginated access, in this case the slug is the page number
            return getNews(Integer.valueOf(slug));
        }

        Article article = articleStore.get().findBySlug(slug)
        if (article == null) {
            return new ErrorWebView().status(404)
        }

        def context = new HashMap<String, Object>([
                "title"  : article.title,
                "content": article.content
        ])

        ThemeDefinition theme = this.context.theme?.definition

        EntityData<Article> data = dataLoader.load(article, StandardOptions.LOCALIZE)

        Optional<ImageGallery> gallery = data.getData(ImageGallery.class);
        List<Image> images = gallery.isPresent() ? gallery.get().images : [] as List<Image>

        GeneralSettings settings = configurationService.getSettings(GeneralSettings.class)

        ArticleWebObject articleWebObject = new ArticleWebObject()
        articleWebObject.withArticle(article, urlFactory, settings.locales.mainLocale.getValue())
        articleWebObject.withAddons(addonsWebObjectBuilder.build(data))
        articleWebObject.withImages(images, article.featuredImageId, Optional.fromNullable(theme))

        context.put("article", articleWebObject)

        return new WebView().template("article.html").model(article.model).data(context)
    }
}
