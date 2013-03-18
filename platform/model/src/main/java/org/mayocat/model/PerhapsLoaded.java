package org.mayocat.model;

import java.io.Serializable;

import com.google.common.base.Objects;

/**
 * Intended to wrap an entity field in an option style construct that let consumers test whether the field has been
 * loaded or not. Typically loaded will mean "loaded from database". When backed by a RDBMS, this is useful for fields
 * that require sub-querying, or joins, or any costy operation. Those fields would be typically loaded only when
 * needed.
 *
 * @version $Id$
 */
public class PerhapsLoaded<T> implements Serializable
{
    private T reference;

    public static PerhapsLoaded empty()
    {
        return new PerhapsLoaded(null);
    }

    public PerhapsLoaded(T t)
    {
        this.reference = t;
    }

    public boolean isLoaded()
    {
        return this.reference != null;
    }

    public T or(T t)
    {
        return this.isLoaded() ? reference : t;
    }

    public T orNull()
    {
        return this.reference;
    }

    public T get()
    {
        if (!isLoaded()) {
            throw new IllegalStateException("Cannot access a value not laden");
        }
        return reference;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PerhapsLoaded<T> other = (PerhapsLoaded<T>) obj;

        return Objects.equal(this.get(), other.get());
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.reference);
    }

    @Override
    public String toString()
    {
        return "PerhapsLoaded of [" + reference + "]";
    }
}
