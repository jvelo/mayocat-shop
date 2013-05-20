package org.mayocat.search.elasticsearch.internal.testsupport;

import java.util.HashMap;
import java.util.Map;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.model.Entity;
import org.mayocat.search.EntityIndexDocumentPurveyor;
import org.mayocat.search.elasticsearch.AbstractGenericEntityIndexDocumentPurveyor;

/**
 * @version $Id$
 */
public class CustomEntityIndexDocumentPurveyor extends AbstractGenericEntityIndexDocumentPurveyor<CustomEntity>
        implements EntityIndexDocumentPurveyor<CustomEntity>
{
    @Override protected Map<String, Object> extractSourceFromEntity(Entity entity, Tenant tenant)
    {
        return new HashMap<String, Object>()
        {
            {
                put("hello", "world");
            }
        };
    }
}
