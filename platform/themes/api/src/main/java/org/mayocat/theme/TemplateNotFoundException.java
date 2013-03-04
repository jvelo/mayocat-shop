package org.mayocat.theme;

/**
 * @version $Id$
 */
public class TemplateNotFoundException extends Exception
{
    public TemplateNotFoundException()
    {
        super();
    }

    public TemplateNotFoundException(String message)
    {
        super(message);
    }

    public TemplateNotFoundException(Throwable t)
    {
        super(t);
    }
}
