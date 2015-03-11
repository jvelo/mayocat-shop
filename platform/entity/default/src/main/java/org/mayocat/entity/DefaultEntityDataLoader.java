/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.entity;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import org.mayocat.localization.EntityLocalizationService;
import org.mayocat.model.Entity;
import org.mayocat.model.Localized;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;

/**
 * @version $Id$
 */
@Component
public class DefaultEntityDataLoader implements EntityDataLoader, Initializable
{
    @Inject
    private List<DataLoaderAssistant> assistants;

    @Inject
    private EntityLocalizationService localizationService;

    public <E extends Entity> EntityData<E> load(E entity, LoadingOption... options)
    {
        boolean localize = Arrays.asList(options).indexOf(StandardOptions.LOCALIZE) >= 0;
        E actual;
        if (localize && Localized.class.isAssignableFrom(entity.getClass())) {
            actual = (E) localizationService.localize((Localized) entity);
        } else {
            actual = entity;
        }
        EntityData<E> data = new EntityData<>(actual);
        for (DataLoaderAssistant assistant : assistants) {
            assistant.load(data, options);
        }
        return data;
    }

    public <E extends Entity> List<EntityData<E>> load(List<E> entities, LoadingOption... options)
    {
        final boolean localize = Arrays.asList(options).indexOf(StandardOptions.LOCALIZE) >= 0;
        List<EntityData<E>> data = FluentIterable.from(entities).transform(new Function<E, EntityData<E>>()
        {
            public EntityData<E> apply(final E input)
            {
                if (input == null) {
                    // Garbage in, garbage out
                    return null;
                }
                E actual;
                if (localize && Localized.class.isAssignableFrom(input.getClass())) {
                    actual = (E) localizationService.localize((Localized) input);
                } else {
                    actual = input;
                }
                return new EntityData<E>(actual);
            }
        }).filter(Predicates.notNull()).toList();

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
