package org.mayocat.shop.rest.views;

import com.fasterxml.jackson.databind.ObjectMapper;


import org.mayocat.shop.theme.ThemeManager;
import org.mayocat.shop.theme.internal.TemplateNotFoundException;
import org.mayocat.shop.views.Template;
import org.mayocat.shop.views.TemplateEngine;
import org.xwiki.component.annotation.Component;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @version $Id$
 */
@Component
public class FrontViewMessageBodyWriter implements MessageBodyWriter<FrontView>, org.mayocat.shop.base.Provider
{
    @Inject
    private Provider<TemplateEngine> engine;

    @Inject
    private ThemeManager themeManager;

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return FrontView.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(FrontView frontView, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType)
    {
        return -1;
    }

    @Override
    public void writeTo(FrontView frontView, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException
    {
        try {
            Template template = themeManager.resolveIndex(frontView.getBreakpoint());
            String layout = themeManager.resolveLayoutName(frontView.getLayout() + ".html",
                    frontView.getBreakpoint());

            frontView.putInContext("layout", layout);

            //
            engine.get().register(themeManager.resolve(frontView.getLayout() + ".html", frontView.getBreakpoint()));

            ObjectMapper mapper = new ObjectMapper();
            String jsonContext = mapper.writeValueAsString(frontView.getContext());
            engine.get().register(template);

            String rendered = engine.get().render(template.getName(), jsonContext);

            entityStream.write(rendered.getBytes());
        } catch (TemplateNotFoundException e) {
            throw new WebApplicationException(e);
        }
    }
}
