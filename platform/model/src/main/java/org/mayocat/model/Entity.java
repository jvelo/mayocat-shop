package org.mayocat.model;

import org.mayocat.model.reference.EntityReference;

public interface Entity extends Slug, Identifiable
{
    EntityReference getReference();
}
