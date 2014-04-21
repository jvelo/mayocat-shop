package org.mayocat.accounts.passwords.zxcvbn;

import javax.inject.Inject;

import org.mayocat.accounts.passwords.PasswordStrengthChecker;
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
public class ZxcvbnPasswordStrengthChecker implements PasswordStrengthChecker
{
    @Inject
    private SecuritySettings securitySettings;

    private ZxcvbnPasswordMeter meter = new ZxcvbnPasswordMeter().inputs("Mayocat", "MayocatShop");

    public boolean isStrongEnough(String password)
    {
        return meter.getStrength(password).getEntropy() > securitySettings.getMinimumZxcvbnPasswordEntroy();
    }
}
