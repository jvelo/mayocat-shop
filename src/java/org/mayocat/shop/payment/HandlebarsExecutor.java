package org.mayocat.shop.payment;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.Global;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class HandlebarsExecutor
{

    static HandlebarsExecutor instance;

    private HandlebarsExecutor()
    {
        // Private constructor to realize singleton.
    }

    public static HandlebarsExecutor getInstance()
    {
        if (instance == null) {
            synchronized (HandlebarsExecutor.class) {
                if (instance == null) {
                    instance = new HandlebarsExecutor();
                }
            }
        }
        return instance;
    }

    public String executeHandlebar(String contents, Map context)
    {
        String handlebar = getFileContent("resources/javascripts/lib/handlebars.js");
        String render = getFileContent("resources/javascripts/lib/render.js");

        Global global = new Global();
        Context scriptContext = ContextFactory.getGlobal().enterContext();
        global.init(scriptContext);

        Scriptable scope = scriptContext.initStandardObjects(global);

        ObjectMapper mapper = new ObjectMapper().configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        try {

            scope.put("contextAsJSON", scope, mapper.writeValueAsString(context));
            scope.put("template", scope, contents);
            scriptContext.evaluateString(scope, handlebar, "handlebar", 1, null);
            Object result = scriptContext.evaluateString(scope, render, "render", 1, null);

            String resultAsString = Context.toString(result);

            Context.exit();

            return resultAsString;
        } catch (JsonGenerationException e) {
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFileContent(String fileName)
    {
        try {
            return Files.toString(new File(CheckPaymentGateway.class.getClassLoader().getResource(fileName).toURI()),
                Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
