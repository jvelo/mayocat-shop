(function () {
    'use strict';

    angular.module('time', [])

        .factory('timeService', function () {

            var defaultPrintFormat = "YYYY-MM-DD HH:mm";

            return {

                convertTimestamp: function (timestamp, format) {
                    if (typeof timestamp === "undefined") {
                        return undefined;
                    }
                    if (typeof timestamp === "number") {
                        timestamp.toString();
                    }
                    if (timestamp.length > 10) {
                        timestamp = timestamp.slice(0, 10);
                    }
                    return moment.unix(parseInt(timestamp, 10)).format(format || defaultPrintFormat);
                },

                convertISO8601toLocalDate: function (datestring, printfmt) {
                    if (!datestring) {
                        return undefined;
                    }
                    // use ISO format without the timezone part to convert to local date
                    // Note: moment will treat the date as not valid if it does not respect
                    // the format (ex: 2013-04-09 won't be valid with this format)
                    // this could be problematic in another context but here the source is
                    // trusted, and moment is just being used internally
                    return moment(datestring, "YYYY-MM-DDTHH:mm:ss").format(printfmt || defaultPrintFormat);
                },

                convert: function (input, format, outputFormat) {
                    return moment(input, format).format(outputFormat);
                },

                convertISO: function (input, outputFormat) {
                    return moment(input).format(outputFormat);
                }

            };

        })

        .filter('timestampAsDate', ['timeService', function (timeService) {
            return function (timestamp, format) {
                return timeService.convertTimestamp(timestamp, format);
            };
        }])

        .filter('iso8601toLocalDate', ['timeService', function (timeService) {
            return function (string, format) {
                return timeService.convertISO8601toLocalDate(string, format);
            };
        }]);
})();
