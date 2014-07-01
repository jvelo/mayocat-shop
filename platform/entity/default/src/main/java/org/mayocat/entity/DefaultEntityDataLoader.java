/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.entity;

import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import org.mayocat.model.Entity;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * @version $Id$
 */
@Component
public class DefaultEntityDataLoader implements EntityDataLoader, Initializable
{
    @Inject
    private List<DataLoaderAssistant> assistants;

    public <E extends Entity> EntityData<E> load(E entity, LoadingOption... options)
    {
        EntityData<E> data = new EntityData<>(entity);
        for (DataLoaderAssistant assistant : assistants) {
            assistant.load(data, options);
        }
        return data;
    }

    public <E extends Entity> List<EntityData<E>> load(List<E> entities, LoadingOption... options)
    {
        List<EntityData<E>> data = FluentIterable.from(entities).transform(new Function<E, EntityData<E>>()
        {
            public EntityData<E> apply(final E input)
            {
                return new EntityData<E>(input);
            }
        }).toList();

        for (DataLoaderAssistant assistant : assistants) {
            assistant.loadList(data, options);
        }

        return data;
    }

    public void initialize() throws InitializationException
    {
        assistants = FluentIterable.from(assistants).toSortedList(new Comparator<DataLoaderAssistant>()
        {
            public int compare(DataLoaderAssistant o1, DataLoaderAssistant o2)
            {
                return o2.priority() - o1.priority();
            }
        });
    }
}
