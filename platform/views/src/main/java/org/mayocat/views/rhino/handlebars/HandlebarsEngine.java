package org.mayocat.views.rhino.handlebars;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

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

    private static final String HANDLEBARS_FILENAME = "handlebars.js";

    private static final String HANDLEBARS_SWAG_HELPERS_FILENAME = "swag.min.js";

    private static final String HANDLEBARS_HELPERS_FILENAME = "helpers.js";

    private static final String HANDLEBARS_FILEPATH = "javascripts/vendor/" + HANDLEBARS_FILENAME;

    private static final String HANDLEBARS_HELPERS_FILEPATH = "javascripts/handlebars/" + HANDLEBARS_HELPERS_FILENAME;

    private static final String HANDLEBARS_SWAG_HELPERS_FILEPATH = "javascripts/vendor/" + HANDLEBARS_SWAG_HELPERS_FILENAME;

    public HandlebarsEngine() throws IOException
    {
        super(HANDLEBARS_FILENAME, getResourceReader(HANDLEBARS_FILEPATH));
    }

    @Override
    protected void initialize()
    {
        try {
            Reader helpersReader = getResourceReader(HANDLEBARS_HELPERS_FILEPATH);
            Reader swagHelpersReader = getResourceReader(HANDLEBARS_SWAG_HELPERS_FILEPATH);
            Context engineContext = Context.enter();
            engineContext.setOptimizationLevel(9);
            try {
                Scriptable globalScope = getScope();
                engineContext.evaluateReader(globalScope,
                        helpersReader,
                        HANDLEBARS_HELPERS_FILENAME,
                        0,
                        null);
                // Export the global object as "window" otherwise swag.js doesn't know in which environment it runs
                // and fails to initialize
                engineContext.evaluateString(globalScope, "var window = this;", "fixswag.js", 0 , null);
                engineContext.evaluateReader(globalScope,
                        swagHelpersReader,
                        HANDLEBARS_SWAG_HELPERS_FILENAME,
                        0,
                        null);
            } finally {
                Context.exit();
            }
        } catch (IOException ex) {
            throw new RuntimeException("ERROR : Unable to load engine resource: ", ex);
        }
    }

    @Override
    public void register(Template template) throws TemplateEngineException
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
    public String render(String templateName, String json) throws TemplateEngineException
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
        return Resources.newReaderSupplier(
                Resources.getResource(resource), Charsets.UTF_8
        ).getInput();
    }
}
