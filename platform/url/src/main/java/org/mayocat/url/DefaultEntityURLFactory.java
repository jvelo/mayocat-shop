/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.url;

import java.net.URL;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.model.Entity;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

/**
 * @version $Id$
 */
@Component
public class DefaultEntityURLFactory extends AbstractGenericEntityURLFactory implements EntityURLFactory
{
    @Inject
    private ComponentManager componentManager;

    @Override
    public URL create(Entity entity, Tenant tenant, URLType type)
    {
        Preconditions.checkNotNull(entity, "Cannot create URL for a null entity");

        // Check against CM if there is a entity URL factory registered with the type reference of
        // EntityURLFactory<EntityClass>, if not, use the "generic" (in the sense of default)
        // URL creation provided by super.create()

        try {
            EntityURLFactory urlFactory =
                    this.componentManager.getInstance(entityURLFactoryOf(entity.getClass()).getType());
            if (urlFactory != null) {
                return urlFactory.create(entity, tenant, type);
            }
        } catch (ComponentLookupException e) {
            // Ignore, will return later
        }
        return super.create(entity, tenant, type);
    }

    @Override
    protected String getEntityName(Entity entity)
    {
        return StringUtils.substringAfterLast(entity.getClass().getName(), ".").toLowerCase();
    }

    static <T extends Entity> TypeToken<EntityURLFactory<T>> entityURLFactoryOf(Class<T> entityType)
    {
        return new TypeToken<EntityURLFactory<T>>(){}
                .where(new TypeParameter<T>(){}, entityType);
    }

}
