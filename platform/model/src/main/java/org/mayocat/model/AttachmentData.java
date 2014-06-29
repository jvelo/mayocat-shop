/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

/**
 * Data of an {@link Attachment}
 *
 * @version $Id$
 */
public class AttachmentData implements Serializable
{
    private Logger logger = LoggerFactory.getLogger(AttachmentData.class);

    private transient InputStream stream;

    private transient byte[] bytes;

    private transient Object object;

    public AttachmentData(InputStream stream)
    {
        this.stream = stream;
    }

    public InputStream getStream()
    {
        return stream;
    }

    public byte[] getData()
    {
        try {
            if (stream.available() == 0) {
                throw new RuntimeException("Attachment data stream has already been consumed");
            }
            IOUtils.readFully(stream, bytes);
            return bytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T getObject(Function<InputStream, T> load, Class<T> clazz)
    {
        try {
            if (object != null) {
                if (!clazz.isAssignableFrom(object.getClass())) {
                    throw new RuntimeException("Object has already been loaded as something else");
                }
                return (T) object;
            } else {
                if (stream.available() == 0) {
                    throw new RuntimeException("Attachment data stream has already been consumed");
                }
                object = load.apply(stream);
                return (T) object;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                logger.error("Failed to close attachment data stream", e);
            }
        }
    }
}
