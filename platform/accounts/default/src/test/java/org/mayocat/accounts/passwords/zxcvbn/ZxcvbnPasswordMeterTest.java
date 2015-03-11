/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.passwords.zxcvbn;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link org.mayocat.accounts.passwords.zxcvbn.ZxcvbnPasswordMeter}
 *
 * @version $Id$
 */
public class ZxcvbnPasswordMeterTest
{
    private ZxcvbnPasswordMeter checker = new ZxcvbnPasswordMeter();

    @Test
    public void testZxcvbnPasswordStrength()
    {
        PasswordStrength result = checker.getStrength("toto");

        Assert.assertEquals("toto", result.getPassword());
        Assert.assertTrue(result.getEntropy() < 5);
    }

    @Test
    public void testInputWordsAffectScore()
    {
        PasswordStrength resultBefore = checker.getStrength("MayocatShop");
        checker.inputs("Mayocat", "SomethingElse");
        PasswordStrength resultAfter = checker.getStrength("MayocatShop");

        Assert.assertTrue(resultBefore.getEntropy() > resultAfter.getEntropy());
    }
}
