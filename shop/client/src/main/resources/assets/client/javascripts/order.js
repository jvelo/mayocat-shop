/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
'use strict';

angular.module('order', ['ngResource'])

    .controller('OrderController', [
        '$scope',
        '$rootScope',
        '$routeParams',
        '$resource',
        '$http',
        '$location',
        '$modal',

        function ($scope, $rootScope, $routeParams, $resource, $http, $location, $modal) {

            $scope.slug = $routeParams.order;

            $scope.updateOrder = function (callback) {
                $scope.isLoading = true;
                $scope.OrderResource.save({ "slug": $scope.slug }, $scope.article, function () {
                    $scope.isLoading = false;
                    callback && callback.call();
                });

            };

            $scope.OrderResource = $resource("/api/orders/:slug");

            // Initialize existing page or new page

            $scope.isLoading = true;
            $scope.getOrder = function () {
                $scope.order = $scope.OrderResource.get({"slug": $scope.slug}, function(){
                    $scope.isLoading = false;
                });
            };

            $scope.getOrder();

            $scope.paymentReceived = function() {
                $scope.setStatus('PAID');
            }

            $scope.prepared = function() {
                $scope.setStatus('PREPARED');
            }

            $scope.shipped = function() {
                $scope.setStatus('SHIPPED');
            }

            $scope.changeStatus = function() {
                $scope.modalInstance = $modal.open({
                    templateUrl: 'changeStatus.html',
                    scope: $scope
                });
                $scope.modalInstance.result.then(function () {
                    $scope.setStatus($scope.order.status);
                });
            }

            $scope.setStatus = function(status, callback) {
                $scope.isLoading = true;
                $scope.order.status = status;
                $scope.OrderResource.save({ "slug": $scope.slug }, $scope.order, function () {
                    $scope.isLoading = false;
                    callback && callback.call();
                });
            }

            $scope.getTranslationProperties = function () {
                var order = $scope.order || {},
                    shipping = ((order.data || {}).shipping || {});

                return {
                    slug: order.slug || "",
                    title: shipping.title || "",
                    strategy : shipping.strategy || ""
                };
            };
        }]);
