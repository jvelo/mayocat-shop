'use strict'

angular.module('orders', [])

    .controller('OrdersController', ['$scope', '$resource', 'configurationService',
        function ($scope, $resource, configurationService) {

            $scope.ordersPerPage = 15;

            $scope.fetchOrders = function () {
                $resource("/api/orders").get({
                    "offset" : $scope.currentPage * $scope.ordersPerPage,
                    "number" : $scope.ordersPerPage
                }, function (orders) {

                    // Prepare pagination variables
                    $scope.pages = Math.floor(orders.total / orders.number);
                    if (orders.total % orders.number === 0) {
                        $scope.pages--;
                    }

                    $scope.orders = orders.items;
                });
            }

            $scope.getStatus = function (status) {
                return status.charAt(0).toUpperCase() + status.slice(1).toLowerCase().replace(/_/g, ' ');
            }

            $scope.getClass = function(status) {
                return status.toLowerCase();
            }

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

            $scope.setPage = function(page) {
                $scope.currentPage = page;
                $scope.fetchOrders();
            }

            $scope.currentPage = 0;

            $scope.fetchOrders();

            $resource("/api/billing/stats").get({}, function (stats) {
                $scope.stats = stats;
            });

            configurationService.get("catalog", function(catalogSettings){
                $scope.mainCurrency = catalogSettings.currencies.main;
            });

        }]);
