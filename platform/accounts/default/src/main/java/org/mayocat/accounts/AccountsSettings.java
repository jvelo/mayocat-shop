/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts;

import javax.validation.Valid;

import org.mayocat.configuration.Configurable;
import org.mayocat.configuration.ExposedSettings;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @version $Id$
 */
public class AccountsSettings implements ExposedSettings
{
    /**
     * Do users need to validate their account ?
     */
    @Valid
    @JsonProperty
    private Configurable<Boolean> userValidation = new Configurable<>(Boolean.FALSE, false);

    /**
     * Template for the validation URI. The string is templated, and the ${validationKey} variable is provided. An
     * example for a validationURi  (the default value if none is provided) is : http://example.com/accounts/validation/${validationKey}
     */
    @Valid
    @JsonProperty
    private Configurable<String> userValidationUriTemplate = new Configurable(null, false);

    /**
     * Template for the password reset URI. The string is templated, and the ${resetKey} variable is provided. An
     * example (the default value if none is provided) for a resetKey is: http://example.com/login/reset-password/{resetKey}
     */
    @Valid
    @JsonProperty
    private Configurable<String> userPasswordResetUriTemplate = new Configurable(null, false);

    /**
     * When set to true and when validation is mandatory, login attempt with a user account that is not validated will
     * fail. Note: this setting is ignored if validation is not enabled (See {@link #userValidation}
     */
    @Valid
    @JsonProperty
    private Configurable<Boolean> userValidationRequiredForLogin = new Configurable(Boolean.TRUE, false);

    /**
     * Duration of a web session, in minutes.
     */
    @Valid
    @JsonProperty
    private Configurable<Integer> webSessionDuration = new Configurable(21600, false);

    /**
     * Duration of a cookie-based API session, in minutes.
     */
    @Valid
    @JsonProperty
    private Configurable<Integer> apiSessionDuration = new Configurable(21600, false);

    /**
     * Is a user automatically signed in after he signed up ?
     */
    @Valid
    @JsonProperty
    private Configurable<Boolean> autoLoginAfterSignup = new Configurable(Boolean.TRUE, false);

    /**
     * Requirements for passwords. This is not configurable at the tenant level by design.
     */
    @Valid
    @JsonProperty
    private PasswordRequirementsSettings passwordRequirements = new PasswordRequirementsSettings();

    public String getKey()
    {
        return "accounts";
    }

    public Configurable<Boolean> getUserValidation()
    {
        return userValidation;
    }

    public Configurable<String> getUserValidationUriTemplate()
    {
        return userValidationUriTemplate;
    }

    public Configurable<String> getUserPasswordResetUriTemplate()
    {
        return userPasswordResetUriTemplate;
    }

    public Configurable<Boolean> getUserValidationRequiredForLogin()
    {
        return userValidationRequiredForLogin;
    }

    public Configurable<Integer> getWebSessionDuration()
    {
        return webSessionDuration;
    }

    public Configurable<Integer> getApiSessionDuration()
    {
        return apiSessionDuration;
    }

    public Configurable<Boolean> isAutoLoginAfterSignup()
    {
        return autoLoginAfterSignup;
    }

    public PasswordRequirementsSettings getPasswordRequirements()
    {
        return passwordRequirements;
    }
}
