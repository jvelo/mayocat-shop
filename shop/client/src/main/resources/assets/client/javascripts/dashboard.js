/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
'use strict'

angular.module('dashboard', [])

    .controller('DashboardController', ['$scope', 'orderMixins',
        function ($scope, mixins) {

            mixins.extendAll($scope, {
               ordersPerPage: 5
            });

            $scope.getTranslationProperties = function (options) {

                return {
                    numberOfItems: options.order.numberOfItems || 0
                }
            };

            $scope.init();
        }])

