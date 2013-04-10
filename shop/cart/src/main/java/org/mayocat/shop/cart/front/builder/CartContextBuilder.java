package org.mayocat.shop.cart.front.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Attachment;
import org.mayocat.shop.cart.front.context.CartContext;
import org.mayocat.shop.cart.front.context.CartItemContext;
import org.mayocat.shop.cart.model.Cart;
import org.mayocat.shop.catalog.front.representation.PriceRepresentation;
import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.front.builder.ImageContextBuilder;
import org.mayocat.shop.front.context.ImageContext;
import org.mayocat.store.AttachmentStore;
import org.mayocat.theme.Theme;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * @version $Id$
 */
public class CartContextBuilder
{
    private AttachmentStore attachmentStore;

    private ThumbnailStore thumbnailStore;

    private ImageContextBuilder imageContextBuilder;

    public CartContextBuilder(AttachmentStore attachmentStore, ThumbnailStore thumbnailStore, Theme theme)
    {
        this.attachmentStore = attachmentStore;
        this.thumbnailStore = thumbnailStore;
        this.imageContextBuilder = new ImageContextBuilder(theme);
    }

    public CartContext build(Cart cart, Locale locale)
    {
        Long numberOfItems = 0l;
        List<CartItemContext> itemsContext = Lists.newArrayList();

        Map<Purchasable, Long> items = cart.getItems();
        PriceRepresentation total = new PriceRepresentation(cart.getTotal(), cart.getCurrency(), locale);

        Collection<Long> featuredImageIds = Collections2.transform(cart.getItems().keySet(),
                new Function<Purchasable, Long>()
                {
                    @Override
                    public Long apply(final Purchasable product)
                    {
                        return product.getFeaturedImageId();
                    }
                }
        );
        List<Long> ids = new ArrayList<Long>(Collections2.filter(featuredImageIds, Predicates.notNull()));
        List<Attachment> allImages;
        List<Thumbnail> allThumbnails;
        if (ids.isEmpty()) {
            allImages = Collections.emptyList();
            allThumbnails = Collections.emptyList();
        } else {
            allImages = this.attachmentStore.findByIds(ids);
            allThumbnails = this.thumbnailStore.findAllForIds(ids);
        }

        for (final Purchasable purchasable : items.keySet()) {

            Collection<Attachment> attachments = Collections2.filter(allImages, new Predicate<Attachment>()
            {
                @Override
                public boolean apply(@Nullable Attachment attachment)
                {
                    return attachment.getId().equals(purchasable.getFeaturedImageId());
                }
            });
            List<Image> images = new ArrayList<Image>();
            for (final Attachment attachment : attachments) {
                Collection<Thumbnail> thumbnails = Collections2.filter(allThumbnails, new Predicate<Thumbnail>()
                {
                    @Override
                    public boolean apply(@Nullable Thumbnail thumbnail)
                    {
                        return thumbnail.getAttachmentId().equals(attachment.getId());
                    }
                });
                Image image = new Image(attachment, new ArrayList<Thumbnail>(thumbnails));
                images.add(image);
            }

            Long quantity = items.get(purchasable);

            CartItemContext cir = new CartItemContext();
            cir.setTitle(purchasable.getTitle());
            cir.setDescription(purchasable.getDescription());
            cir.setQuantity(quantity);
            if (images.size() > 0) {
                ImageContext featuredImageContext = imageContextBuilder.createImageContext(images.get(0));
                cir.setFeaturedImage(featuredImageContext);
            }

            PriceRepresentation unitPrice =
                    new PriceRepresentation(purchasable.getUnitPrice(), cart.getCurrency(), locale);
            PriceRepresentation itemTotal =
                    new PriceRepresentation(cart.getItemTotal(purchasable), cart.getCurrency(), locale);

            cir.setUnitPrice(unitPrice);
            cir.setItemTotal(itemTotal);

            numberOfItems += quantity;
            itemsContext.add(cir);
        }

        return new CartContext(itemsContext, numberOfItems, total);
    }
}
