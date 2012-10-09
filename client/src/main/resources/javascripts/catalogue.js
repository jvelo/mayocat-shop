'use strict'

angular.module('catalogue', [])
  .service('catalogueService', function($http){
    return {
      list: function(callback) {
        $http.get('/product/').success(function(data) {
          callback && callback.call(this, data);
        });
      },
      move: function(handle, insertBefore){
        $http.post('/category/_all/move',
                   "product=" + handle + "&before=" + insertBefore,
                   { "headers" : {'Content-Type': 'application/x-www-form-urlencoded'} })
          .success(function(data){
            console.log("OK");
        });
      }

    };
  })
  .controller('CatalogueController', ['$scope', '$location', 'catalogueService',
      function($scope, $location, catalogueService) {

        // List of products
        $scope.products = [];

        // A "move position" operation to perform. It is set by the 'sortable' directive when the list sort order changes.
        $scope.changeOperation = undefined;

        $scope.setRoute = function(handle) {
          $location.url("/product/" + handle);
        };

        catalogueService.list(function(products) {
          $scope.products = products;
        });

        $scope.changePosition = function() {
          catalogueService.move($scope.changeOperation.moved, $scope.changeOperation.nextItem);
          $scope.changeOperation = undefined;
        };
  }]);
