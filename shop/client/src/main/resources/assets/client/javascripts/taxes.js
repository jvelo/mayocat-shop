/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
(function () {
    'use strict'

    angular.module('taxes', [])

        .factory('taxesService', ["$rootScope", "$q", "configurationService", function ($rootScope, $q, configurationService) {

            function computeExclPrice(price, rateId) {

                var rate,
                    deferred = $q.defer();

                configurationService.get('taxes', function (taxes) {
                    if (typeof rateId === 'undefined' || rateId === null) {
                        rate = taxes.vat.defaultRate;
                    }
                    else {
                        var rateObject = taxes.vat.otherRates.find(function (rate) {
                            return rate.id == rateId;
                        });

                        rate = rateObject.value
                    }

                    deferred.resolve(price * (1 / (1 + rate)));
                });

                return deferred.promise;
            }

            return {
                excl: computeExclPrice
            }
        }]);

})();