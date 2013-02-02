package org.mayocat.shop.model.internal.reference;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.mayocat.shop.model.reference.EntityReference;
import org.mayocat.shop.model.reference.EntityReferenceResolver;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class DefaultEntityReferenceResolver implements EntityReferenceResolver
{
    // product:my-product:variant:my-variant

    @Override
    public EntityReference resolve(String serializedEntityReference)
    {
        String[] parts = StringUtils.split(serializedEntityReference, ":");
        if (parts.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid entity reference : uneven number of parts");
        }

        String slug = parts[parts.length - 1];
        String type = parts[parts.length - 2];

        if (parts.length > 2) {
            String remainder = "";
            for (int i = 0; i < parts.length - 2; i = i + 2) {
                if (i > 0) {
                    remainder += ":";
                }
                remainder += (parts[i] + ":" + parts[i + 1]);
            }
            EntityReference parent = this.resolve(remainder);
            return new EntityReference(type, slug, Optional.of(parent));
        } else {
            return new EntityReference(type, slug, Optional.<EntityReference>absent());
        }
    }
}
