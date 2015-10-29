/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
'use strict';

angular.module('customers', [])

    .controller('CustomersController', ['$scope', '$http',
        function ($scope, $http) {
            $scope.itemsPerPage = 50;
            $scope.offset = 0;
            $scope.currentPage = 1;

            $scope.fetchCustomers = function () {
                $scope.isLoading = true;
                $http.get('/api/customers/?' +
                "&number=" + $scope.itemsPerPage +
                "&offset=" + ($scope.currentPage - 1) * $scope.itemsPerPage)
                    .success(function (data) {
                        $scope.customers = data.customers;
                        $scope.isLoading = false;
                        $scope.totalItems = data._pagination.totalItems;
                    });
            };

            $scope.deleteCustomer = function(customer) {
                $http.delete(customer._href)
                    .then(function () {
                        $scope.fetchCustomers();
                    });
            };

            $scope.fetchCustomers();
        }]);
