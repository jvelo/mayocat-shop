/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

/**
 * Prints out time zones and their standard offset in a JSON object, sorted by region
 * and then by offset (all redondant tz are removed).
 *
 * Some code borrowed from org.joda.example.time.TimeZoneTable
 * https://github.com/JodaOrg/joda-time/blob/master/src/example/org/joda/example/time/TimeZoneTable.java
 */
public class TimeZoneListJson {
    static final long cNow = System.currentTimeMillis();

    static final DateTimeFormatter cOffsetFormatter = new DateTimeFormatterBuilder()
        .appendTimeZoneOffset(null, true, 2, 4)
        .toFormatter();

    public static void main(String[] args) throws Exception {
        Set idSet = DateTimeZone.getAvailableIDs();
        LinkedHashMap<String, ArrayList<ZoneData>> zones = new LinkedHashMap<String, ArrayList<ZoneData>>();

        {
            Iterator it = idSet.iterator();
            int i = 0;
            while (it.hasNext()) {
                String id = (String) it.next();
                // get only standard format (not stuff like Etc/GMT-xx, PST, etc.)
                if (id.matches("^[A-Za-z_-]+?/[A-Za-z_-]+?(/[A-Za-z_-]+?)?")
                        && !id.matches("^Etc/[A-Za-z0-9_-]+?")) {
                    String k = id.split("/")[0];
                    if (zones.containsKey(k)) {
                        zones.get(k).add(new ZoneData(id, DateTimeZone.forID(id)));
                    } else {
                        ArrayList<ZoneData> regionZones = new ArrayList<ZoneData>();
                        regionZones.add(new ZoneData(id, DateTimeZone.forID(id)));
                        zones.put(k, regionZones);
                    }
                }
            }
            // add UTC
            ArrayList<ZoneData> otherZones = new ArrayList<ZoneData>();
            otherZones.add(new ZoneData("UTC", DateTimeZone.forID("UTC")));
            zones.put("Other", otherZones);
        }

        PrintStream out = System.out;

        out.println("{");

        for (Map.Entry<String, ArrayList<ZoneData>> entry : zones.entrySet())
        {
            // skip 'empty' regions where no canonical zone is present
            boolean hasCanonicalZone = false;
            for (ZoneData z : entry.getValue())
            {
                if (z.isCanonical()) {
                    hasCanonicalZone = true;
                    break;
                }
            }
            if (!hasCanonicalZone) continue;

            // sort by std offset
            Collections.sort(entry.getValue());

            out.println(entry.getKey() + ": {");

            for (ZoneData z : entry.getValue())
            {
                if (z.isCanonical()) {
                    printRow(out, z);
                }
            }

            out.println("},");
        }

        out.println("};");
    }

    private static void printRow(PrintStream out, ZoneData zone) {
        out.print(quote(zone.getCanonicalID()) + ":" + " { stdOffset: " + quote(zone.getStandardOffsetStr()) + ", city: " + quote(zone.getCityName()));

        out.println(" },");

    }

    private static String quote(String str) {
        return '"' + str + '"';
    }

    private static class ZoneData implements Comparable {
        private final String iID;
        private final DateTimeZone iZone;

        ZoneData(String id, DateTimeZone zone) {
            iID = id;
            iZone = zone;
        }

        public String getID() {
            return iID;
        }

        public String getCanonicalID() {
            return iZone.getID();
        }

        public boolean isCanonical() {
            return getID().equals(getCanonicalID());
        }

        public String getCityName() {
            String name;
            String[] parts = getID().split("/");
            if (parts.length > 2) {
                name = parts[1] + " - " + parts[2];
            } else if (parts.length > 1) {
                name = parts[1];
            } else {
                name = parts[0];
            }
            return name.replace("_", " ");
        }

        public String getStandardOffsetStr() {
            long millis = cNow;
            while (iZone.getOffset(millis) != iZone.getStandardOffset(millis)) {
                long next = iZone.nextTransition(millis);
                if (next == millis) {
                    break;
                }
                millis = next;
            }
            return cOffsetFormatter.withZone(iZone).print(millis);
        }

        public int compareTo(Object obj) {
            ZoneData other = (ZoneData) obj;

            int offsetA = iZone.getStandardOffset(cNow);
            int offsetB = other.iZone.getStandardOffset(cNow);

            if (offsetA < offsetB) {
                return -1;
            }
            if (offsetA > offsetB) {
                return 1;
            }

            int result = getCanonicalID().compareTo(other.getCanonicalID());

            if (result != 0) {
                return result;
            }

            if (isCanonical()) {
                if (!other.isCanonical()) {
                    return -1;
                }
            } else if (other.isCanonical()) {
                return 1;
            }

            return getID().compareTo(other.getID());
        }
    }
}
