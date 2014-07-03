/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
(function () {

    'use strict'

    /**
     * Helper function that creates an "extended" group definition from an actual server provided group definition.
     * This extended definition is used when listing groups on entity pages.
     *
     * @param group the group definition to get the extended group definition for
     * @param groupKey the key of the group
     * @param sourceName the name of the source for this group definition (for example "platform" or "theme"
     * @returns {Object} the extended group definition
     */
    function getExtendedGroupDefinition(group, groupKey, sourceName) {
        var definition = angular.copy(group);
        definition.key = groupKey;
        definition.source = sourceName;
        definition.fields = [];
        definition.getValueShell = function() {
            // Used for sequence addons, to get a "shell" value with all fields having a
            // null value when adding a new item in the sequence
            var object = {};
            Object.keys(group.fields).forEach(function(key){
                object[key] = null;
            });
            return object;
        }
        return definition
    }

    angular.module('mayocat.addons', ['ngResource'])

        .factory('addonsService', function ($resource, $q, configurationService) {

            // Map of "addon type" -> "default editor"
            var defaultEditors = {
                "html":"wyswiyg",
                "string":"string",
                "json":"textarea"
            };

            // List of registered editors
            var editors = {};

            // See below in the function exports
            var registerEditor = function (key, editor) {
                if (typeof editors[key] === "undefined") {
                    editors[key] = editor;
                }
                else {
                    console && console.warn("Editor [" + key + "] is already registered.");
                }
            }

            // Register default editors supported by the platform
            // - Select box
            registerEditor("string", {
                tagName:"input",
                type:"string",
                extraAttributes:"type='text'"
            });
            registerEditor("textarea", {
                tagName:"textarea",
                closingTag:true,
                type:"string"
            });
            registerEditor("wysiwyg", {
                tagName:"textarea",
                closingTag:true,
                type:"html",
                extraAttributes:"ck-editor"
            });
            registerEditor("selectBox", {
                tagName:"select",
                type:"string",
                extraAttributes:"ng-options='value.key || value as value.name || value for value in addon.properties.listValues'",
                postProcess:function (string) {
                    return "<div>" + string + "</div>"
                }
            });
            registerEditor("image", {
                tagName:"addon-image",
                type:"image"
            });

            return {

                /**
                 * Adds an editor in the list of registered editor.
                 *
                 * @param key the key under which to register this editor. For example "selectBox".
                 * @param editor the editor configuration object.
                 */
                registerEditor:registerEditor,


                /**
                 * Returns the type of data to use for an addon.
                 *
                 * @param type the type defined in the addon field definition. May be null
                 * @param editor the editor to use.
                 */
                type:function (type, editor) {
                    if (typeof type !== 'undefined' && type !== null) {
                        return type;
                    }
                    if (typeof editors[editor] !== "undefined") {
                        return editors[editor].type();
                    }
                    return "string";
                },

                /**
                 * Obtain the editor ng-html code to display for the passed type and definition of addon.
                 * If no editor is passed in the definition, the default editor for the passed type is looked up
                 * and used (for example, type "string" uses a editor that creates a standard input type string).
                 *
                 * @param {string} type the type of addon field (i.e. "string" or "html" or "json", etc.).
                 * @param {object} definition the addon field definition object.
                 * @param {object} options an optional option hash
                 * @returns {string}
                 */
                editor:function (type, definition, options) {

                    // Safeguards against undefined addon definition and options
                    definition = typeof definition !== "undefined" ? definition : {};
                    options = typeof options !== "undefined" ? options : {};

                    var editor = definition.editor || type,
                        output = "",
                        tagName;

                    if (typeof editor === "undefined") {
                        editor = defaultEditors[type];
                    }

                    tagName = editors[editor].tagName;

                    output += "<" + tagName;

                    if (typeof definition.properties !== "undefined" && !!definition.properties.localized) {
                        output += " ng-model=localizedObject[key] ";
                    }
                    else {
                        output += " ng-model=object[key] ";
                    }

                    if (typeof editors[editor].extraAttributes !== "undefined") {
                        if (typeof editors[editor].extraAttributes === "function") {
                            output += editors[editor].extraAttributes();
                        }
                        else {
                            output += editors[editor].extraAttributes;
                        }
                        output += " "; // safety guard
                    }

                    if (typeof definition.properties !== "undefined" && !!definition.properties.localized) {
                        output += "localized ";
                    }

                    if (typeof definition.placeholder !== "undefined") {
                        output += " placeholder={{addon.placeholder}} ";
                    }

                    if (typeof definition.properties !== "undefined" && !!definition.properties.readOnly
                        && !options.ignoreReadOnly) {
                        output += "disabled='disabled' "
                    }

                    output += ">";

                    if (editors[editor].closingTag) {
                        output += "</";
                        output += tagName;
                        output += '>';
                    }

                    if (typeof editors[editor].postProcess === "function") {
                        output = editors[editor].postProcess(output);
                    }

                    return output;
                },

                /**
                 * Initializes the list of addons for an entity
                 *
                 * @param entityType the type of entity to initialize. For example "page" or "product", or "user" etc.
                 * @param entity the actual entity object to initialize addons for
                 * @param options an hash with additional options. Supported options:
                 * <ul>
                 *     <li><code>getDefinition</code>: an optional function to get the actual desired addons definition
                 *     from the entity definition. This is useful for example when initializing a sub-entity addons,
                 *     like addons for product variants or features for a product type definition.</li>
                 * </ul>
                 * @returns {Object} the promise of this realization
                 */
                initializeEntityAddons: function (entityType, entity, options) {

                    options = typeof options === "undefined" ? {} : options;

                    var entityAddons = [],
                        deferred = $q.defer();

                    configurationService.get(function (configuration) {
                        var entities = configuration.entities,
                            locales = configuration.general.locales.others || [];

                        if (typeof entities === 'undefined' || typeof entities[entityType] === 'undefined') {
                            deferred.resolve(entityAddons);
                        }

                        if (typeof entity._localized === "undefined") {
                            entity._localized = {};
                        }
                        locales.forEach(function (locale) {
                            if (typeof entity._localized[locale] === "undefined") {
                                entity._localized[locale] = {};
                            }
                            if (typeof entity._localized[locale].addons === "undefined") {
                                entity._localized[locale].addons = {};
                            }
                        });

                        var addons;
                        if (typeof options.getDefinition === "function") {
                            addons = options.getDefinition.call(this, entities[entityType]);
                        } else {
                            addons = entities[entityType].addons
                        }

                        addons && Object.keys(addons).forEach(function(sourceName) {
                            var source = addons[sourceName];
                            Object.keys(source).forEach(function(groupKey){
                                var group = source[groupKey],
                                    definitions = group.fields,
                                    sequence = group.sequence,
                                    definitionKeys = Object.keys(definitions),
                                    addonGroupDefinition = getExtendedGroupDefinition(group, groupKey, sourceName);

                                entityAddons.push(addonGroupDefinition);

                                if (typeof entity.addons[groupKey] === "undefined") {
                                    entity.addons[groupKey] = {
                                        group: groupKey,
                                        source: sourceName,
                                        value: sequence ? [] : {},
                                        model: {}
                                    }
                                }
                                else if (sequence && !entity.addons[groupKey].value.length) {
                                    // If the addon definition says it's a sequence and the value is not a list, put
                                    // it as a first item of the list.
                                    // (This could happen if the theme developer changes the definition after setting a
                                    // value).

                                    entity.addons[groupKey].value = [ entity.addons[groupKey].value ];
                                }
                                else if (!sequence && entity.addons[groupKey].value.length) {
                                    // Same: if the addon definition says it's a not sequence and the value is a list,
                                    // take the first object in the list if there's one, or put an empty object.

                                    if (entity.addons[groupKey].value.length > 0) {
                                        entity.addons[groupKey].value = entity.addons[groupKey].value[0];
                                    }
                                    else {
                                        entity.addons[groupKey].value = {};
                                    }
                                }

                                var fieldSequence = sequence ?
                                    entity.addons[groupKey].value : [ entity.addons[groupKey].value ];

                                fieldSequence.forEach(function (sequence) {
                                    // Initialize all field values with "null" if not present

                                    definitionKeys.forEach(function (key) {
                                        if (typeof sequence[key] === 'undefined') {
                                            sequence[key] = null;
                                        }
                                    });
                                });

                                definitionKeys.forEach(function (key) {
                                    var definition = definitions[key];

                                    if (typeof entity.addons[groupKey].model === 'undefined') {
                                        entity.addons[groupKey].model = {}
                                    }

                                    entity.addons[groupKey].model[key] = { "type": definition.type };

                                    addonGroupDefinition.fields.push({
                                        key: key,
                                        definition: definition
                                    });
                                });
                            });
                        });

                        // Initialize localized copies

                        if (typeof entity.addons === "undefined") {
                            entity.addons = {};
                        }

                        Object.keys(entity.addons).forEach(function (groupKey) {

                            var group = entity.addons[groupKey];
                            locales.forEach(function (locale) {
                                if (typeof entity._localized[locale].addons === 'undefined') {
                                    entity._localized[locale].addons = {};
                                }

                                if (typeof entity._localized[locale].addons[groupKey] === 'undefined') {
                                    entity._localized[locale].addons[groupKey] = angular.copy(group);
                                    var localizedGroup = entity._localized[locale].addons[groupKey];

                                    if (!localizedGroup.value.length) {
                                        // Non-sequence
                                        Object.keys(localizedGroup.value).forEach(function (key) {
                                            localizedGroup.value[key] = null;
                                        });
                                    }
                                    else {
                                        // Sequence
                                        localizedGroup.value = [];
                                    }
                                }

                                var localizedGroup = entity._localized[locale].addons[groupKey];
                                if (typeof localizedGroup.value === 'undefined') {
                                    localizedGroup.value = {};
                                }
                                if (!group.value instanceof Array) {
                                    Object.keys(group.value).forEach(function (key) {
                                        if (typeof localizedGroup.value[key] === 'undefined') {
                                            localizedGroup.value[key] = null;
                                        }
                                    });
                                }

                            });
                        });

                        deferred.resolve(entityAddons);
                    });
                    return deferred.promise;
                }

            }
        })

        .directive("addonImage", ['$rootScope', function ($rootScope) {
            return {
                templateUrl: '/common/partials/addonImage.html',
                require : 'ngModel',
                restrict: 'E',
                controller: ['$scope', function ($scope) {
                    $scope.getImageUploadUri = function () {
                        return $rootScope.entity ? ($rootScope.entity.uri + "/attachments") : "";
                    }

                    $rootScope.$on("upload:progress", function(event, memo) {
                        var index = memo.queue.findIndex(function (upload) {
                            return upload.id == $scope.id;
                        });
                        if (index >= 0) {
                            $scope.uploading = true;
                        }
                    });

                    $rootScope.$on("upload:done", function(event, memo) {
                        if (memo.id == $scope.id) {
                            var parts = memo.fileUri.split('/');
                            $scope.internalModel = parts[parts.length - 1];
                            $scope.uploading = false;
                        }
                    });
                }],
                link: function (scope, element, attrs, ngModel) {
                    // Generate a random image list/upload id
                    scope.id = Math.random().toString(36).substring(8);
                    scope.uploading = false;
                    var clear = scope.$watch("ngModel.$modelValue", function (modelValue) {
                        scope.internalModel = ngModel.$modelValue;
                        clear();
                        scope.$watch("internalModel", function (newValue) {
                            ngModel.$setViewValue(newValue);
                        });
                    });
                }
            }
        }])

        .directive('addonList', [function () {
            return {
                restrict: 'E',
                scope: {
                    addons: '=',
                    entity: '=',
                    localized: '=localizedEntity'
                },
                templateUrl: '/common/partials/addonList.html',
                controller: function ($scope) {
                    $scope.removeSequenceAddonItem = function (group, index) {
                        $scope.entity.addons[group.key].value.splice(index, 1);

                        if (typeof $scope.entity._localized !== 'undefined') {
                            Object.keys($scope.entity._localized).forEach(function (locale) {
                                $scope.entity._localized[locale].addons[group.key].value.splice(index, 1);
                            });
                        }
                    }

                    $scope.addSequenceAddonItem = function (group) {
                        $scope.entity.addons[group.key].value.push(group.getValueShell());

                        if (typeof $scope.entity._localized !== 'undefined') {
                            Object.keys($scope.entity._localized).forEach(function (locale) {
                                $scope.entity._localized[locale].addons[group.key].value.push(group.getValueShell());
                            });
                        }
                    }
                }
            };
        }])

        .directive("addon", ['$compile', 'addonsService', function ($compile, addonsService) {
        return {
            scope:{
                addon:'=definition',
                object:'=',
                localizedObject:"=",
                key: '='
            },
            restrict:"E",
            link:function (scope, element, attrs) {
                scope.$watch(
                    'addon',
                    function (definition) {
                        scope.type = addonsService.type(definition.type, definition.editor);

                        var editor = addonsService.editor(definition.type, definition, {
                            "ignoreReadOnly":typeof scope.ignoreReadOnly !== 'undefined' ? scope.ignoreReadOnly : false
                        });
                        // The "template" option allow to override default behavior
                        if (typeof definition.template !== 'undefined') {
                            editor = definition.template;
                        }

                        element.html(editor);

                        $compile(element.contents())(scope);
                    }
                );
            }
        }
    }]);

})();