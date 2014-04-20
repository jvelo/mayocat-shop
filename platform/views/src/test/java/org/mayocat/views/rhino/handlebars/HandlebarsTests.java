/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.views.rhino.handlebars;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mayocat.views.Template;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import junit.framework.Assert;

/**
 * @version $Id$
 */
public class HandlebarsTests
{
    @Test
    public void simpleHandlebarsTest() throws Exception
    {
        for (String test : Arrays.asList("handlebars/simple/test1")) {
            String template = getFileContent(test + ".hbs");
            String json = getFileContent(test + ".json");
            String expected = getFileContent(test + ".out");

            this.executeHandlebarsTest(test, template, json, expected);
        }
    }

    @Test
    public void partialsTest() throws Exception
    {
        for (String test : Arrays.asList("handlebars/partials/test1")) {
            String template = getFileContent(test + ".hbs");
            String json = getFileContent(test + ".json");
            String expected = getFileContent(test + ".out");

            final String partial = getFileContent("handlebars/partials/partial.hbs");

            this.executeHandlebarsTest(test, template, json, expected, new HashMap<String, String>()
            {{
                    put("partial", partial);
                }});
        }
    }

    @Test
    public void layoutTest() throws Exception
    {
        for (String test : Arrays.asList("handlebars/layout/test1")) {
            String template = getFileContent(test + ".hbs");
            String json = getFileContent(test + ".json");
            String expected = getFileContent(test + ".out");

            final String partial = getFileContent("handlebars/layout/test.hbs");

            this.executeHandlebarsTest(test, template, json, expected, new HashMap<String, String>(){{
                    put("test", partial);
            }});
        }
    }

    private String getFileContent(String file) throws IOException
    {
        return Resources.toString(Resources.getResource(file), Charsets.UTF_8);
    }

    private void executeHandlebarsTest(String name, String template, String json, String expected) throws Exception
    {
        this.executeHandlebarsTest(name, template, json, expected, Collections.<String, String>emptyMap());
    }

    private void executeHandlebarsTest(String name, String content, String json, String expected,
            Map<String, String> partials) throws Exception
    {
        HandlebarsEngine engine = new HandlebarsEngine();
        Template template = new Template(name, content);
        engine.register(template);

        for (String partial : partials.keySet()) {
            Template partialTemplate = new Template(partial, partials.get(partial), true);
            engine.register(partialTemplate);
        }

        String result = engine.render(name, json);
        Assert.assertEquals(expected, result);
    }
}
