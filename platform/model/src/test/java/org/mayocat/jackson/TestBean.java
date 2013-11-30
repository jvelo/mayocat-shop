/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.jackson;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;

public class TestBean
{
    @JsonDeserialize(using = OptionalStringListDeserializer.class)
    private Optional<List<String>> foo = Optional.absent();

    private String bar;

    public Optional<List<String>> getFoo()
    {
        return foo;
    }

    public String getBar()
    {
        return bar;
    }
}