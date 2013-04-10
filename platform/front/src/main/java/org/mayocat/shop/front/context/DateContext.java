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

    public DateContext(Date date, Locale locale)
    {
        DateTime dateTime = new DateTime(date);

        shortDate = DateTimeFormat.shortDate().withLocale(locale).print(dateTime);
        longDate = DateTimeFormat.longDate().withLocale(locale).print(dateTime);

        dayOfMonth = dateTime.getDayOfMonth();
        monthOfYear = dateTime.getMonthOfYear();
        year = dateTime.getYear();
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
}
