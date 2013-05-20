package org.mayocat.shop.catalog.search;

import org.mayocat.search.elasticsearch.AbstractGenericEntityMappingGenerator;
import org.mayocat.shop.catalog.model.Product;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("product")
public class ProductEntityMappingGenerator extends AbstractGenericEntityMappingGenerator
{
    @Override
    public Class forClass()
    {
        return Product.class;
    }
}
