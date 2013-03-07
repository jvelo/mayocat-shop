package org.mayocat.configuration.gestalt;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.mayocat.configuration.GestaltConfigurationSource;
import org.mayocat.configuration.PlatformSettings;
import org.mayocat.configuration.thumbnails.Dimensions;
import org.mayocat.configuration.thumbnails.Source;
import org.mayocat.configuration.thumbnails.ThumbnailDefinition;
import org.mayocat.context.Execution;
import org.mayocat.theme.Theme;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.InstantiationStrategy;
import org.xwiki.component.descriptor.ComponentInstantiationStrategy;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
@Component("thumbnails")
@InstantiationStrategy(ComponentInstantiationStrategy.PER_LOOKUP)
public class ThumbnailsGestaltConfigurationSource implements Initializable, GestaltConfigurationSource
{
    @Inject
    private PlatformSettings source;

    @Inject
    private Execution execution;

    private boolean themeThumbnailsInitialized = false;

    private Map<Source, List<ThumbnailDefinition>> thumbnailDefinitions;

    @Override
    public void initialize() throws InitializationException
    {
        this.thumbnailDefinitions = Maps.newHashMap();
        List<ThumbnailDefinition> platformThumbnailDefinitions = Lists.newArrayList();
        for (String name : source.getThumbnails().keySet()) {
            platformThumbnailDefinitions.add(new ThumbnailDefinition(name, source.getThumbnails().get(name)));
        }
        this.thumbnailDefinitions.put(Source.PLATFORM, platformThumbnailDefinitions);
    }

    @Override
    public Object get()
    {
        if (!themeThumbnailsInitialized && this.execution.getContext().getTheme() != null) {
            // Lazy theme thumbnails initialization

            Theme activeTheme = this.execution.getContext().getTheme();
            List<ThumbnailDefinition> themeThumbnailDefinitions = Lists.newArrayList();
            Map<String, Dimensions> themeThumbnails = activeTheme.getThumbnails();

            for (String name : themeThumbnails.keySet()) {
                themeThumbnailDefinitions.add(new ThumbnailDefinition(name, themeThumbnails.get(name)));
            }

            this.thumbnailDefinitions.put(Source.THEME, themeThumbnailDefinitions);
            this.themeThumbnailsInitialized = true;
        }

        return this.thumbnailDefinitions;
    }
}
