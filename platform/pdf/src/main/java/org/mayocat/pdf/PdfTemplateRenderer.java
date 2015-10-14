/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.pdf;

import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Map;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface PdfTemplateRenderer
{
    void generatePDF(OutputStream outputStream, Path template, Map<String, Object> context)
            throws PdfRenderingException;

    void generatePDF(OutputStream outputStream, Path template, Path renderingRoot, Map<String, Object> context)
            throws PdfRenderingException;
}
