package org.mayocat.shop.front.internal;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.mayocat.shop.context.Execution;
import org.mayocat.shop.front.EntityContextProvider;
import org.mayocat.shop.front.EntityContextProviderSupplier;
import org.mayocat.shop.front.EntityFrontViewBuilder;
import org.mayocat.shop.model.Entity;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.rest.views.FrontView;
import org.mayocat.shop.theme.Breakpoint;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class DefaultEntityFrontViewBuilder implements EntityFrontViewBuilder
{
    @Inject
    private Execution execution;

    @Inject
    private EntityContextProviderSupplier providerSupplier;

    @Override
    public <E extends Entity> FrontView buildFrontView(String layout, E entity, Breakpoint breakpoint)
    {
        FrontView frontView = new FrontView(layout, breakpoint);

        Tenant tenant = execution.getContext().getTenant();
        // FIXME find a way to get tenant configurations injected as value objects
        Map<String, Object> generalConfiguration = (Map<String, Object>) tenant.getConfiguration().get("general");
        final String title = (String) generalConfiguration.get("name");
        String pageTitle = title;

        if (entity != null && providerSupplier.canSupply(entity.getClass())) {

            EntityContextProvider provider = providerSupplier.supply(entity.getClass());
            Optional<String> entityTitle = provider.getTitle(entity);
            Optional<String> entityDescription = provider.getDescription(entity);

            if (entityTitle.isPresent()) {
                pageTitle += " - " + entityTitle.get();
            }

            frontView.putInContext(nameFromEntity(entity), provider.getContext(entity));
        }

        frontView.putInContext("site", new HashMap(){{
            put("title", title);
        }});

        frontView.putInContext("page_title", pageTitle);

        return frontView;
    }

    @Override
    public <E extends Entity> FrontView buildFrontView(E entity)
    {
        return this.buildFrontView(nameFromEntity(entity), entity, Breakpoint.DEFAULT);
    }

    @Override
    public <E extends Entity> FrontView buildFrontView(E entity, Breakpoint breakpoint)
    {
        return this.buildFrontView(nameFromEntity(entity), entity, breakpoint);
    }

    @Override
    public FrontView build404(Breakpoint breakpoint)
    {
        return this.buildFrontView("404", breakpoint);
    }

    @Override
    public FrontView buildFrontView(String layout, Breakpoint breakpoint)
    {
        return this.buildFrontView(layout, null, breakpoint);
    }

    private <E extends Entity> String nameFromEntity(E entity)
    {
        return entity.getClass().getSimpleName().toLowerCase();
    }
}
