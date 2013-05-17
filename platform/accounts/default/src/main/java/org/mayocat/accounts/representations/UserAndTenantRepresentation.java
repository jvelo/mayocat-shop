package org.mayocat.accounts.representations;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.mayocat.accounts.model.User;

/**
 * @version $Id$
 */
public class UserAndTenantRepresentation
{
    @NotNull
    @Valid
    private User user;

    @NotNull
    @Valid
    private TenantRepresentation tenant;

    public UserAndTenantRepresentation()
    {
    }

    public TenantRepresentation getTenant()
    {
        return tenant;
    }

    public void setTenant(TenantRepresentation tenant)
    {
        this.tenant = tenant;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }
}

