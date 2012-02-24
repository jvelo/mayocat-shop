package org.mayocat.shop.thymeleaf;

import java.io.InputStream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;

import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.resourceresolver.ServletContextResourceResolver;

public class EschoppeThymeleafResourceResolver implements IResourceResolver {

    public static final String NAME = "mayocat";

    private ServletContextResourceResolver delegate = new ServletContextResourceResolver();

    public EschoppeThymeleafResourceResolver() {
        super();
    }
    
    public String getName() {
        return NAME; 
    }

    public InputStream getResourceAsStream(final org.thymeleaf.TemplateProcessingParameters parameters, final String resourceName) {
        
        List<String> fragments = new ArrayList<String>(Arrays.asList(StringUtils.split(resourceName, '/')));
        if (fragments.size() > 1) {
          fragments.remove(fragments.size() - 2);
        }
        String targetResourceName = StringUtils.join(fragments.toArray(), '/');

        return this.delegate.getResourceAsStream(parameters, resourceName);
    }

}
