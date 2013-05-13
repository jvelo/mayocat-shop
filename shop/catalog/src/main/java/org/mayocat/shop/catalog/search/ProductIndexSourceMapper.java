package org.mayocat.shop.catalog.search;

import java.util.Map;

import javax.inject.Inject;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.context.Execution;
import org.mayocat.model.Entity;
import org.mayocat.search.EntityIndexSourceMapper;
import org.mayocat.shop.catalog.model.Product;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
@Component("product")
public class ProductIndexSourceMapper extends AbstractEntityIndexSourceMapper implements EntityIndexSourceMapper
{
    public Class forClass()
    {
        return Product.class;
    }

    public Map<String, Object> mapSource(Entity entity, Tenant tenant)
    {
        Map<String, Object> source = Maps.newHashMap();

        //source.put("tenant", extractSourceFromEntity(tenant));
        source.putAll(extractSourceFromEntity(entity));

        return source;
    }
}
