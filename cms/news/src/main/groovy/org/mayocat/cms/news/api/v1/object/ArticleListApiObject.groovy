package org.mayocat.cms.news.api.v1.object

import groovy.transform.CompileStatic
import org.mayocat.rest.api.object.BasePaginatedListApiObject

/**
 * API object for a list of articles
 *
 * @version $Id$
 */
@CompileStatic
class ArticleListApiObject extends BasePaginatedListApiObject
{
    List<ArticleApiObject> articles;
}
