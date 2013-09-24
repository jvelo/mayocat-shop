(function () {
    'use strict'

    angular.module('mayocat.locales', [])

        .factory('localesService', ["$http", "$q", function ($http, $q) {

            var data,
                dataPromise

            /**
             * Promise of locales data loaded from the server at /api/locales API.
             *
             * This data represents all available locales supported by the back-end
             */
            var loadData = function() {

                //if we already have a promise, just return that so it doesn't run twice.
                if (dataPromise) {
                    return dataPromise;
                }

                var deferred = $q.defer();
                dataPromise = deferred.promise;

                if (data) {
                    //if we already have data, return that.
                    deferred.resolve(data);
                } else {
                    $http.get('/api/locales')
                        .success(function (result) {
                            data = result;
                            deferred.resolve(result);
                        })
                        .error(function () {
                            deferred.reject('Failed to load data');
                        })
                }
                return dataPromise;
            }

            return {
                getData: loadData
            };

        }])

})();
