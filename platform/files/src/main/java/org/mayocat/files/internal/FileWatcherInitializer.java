/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.files.internal;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.mayocat.event.ApplicationStartedEvent;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.event.Event;

/**
 * Initializes the file watcher upon application startup.
 *
 * @version $Id$
 */
@Component
@Named("fileWatcherInitializer")
public class FileWatcherInitializer implements EventListener
{
    @Inject
    private ComponentManager componentManager;

    @Override
    public String getName()
    {
        return "fileWatcherInitializer";
    }

    @Override
    public List<Event> getEvents()
    {
        return Arrays.<Event>asList(new ApplicationStartedEvent());
    }

    @Override
    public void onEvent(Event event, Object source, Object memo)
    {
        FileWatcher watcher = null;
        try {
            watcher = new FileWatcher(componentManager);
            watcher.start();
        } catch (InitializationException e) {
            throw new RuntimeException("Failed to start the file watcher", e);
        }
    }
}
