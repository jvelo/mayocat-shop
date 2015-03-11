/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.passwords.zxcvbn;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Set;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.mayocat.configuration.SecuritySettings;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;

/**
 * zxcvbn.js based password strength checker.
 *
 * See https://tech.dropbox.com/2012/04/zxcvbn-realistic-password-strength-estimation/ and
 * https://github.com/dropbox/zxcvbn
 *
 * @version $Id$
 */
public class ZxcvbnPasswordMeter
{
    private Scriptable global;

    private String zxcvbnFileName = "zxcvbn.js";

    private String zxcvbnFilePath = "javascripts/vendor/" + zxcvbnFileName;

    private boolean hasInitialized = false;

    private Set<String> inputs = Sets.newHashSet();

    public ZxcvbnPasswordMeter inputs(String... input)
    {
        inputs.addAll(Arrays.asList(input));
        return this;
    }

    public void initialize()
    {
        try (Reader reader = getResourceReader(zxcvbnFilePath)) {
            Context engineContext = Context.enter();
            engineContext.setOptimizationLevel(9);
            try {
                global = engineContext.initStandardObjects();
                // No one loves rhino... fake a window object so that zxcvbn is exported to the global scope
                engineContext.evaluateString(global, "var window = this;", "rhino-no-love.js", 1, null);

                engineContext.evaluateReader(global, reader, zxcvbnFileName, 0, null);
            } finally {
                Context.exit();
                this.hasInitialized = true;
            }
        } catch (IOException ex) {
            throw new RuntimeException("Unable to initialize zxcvbn strength checker: ", ex);
        }
    }

    public PasswordStrength getStrength(String password)
    {
        if (!hasInitialized) {
            this.initialize();
        }

        Context context = Context.enter();
        Scriptable scope = context.newObject(global);
        scope.setParentScope(global);
        StringWriter stringWriter = new StringWriter();

        scope.put("writer", scope, stringWriter);
        scope.put("password", scope, password);

        String inputsAsArrayString =  '[' + Joiner.on(",").join(
                FluentIterable.from(inputs).transform(new Function<String, String>()
                {
                    public String apply(String input)
                    {
                        return "'" + input + "'";
                    }
                })) + ']';

        context.evaluateString(scope, "var dump = function(o) { return JSON.stringify(o) };", "dump.js", 1, null);
        context.evaluateString(scope, "var result = zxcvbn(password, " + inputsAsArrayString + ");" +
                "writer.write(JSON.stringify(result))",
                "eval-zxcvbn.js", 1, null);

        ObjectMapper mapper = new ObjectMapper();
        final JsonNode node;
        try {
            node = mapper.readTree(stringWriter.toString());
            PasswordStrength result = mapper.readValue(new TreeTraversingParser(node), PasswordStrength.class);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to check password strength", e);
        }
    }

    private static Reader getResourceReader(String resource) throws IOException
    {
        return Resources.newReaderSupplier(Resources.getResource(resource), Charsets.UTF_8).getInput();
    }
}
