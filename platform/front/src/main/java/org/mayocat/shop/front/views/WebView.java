/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.front.views;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @version $Id$
 */
public class WebView
{
    public enum Option
    {
        FALLBACK_ON_DEFAULT_THEME,
        FALLBACK_ON_GLOBAL_TEMPLATES
    }

    private Path template;

    private Optional<String> model = Optional.absent();

    private Map<String, Object> data = Maps.newHashMap();

    private Set<Option> options = Sets.newHashSet();

    public Path template()
    {
        return template;
    }

    public WebView template(Path path)
    {
        this.template = path;
        return this;
    }

    public WebView template(String path)
    {
        this.template = Paths.get(path);
        return this;
    }

    public Map<String, Object> data()
    {
        return this.data;
    }

    public WebView data(Map<String, Object> context)
    {
        this.data.putAll(context);
        return this;
    }

    public WebView data(String key, Map<String, Object> data)
    {
        this.data.put(key, data);
        return this;
    }

    public Optional<String> model()
    {
        return model;
    }

    public WebView model(Optional<String> model)
    {
        this.model = model;
        return this;
    }

    public WebView model(String model)
    {
        this.model = Optional.of(model);
        return this;
    }

    public WebView with(Option... options)
    {
        for (Option option : options) {
            this.options.add(option);
        }
        return this;
    }

    public boolean hasOption(Option option)
    {
        return this.options.contains(option);
    }
}
