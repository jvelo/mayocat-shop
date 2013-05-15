package org.mayocat.url;

import java.net.MalformedURLException;
import java.net.URL;

import org.jvnet.inflector.Noun;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.model.Entity;
import org.mayocat.model.annotation.PluralForm;

/**
 * @version $Id$
 */
public abstract class AbstractGenericEntityURLFactory<E extends Entity> extends AbstractEntityURLFactory<E>
{
    @Override
    public URL create(E entity, Tenant tenant)
    {
        return this.create(entity, tenant, URLType.PUBLIC);
    }

    @Override
    public URL create(E entity, Tenant tenant, URLType type)
    {
        try {
            // TODO
            // See if/how we want to support other protocol than HTTP

            URL url = new URL("http://" + getDomain(tenant) + (type.equals(URLType.API) ? "/api/" : "/") +
                    getPluralForm(entity) + "/" + entity.getSlug());

            return url;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    protected String getPluralForm(E entity)
    {
        if (entity.getClass().isAnnotationPresent(PluralForm.class)) {
            return entity.getClass().getAnnotation(PluralForm.class).value();
        }

        return Noun.pluralOf(getEntityName(entity));
    }

    protected abstract String getEntityName(E entity);
}
