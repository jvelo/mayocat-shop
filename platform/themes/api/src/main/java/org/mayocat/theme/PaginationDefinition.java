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
}
