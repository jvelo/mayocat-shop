package org.mayocat.search;

import java.util.Map;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.model.Entity;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface EntityIndexDocumentPurveyor<E extends Entity>
{
    Map<String, Object> purveyDocument(E entity);

    Map<String, Object> purveyDocument(E entity, Tenant tenant);
}
