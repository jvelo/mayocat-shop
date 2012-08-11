package org.mayocat.shop.store.datanucleus;

import org.mayocat.shop.model.Product;
import org.mayocat.shop.store.ProductStore;
import org.xwiki.component.annotation.Component;

@Component(hints = {"datanucleus", "default"})
public class DNProductStore extends AbstractHandleableEntityStore<Product, Long> implements ProductStore
{
}
