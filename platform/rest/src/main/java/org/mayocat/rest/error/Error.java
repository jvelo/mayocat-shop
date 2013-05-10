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
