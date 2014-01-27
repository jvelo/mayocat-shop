/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
'use strict';

angular.module('mayocat.configuration', ['ngResource'])
    .factory('configurationService', function ($resource, $q, $rootScope) {

        var configuration,
            configurationResource = $resource("/api/configuration/gestalt"),
            settings,
            settingsResource = $resource("/api/configuration/settings", {}, {
                update: {method: "PUT"}
            });

        var getSettings = function () {
            var deferred = $q.defer();
            if (settings) {
                deferred.resolve(settings);
            }
            else {
                settingsResource.get(function (result) {
                    saveOriginalValues(result);
                    settings = result;
                    deferred.resolve(settings);
                });
            }
            return deferred.promise;
        };


        /**
         * Get a property from a conf object or undefined if it does not exist
         * The 'path' argument must be a string representing the access of
         * 'obj' inner property
         *
         * Ex:
         *
         * >>> getObjectPropertyFromPath({a: {b: "b"}}, "a.b")
         * "b"
         * >>> getObjectPropertyFromPath({a: {b: "b"}}, "a.b.c")
         * undefined
         * >>> getObjectPropertyFromPath({a: {b: "b"}}, "bluk")
         * undefined
         */
        var getObjectPropertyFromPath = function (obj, path) {
            var props = path.split(".");
            return props.reduce(function(memo, prop) {
                return memo && memo[prop];
            }, obj);
        };

        var getConfiguration = function () {
            var deferred = $q.defer();
            if (configuration) {
                deferred.resolve(configuration);
            }
            else {
                configurationResource.get(function (result) {
                    configuration = result;
                    deferred.resolve(configuration);
                });
            }
            return deferred.promise;
        };

        var isConfigurable = function (node) {
            return typeof node !== "undefined"
                && node !== null
                && typeof node.configurable !== "undefined"
                && typeof node.value !== "undefined"
                && typeof node.default !== "undefined"
                && typeof node.visible !== "undefined";
        };

        var saveOriginalValues = function (settings) {
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
            };

            walk(settings);
        };

        var prepareSettings = function (settings) {

            var isStillDefaultValue = function (node) {
                if (angular.equals(node.value, node.__originalValue) && angular.equals(node.value, node.default)) {
                    // Nothing changed
                    return true;
                }
                return false;
            };

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
                            var deeper = walk(node[property], {});
                            // if not empty add it
                            if (Object.keys(deeper).length !== 0) container[property] = deeper;
                        }
                        else {
                            // What do we do with properties that are not configurable ?
                            // Nothing for now. We need to see if we want to support them first.
                        }
                    }
                }
                return container;
            };

            return walk(settings, {});
        };

        $rootScope.$on("configuration:updated", function () {
            configuration = undefined;
        });

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
            get: function () {
                var path = arguments.length === 2 ? arguments[0] : undefined,
                    callback = arguments.length === 2 ? arguments[1] : arguments[0];
                getConfiguration().then(function (configuration) {
                    if (typeof path === "undefined") {
                        callback(configuration);
                        return;
                    }
                    try {
                        var configurationElement = getObjectPropertyFromPath(configuration, path);
                        if (typeof configurationElement === "undefined") {
                            // The configuration does not exist
                            callback && callback(undefined);
                        }

                        try {
                            callback && callback(configurationElement);
                        }
                        catch (error) {
                            // Don't fail on callback error, it's not our responsibility...
                        }
                        return;
                    }
                    catch (error) {
                        callback && callback(undefined);
                        return;
                    }
                });
            },

            /**
             * Gets access to either the whole settings object, or to a single settings property.
             *
             * To get access to the whole settings object :
             *
             * configurationService.getSettings(function(settings){
             *   // Something something settings
             * });
             *
             * To get access to a single settings property :
             *
             * configurationService.get("module.sample.property", function(value){
             *   // Something with value
             * });
             */
            getSettings: function () {
                var path = arguments.length === 2 ? arguments[0] : undefined,
                    callback = arguments.length === 2 ? arguments[1] : arguments[0];
                getSettings().then(function (settings) {
                    if (typeof path === "undefined") {
                        callback(settings);
                        return;
                    }
                    try {
                        var setting = getObjectPropertyFromPath(settings, path);
                        if (typeof setting === "undefined") {
                            // The configuration does not exist
                            callback && callback(undefined);
                        }
                        callback && callback(setting);
                        return;
                    }
                    catch (error) {
                        callback && callback(undefined);
                        return;
                    }
                });
            },

            /**
             * Updates the settings with the passed settings, hitting the /settings PUT API.
             *
             * @param {Object} config the settings object to put
             * @param {Function} callback the callback function
             */
            put: function (config, callback) {

                /**
                 * Similar to an aspect "after"
                 */
                var wrap = function (functionToWrap, doAfter) {
                    return function () {
                        var args = Array.prototype.slice.call(arguments),
                            result = functionToWrap.apply(this, args);

                        doAfter.apply(this, args);
                        return result;
                    };
                };

                // Here we wrap the "consumer callback" (the callback passed by client code calling this API) to inject
                // our own behavior (firing an event) upon AJAX callback.
                // We need to do this since angular $resource module is callback based and not promised based
                settingsResource.update(prepareSettings(settings), wrap(callback, function () {
                    $rootScope.$broadcast("configuration:updated");
                }));
            },

            /**
             * Checks if a settings property is visible (i.e. should be exposed to users) or not.
             *
             * @param {Object} settings the settings object to test a path for visibility for.
             * @param {String} path the settings path to test visibility for. For example: "general.locales.main"
             * @return {*} undefined if the settings does not exists at this path for this settings object,
             * false if the settings is not visible (i.e. it should not be exposed to the users), true if it is.
             */
            isVisible: function (settings, path) {
                if (typeof settings === "undefined") {
                    return;
                }
                var settingsElement = getObjectPropertyFromPath(settings, path);
                if (typeof settingsElement === "undefined") {
                    // The settings does not exist
                    return;
                }
                return typeof settingsElement.visible === "undefined"
                    || settingsElement.visible;
            },

            /**
             * Checks if a settings property is configurable (users are allowed to override the value set at the
             * platform level) or not.
             *
             * @param {Object} settings the settings object to test a path for configurability for.
             * @param {String} path the settings path to test configurability for.
             * For example: "general.locales.main"
             * @return {Boolean|undefined} undefined if the settings does not exists at this path for this
             * settings object, false if the settings is not configurable, true if it is.
             */
            isConfigurable: function (settings, path) {
                if (typeof settings === "undefined") {
                    return undefined;
                }
                var settingsElement = getObjectPropertyFromPath(settings, path);
                if (typeof settingsElement === "undefined") {
                    // The settings does not exist
                    return undefined;
                }
                return typeof settingsElement.configurable === "undefined"
                    || settingsElement.configurable;
            },

            /**
             * Checks if a settings property value is the default value (the one set at the platform level).
             *
             * @param {Object} settings the settings object to check the default value with
             * @param {String} path the path of the settings to check if the value is the default one for
             * @return {Boolean|undefined} undefined if the settings does not exists at this path for this
             * settings object, true if the value for this settings path is the default one, false otherwise
             */
            isDefaultValue: function (settings, path) {
                if (typeof settings === "undefined") {
                    return undefined;
                }
                var settingsElement = getObjectPropertyFromPath(settings, path);
                if (typeof settingsElement === "undefined") {
                    // The settings does not exist
                    return undefined;
                }
                return typeof settingsElement.default !== "undefined"
                    && angular.equals(settingsElement.default, settingsElement.value);

            }
        };
    });