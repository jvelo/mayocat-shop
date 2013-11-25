package org.mayocat.shop.front.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Helper class to facilitate building contexts with pagination
 *
 * @version $Id$
 */
public class PaginationContextBuilder
{
    public static interface UrlBuilder
    {
        String build(int page);
    }

    public Map<String, Object> build(final int currentPage, final int totalPages, final UrlBuilder builder)
    {
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> pages = Lists.newArrayList();
        for (int i = 1; i <= totalPages; i++) {
            Map<String, Object> iPage = Maps.newHashMap();
            iPage.put("number", i);
            iPage.put("url", builder.build(i));
            if (i == currentPage) {
                iPage.put("current", true);
            }
            pages.add(iPage);
        }

        result.put("pages", pages);
        result.put("currentPage", currentPage);

        if (currentPage > 1) {
            result.put("previous", new HashMap<String, Object>()
            {
                {
                    put("number", currentPage - 1);
                    put("url", builder.build(currentPage - 1));
                }
            });
        }

        if (currentPage < totalPages) {
            result.put("next", new HashMap<String, Object>()
            {
                {
                    put("number", currentPage + 1);
                    put("url", builder.build(currentPage + 1));
                }
            });
        }

        return result;
    }
}
