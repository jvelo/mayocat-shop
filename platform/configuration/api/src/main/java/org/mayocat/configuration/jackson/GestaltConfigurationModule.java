package org.mayocat.configuration.jackson;

import java.io.IOException;

import org.mayocat.configuration.Configurable;
import org.mayocat.configuration.thumbnails.Dimensions;
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
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.fasterxml.jackson.databind.ser.Serializers;

/**
 * @version $Id$
 */
@Component("gestaltConfiguration")
public class GestaltConfigurationModule extends Module
{
    private static class ConfigurableSerializer extends JsonSerializer<Configurable>
    {
        @Override
        public void serialize(Configurable value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException
        {
            provider.defaultSerializeValue(value.getValue(), jgen);
        }
    }

    @Override
    public String getModuleName()
    {
        return "gestaltConfiguration";
    }

    @Override
    public Version version()
    {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(SetupContext context)
    {
        SimpleSerializers serializers = new SimpleSerializers();
        serializers.addSerializer(Configurable.class, new ConfigurableSerializer());
        context.addSerializers(serializers);
    }
}
