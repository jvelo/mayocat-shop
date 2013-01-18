describe('Configuration', function () {

    describe('Service', function () {
        var configurationService,
            httpBackend;

        beforeEach(module('mayocat'));

        beforeEach(inject(function ($injector) {
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

            configurationService = $injector.get('configurationService');
        }));

        it("Should be defined", function () {
            expect(configurationService).toBeDefined();
        });

        it("Should offer access to configurations", function () {
            httpBackend.flush();
            expect(configurationService.get("module.propertySet.property")).toBe("Hello");
        });

        it("Should verify a configuration configurability and visibility", function () {
            httpBackend.flush();
            expect(configurationService.isConfigurable("module.propertySet.property")).toBe(false);
            expect(configurationService.isVisible("module.propertySet.property")).toBe(true);
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

        it("Should prepare configuration object for submit when value is overriding but has not been changed", function () {
            httpBackend.flush();
            httpBackend.expectPUT("/configuration", /{"module":{"propertySet":{"property":"Hello"}}}/).respond(200);
            configurationController.updateConfiguration();
        });
    });


});
