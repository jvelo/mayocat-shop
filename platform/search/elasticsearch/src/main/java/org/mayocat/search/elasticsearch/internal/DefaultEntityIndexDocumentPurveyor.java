/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.search.elasticsearch.internal;

import java.util.Map;

import javax.inject.Inject;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.model.Entity;
import org.mayocat.search.EntityIndexDocumentPurveyor;
import org.mayocat.search.elasticsearch.AbstractGenericEntityIndexDocumentPurveyor;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

/**
 * @version $Id$
 */
@Component
public class DefaultEntityIndexDocumentPurveyor extends AbstractGenericEntityIndexDocumentPurveyor implements EntityIndexDocumentPurveyor
{
    @Inject
    private ComponentManager componentManager;

    @Override
    public Map<String, Object> purveyDocument(Entity entity, Tenant tenant)
    {
        // Check against CM if there is a index handler registered with the type reference of
        // EntityIndexDocumentPurveyor<EntityClass>, if not, use the "generic" (in the sense of default)
        // index handling provided by the abstract generic entity index handler.

        try {
            EntityIndexDocumentPurveyor indexHandler =
                    this.componentManager.getInstance(indexDocumentPurveyorOf(entity.getClass()).getType());
            if (indexHandler != null) {
                return indexHandler.purveyDocument(entity, tenant);
            }
        } catch (ComponentLookupException e) {
            // Ignore, will return later
        }

        return super.purveyDocument(entity, tenant);
    }

    static <T extends Entity> TypeToken<EntityIndexDocumentPurveyor<T>> indexDocumentPurveyorOf(Class<T> entityType)
    {
        return new TypeToken<EntityIndexDocumentPurveyor<T>>(){}
                .where(new TypeParameter<T>(){}, entityType);
    }
}
