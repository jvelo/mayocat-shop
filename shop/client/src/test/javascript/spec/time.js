describe('Time', function () {

    describe('Time service', function () {

        var timeService;

        beforeEach(module('mayocat'))

        beforeEach(inject(function ($injector) {
            timeService = $injector.get('timeService');
        }));

        it("Should be defined", function () {
            expect(timeService).toBeDefined();
        });
    });

    describe('Timestamp as date filter', function () {

        var timestampAsDateFilter;

        beforeEach(module('mayocat'))

        beforeEach(inject(function ($filter) {
            timestampAsDateFilter = $filter('timestampAsDate');
        }));

        it("Should be defined", function () {
            expect(timestampAsDateFilter).toBeDefined();
        });

        it("Sould not fail with an undefined timestamp", function() {
            expect(timestampAsDateFilter(undefined)).toBe(undefined);
        });

        it("Sould convert timestamps to dates", function() {
            // FIXME
            // Ignore until it accounts for timezone
            // expect(timestampAsDateFilter("1364488887772")).toBe("2013-03-28 17:41");
        });

        it("Sould accept timestamps as numbers", function() {
            // FIXME
            // Ignore until it accounts for timezone
            expect(timestampAsDateFilter(1364488887772)).toBe("2013-03-28 17:41");
        });

        it("Sould support date formats as arugment", function() {
            // FIXME
            // Ignore until it accounts for timezone
            expect(timestampAsDateFilter("1364488887772", "LLL")).toBe("March 28 2013 5:41 PM");
        });


    });

});
