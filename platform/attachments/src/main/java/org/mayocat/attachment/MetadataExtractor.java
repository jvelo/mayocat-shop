/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.attachment;

import java.util.Map;

import org.mayocat.model.Attachment;
import org.xwiki.component.annotation.Role;

import com.google.common.base.Optional;

/**
 * Metadata extractors take an attachment as input, and returns an optional map of meta data that they could extract
 * from the attachment. Different metadata extractors can be meant to extract different type of data, and handle
 * different kind of attachments. For example an extractor dedicated to extract EXIF data from images will only perform
 * work when it detects the attachment is an image.
 *
 * @version $Id$
 */
@Role
public interface MetadataExtractor
{
    /**
     * Performs the actual meta data extraction.
     */
    Optional<Map<String, Object>> extractMetadata(Attachment attachment);
}
