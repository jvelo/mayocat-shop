/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.news.web.object

import groovy.transform.CompileStatic
import org.mayocat.rest.web.object.PaginationWebObject

/**
 * Web object for an article list
 *
 * @version $Id$
 */
@CompileStatic
class ArticleListWebObject
{
    List<ArticleWebObject> list

    PaginationWebObject pagination
}
