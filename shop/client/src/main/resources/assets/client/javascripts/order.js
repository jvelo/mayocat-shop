'use strict';

angular.module('order', ['ngResource'])

    .controller('OrderController', [
        '$scope',
        '$rootScope',
        '$routeParams',
        '$resource',
        '$http',
        '$location',

        function ($scope, $rootScope, $routeParams, $resource, $http, $location) {


            $scope.slug = $routeParams.order;

            $scope.updateOrder = function (callback) {
                $scope.isSaving = true;
                $scope.OrderResource.save({ "slug": $scope.slug }, $scope.article, function () {
                    $scope.isSaving = false;
                    callback && callback.call();
                });

            };

            $scope.OrderResource = $resource("/api/orders/:slug");

            // Initialize existing page or new page

            $scope.getOrder = function () {
                $scope.order = $scope.OrderResource.get({"slug": $scope.slug});
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

            $scope.setStatus = function(status, callback) {
                $scope.isSaving = true;
                $scope.order.status = status;
                $scope.OrderResource.save({ "slug": $scope.slug }, $scope.order, function () {
                    $scope.isSaving = false;
                    callback && callback.call();
                });
            }

            $scope.getTranslationProperties = function () {
                var order = $scope.order || {},
                    shipping = ((order.data || {}).shipping || {});

                return {
                    slug: order.slug || "",
                    shippingTitle: shipping.title || "",
                    shippingStrategy : shipping.strategy || ""
                };
            };
        }]);
