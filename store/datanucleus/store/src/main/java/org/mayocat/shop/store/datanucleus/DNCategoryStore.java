package org.mayocat.shop.store.datanucleus;

import org.mayocat.shop.model.Category;
import org.mayocat.shop.store.CategoryStore;
import org.mayocat.shop.store.ProductStore;
import org.xwiki.component.annotation.Component;

@Component(hints = {"datanucleus", "default"})
public class DNCategoryStore extends AbstractHandleableEntityStore<Category, Long> implements CategoryStore
{
}
