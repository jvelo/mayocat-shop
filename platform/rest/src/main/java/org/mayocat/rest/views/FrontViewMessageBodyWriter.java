package org.mayocat.rest.views;

import com.fasterxml.jackson.databind.ObjectMapper;


import org.mayocat.theme.ThemeManager;
import org.mayocat.theme.TemplateNotFoundException;
import org.mayocat.views.Template;
import org.mayocat.views.TemplateEngine;
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
@Component("frontViewMessageBodyWriter")
public class FrontViewMessageBodyWriter implements MessageBodyWriter<FrontView>, org.mayocat.base.Provider
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

            frontView.getBindings().put("layout", layout);

            //
            engine.get().register(themeManager.resolve(frontView.getLayout() + ".html", frontView.getBreakpoint()));

            ObjectMapper mapper = new ObjectMapper();
            String jsonContext = mapper.writeValueAsString(frontView.getBindings());
            engine.get().register(template);

            String rendered = engine.get().render(template.getName(), jsonContext);

            entityStream.write(rendered.getBytes());
        } catch (TemplateNotFoundException e) {
            throw new WebApplicationException(e);
        }
    }


}
