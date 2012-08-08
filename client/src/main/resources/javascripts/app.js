'use strict';

angular.module('mayocat', []).
  // config(['$locationProvider', function($locationProvider) {
  //   $locationProvider.html5Mode(true);
  // }]).
  config(['$routeProvider', function($routeProvider) {
  $routeProvider.
      when('/', {templateUrl: 'partials/home.html',   controller: HomeCtrl}).
      otherwise({redirectTo: '/'});
}]);
