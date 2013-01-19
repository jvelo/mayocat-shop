'use strict'

angular.module('configuration', ['ngResource'])
    .factory('configurationService', function ($resource, $q) {

        var configurationResource = $resource("/configuration/", {}, {
            update:{method:"PUT"}
        });

        var configuration;

        function getConfiguration() {
            var deferred = $q.defer();
            if (configuration != null) {
                deferred.resolve(configuration);
            }
            else {
                configurationResource.get(function (result) {
                    saveOriginalValues(result);
                    configuration = result;
                    deferred.resolve(configuration);
                });
            }
            return deferred.promise;
        }

        function saveOriginalValues(configuration) {
            function isConfigurable(node) {
                return typeof node.configurable !== "undefined"
                    && typeof node.value !== "undefined"
                    && typeof node.default !== "undefined"
                    && typeof node.visible !== "undefined";
            }

            function walk(node) {
                console.log(node);
                for (var property in node) {
                    if (node.hasOwnProperty(property)) {
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
                    }
                }
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
                for (var property in node) {
                    if (node.hasOwnProperty(property)) {
                        if (isConfigurable(node[property])) {
                            if (!isStillDefaultValue(node[property])) {
                                container[property] = node[property].value;
                            }
                        }
                        else if (typeof node[property] === "object") {
                            // We need to go deeper...
                            container[property] = walk(node[property], {});
                        }
                        else {
                            // What do we do with properties that are not configurable ?
                            // Nothing for now. We need to see if we want to support them first.
                        }
                    }
                }
                return container;
            }

            return walk(configuration, {});
        };

        return {
            get:function (path, callback) {
                // TODO check number of args instead
                getConfiguration().then(function (configuration) {
                    if (typeof path === "undefined") {
                        callback(configuration);
                        return;
                    }
                    try {
                        var configurationElement = eval("configuration." + path);
                        if (typeof configurationElement === "undefined") {
                            // The configuration does not exist
                            callback && callback(undefined);
                        }
                        callback && callback(configurationElement.value);
                        return;
                    }
                    catch (error) {
                        callback && callback(undefined);
                        return;
                    }
                });
            },

            put:function (config) {
                configurationResource.update(prepareConfiguration(configuration));
            },

            isVisible:function (configuration, path) {
                if (typeof configuration === "undefined") {
                    return;
                }
                var configurationElement = eval("configuration." + path);
                if (typeof configurationElement === "undefined") {
                    // The configuration does not exist
                    return false;
                }
                return typeof configurationElement.visible === "undefined"
                    || configurationElement.visible;

            },

            isConfigurable:function (configuration, path) {
                if (typeof configuration === "undefined") {
                    return;
                }
                var configurationElement = eval("configuration." + path);
                if (typeof configurationElement === "undefined") {
                    // The configuration does not exist
                    return false;
                }
                return typeof configurationElement.configurable === "undefined"
                    || configurationElement.configurable;

            },

            isDefaultValue:function (configuration, path) {
                if (typeof configuration === "undefined") {
                    return;
                }
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

    function ($scope, configurationService) {

        $scope.updateConfiguration = function () {
            configurationService.put($scope.configuration);
        };

        $scope.isVisible = function (path) {
            return configurationService.isVisible($scope.configuration, path);
        }

        $scope.isConfigurable = function (path) {
            return configurationService.isConfigurable($scope.configuration, path);
        }

        $scope.isDefaultValue = function (path) {
            return configurationService.isDefaultValue($scope.configuration, path);
        };

        configurationService.get(undefined, function (configuration) {
            $scope.configuration = configuration;
        });
    }

]);
