package org.mayocat.views.rhino;

import java.io.IOException;
import java.io.Reader;

import org.mayocat.views.TemplateEngine;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.xwiki.component.phase.Initializable;

/**
 * @version $Id$
 */
public abstract class AbstractRhinoEngine implements TemplateEngine, Initializable
{
    private Scriptable globalScope;

    private String engineFileName;
    private Reader engineReader;

    public AbstractRhinoEngine(String engineFileName, Reader engineReader)
    {
        this.engineFileName = engineFileName;
        this.engineReader = engineReader;
    }

    public void initialize()
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
                engineContext.evaluateString(globalScope, ";var templates = {};", "templates.js", 0, null);
            } finally {
                Context.exit();
            }
        } catch (IOException ex) {
            throw new RuntimeException("ERROR : Unable to load engine resource: ", ex);
        }

        initializeEngine();
    }

    protected abstract void initializeEngine();

    protected Scriptable getScope()
    {
        return this.globalScope;
    }
}
