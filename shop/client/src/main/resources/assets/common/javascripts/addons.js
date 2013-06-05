'use strict'

angular.module('mayocat.addons', ['ngResource'])

    .factory('addonsService', function ($resource, $q, configurationService) {

        // Map of "addon type" -> "default editor"
        var defaultEditors = {
            "html" : "wyswiyg",
            "string" : "string",
            "json" : "textarea"
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
        registerEditor("selectBox", {
           type: function() {
               return "string";
           },
           extraAttributes: function(){
               return "options=addon.properties.listValues";
           }
        });

        /**
         * Finds the index of an addon with a certain group+field+source in an array of addons.
         *
         * @param {array} addons the array of addons to find the addon in
         * @param {string} group the group of the addon to find
         * @param {string} field the field of the addon to find
         * @param {string} source the source of the addon to find. E.g. "platform" or "theme"
         * @returns {number} the index at which the addon has been found, -1 if not found.
         */
        var getAddonIndex = function (addons, group, field, source) {
            if (typeof addons === "undefined") {
                return -1;
            }
            for (var i = 0; i < addons.length; i++) {
                var addon = addons[i];
                if (addon.key == field
                    && addon.source == source
                    && addon.group == group) {
                    return i;
                }
            }
            return -1;
        }

        return {

            /**
             * Adds an editor in the list of registered editor.
             *
             * @param key the key under which to register this editor. For example "selectBox".
             * @param editor the editor configuration object.
             */
            registerEditor: registerEditor,


            /**
             * Returns the type of data to use for an addon.
             *
             * @param type the type defined in the addon field definition. May be null
             * @param editor the editor to use.
             */
            type: function (type, editor) {
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
            editor: function (type, definition, options) {

                var editor = definition.editor,
                    dasherize = function (s) {
                        if (typeof s === 'undefined') return;
                        return s.replace(/([A-Z])/g, function ($1) {
                            return "-" + $1.toLowerCase();
                        });
                };

                options = typeof options !== "undefined" ? options : {};

                if (typeof editor === "undefined") {
                    editor = defaultEditors[type];
                }

                var output = '<addon-' + dasherize(editor) + ' placeholder={{addon.placeholder}} value=value ';

                if (typeof definition.properties !== "undefined" && !!definition.properties.readOnly
                    && !options.ignoreReadOnly) {
                    output += "disabled='disabled' "
                }

                if (typeof editors[editor] !== "undefined" && typeof editors[editor].extraAttributes === "function") {
                    output += editors[editor].extraAttributes();
                }

                output += '/>';

                return output;
            },

            /**
             * Initializes the list of addons for an entity
             *
             * @param entityType the type of entity to initialize. For example "page" or "product", or "user" etc.
             * @param entity the actual entity object to initialize addons for
             * @returns {Object} the promise of this realization
             */
            // TODO refactor this method to reduce the level of conditional branch nesting
            initializeEntityAddons: function (entityType, entity) {
                var entityAddons = [];
                var deferred = $q.defer();
                configurationService.get("entities", function (entities) {
                    if (typeof entities !== 'undefined' && typeof entities[entityType] !== 'undefined') {
                        var addons = entities[entityType].addons;
                        for (var sourceName in addons) {
                            if (addons.hasOwnProperty(sourceName)) {
                                var source = addons[sourceName];
                                for (var groupKey in source) {
                                    if (source.hasOwnProperty(groupKey)) {
                                        var group = source[groupKey];
                                        var definitions = group.fields;
                                        var currentGroupIndex = entityAddons.push({
                                            key: groupKey,
                                            source: sourceName,
                                            name: group.name,
                                            text: group.text,
                                            properties: group.properties,
                                            fields: []
                                        }) - 1;
                                        for (var key in definitions) {
                                            if (definitions.hasOwnProperty(key)) {
                                                var index = getAddonIndex(entity.addons, groupKey, key, sourceName),
                                                    definition = definitions[key];
                                                if (index < 0) {
                                                    // Create addon container lazily
                                                    entity.addons.push({
                                                        'key': key,
                                                        'group': groupKey,
                                                        source: sourceName,
                                                        type: definition.type,
                                                        value: null
                                                    });
                                                    index = getAddonIndex(entity.addons, groupKey, key, sourceName);
                                                }
                                                entityAddons[currentGroupIndex].fields.push({
                                                    key: key,
                                                    definition: definition,
                                                    index: index
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    deferred.resolve(entityAddons);
                });
                return deferred.promise;
            }

        }
    })

    .directive("addonSelectBox", [function ($compile) {
        return {
            restrict: "E",
            scope: {
                disabled: '@',
                value: '=',
                options: '='
            },
            template: "<div><select name='whatever' ng-model='value' " +
                "ng-options='value.key || value as value.name || value for value in options'" +
                "ng-disabled=disabled></select></div>"
        };
    }])

    .directive("addonString", [function ($compile) {
        return {
            restrict: "E",
            scope: {
                name: '@',
                placeholder: '@',
                disabled: '@',
                value: '='
            },
            template: "<input type='text' name='whatever' placeholder={{placeholder}} ng-model='value' ng-disabled=disabled />"
        };
    }])

    .directive("addonTextarea", [function ($compile) {
        return {
            restrict: "E",
            scope: {
                name: '@',
                placeholder: '@',
                disabled: '@',
                value: '='
            },
            template: "<textarea name='whatever' placeholder={{placeholder}} ng-model='value' ng-disabled=disabled />"
        };
    }])

    .directive("addonWysiwyg", [function ($compile) {
        return {
            restrict: "E",
            scope: {
                name: '@',
                placeholder: '@',
                disabled: '@',
                value: '='
            },
            template: "<textarea name='whatever' placeholder={{placeholder}} ng-model='value' ck-editor ng-disabled=disabled></textarea>"
        };
    }])

    .directive("addon", ['$compile', 'addonsService', function ($compile, addonsService) {
        return {
            scope: {
                addon: '=definition',
                value: '=value',
                type: '=type',
                ignoreReadOnly: '='
            },
            restrict: "E",
            link: function (scope, element, attrs) {

                scope.$watch(
                    'addon',
                    function (definition) {
                        scope.type = addonsService.type(definition.type, definition.editor);

                        var editor = addonsService.editor(scope.type, definition, {
                            "ignoreReadOnly" : typeof scope.ignoreReadOnly !== 'undefined' ? scope.ignoreReadOnly : false
                        });

                        // The "template" option allow to override default behavior
                        if (typeof definition.template !== 'undefined') {
                            editor = definition.template;
                        }

                        element.html(editor);
                        var updated = $compile(element.contents())(scope);
                    }
                );
            }
        }
    }]);