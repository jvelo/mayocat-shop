'use strict'

angular.module('catalogue', [])
  .service('catalogueService', function($http){
    return {
      list: function(callback) {
        $http.get('/product/').success(function(data) {
          callback && callback.call(this, data);
        });
      }
    };
  })
  .controller('CatalogueController', ['$scope', '$location', 'catalogueService',
      function($scope, $location, catalogueService) {
        $scope.products = [];

        $scope.setRoute = function(handle) {
          $location.url("/product/" + handle);
        }

        catalogueService.list(function(products) {
          $scope.products = products;
        });
  }]);
