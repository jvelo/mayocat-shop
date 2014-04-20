/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.representations;

import java.util.List;

/**
 * @version $Id$
 */
public class ResultSetRepresentation<E> extends LinkRepresentation
{

    private Integer offset;

    private Integer number;

    private List<E> items;

    private Integer total;

    private LinkRepresentation first;

    private LinkRepresentation last;

    private LinkRepresentation previous;

    private LinkRepresentation next;

    public ResultSetRepresentation(String href, Integer number, Integer offset, List<E> items,
            Integer total)
    {
        super(href);
        this.offset = offset;
        this.number = number;
        this.items = items;
        this.total = total;

        if (offset - items.size() > 0) {
            this.previous = new LinkRepresentation(this.getHref() + "?number=" + number + "&offset=" + (offset - number));
        }
        if (offset + number < total) {
            this.next = new LinkRepresentation(this.getHref() + "?number=" + number + "&offset=" + (offset + number));
        }
        this.first = new LinkRepresentation(this.getHref() + "?number=" + number + "&offset=0");
        this.last = new LinkRepresentation(this.getHref() + "?number=" + number + "&offset=0");
    }

    public Integer getOffset()
    {
        return offset;
    }

    public Integer getNumber()
    {
        return number;
    }

    public List<E> getItems()
    {
        return items;
    }

    public Integer getTotal()
    {
        return total;
    }

    public void setTotal(Integer total)
    {
        this.total = total;
    }

    public LinkRepresentation getFirst()
    {
        return first;
    }

    public void setFirst(LinkRepresentation first)
    {
        this.first = first;
    }

    public LinkRepresentation getLast()
    {
        return last;
    }

    public void setLast(LinkRepresentation last)
    {
        this.last = last;
    }

    public LinkRepresentation getPrevious()
    {
        return previous;
    }

    public void setPrevious(LinkRepresentation previous)
    {
        this.previous = previous;
    }

    public LinkRepresentation getNext()
    {
        return next;
    }

    public void setNext(LinkRepresentation next)
    {
        this.next = next;
    }
}
