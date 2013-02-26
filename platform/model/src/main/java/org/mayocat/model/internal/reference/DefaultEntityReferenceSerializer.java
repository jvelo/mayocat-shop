package org.mayocat.model.internal.reference;

import org.apache.commons.lang3.StringUtils;
import org.mayocat.model.reference.EntityReference;
import org.mayocat.model.reference.EntityReferenceSerializer;

/**
 * @version $Id$
 */
public class DefaultEntityReferenceSerializer implements EntityReferenceSerializer
{
    @Override
    public String serialize(EntityReference reference)
    {
        if (reference == null) {
            return null;
        }

        if (reference.getType() == null || reference.getSlug() == null) {
            throw new IllegalArgumentException("Invalid entity : type or slug is null");
        }

        String parentReference = serialize(reference.getParent());
        if (!StringUtils.isBlank(parentReference)) {
            parentReference += ":";
        }
        return
                StringUtils.defaultIfBlank(parentReference, "") +
                        reference.getType() + ":" + reference.getSlug();
    }
}
