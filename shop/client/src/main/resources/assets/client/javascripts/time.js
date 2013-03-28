(function(){
    'use strict'

    angular.module('time', [])

        .factory('timeService', function () {

            return {

                convertTimestamp: function(timestamp, format) {
                    if (typeof timestamp === "undefined") {
                        return undefined;
                    }
                    if (typeof timestamp === "number") {
                        timestamp = "" + timestamp;
                    }
                    if (timestamp.length > 10) {
                        timestamp = timestamp.slice(0, 10);
                    }
                    if (typeof format === "undefined" || format === "") {
                        format = "YYYY-MM-DD HH:mm";
                    }
                    return moment.unix(timestamp).format(format);
                },

                convert: function(input, format, outputFormat) {
                    return moment(input, format).format(outputFormat);
                }

            };

        })


        .filter('timestampAsDate', ['timeService',  function (timeService) {
            return function (timestamp, format) {
                return timeService.convertTimestamp(timestamp, format);
            }
        }]);

})();
