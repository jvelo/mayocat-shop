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
                expect(returnedAddons[0].fields[0].index).toBe(0);

                expect(todo.addons.length).toBe(1);
                expect(todo.addons[0].value).toBe(null);
            });

            httpBackend.flush();
        });

        it("Should initialize defined addons for entities", function () {

            var hasReturned = false,
                returnedAddons,
                todo = {
                    title: "My todo",
                    addons: [
                        {
                            'key': "delegate_to",
                            'group': "extra",
                            source: "platform",
                            type: "string",
                            value: "Somebody better"
                        }
                    ]
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
                expect(returnedAddons[0].fields[0].index).toBe(0);

                expect(todo.addons.length).toBe(1);
                expect(todo.addons[0].value).toBe("Somebody better");
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
                "<input ng-model=object.value type='text' >"
            );

            // Simple string, disabled
            expect(
                addonsService.editor("string", {
                    "properties" : {
                        "readOnly": true
                    }
                })
            ).toBe(
                "<input ng-model=object.value type='text' disabled='disabled' >"
            );

            // Textarea
            expect(
                addonsService.editor("string", {
                    "editor" : "textarea"
                })
            ).toBe(
                "<textarea ng-model=object.value ></textarea>"
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
