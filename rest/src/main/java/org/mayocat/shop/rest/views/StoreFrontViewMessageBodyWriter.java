package org.mayocat.shop.rest.views;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.mayocat.shop.base.Provider;
import org.mayocat.shop.theme.ThemeManager;
import org.mayocat.shop.theme.internal.TemplateNotFoundException;
import org.mayocat.shop.views.Template;
import org.mayocat.shop.views.TemplateEngine;
import org.xwiki.component.annotation.Component;

import javax.inject.Inject;
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
public class StoreFrontViewMessageBodyWriter implements MessageBodyWriter<StoreFrontView>, Provider
{
    @Inject
    private TemplateEngine engine;

    @Inject
    private ThemeManager themeManager;

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return StoreFrontView.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(StoreFrontView storeFrontView, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType)
    {
        return -1;
    }

    @Override
    public void writeTo(StoreFrontView storeFrontView, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException
    {
        try {
            Template template = themeManager.resolveIndex(storeFrontView.getBreakpoint());
            String layout = themeManager.resolveLayoutName(storeFrontView.getLayout() + ".html",
                    storeFrontView.getBreakpoint());

            storeFrontView.putInContext("layout", layout);

            //
            engine.register(themeManager.resolve(storeFrontView.getLayout() + ".html", storeFrontView.getBreakpoint()));

            ObjectMapper mapper = new ObjectMapper();
            String jsonContext = mapper.writeValueAsString(storeFrontView.getContext());
            engine.register(template);

            String rendered = engine.render(template.getName(), jsonContext);

            entityStream.write(rendered.getBytes());
        } catch (TemplateNotFoundException e) {
            throw new WebApplicationException(e);
        }
    }
}
