/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.jersey;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.ws.rs.core.Context;

import org.mayocat.rest.Provider;
import org.mayocat.rest.parameters.ImageOptions;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

/**
 * @version $Id$
 */
@Component("imageOptionsProvider")
public class ImageOptionsProvider implements InjectableProvider<Context, Parameter>, Provider
{
    public static final String WIDTH_OPTION = "width";

    public static final String HEIGHT_OPTION = "height";

    private class ImageOptionsInjectable extends AbstractHttpContextInjectable<Optional<ImageOptions>>
    {
        @Override
        public Optional<ImageOptions> getValue(HttpContext httpContext)
        {
            if (httpContext.getRequest().getQueryParameters().containsKey(WIDTH_OPTION) ||
                    httpContext.getRequest().getQueryParameters().containsKey(HEIGHT_OPTION))
            {
                Optional<Integer> width = extractValue(httpContext.getRequest().getQueryParameters().get(WIDTH_OPTION));
                Optional<Integer> height =
                        extractValue(httpContext.getRequest().getQueryParameters().get(HEIGHT_OPTION));

                if (!width.isPresent() && !height.isPresent()) {
                    return Optional.absent();
                }

                return Optional.of(new ImageOptions(width, height));
            }
            return Optional.absent();
        }

        private Optional<Integer> extractValue(List<String> value)
        {
            try {
                return Optional.fromNullable(Integer.parseInt(value.get(0)));
            } catch (Exception e) {
                return Optional.absent();
            }
        }
    }

    @Override
    public ComponentScope getScope()
    {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable getInjectable(ComponentContext ic, Context context, Parameter parameter)
    {
        if (isExtractable(parameter)) {
            return new ImageOptionsInjectable();
        }
        return null;
    }

    private boolean isExtractable(Parameter param)
    {
        return param.getParameterClass().isAssignableFrom(Optional.class) &&
               (param.getParameterType() instanceof ParameterizedType)
               && ((ParameterizedType) param.getParameterType()).getActualTypeArguments().length == 1
               &&
               ((ParameterizedType) param.getParameterType()).getActualTypeArguments()[0].equals(ImageOptions.class);
    }
}
