package org.mayocat.rest.views;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

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
public class FrontViewMessageBodyWriter implements MessageBodyWriter<FrontView>, org.mayocat.rest.Provider
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

            Template template = themeManager.resolveIndexTemplate(frontView.getBreakpoint());
            Template layout = null;

            if (frontView.getModel().isPresent()) {
                Optional<String> path = themeManager.resolveModelPath(frontView.getModel().get());
                if (path.isPresent()) {
                    try {
                    layout = themeManager.resolveTemplate(path.get(), frontView.getBreakpoint());
                    }
                    catch (TemplateNotFoundException e) {
                        // Keep going
                    }
                }
                // else just fallback on the default layout
            }

            if (layout == null) {
                layout = themeManager.resolveTemplate(frontView.getLayout() + ".html", frontView.getBreakpoint());
            }

            frontView.getBindings().put("layout", layout.getId());

            ObjectMapper mapper = new ObjectMapper();
            String jsonContext = mapper.writeValueAsString(frontView.getBindings());

            engine.get().register(layout);
            engine.get().register(template);

            String rendered = engine.get().render(template.getId(), jsonContext);

            entityStream.write(rendered.getBytes());
        } catch (TemplateNotFoundException e) {
            throw new WebApplicationException(e);
        }
    }
}
