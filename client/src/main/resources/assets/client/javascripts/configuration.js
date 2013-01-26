'use strict'

angular.module('configuration', ['ngResource'])
    .factory('configurationService', function ($resource, $q) {

        var configuration,
            configurationResource = $resource("/api/1.0/configuration/", {}, {
                update:{method:"PUT"}
            });

        var getConfiguration = function () {
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

        var saveOriginalValues = function (configuration) {
            var isConfigurable = function (node) {
                return typeof node.configurable !== "undefined"
                    && typeof node.value !== "undefined"
                    && typeof node.default !== "undefined"
                    && typeof node.visible !== "undefined";
            }

            var walk = function (node) {
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

        var prepareConfiguration = function (configuration) {
            var isConfigurable = function (node) {
                return typeof node.configurable !== "undefined"
                    && typeof node.value !== "undefined"
                    && typeof node.default !== "undefined"
                    && typeof node.visible !== "undefined";
            }

            var isStillDefaultValue = function (node) {
                if (node.value === node.__originalValue && node.value === node.defaultValue) {
                    // Nothing changed
                    return true;
                }
                return false;
            }

            var walk = function (node, container) {
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
            /**
             * Gets access to either the whole configuration object, or to a single configuration property.
             *
             * To get access to the whole configuration object :
             *
             * configurationService.get(function(configuration){
             *   // Something with configuration
             * });
             *
             * To get access to a single configuration property :
             *
             * configurationService.get("module.sample.property", function(value){
             *   // Something with value
             * });
             */
            get:function () {
                var path = arguments.length === 2 ? arguments[0] : undefined,
                    callback = arguments.length === 2 ? arguments[1] : arguments[0];
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

            /**
             * Updates the configuration with the passed configuration, hitting the /configuration PUT API.
             *
             * @param {Object} config the configuration object to put
             */
            put:function (config) {
                configurationResource.update(prepareConfiguration(configuration));
            },

            /**
             * Checks if a configuration property is visible (i.e. should be exposed to users) or not.
             *
             * @param {Object} configuration the configuration object to test a path for visibility for.
             * @param {String} path the configuration path to test visibility for. For example: "general.locales.main"
             * @return {*} undefined if the configuration does not exists at this path for this configuration object,
             * false if the configuration is not visible (i.e. it should not be exposed to the users), true if it is.
             */
            isVisible:function (configuration, path) {
                if (typeof configuration === "undefined") {
                    return;
                }
                var configurationElement = eval("configuration." + path);
                if (typeof configurationElement === "undefined") {
                    // The configuration does not exist
                    return;
                }
                return typeof configurationElement.visible === "undefined"
                    || configurationElement.visible;
            },

            /**
             * Checks if a configuration property is configurable (users are allowed to override the value set at the
             * platform level) or not.
             *
             * @param {Object} configuration the configuration object to test a path for configurability for.
             * @param {String} path the configuration path to test configurability for.
             * For example: "general.locales.main"
             * @return {Boolean|undefined} undefined if the configuration does not exists at this path for this
             * configuration object, false if the configuration is not configurable, true if it is.
             */
            isConfigurable:function (configuration, path) {
                if (typeof configuration === "undefined") {
                    return undefined;
                }
                var configurationElement = eval("configuration." + path);
                if (typeof configurationElement === "undefined") {
                    // The configuration does not exist
                    return undefined;
                }
                return typeof configurationElement.configurable === "undefined"
                    || configurationElement.configurable;
            },

            /**
             * Checks if a configuration property value is the default value (the one set at the platform level).
             *
             * @param {Object} configuration the configuration object to check the default value with
             * @param {String} path the path of the configuration to check if the value is the default one for
             * @return {Boolean|undefined} undefined if the configuration does not exists at this path for this
             * configuration object, true if the value for this configuration path is the default one, false otherwise
             */
            isDefaultValue:function (configuration, path) {
                if (typeof configuration === "undefined") {
                    return undefined;
                }
                var configurationElement = eval("configuration." + path);
                if (typeof configurationElement === "undefined") {
                    // The configuration does not exist
                    return undefined;
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

        configurationService.get(function (configuration) {
            $scope.configuration = configuration;
        });
    }

]);
