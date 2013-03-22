package org.mayocat.configuration.general;

import java.util.TimeZone;

import javax.validation.Valid;

import org.mayocat.configuration.Configurable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @version $Id$
 */
public class TimeSettings
{
    @Valid
    @JsonProperty
    private Configurable<TimeZone> timeZone = new Configurable<TimeZone>(TimeZone.getDefault(), true);

    public Configurable<TimeZone> getTimeZone()
    {
        return timeZone;
    }
}
