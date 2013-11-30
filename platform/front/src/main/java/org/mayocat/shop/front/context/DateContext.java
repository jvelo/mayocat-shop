/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.front.context;

import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * @version $Id$
 */
public class DateContext
{
    private String shortDate;

    private String longDate;

    private Integer dayOfMonth;

    private Integer monthOfYear;

    private Integer year;

    private Long time;

    private String dateTime;

    public DateContext(Date date, Locale locale)
    {
        DateTime dt = new DateTime(date);

        shortDate = DateTimeFormat.shortDate().withLocale(locale).print(dt);
        longDate = DateTimeFormat.longDate().withLocale(locale).print(dt);

        dayOfMonth = dt.getDayOfMonth();
        monthOfYear = dt.getMonthOfYear();
        year = dt.getYear();
        time = dt.toDate().getTime();
        dateTime = dt.toString();
    }

    public String getShortDate()
    {
        return shortDate;
    }

    public String getLongDate()
    {
        return longDate;
    }

    public Integer getDayOfMonth()
    {
        return dayOfMonth;
    }

    public Integer getMonthOfYear()
    {
        return monthOfYear;
    }

    public Integer getYear()
    {
        return year;
    }

    public Long getTime()
    {
        return time;
    }

    public String getDateTime()
    {
        return dateTime;
    }
}
