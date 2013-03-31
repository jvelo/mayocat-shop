package org.mayocat.context;

import java.io.FilterOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version $Id$
 */
public class WrappedServletOutputStream extends ServletOutputStream
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WrappedServletOutputStream.class);

    private final FilterOutputStream output;

    public WrappedServletOutputStream(ServletOutputStream output)
    {
        this.output = new FilterOutputStream(output);
    }

    @Override
    public void write(int b) throws IOException
    {
        //LOGGER.debug("write()");
        output.write(b);
    }

    @Override
    public void flush() throws IOException
    {
        LOGGER.debug("flush()");
        output.flush();
    }
}
