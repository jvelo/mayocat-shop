package org.mayocat.internal;

import java.text.Normalizer;

import org.mayocat.Slugifier;
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
                .trim()
                .replaceAll("\\s+", "-");
    }
}
