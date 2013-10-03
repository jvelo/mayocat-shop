(function () {

    'use strict'

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

                    var editor = definition.editor,
                        output = "",
                        tagName;

                    if (typeof editor === "undefined") {
                        editor = defaultEditors[type];
                    }

                    tagName = editors[editor].tagName;

                    output += "<" + tagName;

                    if (typeof definition.properties !== "undefined" && !!definition.properties.localized) {
                        output += " ng-model=localizedObject.value ";
                    }
                    else {
                        output += " ng-model=object.value ";
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
                 * @returns {Object} the promise of this realization
                 */
                initializeEntityAddons:function (entityType, entity) {

                    var entityAddons = [],
                        deferred = $q.defer();

                    configurationService.get(function (configuration) {
                        var entities = configuration.entities,
                            locales = configuration.general.locales.others || [];


                        if (typeof entities === 'undefined' || typeof entities[entityType] === 'undefined') {
                            deferred.resolve(entityAddons);
                        }

                        if (typeof entity.localizedVersions === "undefined") {
                            entity.localizedVersions = {};
                        }
                        locales.forEach(function (locale) {
                            if (typeof entity.localizedVersions[locale] === "undefined") {
                                entity.localizedVersions[locale] = {};
                            }
                            if (typeof entity.localizedVersions[locale].addons === "undefined") {
                                entity.localizedVersions[locale].addons = [];
                            }
                        });

                        var addons = entities[entityType].addons,
                            addonKeys = Object.keys(addons);
                        for (var i=0; i<addonKeys.length; i++) {
                            var sourceName = addonKeys[i],
                                source = addons[sourceName],
                                groupKeys = Object.keys(source);
                            for (var j=0; j<groupKeys.length; j++) {
                                var groupKey = groupKeys[j],
                                    group = source[groupKey],
                                    definitions = group.fields,
                                    definitionKeys = Object.keys(definitions),
                                    currentGroupIndex = entityAddons.push({
                                        key:groupKey,
                                        source:sourceName,
                                        name:group.name,
                                        text:group.text,
                                        properties:group.properties,
                                        fields:[]
                                    }) - 1;
                                for (var k = 0; k < definitionKeys.length; k++) {
                                    var key = definitionKeys[k],
                                        index = getAddonIndex(entity.addons, groupKey, key, sourceName),
                                        definition = definitions[key];
                                    if (index < 0) {
                                        // Create addon container lazily
                                        entity.addons.push({
                                            key:key,
                                            group:groupKey,
                                            source:sourceName,
                                            type:definition.type,
                                            value:null
                                        });
                                        index = getAddonIndex(entity.addons, groupKey, key, sourceName);
                                    }
                                    entityAddons[currentGroupIndex].fields.push({
                                        key:key,
                                        definition:definition,
                                        index:index
                                    });
                                }
                            }

                        }
                        // Initialize localized copies
                        for (var i = 0; i < entity.addons.length; i++) {
                            var addon = entity.addons[i];
                            locales.forEach(function (locale) {
                                var localIndex = getAddonIndex(
                                        entity.localizedVersions[locale].addons,
                                        addon.group,
                                        addon.key,
                                        addon.source
                                    ),
                                    localizedValue = null

                                if (localIndex > 0) {
                                    // We found a value for this addon for this locale, so get it
                                    localizedValue = entity.localizedVersions[locale].addons[localIndex].value
                                }

                                // We always push the localized version of an addon at the exact same index as the "main"
                                // one, effectively ignoring the local one's index
                                entity.localizedVersions[locale].addons[i] = angular.copy(entity.addons[i]);
                                entity.localizedVersions[locale].addons[i].value = localizedValue;
                            });
                        }
                        deferred.resolve(entityAddons);
                    });
                    return deferred.promise;
                }

            }
        })

        .directive("addon", ['$compile', 'addonsService', function ($compile, addonsService) {
        return {
            scope:{
                addon:'=definition',
                object:'=',
                localizedObject:"="
            },
            restrict:"E",
            link:function (scope, element, attrs) {

                scope.$watch(
                    'addon',
                    function (definition) {
                        scope.type = addonsService.type(definition.type, definition.editor);

                        var editor = addonsService.editor(scope.object.type, definition, {
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