package org.mayocat.shop.model;

import org.mayocat.shop.model.reference.EntityReference;

public interface Entity extends Slug, Identifiable
{
    EntityReference getReference();
}
