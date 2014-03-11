package org.mayocat.shop.catalog.web.object

import groovy.transform.CompileStatic

/**
 * Web object for an entity model : contains the template applied by the model as well as its slug.
 *
 * @version $Id$
 */
@CompileStatic
class EntityModelWebObject
{
    String template

    String slug
}
