describe('Configuration', function () {

    describe('Service', function () {
        var configurationService,
            httpBackend,
            sampleConfiguration = {
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
            httpBackend.when('GET', '/configuration').respond(sampleConfiguration);

            configurationService = $injector.get('configurationService');
        }));

        it("Should be defined", function () {
            expect(configurationService).toBeDefined();
        });

        it("Should offer access to the whole configuration object", function () {
            var config;
            configurationService.get(function(c){
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

        it("Should offer access to individual configuration properties", function () {
            var property;
            configurationService.get("module.propertySet.property", function(p){
                property = p;
            });
            httpBackend.flush();

            waitsFor(function() {
                return typeof property !== "undefined";
            }, "Configuration property never returned", 100);

            runs(function(){
                expect(property).toBe("Hello");
            });
        });

        it("Should verify a configuration configurability and visibility", function () {
            expect(configurationService.isConfigurable(sampleConfiguration, "module.propertySet.property")).toBe(false);
            expect(configurationService.isVisible(sampleConfiguration, "module.propertySet.property")).toBe(true);
        });
    });


    describe('Controller', function () {
        var configurationController,
            httpBackend;

        beforeEach(module('mayocat'));

        beforeEach(inject(function ($injector, $rootScope, $controller) {
            httpBackend = $injector.get('$httpBackend');
            httpBackend.when('GET', '/configuration').respond({
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
            expect(configurationController.configuration.module.propertySet.property.__originalValue).toBe("Hello");
        });

        it("Should prepare configuration object for submit", function () {
            httpBackend.flush();
            configurationController.configuration.module.propertySet.property.value = "Salut";
            httpBackend.expectPUT("/configuration", /{"module":{"propertySet":{"property":"Salut"}}}/).respond(200);
            configurationController.updateConfiguration();
        });

        it("Should prepare configuration object for submit when value is overriding but has not been changed",
            function () {
            httpBackend.flush();
            httpBackend.expectPUT("/configuration", /{"module":{"propertySet":{"property":"Hello"}}}/).respond(200);
            configurationController.updateConfiguration();
        });
    });


});
