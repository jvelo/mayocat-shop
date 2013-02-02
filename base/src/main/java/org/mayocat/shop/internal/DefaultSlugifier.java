package org.mayocat.shop.internal;

import java.text.Normalizer;

import org.mayocat.shop.Slugifier;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component
public class DefaultSlugifier implements Slugifier
{
    @Override
    public String slugify(String toSlugify)
    {
        return Normalizer.normalize(toSlugify.trim().toLowerCase(), java.text.Normalizer.Form.NFKD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "").replaceAll("[^\\w\\ ]", "")
                .replaceAll("\\s+", "-");
    }
}
