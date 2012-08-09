'use strict'

function HomeCtrl($scope) {
}

function UserCtrl($scope) {
  $scope.user = "";
  $scope.$on("event:authenticationSuccessful", function(event, user) {
    $scope.user = user;
  });
}

function LoginCtrl($scope) {
  $scope.username = "";
  $scope.password = "";
  $scope.remember = false;
  $scope.requestLogin = function() {
    $scope.$emit("event:authenticationRequest", $scope.username, $scope.password, $scope.remember);
  };
}

function AppController($scope) {

  $scope.authenticated = false;
  $scope.$on("event:authenticationRequired", function() {
    $scope.authenticated = false;
  });
  $scope.$on("event:authenticationSuccessful", function() {
    console.log("rodger that");
    $scope.authenticated = true;
  });
}
