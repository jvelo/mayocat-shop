package org.mayocat.cms.news.front.resource;

import java.math.RoundingMode;
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

import org.apache.commons.lang3.text.StrSubstitutor;
import org.mayocat.addons.front.builder.AddonContextBuilder;
import org.mayocat.addons.model.AddonGroup;
import org.mayocat.cms.news.NewsSettings;
import org.mayocat.cms.news.meta.ArticleEntity;
import org.mayocat.cms.news.model.Article;
import org.mayocat.cms.news.store.ArticleStore;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.context.WebContext;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Attachment;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.views.FrontView;
import org.mayocat.shop.front.builder.ImageContextBuilder;
import org.mayocat.shop.front.context.ContextConstants;
import org.mayocat.shop.front.context.DateContext;
import org.mayocat.shop.front.resources.AbstractFrontResource;
import org.mayocat.shop.front.util.ContextUtils;
import org.mayocat.store.AttachmentStore;
import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.ThemeDefinition;
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
        if (page < 1) {
            page = 1;
        }

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

        List<Map<String, Object>> articlesContext = Lists.newArrayList();
        for (Article article : articles) {
            articlesContext.add(buildArticleContext(article));
        }
        context.put("articles", articlesContext);

        Map<String, Object> pagination = Maps.newHashMap();

        Integer totalPages = IntMath.divide(totalCount, numberOfArticlesPerPAge, RoundingMode.UP);

        List<Map<String, Object>> pages = Lists.newArrayList();
        for (int i = 1; i <= totalPages; i++) {
            Map<String, Object> iPage = Maps.newHashMap();
            iPage.put("number", i);
            if (i == page.intValue()) {
                iPage.put("current", true);
            }
            pages.add(iPage);
        }

        pagination.put("pages", pages);
        pagination.put("currentPage", page);
        if (page > 1) {
            pagination.put("hasNewer", true);
            pagination.put("newer", page - 1);
        } else {
            pagination.put("hasNewer", false);
        }

        if (page < totalPages) {
            pagination.put("hasOlder", true);
            pagination.put("older", page + 1);
        } else {
            pagination.put("hasOlder", false);
        }

        context.put("pagination", pagination);

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
        ThemeDefinition theme = this.context.getTheme().getDefinition();
        GeneralSettings settings = configurationService.getSettings(GeneralSettings.class);

        Map<String, Object> context = Maps.newHashMap();
        context.put("title", ContextUtils.safeString(article.getTitle()));
        context.put("content", ContextUtils.safeHtml(article.getContent()));
        context.put(ContextConstants.URL, urlFactory.create(article));
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

        // Addons
        if (article.getAddons().isLoaded() && theme != null) {
            AddonContextBuilder addonContextBuilder = new AddonContextBuilder();
            Map<String, AddonGroup> themeAddons = theme.getAddons();
            context.put("theme_addons", addonContextBuilder.build(themeAddons, article.getAddons().get()));
        }

        imagesContext.put("all", allImages);
        context.put("images", imagesContext);

        return context;
    }
}
