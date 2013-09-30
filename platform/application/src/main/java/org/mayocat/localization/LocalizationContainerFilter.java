package org.mayocat.localization;

import java.net.URI;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.core.UriBuilder;

import org.mayocat.application.AbstractService;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.context.Execution;
import org.mayocat.util.Utils;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * @version $Id$
 */
public class LocalizationContainerFilter implements ContainerRequestFilter
{
    @Override
    public ContainerRequest filter(ContainerRequest containerRequest)
    {
        if (isStaticPath(containerRequest.getRequestUri().getPath())) {
            return containerRequest;
        }

        Execution execution = Utils.getComponent(Execution.class);

        if (execution.getContext().getTenant() == null) {
            return containerRequest;
        }

        boolean localeSet = false;
        GeneralSettings settings = execution.getContext().getSettings(GeneralSettings.class);
        URI requestURI = containerRequest.getRequestUri();

        List<Locale> alternativeLocales = Objects.firstNonNull(
                settings.getLocales().getOtherLocales().getValue(), ImmutableList.<Locale>of());

        if (!alternativeLocales.isEmpty()) {
            for (Locale locale : alternativeLocales) {
                if (requestURI.getPath().startsWith("/" + locale.toLanguageTag())) {
                    UriBuilder builder = UriBuilder.fromUri(requestURI);
                    builder.replacePath(requestURI.getPath().substring(locale.toString().length() + 1));
                    containerRequest.setUris(containerRequest.getBaseUri(), builder.build());

                    execution.getContext().setLocale(locale);
                    execution.getContext().setAlternativeLocale(true);
                    localeSet = true;
                    break;
                }
            }
        }

        if (!localeSet) {
            execution.getContext().setLocale(settings.getLocales().getMainLocale().getValue());
            execution.getContext().setAlternativeLocale(false);
        }

        return containerRequest;
    }

    private boolean isStaticPath(String path)
    {
        for (String staticPath : AbstractService.getStaticPaths()) {
            if (path.startsWith(staticPath)) {
                return true;
            }
        }
        return false;
    }
}
