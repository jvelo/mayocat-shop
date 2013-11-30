/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.model;

import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


import org.mayocat.jackson.PasswordSerializer;
import org.mayocat.model.Entity;
import org.mayocat.model.annotation.Index;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class User implements Entity
{
    @JsonIgnore
    UUID id;

    @Index
    @NotNull
    @Size(min = 1)
    String slug;

    @Index
    @Pattern(regexp = "^(([^@\\s]+)@((?:[-a-zA-Z0-9]+\\.)+[a-zA-Z]{2,}))?$", message = "Not a valid email")
    @NotNull
    String email;

    /**
     * The password hash.
     */
    @NotNull
    @JsonSerialize(using = PasswordSerializer.class)
    String password;

    @JsonIgnore
    private boolean global = false;

    // /////////////////////////////////////////////////

    public User()
    {
    }

    public User(UUID id)
    {
        this.id = id;
    }

    // /////////////////////////////////////////////////

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public String getSlug()
    {
        return this.slug;
    }

    public void setSlug(String slug)
    {
        this.slug = slug;
    }

    public boolean isGlobal()
    {
        return global;
    }

    public void setGlobal(boolean global)
    {
        this.global = global;
    }
}
