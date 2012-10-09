'use strict'

angular.module('catalog', [])
  .service('catalogService', function($http){
    return {
      list: function(callback) {
        $http.get('/product/').success(function(data) {
          callback && callback.call(this, data);
        });
      },
      move: function(handle, target, position){
        $http.post('/category/_all/move',
                   "product=" + handle + "&" + position + "=" + target,
                   { "headers" : {'Content-Type': 'application/x-www-form-urlencoded'} })
          .success(function(data) {
          });
      }

    };
  })
  .controller('CatalogController', ['$scope', '$location', 'catalogService',
      function($scope, $location, catalogService) {

        // List of products
        $scope.products = [];

        // A "move position" operation to perform. It is set by the 'sortable' directive when the list sort order changes.
        $scope.changeOperation = undefined;

        $scope.setRoute = function(handle) {
          $location.url("/product/" + handle);
        };

        catalogService.list(function(products) {
          $scope.products = products;
        });

        $scope.changePosition = function() {
          if (typeof $scope.changeOperation === "undefined") {
            return;
          }
          catalogService.move($scope.changeOperation.handle, $scope.changeOperation.target, $scope.changeOperation.position);
          $scope.changeOperation = undefined;
        };
  }]);
