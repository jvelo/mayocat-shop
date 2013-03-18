'use strict'

angular.module('addons', ['ngResource'])
    .factory('addonsService', function ($resource, $q, configurationService) {

        var getAddonIndex = function (addons, group, field, source) {
            if (typeof addons === "undefined") {
                return -1;
            }
            for (var i = 0; i < addons.length; i++) {
                var addon = addons[i];
                if (addon.key == field
                    // && addon.source == source TODO: put source in addon gestalt configuration
                    && addon.source == 'theme'
                    && addon.group == group) {
                    return i;
                }
            }
            return -1;
        }

        return {

            initialize: function (entityName, entity) {
                var entityAddons = [];
                var deferred = $q.defer();
                configurationService.get("entities", function (entities) {
                    if (typeof entities !== 'undefined') {
                        var addons = entities[entityName].addons;
                        for (var groupKey in addons) {
                            if (addons.hasOwnProperty(groupKey)) {
                                var group = addons[groupKey];
                                var definitions = group.fields;
                                var currentGroupIndex = entityAddons.push({
                                    key: groupKey,
                                    name: group.name,
                                    text: group.text,
                                    fields: []
                                }) - 1;
                                for (var key in definitions) {
                                    if (definitions.hasOwnProperty(key)) {
                                        var index = getAddonIndex(entity.addons, groupKey, key),
                                            definition = definitions[key];
                                        if (index < 0) {
                                            // Create addon container lazily
                                            entity.addons.push({
                                                'key': key,
                                                'group': groupKey,
                                                source: "theme",
                                                type: definition.type,
                                                value: null
                                            });
                                            index = getAddonIndex(entity.addons, groupKey, key);
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
                    deferred.resolve(entityAddons);
                });
                return deferred.promise;
            }

        }
    });