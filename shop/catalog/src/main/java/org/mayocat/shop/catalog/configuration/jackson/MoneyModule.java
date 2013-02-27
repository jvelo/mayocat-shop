package org.mayocat.shop.catalog.configuration.jackson;

import java.io.IOException;

import org.joda.money.CurrencyUnit;
import org.mayocat.base.JacksonModule;
import org.xwiki.component.annotation.Component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.ser.Serializers;

/**
 * @version $Id$
 */
@Component("money")
public class MoneyModule extends Module implements JacksonModule
{
    private static class CurrencyUnitDeserializer extends JsonDeserializer<CurrencyUnit>
    {
        @Override
        public CurrencyUnit deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException
        {
            return CurrencyUnit.of(jp.getText());
        }
    }

    private static class CurrencyUnitSerializer extends JsonSerializer<CurrencyUnit>
    {
        @Override
        public void serialize(CurrencyUnit value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException
        {
            jgen.writeString(value.getCurrencyCode());
        }
    }

    private static class MoneyDeserializers extends Deserializers.Base
    {
        @Override
        public JsonDeserializer<?> findBeanDeserializer(JavaType type,
                DeserializationConfig config,
                BeanDescription beanDesc) throws JsonMappingException
        {
            if (CurrencyUnit.class.isAssignableFrom(type.getRawClass())) {
                return new CurrencyUnitDeserializer();
            }
            return super.findBeanDeserializer(type, config, beanDesc);
        }
    }

    private static class MoneySerializers extends Serializers.Base
    {
        @Override
        public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc)
        {
            if (CurrencyUnit.class.isAssignableFrom(type.getRawClass())) {
                return new CurrencyUnitSerializer();
            }
            return super.findSerializer(config, type, beanDesc);
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
        context.addSerializers(new MoneySerializers());
        context.addDeserializers(new MoneyDeserializers());
    }
}
