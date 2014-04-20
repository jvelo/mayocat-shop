/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.localization.internal;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.mayocat.model.Localized;
import org.mayocat.model.annotation.LocalizedField;

/**
 * @version $Id$
 */
public class TestEntity implements Localized
{
    @LocalizedField
    private String localizedString;

    private String notLocalizedString;

    private String slug;

    private UUID id;

    private Map<Locale, Map<String, Object>> localizedVersions;

    public TestEntity(UUID id, String slug, Map<Locale, Map<String, Object>> localizedVersions)
    {
        this.slug = slug;
        this.id = id;
        this.localizedVersions = localizedVersions;
    }

    public String getLocalizedString()
    {
        return localizedString;
    }

    public void setLocalizedString(String localizedString)
    {
        this.localizedString = localizedString;
    }

    public String getNotLocalizedString()
    {
        return notLocalizedString;
    }

    public void setNotLocalizedString(String notLocalizedString)
    {
        this.notLocalizedString = notLocalizedString;
    }

    @Override
    public Map<Locale, Map<String, Object>> getLocalizedVersions()
    {
        return this.localizedVersions;
    }

    @Override
    public UUID getId()
    {
        return this.id;
    }

    @Override
    public void setId(UUID id)
    {
        this.id = id;
    }

    @Override
    public String getSlug()
    {
        return this.slug;
    }

    @Override
    public void setSlug(String slug)
    {
        this.slug = slug;
    }
}