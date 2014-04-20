/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.error;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @version $Id$
 */
public class Error
{
    @JsonIgnore
    private Response.Status status;

    @JsonIgnore
    private ErrorCode errorCode;

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String developerMessage;

    public Error(Response.Status status, ErrorCode code, String message)
    {
        this(status, code, message, null);
    }

    public Error(Response.Status status, ErrorCode code, String message, String developerMessage)
    {
        this.status = status;
        this.errorCode = code;
        this.message = message;
        this.developerMessage = developerMessage;
    }

    @JsonInclude
    public int getStatus()
    {
        return status.getStatusCode();
    }

    @JsonInclude
    public int getCode()
    {
        return errorCode.getCode();
    }

    @JsonInclude
    public String getIdentifier()
    {
        return errorCode.getIdentifier();
    }

    public String getMessage()
    {
        return message;
    }

    public String getDeveloperMessage()
    {
        return developerMessage;
    }
}
