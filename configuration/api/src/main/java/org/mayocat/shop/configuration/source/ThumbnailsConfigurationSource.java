package org.mayocat.shop.configuration.source;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.mayocat.shop.configuration.ConfigurationSource;
import org.mayocat.shop.configuration.PlatformConfiguration;
import org.mayocat.shop.configuration.thumbnails.Source;
import org.mayocat.shop.configuration.thumbnails.ThumbnailDefinition;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
@Component("thumbnails")
public class ThumbnailsConfigurationSource implements Initializable, ConfigurationSource
{
    @Inject
    private PlatformConfiguration source;

    Map<Source, List<ThumbnailDefinition>> thumbnailDefinitions;

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
        return this.thumbnailDefinitions;
    }
}
