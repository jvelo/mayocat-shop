/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.jackson;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

/**
 * @version $Id$
 *
 * Typical usage will be :
 * <code>
 * @JsonDeserialize(using = OptionalStringListDeserializer.class)
 * private Optional<List<String>> foo = Optional.absent();
 * </code>
 *
 * The deserializer will accept as foo's value either a list of strings or a single string value, and will load it
 * always as an (present) option of list of strings (even in the case of a single value).
 */
public class OptionalStringListDeserializer extends JsonDeserializer<Optional<List<String>>>
{
    @Override
    public Optional<List<String>> deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException
    {
        if (jp.getCurrentToken().isScalarValue()) {
            // value is only one string
            return Optional.of(Arrays.asList(jp.getValueAsString()));
        } else {
            // Value is a list of strings
            List<String> result = Lists.newArrayList();
            while (jp.nextToken().isScalarValue()) {
                String value = jp.getValueAsString();
                result.add(value);
            }
            return Optional.of(result);
        }
    }
}
