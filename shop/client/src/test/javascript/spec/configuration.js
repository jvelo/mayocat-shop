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
            httpBackend.when('GET', '/api/1.0/configuration/settings').respond(sampleSettings);

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

        it("Should verify a configuration configurability and visibility", function () {
            expect(configurationService.isConfigurable(sampleSettings, "module.propertySet.property")).toBe(false);
            expect(configurationService.isVisible(sampleSettings, "module.propertySet.property")).toBe(true);
        });
    });


    describe('Controller', function () {
        var configurationController,
            httpBackend;

        beforeEach(module('mayocat'));

        beforeEach(inject(function ($injector, $rootScope, $controller) {
            httpBackend = $injector.get('$httpBackend');
            httpBackend.when('GET', '/api/1.0/configuration/settings').respond({
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
            });

            configurationController = $rootScope.$new();
            configurationController.$apply();
            $controller('ConfigurationController', { $scope:configurationController });
        }));

        it("Should be defined", function () {
            expect(configurationController).toBeDefined();
        });

        it("Should save original values", function () {
            httpBackend.flush();
            expect(configurationController.settings.module.propertySet.property.__originalValue).toBe("Hello");
        });

        it("Should prepare settings object for submit", function () {
            httpBackend.flush();
            configurationController.settings.module.propertySet.property.value = "Salut";
            httpBackend.expectPUT("/api/1.0/configuration/settings", /{"module":{"propertySet":{"property":"Salut"}}}/).respond(200);
            configurationController.updateSettings();
        });

        it("Should prepare configuration object for submit when value is overriding but has not been changed",
            function () {
            httpBackend.flush();
            httpBackend.expectPUT("/api/1.0/configuration/settings", /{"module":{"propertySet":{"property":"Hello"}}}/).respond(200);
            configurationController.updateSettings();
        });
    });


});
