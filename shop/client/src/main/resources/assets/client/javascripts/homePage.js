/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
'use strict'

angular.module('homePage', [])

    .controller('HomePageController', ['$scope', '$http', '$modal',
        function ($scope, $http, $modal) {

            $scope.featuredProducts = [];

            $scope.isDirty = false;

            $scope.isLoading = true;
            $http.get('/api/home').success(function (data) {
                $scope.featuredProducts = data.featuredProducts;
                $scope.isLoading = false;
            });

            $scope.featuredProductsSortableOptions = {
                update: function(e, ui) {
                    if (ui.item.hasClass("no-drag")) {
                        ui.item.sortable.cancel();
                    }
                }
            };

            $scope.removeProduct = function(product) {
                var index = $scope.featuredProducts.findIndex(function(p){
                    return p.slug === product.slug
                });
                $scope.featuredProducts.splice(index, 1);
                $scope.isDirty = true;
            }

            $scope.addProduct = function () {
                $scope.modalInstance = $modal.open({
                    templateUrl: 'addFeaturedProduct.html',
                    controller: 'ProductSelectModalController',
                    scope: $scope
                });

                $scope.modalInstance.result.then(function (product) {
                    if (typeof product !== 'undefined') {
                        $scope.featuredProducts.push(product);
                        $scope.isDirty = true;
                    }
                });
            }

            $scope.save = function () {
                $scope.isLoading = true;
                $http.post('/api/home', {
                    featuredProducts: $scope.featuredProducts
                }).success(function () {
                        $scope.isLoading = false;
                        $scope.isDirty = false;
                });
            }

        }])

    .controller('ProductSelectModalController', ['$scope', '$http', '$modalInstance', function ($scope, $http, $modalInstance) {
        $scope.filter = {
            title: ""
        };
        // NOTE: Why using a filter object and not directly a filter string ? because http://stackoverflow.com/a/19572776

        $scope.products = [];

        $scope.$watch("filter.title", function () {
            // TODO
            // Add 300 ms throttling so that we don't hit the server for every keystroke?
            $scope.fetchProducts();
        });

        $scope.isFeaturedAlready = function (product) {
            return typeof $scope.featuredProducts.find(function (featured) {
                return featured.slug === product.slug;
            }) !== 'undefined';
        }

        $scope.selectProduct = function (product) {
            $modalInstance.close(product);
        }

        $scope.fetchProducts = function () {
            // TODO:
            // pagination
            // loader indication

            $http.get('/api/products/?titleMatches=' + $scope.filter.title).success(function (data) {
                $scope.products = data.products;
            });
        }

        $scope.fetchProducts();
    }]);


