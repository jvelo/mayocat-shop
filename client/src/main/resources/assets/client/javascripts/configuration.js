'use strict'

angular.module('configuration', ['ngResource'])
  .factory('configurationService', function($resource){

        var configurationResource = $resource("/configuration/", {}, {
            update: {method: "PUT"}
        });

        var configuration = configurationResource.get(function(configuration){
            saveOriginalValues();
        });

        function saveOriginalValues() {
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
            walk(configuration);
        }

        function prepareConfiguration(configuration) {
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
            return walk(configuration, {});
        };

        return {
            get:function (path) {
                if (typeof path === "undefined") {
                    return configuration;
                }
                var configurationElement = eval("configuration." + path);
                if (typeof configurationElement === "undefined") {
                    // The configuration does not exist
                    return undefined;
                }
                return configurationElement.value;
            },

            put: function(config) {
                configurationResource.update(prepareConfiguration(configuration));
            },

            isVisible:function (path) {
                var configurationElement = eval("configuration." + path);
                if (typeof configurationElement === "undefined") {
                    // The configuration does not exist
                    return false;
                }
                return typeof configurationElement.visible === "undefined"
                    || configurationElement.visible;
            },

            isConfigurable:function (path) {
                var configurationElement = eval("configuration." + path);
                if (typeof configurationElement === "undefined") {
                    // The configuration does not exist
                    return false;
                }
                return typeof configurationElement.configurable === "undefined"
                    || configurationElement.configurable;
            },

            isDefaultValue: function(path) {
                var configurationElement = eval("configuration." + path);
                if (typeof configurationElement === "undefined") {
                    // The configuration does not exist
                    return false;
                }
                return typeof configurationElement.default !== "undefined"
                    && angular.equals(configurationElement.default, configurationElement.value);
            }
        };
   })
  .controller('ConfigurationController', ['$scope', 'configurationService',

      function($scope, configurationService) {

        $scope.updateConfiguration = function() {
          configurationService.put($scope.configuration);
        };

        $scope.isVisible = function(path) {
            return configurationService.isVisible(path);
        }

        $scope.isConfigurable = function(path) {
            return configurationService.isConfigurable(path);
        }

        $scope.isDefaultValue = function(path) {
            return configurationService.isDefaultValue(path);
        };

        $scope.configuration = configurationService.get();
      }

  ]);
