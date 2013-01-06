'use strict'

angular.module('configuration', ['ngResource'])
  .controller('ConfigurationController', ['$scope', '$resource',

      function($scope, $resource) {

        $scope.updateConfiguration = function() {

          $scope.ConfigurationResource.save($scope.configuration);
          $scope.TenantResource.save($scope.tenant);

        };

        $scope.ConfigurationResource = $resource("/configuration/");
        $scope.TenantResource = $resource("/tenant/", {}, {
            "save" : {method: "PUT"}
          });

        $scope.configuration = $scope.ConfigurationResource.get();

      }
  ]
)
  .controller('ConfigurationController2', ['$scope', '$resource',
      function($scope, $resource) {

      }
  ]
);
