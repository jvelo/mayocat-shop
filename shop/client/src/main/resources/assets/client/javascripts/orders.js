/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
'use strict';

angular.module('orders', [])

    .factory('baseOrderMixin', ["$resource", "$translate", function ($resource, $translate) {
        return function (options) {
            options = typeof options === 'undefined' ? {} : options;
            return {
                getStatus: function(status) {
                    var camelCaseStatus = status.toLowerCase().replace(/_(.)/g, function(match, grp1) {
                        return grp1.toUpperCase();
                    });

                    return $translate('order.status.' + camelCaseStatus);
                },

                getClass: function(status) {
                    return status.toLowerCase();
                },

                fetchOrders: function() {
                    var $scope = this;
                    $scope.isLoading = true;
                    $resource("/api/orders").get({
                        "offset": (($scope.currentPage - 1) * $scope.ordersPerPage),
                        "number": $scope.ordersPerPage
                    }, function (result) {
                        $scope.orders = result.orders;

                        // Prepare pagination variables
                        $scope.pages = Math.floor(result._pagination.totalItems / $scope.ordersPerPage);
                        if (result._pagination.totalItems % $scope.ordersPerPage === 0) {
                            $scope.pages--;
                        }

                        $scope.isLoading = false;
                    });
                },

                init: function() {
                    var $scope = this;
                    $scope.ordersPerPage = options.ordersPerPage || 15;
                    $scope.isLoading = true;
                    $scope.currentPage = 1;
                    $scope.fetchOrders();
                }
            }
        }
    }])

    .factory('orderMixins', [
        'mixins',
        'baseOrderMixin',
        function (mixins, base) {
            return mixins({
                base: base
            });
        }
    ])

    .controller('OrdersController', ['$scope', '$resource', 'configurationService', 'timeService', 'orderMixins',
        function ($scope, $resource, configurationService, timeService, mixins) {

            mixins.extend("base", $scope);

            $scope.setPage = function(page) {
                $scope.currentPage = page;
                $scope.fetchOrders();
            };

            $scope.range = function (start, end) {
                var ret = [];
                if (!end) {
                    end = start;
                    start = 0;
                }
                for (var i = start; i < end; i++) {
                    ret.push(i);
                }
                return ret;
            };

            $scope.getTranslationProperties = function (options) {
                if (options.period) {
                    return {
                        numberOfOrders: (($scope.stats || {})[options.period] || {}).numberOfOrders || 0
                    };
                } else if (options.order) {
                    return {
                        numberOfItems: options.order.numberOfItems || 0
                    };
                }
            };

            $resource("/api/billing/stats").get({}, function (stats) {
                $scope.stats = stats;
            });

            configurationService.get("catalog", function(catalogSettings){
                $scope.mainCurrency = catalogSettings.currencies.main;
            });

            $scope.init();
        }]);