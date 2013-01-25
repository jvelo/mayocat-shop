package org.mayocat.shop.views;

/**
 * @version $Id$
 */
public interface TemplateEngine
{
    void register(Template template);

    String render(String templateName, String json);
}
