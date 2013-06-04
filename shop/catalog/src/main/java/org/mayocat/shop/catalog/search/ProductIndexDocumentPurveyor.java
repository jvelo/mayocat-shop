package org.mayocat.shop.catalog.search;

import java.util.Map;

import javax.inject.Inject;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.search.EntityIndexDocumentPurveyor;
import org.mayocat.search.elasticsearch.AbstractGenericEntityIndexDocumentPurveyor;
import org.mayocat.shop.catalog.model.Product;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
@Component
public class ProductIndexDocumentPurveyor extends AbstractGenericEntityIndexDocumentPurveyor<Product>
        implements EntityIndexDocumentPurveyor<Product>
{
    @Inject
    private Logger logger;

    public Class forClass()
    {
        return Product.class;
    }

    public Map<String, Object> purveyDocument(Product entity, Tenant tenant)
    {
        Map<String, Object> source = Maps.newHashMap();

        source.put("site", extractSourceFromEntity(tenant, tenant));
        source.putAll(extractSourceFromEntity(entity, tenant));

        return source;
    }

}
