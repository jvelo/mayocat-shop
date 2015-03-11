/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.passwords;

import javax.inject.Inject;

import org.mayocat.accounts.AccountsSettings;
import org.mayocat.accounts.passwords.PasswordStrengthChecker;
import org.mayocat.accounts.passwords.zxcvbn.ZxcvbnPasswordMeter;
import org.mayocat.configuration.SecuritySettings;
import org.xwiki.component.annotation.Component;

/**
 * zxcvbn.js based password strength checker.
 *
 * See https://tech.dropbox.com/2012/04/zxcvbn-realistic-password-strength-estimation/ and
 * https://github.com/dropbox/zxcvbn
 *
 * @version $Id$
 */
@Component
public class DefaultPasswordStrengthChecker implements PasswordStrengthChecker
{
    @Inject
    private AccountsSettings accountsSettings;

    private ZxcvbnPasswordMeter meter = new ZxcvbnPasswordMeter().inputs("Mayocat", "MayocatShop");

    @Override
    public boolean checkLength(String password)
    {
        return password.length() >= accountsSettings.getPasswordRequirements().getMinimalLength();
    }

    @Override
    public boolean checkEntropy(String password)
    {
        if (!accountsSettings.getPasswordRequirements().getMinimalEntropyBits().isPresent()) {
            return true;
        }
        return meter.getStrength(password).getEntropy() >=
                accountsSettings.getPasswordRequirements().getMinimalEntropyBits().get();
    }
}
