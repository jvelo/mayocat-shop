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
