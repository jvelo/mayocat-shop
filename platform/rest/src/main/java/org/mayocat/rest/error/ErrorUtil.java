package org.mayocat.rest.error;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @version $Id$
 */
public class ErrorUtil
{
    public static Response buildError(Response.Status status, ErrorCode code, String message)
    {
        return Response.status(status)
                .entity(new Error(status, code, message)).type(MediaType.APPLICATION_JSON).build();
    }

    public static Response buildError(Response.Status status, ErrorCode code, String message, String developerMessage)
    {
        return Response.status(status)
                .entity(new Error(status, code, message, developerMessage)).type(MediaType.APPLICATION_JSON).build();
    }
}
