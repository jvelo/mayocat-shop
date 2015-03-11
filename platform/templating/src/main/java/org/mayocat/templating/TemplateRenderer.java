/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.templating;

import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Map;

import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface TemplateRenderer
{
    void render(Path template, Map<String, Object> data, OutputStream outputStream) throws TemplateRenderingException;

    String renderAsString(Path template, Map<String, Object> data) throws TemplateRenderingException;
}
