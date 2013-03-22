package org.mayocat.cms.news.front.resource;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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

import org.mayocat.base.Resource;
import org.mayocat.cms.news.model.Article;
import org.mayocat.cms.news.store.ArticleStore;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.context.Execution;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Attachment;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.views.FrontView;
import org.mayocat.shop.front.builder.ImageBindingBuilder;
import org.mayocat.shop.front.resources.AbstractFrontResource;
import org.mayocat.store.AttachmentStore;
import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.Theme;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
@Component("/news/")
@Path("/news/")
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class NewsResource extends AbstractFrontResource implements Resource
{
    private static final Integer DEFAULT_NUMBER_OF_ARTICLES_PER_PAGE = 20;

    @Inject
    private Provider<ArticleStore> articleStore;

    @Inject
    private Provider<AttachmentStore> attachmentStore;

    @Inject
    private Provider<ThumbnailStore> thumbnailStore;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private Execution execution;

    @GET
    public FrontView getNews(@Context Breakpoint breakpoint, @Context UriInfo uriInfo,
            @QueryParam("page") @DefaultValue("0") Integer page)
    {
        if (page < 0) {
            page = 0;
        }
        Integer offset = page * DEFAULT_NUMBER_OF_ARTICLES_PER_PAGE;
        List<Article> articles = articleStore.get().findAllPublished(offset, DEFAULT_NUMBER_OF_ARTICLES_PER_PAGE);

        Map<String, Object> bindings = Maps.newHashMap();
        List<Map<String, Object>> articlesContext = Lists.newArrayList();
        for (Article article : articles) {
            articlesContext.add(buildArticleContext(article));
        }
        bindings.put("articles", articlesContext);

        FrontView result = new FrontView("news", breakpoint);
        result.putBindings(bindings);

        // TODO
        // add "pagination" context

        return result;
    }

    @Path("{slug}")
    @GET
    public FrontView getArticle(@PathParam("slug") String slug, @Context Breakpoint breakpoint,
            @Context UriInfo uriInfo)
    {
        return null;
    }

    private Map<String, Object> buildArticleContext(Article article)
    {
        Theme theme = this.execution.getContext().getTheme();
        GeneralSettings settings = configurationService.getSettings(GeneralSettings.class);

        Map<String, Object> context = Maps.newHashMap();
        context.put("title", article.getTitle());
        context.put("content", article.getContent());
        context.put("href", "/news/" + article.getSlug());
        context.put("slug", article.getSlug());


        Map<String, Object> dateContext = Maps.newHashMap();
        DateFormat shortFormat = DateFormat.getDateInstance(DateFormat.SHORT, settings.getLocales().getMainLocale().getValue());
        DateFormat longFormat = DateFormat.getDateInstance(DateFormat.LONG, settings.getLocales().getMainLocale().getValue());
        dateContext.put("short", shortFormat.format(article.getPublicationDate()));
        dateContext.put("long", shortFormat.format(article.getPublicationDate()));
        context.put("publication_date",dateContext);

        List<Attachment> attachments = this.attachmentStore.get().findAllChildrenOf(article);

        List<Image> images = new ArrayList<Image>();
        for (Attachment attachment : attachments) {
            if (AbstractFrontResource.isImage(attachment)) {
                List<Thumbnail> thumbnails = thumbnailStore.get().findAll(attachment);
                Image image = new Image(attachment, thumbnails);
                images.add(image);
            }
        }



        Map<String, Object> imagesContext = Maps.newHashMap();
        List<Map<String, String>> allImages = Lists.newArrayList();
        ImageBindingBuilder imageBindingBuilder = new ImageBindingBuilder(theme);

        if (images.size() > 0) {
            imagesContext.put("featured", imageBindingBuilder.createImageContext(images.get(0)));
            for (Image image : images) {
                allImages.add(imageBindingBuilder.createImageContext(image));
            }
        } else {
            Map<String, String> placeholder = imageBindingBuilder.createPlaceholderImageContext();
            imagesContext.put("featured", placeholder);
            allImages = Arrays.asList(placeholder);
        }

        imagesContext.put("all", allImages);
        context.put("images", imagesContext);

        return context;
    }
}
