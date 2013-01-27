package org.mayocat.shop.front.context;

import java.util.Map;

import javax.inject.Named;

import org.mayocat.shop.front.EntityContextProvider;
import org.mayocat.shop.model.Entity;
import org.mayocat.shop.model.Product;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class ProductContextProvider implements EntityContextProvider<Product>
{
    @Override
    public Optional<String> getTitle(Product entity)
    {
        if (Strings.isNullOrEmpty(entity.getTitle())) {
            return Optional.absent();
        } else {
            return Optional.of(entity.getTitle());
        }
    }

    @Override
    public Optional<String> getDescription(Product entity)
    {
        if (Strings.isNullOrEmpty(entity.getTitle())) {
            return Optional.absent();
        } else {
            return Optional.of(entity.getTitle());
        }
    }

    @Override
    public  Optional<String> getImageURI(Product entity)
    {
        return Optional.absent();
    }

    @Override
    public Map<String, Object> getContext(Product entity)
    {
        Map<String, Object> productContext = Maps.newHashMap();
        productContext.put("title", entity.getTitle());
        productContext.put("description", entity.getDescription());

        return productContext;
    }

}
