/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.front.views;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.mayocat.context.WebContext;
import org.mayocat.shop.front.WebDataSupplier;
import org.mayocat.theme.TemplateNotFoundException;
import org.mayocat.theme.ThemeFileResolver;
import org.mayocat.views.Template;
import org.mayocat.views.TemplateEngine;
import org.mayocat.views.TemplateEngineException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;

/**
 * @version $Id$
 */
@Component("webViewMessageBodyWriter")
public class WebViewMessageBodyWriter implements MessageBodyWriter<WebView>, org.mayocat.rest.Provider
{
    @Inject
    private Provider<TemplateEngine> engine;

    @Inject
    private ThemeFileResolver themeFileResolver;

    @Inject
    private Logger logger;

    @Inject
    private WebContext webContext;

    @Inject
    private Map<String, WebDataSupplier> dataSuppliers;

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return WebView.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(WebView webView, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType)
    {
        return -1;
    }

    @Override
    public void writeTo(WebView webView, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException
    {
        try {

            if (!webContext.getTheme().isValidDefinition()) {
                // Fail fast with invalid theme error page, so that the developer knows ASAP and can correct it.
                writeHttpError("Invalid theme definition", entityStream);
                return;
            }

            Template masterTemplate = null;
            try {
                masterTemplate = themeFileResolver.getIndexTemplate(webContext.getRequest().getBreakpoint());
            } catch (TemplateNotFoundException e) {
                if (!mediaType.equals(MediaType.APPLICATION_JSON_TYPE)) {
                    // For JSON API calls, we don't care if the template is found or not.
                    // For other calls, raise the exception
                    throw e;
                }
            }

            Template template = null;

            if (webView.model().isPresent()) {
                Optional<String> path = themeFileResolver.resolveModelPath(webView.model().get());
                if (path.isPresent()) {
                    try {
                        template = themeFileResolver.getTemplate(path.get(), webContext.getRequest().getBreakpoint());
                    } catch (TemplateNotFoundException e) {
                        // Keep going
                    }
                }
                // else just fallback on the default model
            }

            if (template == null) {
                try {
                    template = themeFileResolver
                            .getTemplate(webView.template().toString(), webContext.getRequest().getBreakpoint());
                } catch (TemplateNotFoundException e) {
                    if (webView.hasOption(WebView.Option.FALLBACK_ON_GLOBAL_TEMPLATES)) {
                        template = themeFileResolver.getGlobalTemplate(webView.template().toString(),
                                webContext.getRequest().getBreakpoint());
                    } else {
                        if (!mediaType.equals(MediaType.APPLICATION_JSON_TYPE)) {
                            // For JSON API calls, we don't care if the template is found or not.
                            // For other calls, raise the exception
                            throw e;
                        }
                    }
                }
            }

            String jsonContext = null;

            if (!mediaType.equals(MediaType.APPLICATION_JSON_TYPE) ||
                    httpHeaders.containsKey("X-Mayocat-Full-Context"))
            {
                webView.data().put("templateContent", template.getId());
                webView.data().put("template", FilenameUtils.getBaseName(webView.template().toString()));

                for (WebDataSupplier supplier : dataSuppliers.values()) {
                    supplier.supply(webView.data());
                }
            }

            try {
                ObjectMapper mapper = new ObjectMapper();

                if (mediaType.equals(MediaType.APPLICATION_JSON_TYPE)) {
                    mapper.writeValue(entityStream, webView.data());
                    return;
                }

                jsonContext = mapper.writeValueAsString(webView.data());
                engine.get().register(template);
                engine.get().register(masterTemplate);
                String rendered = engine.get().render(masterTemplate.getId(), jsonContext);
                entityStream.write(rendered.getBytes());
            } catch (JsonMappingException e) {
                this.logger.warn("Failed to serialize JSON context", e);
                writeDeveloperError(webView, e, entityStream);
            } catch (TemplateEngineException e) {
                writeDeveloperError(webView, e, entityStream);
            }
        } catch (TemplateNotFoundException e) {
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Template not found : " + webView.template().toString()).build());
        }
    }

    private void writeHttpError(String message, OutputStream entityStream)
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            Template error;
            try {
                error = themeFileResolver.getTemplate("500.html", webContext.getRequest().getBreakpoint());
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

    private void writeDeveloperError(WebView webView, Exception e, OutputStream entityStream)
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
            Map<String, Object> context = webView.data();
            String jsonContext = mapper.writeValueAsString(context);
            Template error = new Template("developerError",
                    Resources.toString(Resources.getResource("templates/developerError.html"), Charsets.UTF_8));
            Map<String, Object> errorContext = Maps.newHashMap();
            errorContext.put("error", StringEscapeUtils.escapeXml(cleanErrorMessageForDisplay(e.getMessage())));
            errorContext.put("stackTrace", StringEscapeUtils.escapeXml(ExceptionUtils.getStackTrace(e)));
            errorContext.put("context", StringEscapeUtils.escapeXml(jsonContext).trim());
            errorContext.put("rawContext", jsonContext);
            errorContext.put("template", webView.template().toString());

            engine.get().register(error);
            String rendered = engine.get().render(error.getId(), mapper.writeValueAsString(errorContext));
            entityStream.write(rendered.getBytes());
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }

    private String cleanErrorMessageForDisplay(String errorMessage)
    {
        String clean = StringUtils.substringAfter(errorMessage, "org.mozilla.javascript.JavaScriptException: Error:");
        clean = clean.replaceAll("\\(handlebars\\.js#\\d+\\)", "");
        return clean;
    }
}
