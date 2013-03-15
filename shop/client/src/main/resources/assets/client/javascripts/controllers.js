'use strict'

function HomeCtrl($scope) {
}

function LoginCtrl($rootScope, $scope) {
  $scope.username = "";
  $scope.password = "";
  $scope.remember = false;
  $scope.authenticationFailed = false;
  $scope.requestLogin = function() {
    $rootScope.$broadcast("event:authenticationRequest", $scope.username, $scope.password, $scope.remember);
  };
  $scope.$on("event:authenticationFailure", function() {
    $scope.authenticationFailed = true;
  });
  $scope.$on("event:authenticationSuccessful", function(event, data) {
    $scope.authenticationFailed = false;
  });
}

function AppController($rootScope, $scope, $location) {
  // Ensure authenticated
  $scope.$parent.ping();

  $scope.tenant = undefined;
  $scope.user = undefined;
  $scope.authenticated = undefined;

  $scope.logout = function() {
    $rootScope.$broadcast("event:forgetAuthenticationRequest");
  };

  $scope.$on("event:authenticationRequired", function() {
    $scope.authenticated = false;
  });

  $scope.$on("event:authenticationSuccessful", function(event, data) {
    $scope.authenticated = true;
    $scope.user = data.user;
    $scope.tenant = data.tenant;
  });

  $scope.isCatalog = false;
  $scope.isPages = false;

  $scope.setRoute = function (href) {
    $location.url(href);
  };

  $scope.$watch('location.path()', function(path) {
      $scope.isCatalog = false;
      $scope.isPages = false;
      angular.forEach(["/catalog", "/product/", "/collection/"], function(catalogPath) {
          if (path.indexOf(catalogPath) == 0) {
              $scope.isCatalog = true;
          }
      });
      angular.forEach(["/contents", "/page/"], function(pagePage) {
          if (path.indexOf([pagePage]) == 0) {
              $scope.isPages = true;
          }
      });
  });
}
