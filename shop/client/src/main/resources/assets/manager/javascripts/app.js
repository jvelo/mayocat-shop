'use strict'

angular.module('TenantManager.tenants', [])

    .controller('TenantListController', ['$scope', '$resource',
        function ($scope, $resource) {

            $scope.fetchTenants = function () {
                $resource("/api/tenants").get({}, function (tenants) {
                    $scope.tenants = tenants.items;
                });
            }

            $scope.fetchTenants();

        }]);


var TenantManager = angular.module('TenantManager', [
    'mayocat',
    'TenantManager.tenants'
]);

TenantManager.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/', {templateUrl: 'partials/tenants.html', controller: 'TenantListController'}).
        otherwise({redirectTo: '/'});
}]);