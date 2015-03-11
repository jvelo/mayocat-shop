/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.jackson;

/**
 * @version $Id$
 */

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.deser.std.EnumDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;

/**
 * Temporary solution for @JsonCreator support in DW 0.7 (this is fixed with in 0.8)
 */
public class PermissiveFuzzyEnumModule extends Module
{
    private static class PermissiveEnumDeserializer extends StdScalarDeserializer<Enum<?>>
    {
        private final Enum<?>[] constants;

        private final List<String> acceptedValues;

        @SuppressWarnings("unchecked")
        protected PermissiveEnumDeserializer(Class<Enum<?>> clazz)
        {
            super(clazz);
            this.constants = ((Class<Enum<?>>) handledType()).getEnumConstants();
            this.acceptedValues = Lists.newArrayList();
            for (Enum<?> constant : constants) {
                acceptedValues.add(constant.name());
            }
        }

        @Override

        public Enum<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
        {
            final String text = CharMatcher.WHITESPACE
                    .removeFrom(jp.getText())
                    .replace('-', '_')
                    .replace('.', '_');
            for (Enum<?> constant : constants) {
                if (constant.name().equalsIgnoreCase(text)) {
                    return constant;
                }
            }

            throw ctxt.mappingException(text + " was not one of " + acceptedValues);
        }
    }

    private static class PermissiveEnumDeserializers extends Deserializers.Base
    {
        @Override
        @SuppressWarnings("unchecked")
        public JsonDeserializer<?> findEnumDeserializer(Class<?> type,
                DeserializationConfig config,
                BeanDescription desc) throws JsonMappingException
        {
            // If there is a JsonCreator annotation we should use that instead of the PermissiveEnumDeserializer
            final Collection<AnnotatedMethod> factoryMethods = desc.getFactoryMethods();
            if (factoryMethods != null) {
                for (AnnotatedMethod am : factoryMethods) {
                    final JsonCreator creator = am.getAnnotation(JsonCreator.class);
                    if (creator != null) {
                        return EnumDeserializer.deserializerForCreator(config, type, am);
                    }
                }
            }

            return new PermissiveEnumDeserializer((Class<Enum<?>>) type);
        }
    }

    @Override
    public String getModuleName()
    {
        return "permissive-enums";
    }

    @Override
    public Version version()
    {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(final SetupContext context)
    {
        context.addDeserializers(new PermissiveEnumDeserializers());
    }
}
