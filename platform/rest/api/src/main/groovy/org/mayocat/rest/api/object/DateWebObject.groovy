/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.api.object

import groovy.transform.CompileStatic
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
 * Web object for a {@link Date}
 *
 * @version $Id$
 */
@CompileStatic
class DateWebObject
{
    String shortDate;

    String longDate;

    Integer dayOfMonth;

    Integer monthOfYear;

    Integer year;

    Long time;

    String dateTime;

    def withDate(Date date, Locale locale)
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
}
