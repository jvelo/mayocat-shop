package org.mayocat.context;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version $Id$
 */
public class FilterResponseWrapper extends HttpServletResponseWrapper
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FilterResponseWrapper.class);

    private final WrappedServletOutputStream output;

    private final PrintWriter writer;

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @throws IllegalArgumentException if the response is null
     */
    public FilterResponseWrapper(HttpServletResponse response) throws IOException
    {
        super(response);
        output = new WrappedServletOutputStream(response.getOutputStream());
        writer = new PrintWriter(output, true);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException
    {
        LOGGER.debug("getOutputStream()");
        return output;
    }

    @Override
    public PrintWriter getWriter() throws IOException
    {
        LOGGER.debug("getWriter()");
        return writer;
    }

    @Override
    public void flushBuffer() throws IOException
    {
        LOGGER.debug("flushBuffer()");
        writer.flush();
    }
}
