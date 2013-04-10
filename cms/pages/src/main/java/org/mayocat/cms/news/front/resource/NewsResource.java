package org.mayocat.cms.news.front.resource;

import java.util.ArrayList;
import java.util.Arrays;
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

import org.mayocat.addons.model.AddonGroup;
import org.mayocat.cms.news.meta.ArticleEntity;
import org.mayocat.cms.news.model.Article;
import org.mayocat.cms.news.store.ArticleStore;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.context.Execution;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Addon;
import org.mayocat.model.Attachment;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.views.FrontView;
import org.mayocat.shop.front.builder.AddonContextBuilderHelper;
import org.mayocat.shop.front.context.ContextConstants;
import org.mayocat.shop.front.builder.ImageContextBuilder;
import org.mayocat.shop.front.context.DateContext;
import org.mayocat.shop.front.resources.AbstractFrontResource;
import org.mayocat.shop.front.util.ContextUtils;
import org.mayocat.store.AttachmentStore;
import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.Theme;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
@Component(NewsResource.PATH)
@Path(NewsResource.PATH)
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class NewsResource extends AbstractFrontResource implements Resource
{
    public static final String PATH = ROOT_PATH + ArticleEntity.PATH;

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

        Map<String, Object> context = getContext(uriInfo);
        List<Map<String, Object>> articlesContext = Lists.newArrayList();
        for (Article article : articles) {
            articlesContext.add(buildArticleContext(article));
        }
        context.put("articles", articlesContext);

        FrontView result = new FrontView("news", breakpoint);
        result.putContext(context);

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
        context.put("title", ContextUtils.safeString(article.getTitle()));
        context.put("content", ContextUtils.safeHtml(article.getContent()));
        context.put(ContextConstants.URL, PATH + SLASH + article.getSlug());
        context.put(ContextConstants.SLUG, article.getSlug());

        if (article.getPublicationDate() != null) {
            DateContext date =
                    new DateContext(article.getPublicationDate(),
                            settings.getLocales().getMainLocale().getValue());
            context.put("publicationDate", date);
        }

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
        ImageContextBuilder imageContextBuilder = new ImageContextBuilder(theme);
        Image featuredImage = null;

        if (images.size() > 0) {
            for (Image image : images) {
                if (featuredImage == null && image.getAttachment().getId().equals(article.getFeaturedImageId())) {
                    featuredImage = image;
                }
                allImages.add(imageContextBuilder.createImageContext(image));
            }
            if (featuredImage == null) {
                // If no featured image has been set, we use the first image in the array.
                featuredImage = images.get(0);
            }
            imagesContext.put("featured", imageContextBuilder.createImageContext(featuredImage));
        } else {
            // Create placeholder image
            Map<String, String> placeholder = imageContextBuilder.createPlaceholderImageContext();
            imagesContext.put("featured", placeholder);
            allImages = Arrays.asList(placeholder);
        }

        if (article.getAddons().isLoaded()) {
            Map<String, Object> themeAddonsContext = Maps.newHashMap();
            Map<String, AddonGroup> themeAddons = theme.getAddons();
            for (String groupKey : themeAddons.keySet()) {

                AddonGroup group = themeAddons.get(groupKey);
                Map<String, Object> groupContext = Maps.newHashMap();

                for (String field : group.getFields().keySet()) {
                    Optional<Addon> addon =
                            AddonContextBuilderHelper.findAddon(groupKey, field, article.getAddons().get());
                    if (addon.isPresent()) {
                        groupContext.put(field, ContextUtils.addonValue(addon.get().getValue()));
                    } else {
                        groupContext.put(field, null);
                    }
                }

                themeAddonsContext.put(groupKey, groupContext);
            }
            context.put("theme_addons", themeAddonsContext);
        }

        imagesContext.put("all", allImages);
        context.put("images", imagesContext);

        return context;
    }
}
