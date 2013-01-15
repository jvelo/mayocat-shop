describe('Configuration', function() {

  var configurationController,
      httpBackend;

  beforeEach(module('mayocat'));

  beforeEach(inject(function($injector, $rootScope, $controller) {
    httpBackend = $injector.get('$httpBackend');
    httpBackend.when('GET', '/configuration').respond({
      "module" : {
        "propertySet" : {
          "property" : {
            "value": "Hello",
            "default" : "Bonjour",
            "configurable": false,
            "visible" : true
          }
        }
      }
    });

    configurationController = $rootScope.$new();
    configurationController.$apply();
    $controller('ConfigurationController', { $scope: configurationController });
  }));

  it("Should be defined", function() {
    expect(configurationController).toBeDefined();
  });

  it("Should save original values", function(){
    httpBackend.flush();
    expect(configurationController.configuration.module.propertySet.property.__originalValue).toBe("Hello");
  });

  it("Should verify a configuration configurability and visibility", function(){
    httpBackend.flush();
    expect(configurationController.isConfigurable("module.propertySet.property")).toBe(false);
    expect(configurationController.isVisible("module.propertySet.property")).toBe(true);
  });

  it("Should prepare configuration object for submit", function(){
    httpBackend.flush();
    configurationController.configuration.module.propertySet.property.value = "Salut";
    var prepared = configurationController.prepareConfiguration();
    expect(prepared.module.propertySet.property).toBe("Salut");
  });

  it("Should prepare configuration object for submit when value is overriding but has not been changed", function(){
    httpBackend.flush();
    var prepared = configurationController.prepareConfiguration();
    expect(prepared.module.propertySet.property).toBe("Hello");
  });


});
