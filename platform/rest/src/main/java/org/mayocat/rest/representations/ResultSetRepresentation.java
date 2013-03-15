package org.mayocat.rest.representations;

import java.util.List;

/**
 * @version $Id$
 */
public class ResultSetRepresentation<E>
{

    private String href;

    private Integer offset;

    private Integer limit;

    private List<E> items;

    public ResultSetRepresentation(String href, Integer offset, Integer limit, List<E> items)
    {
        this.href = href;
        this.offset = offset;
        this.limit = limit;
        this.items = items;
    }

    public String getHref()
    {
        return href;
    }

    public Integer getOffset()
    {
        return offset;
    }

    public Integer getLimit()
    {
        return limit;
    }

    public List<E> getItems()
    {
        return items;
    }
}
