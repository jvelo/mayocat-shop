package org.mayocat.shop.catalog.configuration.jackson;

import java.io.IOException;
import java.util.Currency;

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
