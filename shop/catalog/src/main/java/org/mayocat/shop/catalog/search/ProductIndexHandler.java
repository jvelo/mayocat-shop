package org.mayocat.shop.catalog.search;

import java.util.Map;

import javax.inject.Inject;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.model.Entity;
import org.mayocat.search.elasticsearch.AbstractEntityIndexHandler;
import org.mayocat.search.EntityIndexHandler;
import org.mayocat.shop.catalog.model.Product;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentManager;

import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
@Component("product")
public class ProductIndexHandler extends AbstractEntityIndexHandler implements EntityIndexHandler
{
    @Inject
    private Logger logger;

    public Class forClass()
    {
        return Product.class;
    }

    public Map<String, Object> getDocument(Entity entity, Tenant tenant)
    {
        Map<String, Object> source = Maps.newHashMap();

        source.put("site", extractSourceFromEntity(tenant, tenant));
        source.putAll(extractSourceFromEntity(entity, tenant));

        return source;
    }

    @Override
    public void updateMapping()
    {

    }
}
