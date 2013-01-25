package org.mayocat.shop.views.rhino;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.mayocat.shop.views.TemplateEngine;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * @version $Id$
 */
public abstract class AbstractRhinoEngine implements TemplateEngine
{
    private Scriptable globalScope;

    public AbstractRhinoEngine(String engineFileName, Reader engineReader)
    {
        try {
            Context engineContext = Context.enter();
            engineContext.setOptimizationLevel(9);
            try {
                globalScope = engineContext.initStandardObjects();
                engineContext.evaluateReader(globalScope,
                        engineReader,
                        engineFileName,
                        0,
                        null);
                engineContext.evaluateString(globalScope, ";var templates = {};", "foo", 0, null);
            } finally {
                Context.exit();
            }
        } catch (IOException ex) {
            throw new RuntimeException("ERROR : Unable to load engine resource: ", ex);
        }

        this.initialize();
    }

    protected void initialize()
    {
        // Intended to be overridden by implementing classes;
    }

    protected Scriptable getScope()
    {
        return this.globalScope;
    }
}
