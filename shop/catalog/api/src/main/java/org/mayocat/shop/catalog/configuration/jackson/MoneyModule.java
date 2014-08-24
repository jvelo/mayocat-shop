/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.configuration.jackson;

import java.io.IOException;
import java.util.Currency;

import org.xwiki.component.annotation.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.Deserializers;

/**
 * @version $Id$
 */
@Component("money")
public class MoneyModule extends Module
{
    private static class CurrencyDeserializer extends JsonDeserializer<Currency>
    {
        @Override
        public Currency deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException
        {
            return Currency.getInstance(jp.getText());
        }
    }

    private static class MoneyDeserializers extends Deserializers.Base
    {
        @Override
        public JsonDeserializer<?> findBeanDeserializer(JavaType type,
                DeserializationConfig config,
                BeanDescription beanDesc) throws JsonMappingException
        {
            if (Currency.class.isAssignableFrom(type.getRawClass())) {
                return new CurrencyDeserializer();
            }
            return super.findBeanDeserializer(type, config, beanDesc);
        }
    }

    @Override
    public String getModuleName()
    {
        return "MayocatMoneyModule";
    }

    @Override
    public Version version()
    {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(SetupContext context)
    {
        context.addDeserializers(new MoneyDeserializers());
    }
}
