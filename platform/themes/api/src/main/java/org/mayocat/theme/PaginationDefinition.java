/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.theme;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;

/**
 * Pagination element of a {@link ThemeDefinition}.
 *
 * @version $Id$
 */
public class PaginationDefinition
{
    /**
     * @see {@link #getItemsPerPage()}
     */
    @JsonProperty
    private Integer itemsPerPage = 24;

    /**
     * @see {@link #getModels()}
     */
    @JsonProperty
    Map<String, PaginationDefinition> models = Collections.emptyMap();

    /**
     * @see {@link #getOthers()}
     */
    @JsonProperty
    private Map<String, Integer> others = Maps.newHashMap();

    /**
     * Default value for when the pagination is not defined. We chose 24 as the default value because it can be divided
     * in 2, 3, 4 and 6
     */
    public Integer getItemsPerPage()
    {
        return itemsPerPage;
    }

    /**
     * The pagination definition for alternative models for the template this pagination definition applies to.
     */
    public Map<String, PaginationDefinition> getModels()
    {
        return models;
    }

    /**
     * A map of optional others pagination for this entity/page
     */
    public Map<String, Integer> getOthers()
    {
        return others;
    }

    /**
     *
     */
    public Optional<Integer> getOtherDefinition(String key)
    {
        return this.others.containsKey(key) ? Optional.of(this.others.get(key)) : Optional.<Integer>absent();
    }
}
