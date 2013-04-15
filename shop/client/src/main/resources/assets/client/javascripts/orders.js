'use strict'

angular.module('orders', [])

    .controller('OrdersController', ['$scope', '$resource',
        function ($scope, $resource) {

            $scope.fetchOrders = function () {
                $resource("/api/orders").get({}, function (orders) {
                    $scope.orders = orders.items;
                });
            }

            $scope.getStatus = function (status) {
                return status.charAt(0).toUpperCase() + status.slice(1).toLowerCase().replace(/_/g, ' ');
            }

            $scope.getClass = function(status) {
                return status.toLowerCase();
            }

            $scope.fetchOrders();

        }]);
