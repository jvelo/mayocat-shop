package org.mayocat.shop.front.resources;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import org.mayocat.model.Attachment;
import org.mayocat.shop.front.FrontBindingManager;

/**
 * @version $Id$
 */
public class AbstractFrontResource
{
    protected final static Set<String> IMAGE_EXTENSIONS = new HashSet<String>();

    @Inject
    private FrontBindingManager bindingManager;

    static {
        IMAGE_EXTENSIONS.add("jpg");
        IMAGE_EXTENSIONS.add("jpeg");
        IMAGE_EXTENSIONS.add("png");
        IMAGE_EXTENSIONS.add("gif");
    }

    protected static boolean isImage(Attachment attachment)
    {
        return IMAGE_EXTENSIONS.contains(attachment.getExtension().toLowerCase());
    }

    protected FrontBindingManager getBindingManager()
    {
        return bindingManager;
    }

    protected Map<String, Object> getBindings(UriInfo uriInfo)
    {
        return bindingManager.getBindings(uriInfo);
    }
}
