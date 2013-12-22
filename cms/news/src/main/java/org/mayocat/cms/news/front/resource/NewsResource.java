/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.news.front.resource;

import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.mayocat.cms.news.NewsSettings;
import org.mayocat.cms.news.meta.ArticleEntity;
import org.mayocat.cms.news.model.Article;
import org.mayocat.cms.news.store.ArticleStore;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.context.WebContext;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Attachment;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.views.FrontView;
import org.mayocat.shop.front.builder.PaginationContextBuilder;
import org.mayocat.shop.front.context.ContextConstants;
import org.mayocat.shop.front.resources.AbstractFrontResource;
import org.mayocat.store.AttachmentStore;
import org.mayocat.theme.Breakpoint;
import org.mayocat.url.EntityURLFactory;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.math.IntMath;

/**
 * @version $Id$
 */
@Component(NewsResource.PATH)
@Path(NewsResource.PATH)
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class NewsResource extends AbstractFrontResource implements Resource, ContextConstants
{
    public static final String PATH = ROOT_PATH + ArticleEntity.PATH;

    @Inject
    private Provider<ArticleStore> articleStore;

    @Inject
    private Provider<AttachmentStore> attachmentStore;

    @Inject
    private Provider<ThumbnailStore> thumbnailStore;

    @Inject
    private EntityURLFactory urlFactory;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private WebContext context;

    @GET
    public FrontView getNews(@Context Breakpoint breakpoint, @Context UriInfo uriInfo,
            @QueryParam("page") @DefaultValue("1") Integer page)
    {
        final int currentPage = page < 1 ? 1 : page;

        Integer numberOfArticlesPerPAge =
                context.getTheme().getDefinition().getPaginationDefinition("news").getItemsPerPage();

        Integer offset = (page - 1) * numberOfArticlesPerPAge;
        List<Article> articles = articleStore.get().findAllPublished(offset, numberOfArticlesPerPAge);
        Integer totalCount = articleStore.get().countAllPublished();

        Map<String, Object> context = getContext(uriInfo);

        // Compute news page name
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("siteName", this.context.getTenant().getName());
        StrSubstitutor substitutor = new StrSubstitutor(parameters, "{{", "}}");
        NewsSettings settings = configurationService.getSettings(NewsSettings.class);
        context.put(PAGE_TITLE, substitutor.replace(settings.getNewsPageTitle().getValue()));

        Map<String, Object> articlesContext = Maps.newHashMap();
        List<Map<String, Object>> currentPageArticles = Lists.newArrayList();

        for (Article article : articles) {
            currentPageArticles.add(buildArticleContext(article));
        }
        articlesContext.put("list", currentPageArticles);

        Integer totalPages = IntMath.divide(totalCount, numberOfArticlesPerPAge, RoundingMode.UP);
        PaginationContextBuilder paginationContextBuilder = new PaginationContextBuilder();
        articlesContext.put("pagination", paginationContextBuilder
                .build(currentPage, totalPages, new PaginationContextBuilder.UrlBuilder()
                {
                    public String build(int page)
                    {
                        return MessageFormat.format("/news/?page={0}", page);
                    }
                }));

        context.put("articles", articlesContext);

        FrontView result = new FrontView("news", breakpoint);
        result.putContext(context);

        return result;
    }

    @Path("{slug}")
    @GET
    public FrontView getArticle(@PathParam("slug") String slug, @Context Breakpoint breakpoint,
            @Context UriInfo uriInfo)
    {
        Article article = this.articleStore.get().findBySlug(slug);

        if (article == null) {
            return new FrontView("404", breakpoint);
        }

        Map<String, Object> context = getContext(uriInfo);

        // Compute article page name
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("siteName", this.context.getTenant().getName());
        parameters.put("articleTitle", article.getTitle());
        StrSubstitutor substitutor = new StrSubstitutor(parameters, "{{", "}}");
        NewsSettings settings = configurationService.getSettings(NewsSettings.class);
        context.put(PAGE_TITLE, substitutor.replace(settings.getArticlePageTitle().getValue()));

        context.put("article", buildArticleContext(article));

        FrontView result = new FrontView("article", breakpoint);
        result.putContext(context);

        return result;
    }

    private Map<String, Object> buildArticleContext(Article article)
    {
        ArticleContextBuilder articleContextBuilder = new ArticleContextBuilder(this.context.getTheme().getDefinition(),
                this.configurationService, this.urlFactory);

        List<Attachment> attachments = this.attachmentStore.get().findAllChildrenOf(article);
        List<Image> images = new ArrayList<Image>();
        for (Attachment attachment : attachments) {
            if (AbstractFrontResource.isImage(attachment)) {
                List<Thumbnail> thumbnails = thumbnailStore.get().findAll(attachment);
                Image image = new Image(attachment, thumbnails);
                images.add(image);
            }
        }

        return articleContextBuilder.build(article, images);
    }
}
