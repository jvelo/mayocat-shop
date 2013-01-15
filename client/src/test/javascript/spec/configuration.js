describe('Configuration', function() {

  var configurationController;

  beforeEach(module('mayocat'));

  beforeEach(inject(function($injector, $rootScope, $controller) {
    $httpBackend = $injector.get('$httpBackend');
    $httpBackend.when('GET', '/tenant/').respond({userId: 'userX'}, {'A-Token': 'xxx'});
    $httpBackend.when('GET', '/configuration').respond({userId: 'userX'}, {'A-Token': 'xxx'});

    configurationController = $controller('ConfigurationController', { $scope: $rootScope });
  }));

  it("Should be defined", function() {
    expect(configurationController).toBeDefined();
  });

});
