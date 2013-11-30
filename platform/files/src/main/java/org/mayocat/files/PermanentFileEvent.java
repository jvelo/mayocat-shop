/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.files;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

import org.xwiki.observation.event.AbstractFilterableEvent;
import org.xwiki.observation.event.Event;

/**
 * An event related to a permanent file or directory (a file or directory located under the permanent {@link
 * org.mayocat.configuration.general.FilesSettings#getPermanentDirectory()} directory.
 *
 * @version $Id$
 */
public class PermanentFileEvent extends AbstractFilterableEvent implements Event
{
    /**
     * The event data memo that will be passed along such events (in {@link org.xwiki.observation.ObservationManager#notify(org.xwiki.observation.event.Event,
     * Object, Object)}
     */
    public static final class Data
    {
        private Path path;

        private WatchEvent.Kind kind;

        public Data(Path path, WatchEvent.Kind kind)
        {
            this.path = path;
            this.kind = kind;
        }

        public Path getPath()
        {
            return path;
        }

        public WatchEvent.Kind getKind()
        {
            return kind;
        }
    }
}
