'use strict'

angular.module('configuration', ['ngResource'])
  .controller('ConfigurationController', ['$scope', '$resource',

      function($scope, $resource) {

        $scope.updateConfiguration = function() {

          $scope.ConfigurationResource.save($scope.configuration);
          $scope.TenantResource.save($scope.tenant);

        };

        $scope.isVisible = function(path) {
          var configurationElement = $scope.$eval("configuration." + path)
          if (typeof configurationElement === "undefined") {
            // The configuration does not exist
            return false;
          }
          return typeof configurationElement.visible === "undefined"
              || configurationElement.visible;
        }

        $scope.isConfigurable = function(path) {
          var configurationElement = $scope.$eval("configuration." + path)
          if (typeof configurationElement === "undefined") {
            // The configuration does not exist
            return false;
          }
          return typeof configurationElement.configurable === "undefined"
              || configurationElement.configurable;
        }

        $scope.ConfigurationResource = $resource("/configuration/");

        $scope.configuration = $scope.ConfigurationResource.get();
      }

  ]);
