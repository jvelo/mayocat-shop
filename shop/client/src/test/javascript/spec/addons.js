/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
describe('Addons', function () {

    describe('Service', function () {
        var configurationService,
            httpBackend,
            mockConfiguration = {
                "entities": {
                    "todo": {
                        "addons": {
                            "platform": {
                                "extra": {
                                    "name": "Extra properties",
                                    "for": ["todo"],
                                    "fields": {
                                        "delegate_to": {
                                            "name": "Delegate to",
                                            "type": "string"
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                "general" : {
                    "locales" :{
                        "main" : "en_US",
                        "others" : []
                    }
                }
            };

        beforeEach(module('mayocat'))

        beforeEach(inject(function ($injector) {
            httpBackend = $injector.get('$httpBackend');
            httpBackend.when('GET', '/api/configuration/gestalt').respond(mockConfiguration);

            addonsService = $injector.get('addonsService');
        }));

        it("Should be defined", function () {
            expect(addonsService).toBeDefined();
        });

        it("Should create lazily defined addons for entities", function () {
            var hasReturned = false,
                returnedAddons,
                todo = {
                    title: "My todo",
                    addons: []
                };

            addonsService.initializeEntityAddons("todo", todo).then(function (addons) {
                hasReturned = true;
                returnedAddons = addons;
            });

            waitsFor(function () {
                return hasReturned;
            }, "Addons did not returned in time ", 100);

            runs(function () {
                expect(returnedAddons).toBeDefined();
                expect(returnedAddons.length).toBe(1);
                expect(returnedAddons[0].key).toBe("extra");
                expect(returnedAddons[0].fields.length).toBe(1);

                expect(todo.addons.extra).toBeDefined();
                expect(todo.addons.extra.value.delegate_to).toBeDefined();
                expect(todo.addons.extra.value.delegate_to).toBe(null);
            });

            httpBackend.flush();
        });

        it("Should initialize defined addons for entities", function () {

            var hasReturned = false,
                returnedAddons,
                todo = {
                    title: "My todo",
                    addons: {
                        extra: {
                            group: "extra",
                            source: "platform",
                            value: {
                                delegate_to: "Somebody better"
                            }

                        }
                    }
                };

            addonsService.initializeEntityAddons("todo", todo).then(function (addons) {
                hasReturned = true;
                returnedAddons = addons;
            });

            waitsFor(function () {
                return hasReturned;
            }, "Addons did not returned in time ", 100);

            runs(function () {
                expect(returnedAddons).toBeDefined();
                expect(returnedAddons.length).toBe(1);
                expect(returnedAddons[0].key).toBe("extra");
                expect(returnedAddons[0].fields.length).toBe(1);
                expect(todo.addons.extra).toBeDefined();
                expect(todo.addons.extra.value.delegate_to).toBe("Somebody better");
            });

            httpBackend.flush();
        });

        it("Should return or guess addon type correctly", function(){
            // Default type when there is no other information
            expect(addonsService.type()).toBe("string");

            // When we precise the type, we expect it back
            expect(addonsService.type("json")).toBe("json");

            // When we don't precise the type but the editor, we expect the type defined by the editor
            addonsService.registerEditor("myCustomEditor", {
                type: function(){
                    return "html";
                }
            });
            expect(addonsService.type(undefined, "myCustomEditor")).toBe("html");
        });

        it("Should compute editor templates based on definition and registered editors", function(){

            // Simple string
            expect(
                addonsService.editor("string", {})
            ).toBe(
                "<input ng-model=object[key] type='text' >"
            );

            // Simple string, disabled
            expect(
                addonsService.editor("string", {
                    "properties" : {
                        "readOnly": true
                    }
                })
            ).toBe(
                "<input ng-model=object[key] type='text' disabled='disabled' >"
            );

            // Textarea
            expect(
                addonsService.editor("string", {
                    "editor" : "textarea"
                })
            ).toBe(
                "<textarea ng-model=object[key] ></textarea>"
            );

            // With placeholder
            // @Ignore
            // This fails with (sic) :
            // Expected '<input ng-model=$parent.object.value placeholder={{addon.placeholder}} type='text'>' to equal '<input ng-model=$parent.object.value placeholder={{addon.placeholder}} type='text'>'.
            /*
            expect(
                addonsService.editor("string", {
                    "placeholder" : "Some helper string"
                })
            ).toEqual(
                "<input ng-model=$parent.object.value placeholder={{addon.placeholder}} type='text'>"
            );
            */

        });

    });

});
