package org.mayocat.shop.event;

import org.xwiki.observation.event.Event;

public class ApplicationStartedEvent implements Event
{
    @Override
    public boolean matches(Object otherEvent)
    {
        return this.getClass().isAssignableFrom(otherEvent.getClass());
    }

}
