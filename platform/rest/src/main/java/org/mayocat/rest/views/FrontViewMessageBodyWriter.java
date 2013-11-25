package org.mayocat.rest.views;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.mayocat.context.WebContext;
import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.ThemeFileResolver;
import org.mayocat.theme.TemplateNotFoundException;
import org.mayocat.theme.ThemeManager;
import org.mayocat.views.Template;
import org.mayocat.views.TemplateEngine;
import org.mayocat.views.TemplateEngineException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @version $Id$
 */
@Component("frontViewMessageBodyWriter")
public class FrontViewMessageBodyWriter implements MessageBodyWriter<FrontView>, org.mayocat.rest.Provider
{
    @Inject
    private Provider<TemplateEngine> engine;

    @Inject
    private ThemeFileResolver themeFileResolver;

    @Inject
    private ThemeManager themeManager;

    @Inject
    private Logger logger;

    @Inject
    private WebContext webContext;

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

            if (!webContext.getTheme().isValidDefinition()) {
                // Fail fast with invalid theme error page, so that the developer knows ASAP and can correct it.
                writeError("Invalid theme definition", entityStream);
                return;
            }

            Template template = themeFileResolver.getIndexTemplate(frontView.getBreakpoint());
            Template layout = null;

            if (frontView.getModel().isPresent()) {
                Optional<String> path = themeFileResolver.resolveModelPath(frontView.getModel().get());
                if (path.isPresent()) {
                    try {
                        layout = themeFileResolver.getTemplate(path.get(), frontView.getBreakpoint());
                    } catch (TemplateNotFoundException e) {
                        // Keep going
                    }
                }
                // else just fallback on the default layout
            }

            if (layout == null) {
                layout = themeFileResolver.getTemplate(frontView.getLayout() + ".html", frontView.getBreakpoint());
            }

            frontView.getContext().put("templateContent", layout.getId());
            frontView.getContext().put("template", frontView.getLayout());
            String jsonContext = null;

            try {
                ObjectMapper mapper = new ObjectMapper();
                jsonContext = mapper.writeValueAsString(frontView.getContext());
                engine.get().register(layout);
                engine.get().register(template);
                String rendered = engine.get().render(template.getId(), jsonContext);
                entityStream.write(rendered.getBytes());
            } catch (JsonMappingException e) {
                this.logger.warn("Failed to serialize JSON context", e);
                writeException(frontView, e, entityStream);
            } catch (TemplateEngineException e) {
                this.logger.warn("Template exception", e);
                writeException(frontView, e, entityStream);
            }
        } catch (TemplateNotFoundException e) {
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Template not found : " + frontView.getLayout()).build());
        }
    }

    private void writeError(String message, OutputStream entityStream)
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            Template error;
            try {
                error = themeFileResolver.getTemplate("500.html", Breakpoint.DEFAULT);
            } catch (TemplateNotFoundException notFound) {
                // Fallback on the classpath hosted error 500 file
                error = new Template("500", Resources.toString(Resources.getResource("templates/500.html"),
                        Charsets.UTF_8));
            }
            Map<String, Object> errorContext = Maps.newHashMap();
            errorContext.put("error", message);

            engine.get().register(error);
            String rendered = engine.get().render(error.getId(), mapper.writeValueAsString(errorContext));
            entityStream.write(rendered.getBytes());
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }

    private void writeException(FrontView frontView, Exception e, OutputStream entityStream)
    {
        try {
            // Note:
            // This could be seen as a "server error", but we don't set the Status header to 500 because we want to be
            // able to distinguish between actual server errors (internal Mayocat Shop server error) and theme
            // developers errors (which this is).
            // This is comes at play when setting up monitoring with alerts on a number of 5xx response above a
            // certain threshold.

            // Re-serialize the context as json with indentation for better debugging
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            Map<String, Object> context = frontView.getContext();
            String jsonContext = mapper.writeValueAsString(context);
            Template error;
            try {
                error = themeFileResolver.getTemplate("500.html", frontView.getBreakpoint());
            } catch (TemplateNotFoundException notFound) {
                // Fallback on the classpath hosted error 500 file
                error = new Template("500", Resources.toString(Resources.getResource("templates/500.html"),
                        Charsets.UTF_8));
            }
            Map<String, Object> errorContext = Maps.newHashMap();
            errorContext.put("error", e.getMessage());
            errorContext.put("stackTrace", ExceptionUtils.getStackTrace(e));
            errorContext.put("context", StringEscapeUtils.escapeXml(jsonContext).trim());

            engine.get().register(error);
            String rendered = engine.get().render(error.getId(), mapper.writeValueAsString(errorContext));
            entityStream.write(rendered.getBytes());
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }
}
