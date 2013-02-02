package org.mayocat.shop.model;

import org.mayocat.shop.model.reference.EntityReference;

public interface Entity
{
    String getSlug();

    void setSlug(String slug);

    Long getId();

    void setId(Long id);

    EntityReference getReference();

    EntityReference getParentReference();
}
