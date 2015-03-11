/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.representations;

import javax.validation.constraints.NotNull;

/**
 * Doc goes here.
 *
 * @version $Id$
 */
public class PasswordChangeRepresentation
{
    @NotNull
    private String currentPassword;

    @NotNull
    private String newPassword;

    public String getCurrentPassword()
    {
        return currentPassword;
    }

    public String getNewPassword()
    {
        return newPassword;
    }

    public void setCurrentPassword(String currentPassword)
    {
        this.currentPassword = currentPassword;
    }

    public void setNewPassword(String newPassword)
    {
        this.newPassword = newPassword;
    }
}
