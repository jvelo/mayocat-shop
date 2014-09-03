/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.internal.session;

import javax.inject.Inject;
import javax.ws.rs.core.NewCookie;

import org.mayocat.accounts.AccountsSettings;
import org.mayocat.accounts.session.JerseyCookieSessionManager;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.context.WebContext;
import org.mayocat.security.Cipher;
import org.mayocat.security.EncryptionException;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component
public class DefaultJerseyCookieSessionManager implements JerseyCookieSessionManager
{
    @Inject
    private Cipher crypter;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private WebContext context;

    @Override
    public NewCookie[] getCookies(String username, String password, boolean remember) throws EncryptionException
    {
        AccountsSettings accountsSettings = configurationService.getSettings(AccountsSettings.class);

        int ageWhenRemember = 60 * (context.getRequest().isApiRequest() ?
                accountsSettings.getApiSessionDuration().getValue() :
                accountsSettings.getWebSessionDuration().getValue());

        NewCookie newUserCookie = new NewCookie("username", crypter.encrypt(username), "/", null, null,
                remember ? ageWhenRemember : -1, false);
        NewCookie newPassCookie =
                new NewCookie("password", crypter.encrypt(password), "/", null, null,
                        remember ? ageWhenRemember : -1, false);

        return new NewCookie[]{ newUserCookie, newPassCookie };
    }
}
