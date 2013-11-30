/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
describe('Time', function () {

    describe('Time service', function () {

        var timeService;

        beforeEach(module('mayocat'));

        beforeEach(inject(function ($injector) {
            timeService = $injector.get('timeService');
        }));

        it("Should be defined", function () {
            expect(timeService).toBeDefined();
        });
    });

    describe('Timestamp as date filter', function () {

        var timestampAsDateFilter;

        beforeEach(module('mayocat'));

        beforeEach(inject(function ($filter) {
            timestampAsDateFilter = $filter('timestampAsDate');
        }));

        it("Should be defined", function () {
            expect(timestampAsDateFilter).toBeDefined();
        });

        it("Sould not fail with an undefined timestamp", function() {
            expect(timestampAsDateFilter(undefined)).toBe(undefined);
        });

    });

    describe('Full ISO 8691 date string to local date filter', function () {
        var filter;

        beforeEach(module('mayocat'));

        beforeEach(inject(function ($filter) {
            filter = $filter('iso8601toLocalDate');
        }));

        it('Should return undefined when the string is null or undefined', function () {
            expect(filter("")).toBe(undefined);
            expect(filter(null)).toBe(undefined);
            expect(filter(undefined)).toBe(undefined);
        });

        it('Should always resolve to a local date, even when the string contains TZ info', function () {
            expect(filter("2013-04-09T12:16:31+02:00")).toBe("2013-04-09 12:16");
            expect(filter("2013-04-09T12:16:31-06:00")).toBe("2013-04-09 12:16");
            expect(filter("2013-04-09T12:16:31-0600")).toBe("2013-04-09 12:16");
            expect(filter("2013-04-09T12:16:31+0600")).toBe("2013-04-09 12:16");
            expect(filter("2013-04-09T12:16:31+06")).toBe("2013-04-09 12:16");
            expect(filter("2013-04-09T12:16:31Z")).toBe("2013-04-09 12:16");
            expect(filter("2013-04-09T12:16:31Z")).toBe("2013-04-09 12:16");
            expect(filter("2013-04-09T12:16:31")).toBe("2013-04-09 12:16");
            expect(filter("2013-04-09")).toBe("2013-04-09 00:00");
        });

    });

});
