package org.mayocat.url;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import org.jvnet.inflector.Noun;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.context.Execution;
import org.mayocat.model.Entity;
import org.mayocat.model.annotation.PluralForm;

/**
 * @version $Id$
 */
public abstract class AbstractGenericEntityURLFactory<E extends Entity> extends AbstractEntityURLFactory<E>
{
    @Inject
    private Execution execution;

    @Override
    public URL create(E entity, Tenant tenant)
    {
        return this.create(entity, tenant, URLType.PUBLIC);
    }

    @Override
    public URL create(E entity)
    {
        return this.create(entity, execution.getContext().getTenant(), URLType.PUBLIC);
    }

    @Override
    public URL create(E entity, Tenant tenant, URLType type)
    {
        try {
            // TODO
            // See if/how we want to support other protocol than HTTP
            String urlString = "http://" + getDomain(tenant);

            switch (type) {
                case API:
                    urlString += "/api/";
                    break;
                case PUBLIC:
                default:
                    urlString += "/";
                    if (execution.getContext() != null && execution.getContext().isAlternativeLocale()) {
                        urlString += execution.getContext().getLocale() + "/";
                    }
                    break;
            }

            urlString += getPluralForm(entity) + "/" + entity.getSlug();
            URL url = new URL(urlString);
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
