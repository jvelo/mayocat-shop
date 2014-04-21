/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
describe('Configuration', function () {

    describe('Service', function () {
        var configurationService,
            httpBackend,
            sampleSettings = {
                "module":{
                    "propertySet":{
                        "property":{
                            "value":"Hello",
                            "default":"Bonjour",
                            "configurable":false,
                            "visible":true
                        }
                    }
                }
            };

        beforeEach(module('mayocat'));

        beforeEach(inject(function ($injector) {
            httpBackend = $injector.get('$httpBackend');
            httpBackend.when('GET', '/api/configuration/settings').respond(sampleSettings);

            configurationService = $injector.get('configurationService');
        }));

        it("Should be defined", function () {
            expect(configurationService).toBeDefined();
        });

        it("Should offer access to the whole settings object", function () {
            var config;
            configurationService.getSettings(function(c){
                config = c;
            });
            httpBackend.flush();

            waitsFor(function() {
                return typeof config !== "undefined";
            }, "Config never returned", 100);

            runs(function(){
                expect(config).toBeDefined();
                expect(config.module.propertySet.property.value).toBe("Hello");
            });
        });

        it("Should offer access to individual settings properties", function () {
            var property;
            configurationService.getSettings("module.propertySet.property", function(p){
                property = p;
            });
            httpBackend.flush();

            waitsFor(function() {
                return typeof property !== "undefined";
            }, "Configuration property never returned", 100);

            runs(function(){
                expect(property.value).toBe("Hello");
            });
        });

        it("Should return undefined when a property does not exist", function () {
            var property;
            configurationService.getSettings("module.doesNotExist.property", function(p){
                property = p;
            });
            httpBackend.flush();

            runs(function(){
                expect(property).toBe(undefined);
            });
        });


        it("Should verify a configuration configurability and visibility", function () {
            expect(configurationService.isConfigurable(sampleSettings, "module.propertySet.property")).toBe(false);
            expect(configurationService.isVisible(sampleSettings, "module.propertySet.property")).toBe(true);
        });
    });


    describe('Controller', function () {
        var settingsController,
            httpBackend;

        beforeEach(module('mayocat'));
        beforeEach(module('settings'));

        beforeEach(inject(function ($injector, $rootScope, $controller) {
            httpBackend = $injector.get('$httpBackend');
            httpBackend.when('GET', '/api/locales').respond([]);
            httpBackend.when('GET', '/api/configuration/settings').respond({
                "module":{
                    "propertySet":{
                        "property":{
                            "value":"Hello",
                            "default":"Bonjour",
                            "configurable":false,
                            "visible":true
                        },
                        "blukbluk":{
                            "value": "bluk",
                            "default":"bluk",
                            "configurable":true,
                            "visible":true
                        }
                    },
                    "otherPropSet": {
                        "deeper": {
                            "zogotounga":{
                                "value": "zorg",
                                "default":"zorg",
                                "configurable":true,
                                "visible":true
                            }
                        }
                    }
                }
            });

            settingsController = $rootScope.$new();
            settingsController.$apply();
            $controller('SettingsController', { $scope:settingsController });
        }));

        it("Should be defined", function () {
            expect(settingsController).toBeDefined();
        });

        it("Should save original values", function () {
            httpBackend.flush();
            expect(settingsController.settings.module.propertySet.property.__originalValue).toBe("Hello");
        });

        it("Should prepare settings object for submit and not leave empty path", function () {
            httpBackend.flush();
            settingsController.settings.module.propertySet.property.value = "Salut";
            httpBackend.expectPUT("/api/configuration/settings", /{"module":{"propertySet":{"property":"Salut"}}}/).respond(200);
            settingsController.updateSettings();
        });

        it("Should prepare configuration object for submit when value is overriding but has not been changed",
            function () {
            httpBackend.flush();
            httpBackend.expectPUT("/api/configuration/settings", /{"module":{"propertySet":{"property":"Hello"}}}/).respond(200);
            settingsController.updateSettings();
        });

    });


});
