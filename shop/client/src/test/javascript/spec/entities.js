/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
