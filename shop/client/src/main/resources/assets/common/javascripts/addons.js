'use strict'

angular.module('mayocat.addons', ['ngResource'])

    .directive("addonFieldString", [function ($compile) {
        return {
            restrict: "E",
            scope: {
                name: '@',
                placeholder: '@',
                value: '='
            },
            template: "<input type='text' name='whatever' placeholder={{placeholder}} ng-model='value' />"
        };
    }])

    .directive("addonFieldTextarea", [function ($compile) {
        return {
            restrict: "E",
            scope: {
                name: '@',
                placeholder: '@',
                value: '='
            },
            template: "<input type='text' name='whatever' placeholder={{placeholder}} ng-model='value' />"
        };
    }])

    .directive("addonFieldWysiwyg", [function ($compile) {
        return {
            restrict: "E",
            scope: {
                name: '@',
                placeholder: '@',
                value: '='
            },
            template: "<textarea name='whatever' placeholder={{placeholder}} ng-model='value' ck-editor></textarea>",
        };
    }])

    .directive("addon", ['$compile', function ($compile) {
        return {
            scope: {
                addon: '=definition',
                value: '=value',
                type: '=type'
            },
            restrict: "E",
            link: function (scope, element, attrs) {
                scope.$watch(
                    'addon',
                    function (definition) {
                        var displayer,
                            storageType;

                        switch (definition.type) {
                            case 'textarea':
                                storageType = "string";
                                displayer = "<addon-field-textarea placeholder={{addon.placeholder}} value=value>";
                                break;

                            case 'wysiwyg':
                                storageType = "html";
                                displayer = "<addon-field-wysiwyg placeholder={{addon.placeholder}} value=value>";
                                break;

                            case 'string':
                            default:
                                storageType = "string";
                                displayer = "<addon-field-string placeholder={{addon.placeholder}} value=value>";
                                break;
                        }
                        scope.type = storageType;
                        // The "template" option allow to override default behavior
                        if (typeof definition.template !== 'undefined') {
                            displayer = definition.template;
                        }

                        element.html(displayer);
                        var updated = $compile(element.contents())(scope);
                    }
                );
            }
        }
    }])

    .factory('addonsService', function ($resource, $q, configurationService) {

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
            // TODO refactor this method to reduce the level of conditional branch nesting
            initialize: function (entityName, entity) {
                var entityAddons = [];
                var deferred = $q.defer();
                configurationService.get("entities", function (entities) {
                    if (typeof entities !== 'undefined' && typeof entities[entityName] !== 'undefined') {
                        var addons = entities[entityName].addons;
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
    });