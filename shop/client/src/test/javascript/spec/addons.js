describe('Addons', function () {

    describe('Service', function () {
        var configurationService,
            httpBackend,
            mockConfiguration = {
                "entities": {
                    "todo": {
                        "addons": {
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

        it("Should create lazily defined addons for entities", function(){
            var hasReturned = false,
                returnedAddons,
                todo = {
                    title: "My todo",
                    addons: []
                };

            addonsService.initialize("todo", todo).then(function(addons) {
                hasReturned = true;
                returnedAddons = addons;
            });

            waitsFor(function() {
                return hasReturned;
            }, "Addons did not returned in time ", 100);

            runs(function(){
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

        it("Should initialize defined addons for entities", function(){

            var hasReturned = false,
                returnedAddons,
                todo = {
                    title: "My todo",
                    addons: [{
                        'key': "delegate_to",
                        'group': "extra",
                        source: "theme",
                        type: "string",
                        value: "Somebody better"
                    }]
                };

            addonsService.initialize("todo", todo).then(function(addons) {
                hasReturned = true;
                returnedAddons = addons;
            });

            waitsFor(function() {
                return hasReturned;
            }, "Addons did not returned in time ", 100);

            runs(function(){
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

    });

});
