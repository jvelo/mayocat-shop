package org.mayocat.shop.thymeleaf;

import org.thymeleaf.templateresolver.TemplateResolver;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.resourceresolver.ServletContextResourceResolver;

public class EschoppeThymeleafTemplateResolver extends TemplateResolver {

    public EschoppeThymeleafTemplateResolver() {
        super();
        super.setResourceResolver(new EschoppeThymeleafResourceResolver());
    }

    @Override
    public synchronized void setResourceResolver(final IResourceResolver resourceResolver) {
        throw new ConfigurationException(
                "Cannot set a resource resolver on " + this.getClass().getName() + ". If " +
                "you want to set your own resource resolver, use " + TemplateResolver.class.getName() + 
                "instead");
    }

}

