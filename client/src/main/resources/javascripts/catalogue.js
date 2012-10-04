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
  .controller('CatalogueController', ['$scope', 'catalogueService',
      function($scope, catalogueService) {
        $scope.products = [];

        catalogueService.list(function(products) {
          $scope.products = products;
        });
  }]);
