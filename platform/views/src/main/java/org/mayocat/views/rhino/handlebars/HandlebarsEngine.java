/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.views.rhino.handlebars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.Map;

import org.mayocat.views.Template;
import org.mayocat.views.TemplateEngine;
import org.mayocat.views.TemplateEngineException;
import org.mayocat.views.rhino.AbstractRhinoEngine;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @version $Id$
 */
@Component
@Named("handlebars")
public class HandlebarsEngine extends AbstractRhinoEngine implements TemplateEngine
{
    @Inject
    private Logger logger;

    @Inject
    private Map<String, HelpersScript> helpers;

    private enum JSFile
    {

        HANDLEBARS("handlebars.js", "javascripts/vendor"),
        SWAG_HELPERS("swag.min.js", "javascripts/vendor"),
        MAYO_HELPERS("helpers.js", "javascripts/handlebars");

        private String fileName;

        private String path;

        JSFile(String fileName, String path)
        {
            this.fileName = fileName;
            this.path = path;
        }

        private String getFileName()
        {
            return fileName;
        }

        private String getFilePath()
        {
            return path + "/" + getFileName();
        }
    }

    public HandlebarsEngine() throws IOException
    {
        super(JSFile.HANDLEBARS.getFileName(), getResourceReader(JSFile.HANDLEBARS.getFilePath()));
    }

    @Override
    protected void initializeEngine()
    {
        try {
            Reader helpersReader = getResourceReader(JSFile.MAYO_HELPERS.getFilePath());
            Reader swagHelpersReader = getResourceReader(JSFile.SWAG_HELPERS.getFilePath());
            Context engineContext = Context.enter();
            engineContext.setOptimizationLevel(9);
            try {
                Scriptable globalScope = getScope();
                engineContext.evaluateReader(globalScope,
                        helpersReader,
                        JSFile.MAYO_HELPERS.getFileName(),
                        0,
                        null);
                // Export the global object as "window" otherwise swag.js doesn't know in which environment it runs
                // and fails to initialize
                engineContext.evaluateString(globalScope, "var window = this;", "fixswag.js", 0, null);
                engineContext.evaluateReader(globalScope,
                        swagHelpersReader,
                        JSFile.SWAG_HELPERS.getFileName(),
                        0,
                        null);

                // All other helpers declared as handlers.
                for (String scriptName : helpers.keySet()) {
                    Reader helper = new BufferedReader(new InputStreamReader(
                            this.getClass().getResourceAsStream("/" + helpers.get(scriptName).getPath().toString()))
                    );
                    engineContext.evaluateReader(globalScope, helper, scriptName, 0, null);
                }
            } finally {
                Context.exit();
            }
        } catch (IOException ex) {
            throw new RuntimeException("ERROR : Unable to load engine resource: ", ex);
        }
    }

    @Override
    public synchronized void register(Template template) throws TemplateEngineException
    {
        Context context = Context.enter();
        try {

            Scriptable compileScope = context.newObject(getScope());
            compileScope.setParentScope(getScope());
            compileScope.put("content", compileScope, template.getContent());
            compileScope.put("name", compileScope, template.getId());

            try {
                context.evaluateString(compileScope,
                        "templates[name] = Handlebars.compile(content, {noEscape: true});",
                        "JHBSCompiler",
                        0,
                        null);

                if (template.isPartial()) {
                    context.evaluateString(compileScope,
                            "Handlebars.registerPartial(name, templates[name]);",
                            "JHBSPartialRegister",
                            0,
                            null);
                }
            } catch (JavaScriptException e) {
                // Fail hard on any compile time error for templates
                throw new TemplateEngineException(e);
            }
        } finally {
            Context.exit();
        }
    }

    @Override
    public synchronized String render(String templateName, String json) throws TemplateEngineException
    {
        Context context = Context.enter();
        try {
            StringWriter stringWriter = new StringWriter();
            Scriptable renderScope = context.newObject(getScope());
            renderScope.setParentScope(getScope());
            renderScope.put("writer", renderScope, stringWriter);
            renderScope.put("name", renderScope, templateName);
            renderScope.put("json", renderScope, json);

            try {
                context.evaluateString(renderScope,
                        "writer.write( templates[name](JSON.parse(json)) )",
                        "JHBSRenderer",
                        0,
                        null);
                return stringWriter.toString();
            } catch (JavaScriptException e) {
                throw new TemplateEngineException(e);
            }
        } finally {
            Context.exit();
        }
    }

    private static Reader getResourceReader(String resource) throws IOException
    {
        return Resources.newReaderSupplier(Resources.getResource(resource), Charsets.UTF_8).getInput();
    }
}
