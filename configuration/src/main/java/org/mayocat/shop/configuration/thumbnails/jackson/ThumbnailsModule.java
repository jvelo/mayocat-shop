package org.mayocat.shop.configuration.thumbnails.jackson;

import java.io.IOException;

import org.mayocat.shop.configuration.thumbnails.Dimensions;

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
public class ThumbnailsModule extends Module
{
    private static class DimensionDeserializer extends JsonDeserializer<Dimensions>
    {
        @Override
        public Dimensions deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException, JsonProcessingException
        {
            final String text = jsonParser.getText();
            String[] parts = new String[2];
            if (text.indexOf(':') > 0) {
                parts = text.split(":");
            } else if (text.indexOf('x') > 0) {
                parts = text.split("x");
            }
            if (parts[0] != null && parts[1] != null) {
                Integer x = Integer.valueOf(parts[0].trim());
                Integer y = Integer.valueOf(parts[1].trim());
                return new Dimensions(x, y);
            }
            return null;
        }
    }

    private static class ThumbnailsDeserializers extends Deserializers.Base
    {
        @Override
        public JsonDeserializer<?> findBeanDeserializer(JavaType type,
                DeserializationConfig config,
                BeanDescription beanDesc) throws JsonMappingException
        {
            if (Dimensions.class.isAssignableFrom(type.getRawClass())) {
                return new DimensionDeserializer();
            }
            return super.findBeanDeserializer(type, config, beanDesc);
        }
    }

    @Override
    public String getModuleName()
    {
        return "MayocatThumbnailsModule";
    }

    @Override
    public Version version()
    {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(SetupContext setupContext)
    {
        setupContext.addDeserializers(new ThumbnailsDeserializers());
    }
}
