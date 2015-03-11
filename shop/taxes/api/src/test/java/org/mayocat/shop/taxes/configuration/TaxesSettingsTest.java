/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.taxes.configuration;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @version $Id$
 */
public class TaxesSettingsTest
{
    @Test
    public void testModeDeserialization() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        TaxesSettings mode = mapper.readValue("{ \"mode\": { \"value\" : \"incl\" }}", TaxesSettings.class);
        Assert.assertEquals(Mode.INCLUSIVE_OF_TAXES, mode.getMode().getValue());
    }
}
