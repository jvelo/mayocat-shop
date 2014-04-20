/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.store.rdbms.dbi;

import java.util.List;

import org.mayocat.model.Entity;
import org.mayocat.store.HasOrderedCollections;
import org.mayocat.store.InvalidMoveOperation;
import org.mayocat.store.rdbms.dbi.dao.util.CollectionUtil;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class MoveEntityInListOperation<E extends Entity>
{
    private List<E> entities;

    private List<Integer> positions;

    private boolean hasMoved = false;

    public MoveEntityInListOperation(List<E> entities, String fromSlug, String toSlug,
            HasOrderedCollections.RelativePosition relativePosition)
            throws InvalidMoveOperation
    {
        Preconditions.checkNotNull(entities, "entities list may not be null");
        Preconditions.checkNotNull(fromSlug, "\"from\" slug may not be null");
        Preconditions.checkNotNull(toSlug, "\"to\" slug may not be null");

        Integer from = -1;
        Integer to = -1;
        int i = 0;
        this.entities = entities;
        for (E entity : this.entities) {
            if (fromSlug.equals(entity.getSlug())) {
                from = i;
            }
            if (toSlug.equals(entity.getSlug())) {
                if (from == -1) {
                    // From is down the list
                    to = relativePosition.equals(HasOrderedCollections.RelativePosition.AFTER) ?
                            i + 1 : i;
                } else {
                    // From is already found : this position or up the list
                    to = relativePosition.equals(HasOrderedCollections.RelativePosition.AFTER) ?
                            i : i - 1;
                }
            }
            i++;
        }
        if (from < 0 || to < 0) {
            throw new InvalidMoveOperation();
        }

        if (from != to) {
            this.hasMoved = true;
            CollectionUtil.move(this.entities, from, to);
        }
    }

    public List<E> getEntities()
    {
        return this.entities;
    }

    public List<Integer> getPositions()
    {
        // Create a list with the sequence of positions
        List<Integer> positions = Lists.newArrayList();
        for (int j = 0; j < this.entities.size(); j++) {
            positions.add(j);
        }
        return positions;
    }

    public boolean hasMoved()
    {
        return this.hasMoved;
    }
}
