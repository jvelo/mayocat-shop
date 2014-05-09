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

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mayocat.localization.EntityLocalizationService;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class DefaultEntityLocalizationServiceTest
{
    @Rule
    public final MockitoComponentMockingRule<EntityLocalizationService> componentManager =
            new MockitoComponentMockingRule(DefaultEntityLocalizationService.class);

    @Test
    public void testEntityLocalization() throws ComponentLookupException
    {
        Map<Locale, Map<String, Object>> versions = Maps.newHashMap();
        Map<String, Object> englishLocale = Maps.newHashMap();
        englishLocale.put("localizedString", "I am a Berliner");
        versions.put(new Locale("en"), englishLocale);
        Map<String, Object> germanLocale = Maps.newHashMap();
        germanLocale.put("localizedString", "Ich bin ein Berliner");
        versions.put(new Locale("de"), germanLocale);

        TestEntity entity = new TestEntity(UUID.randomUUID(), "test", versions);
        entity.setLocalizedString("Je suis un Berlinois");

        TestEntity inEnglish = this.componentManager.getComponentUnderTest().localize(entity, Locale.ENGLISH);
        Assert.assertNotSame(entity, inEnglish);
        TestEntity inGerman = this.componentManager.getComponentUnderTest().localize(entity, Locale.GERMAN);

        Assert.assertEquals("Je suis un Berlinois", entity.getLocalizedString());
        Assert.assertEquals("I am a Berliner", inEnglish.getLocalizedString());
        Assert.assertEquals("Ich bin ein Berliner", inGerman.getLocalizedString());
    }
}
