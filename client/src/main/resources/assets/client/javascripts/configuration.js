'use strict'

angular.module('configuration', ['ngResource'])
  .controller('ConfigurationController', ['$scope', '$resource',

      function($scope, $resource) {

        $scope.updateConfiguration = function() {
          $scope.ConfigurationResource.update($scope.prepareConfiguration($scope.configuration));
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

        $scope.isDefaultValue = function(path) {
          var configurationElement = $scope.$eval("configuration." + path)
          if (typeof configurationElement === "undefined") {
            // The configuration does not exist
            return false;
          }
          return typeof configurationElement.default !== "undefined"
              && angular.equals(configurationElement.default, configurationElement.value);
        };

        $scope.saveOriginalValues = function() {
          function isConfigurable(node) {
            return typeof node.configurable !== "undefined"
                && typeof node.value !== "undefined"
                && typeof node.default !== "undefined"
                && typeof node.visible !== "undefined";
          }
          function walk(node) {
            for (var property in node) { if (node.hasOwnProperty(property)) {
              if (isConfigurable(node[property])) {
                node[property].__originalValue = node[property].value;
              }
              else if (typeof node[property] === "object") {
                // We need to go deeper...
                walk(node[property]);
              }
              else {
               // What do we do with properties that are not configurable ?
               // Nothing for now. We need to see if we want to support them first.
              }
            }}
          }
          walk($scope.configuration);
        }

        $scope.prepareConfiguration = function() {
          var configuration = {};
          function isConfigurable(node) {
            return typeof node.configurable !== "undefined"
                && typeof node.value !== "undefined"
                && typeof node.default !== "undefined"
                && typeof node.visible !== "undefined";
          }
          function isStillDefaultValue(node) {
            if (node.value === node.__originalValue && node.value === node.defaultValue) {
              // Nothing changed
              return true;
            }
            return false;
          }
          function walk(node, container) {
            for (var property in node) { if (node.hasOwnProperty(property)) {
              if (isConfigurable(node[property])) {
                if (!isStillDefaultValue(node[property])) {
                  container[property] = node[property].value;
                }
              }
              else if (typeof node[property] === "object") {
                // We need to go deeper...
                container[property] =  walk(node[property], {});
              }
              else {
               // What do we do with properties that are not configurable ?
               // Nothing for now. We need to see if we want to support them first.
              }
            }}
            return container;
          }
          configuration = walk($scope.configuration, configuration);
          return configuration;
        };

        $scope.ConfigurationResource = $resource("/configuration/", {}, {
          update: {method: "PUT"}
        });

        $scope.configuration = $scope.ConfigurationResource.get(function(configuration){
          $scope.saveOriginalValues();
        });
      }

  ]);
