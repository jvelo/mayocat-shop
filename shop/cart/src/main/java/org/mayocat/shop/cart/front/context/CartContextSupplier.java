package org.mayocat.shop.cart.front.context;

import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.context.Execution;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.shop.cart.CartAccessor;
import org.mayocat.shop.cart.front.builder.CartContextBuilder;
import org.mayocat.shop.front.FrontContextSupplier;
import org.mayocat.shop.front.annotation.FrontContext;
import org.mayocat.shop.front.annotation.FrontContextContributor;
import org.mayocat.store.AttachmentStore;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("cart")
public class CartContextSupplier implements FrontContextSupplier
{
    @Inject
    private CartAccessor cartAccessor;

    @Inject
    private Provider<AttachmentStore> attachmentStore;

    @Inject
    private Provider<ThumbnailStore> thumbnailStore;

    @Inject
    private Execution execution;

    @FrontContextContributor(path = "/")
    public void contributeRootContext(@FrontContext Map data)
    {
        CartContextBuilder builder =
                new CartContextBuilder(attachmentStore.get(), thumbnailStore.get(), execution.getContext().getTheme());
        data.put("cart", builder.build(cartAccessor.getCart(),
                // TODO we need to find a way to have Jersey @Context injection in context suppliers...
                // so that we could here for example get the request locale via @Context Locale locale
                Locale.getDefault()
        ));
    }
}
