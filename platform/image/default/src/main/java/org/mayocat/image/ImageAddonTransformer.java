/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.image;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.mayocat.addons.AddonFieldTransformer;
import org.mayocat.addons.model.AddonFieldDefinition;
import org.mayocat.addons.web.AddonFieldValueWebObject;
import org.mayocat.entity.EntityData;
import org.mayocat.image.model.Image;
import org.mayocat.url.EntityURLFactory;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

import groovy.text.SimpleTemplateEngine;

/**
 * @version $Id$
 */
@Component("image")
public class ImageAddonTransformer implements AddonFieldTransformer
{
    @Inject
    private EntityURLFactory urlFactory;

    public Optional<AddonFieldValueWebObject> toWebView(EntityData<?> entityData, AddonFieldDefinition definition,
            Object storedValue)
    {
        final String fileName = (String) storedValue;
        List<Image> images = entityData.getDataList(Image.class);
        Optional<Image> found = FluentIterable.from(images).firstMatch(new Predicate<Image>()
        {
            public boolean apply(Image input)
            {
                return input.getAttachment().getFilename().equals(fileName);
            }
        });
        if (!found.isPresent()) {
            return Optional.absent();
        }

        Image image = found.get();
        String uri = urlFactory.create(image.getAttachment()).toString();

        String template;
        Map<String, Object> arguments = Maps.newHashMap();

        Optional<Integer> width = Optional.fromNullable((Integer) definition.getProperties().get("image.width"));
        Optional<Integer> height = Optional.fromNullable((Integer) definition.getProperties().get("image.height"));

        if (definition.getProperties().containsKey("image.valueTemplate")) {
            template = (String) definition.getProperties().get("image.valueTemplate");
        } else {
            StringBuilder defaultTemplate = new StringBuilder();
            defaultTemplate.append("<img src=\"");
            defaultTemplate.append(uri);
            defaultTemplate.append("\" ");

            if (width.isPresent()) {
                defaultTemplate.append("width=\"${width}\" ");
            }
            if (height.isPresent()) {
                defaultTemplate.append("height=\"${height}\" ");
            }

            if (definition.getProperties().containsKey("image.extraAttributes")) {
                Object attributesValue = definition.getProperties().get("image.extraAttributes");
                String attributesAsString;
                if (List.class.isAssignableFrom(attributesValue.getClass())) {
                    attributesAsString = Joiner.on(" ").join((List) attributesValue);
                } else {
                    attributesAsString = (String) attributesValue;
                }
                defaultTemplate.append(attributesAsString);
            }

            defaultTemplate.append("/>");
            template = defaultTemplate.toString();
        }

        if (width.isPresent() || height.isPresent()) {
            uri += "?";
        }

        if (width.isPresent()) {
            arguments.put("width", width.get());
            uri += ("&width=" + width.get());
        }
        if (height.isPresent()) {
            arguments.put("height", height.get());
            uri += ("&height=" + height.get());
        }

        SimpleTemplateEngine templateEngine = new SimpleTemplateEngine();
        String displayValue;
        try {
            displayValue = templateEngine.createTemplate(template).make(arguments).toString();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
        AddonFieldValueWebObject result = new AddonFieldValueWebObject(
                uri,
                displayValue
        );

        return Optional.of(result);
    }
}
