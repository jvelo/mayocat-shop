package org.mayocat.shop.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.validation.ValidationMethod;

/**
 * @version $Id$
 */
public class Configurable<T>
{
    @JsonProperty("default")
    private T value;

    @JsonProperty
    private Boolean configurable = true;

    @JsonProperty
    private Boolean visible;

    public Configurable()
    {
    }

    public Configurable(T value)
    {
        this.value = value;
    }

    public Configurable(T value, boolean configurable)
    {
        this(value);
        this.configurable = configurable;
    }

    public Configurable(T value, boolean configurable, boolean visible)
    {
        this(value, configurable);
        this.visible = visible;
    }

    @JsonIgnore
    @ValidationMethod(message = "Configurable values cannot be invisible")
    public boolean isNotConfigurableAndInvisible()
    {
        return !(this.isConfigurable() && !this.isVisible());
    }

    public T getValue()
    {
        return value;
    }

    public boolean isConfigurable()
    {
        return configurable;
    }

    public boolean isVisible()
    {
        if (this.visible == null) {
            // If the field is configurable, it has to be visible
            // If not, it is invisible by default
            this.visible = configurable;
        }
        return visible;
    }
}
