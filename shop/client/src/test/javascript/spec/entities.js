angular.module('test', []).controller('TestEntityController', function($scope, entityBaseMixin) {
    angular.extend($scope, entityBaseMixin("testEntity"));
});

describe('Entities', function () {

    describe('Mixins', function () {

        var scope, controller;

        beforeEach(module('mayocat.entities'));
        beforeEach(function(){
            module('test', function($provide){
                var mockRouteParams = {
                    "testEntity" : "foo"
                };

                $provide.value('$routeParams', mockRouteParams);
            });
        });

        beforeEach(inject(function ($rootScope, $controller, entityBaseMixin) {
            scope = $rootScope.$new();
            controller = $controller('TestEntityController', {
                $scope: scope,
                entityBaseMixin:entityBaseMixin
            });
        }));

        it('Should mixin base mixin', function () {
            expect(scope.slug).toEqual("foo");
        });

    });

});
