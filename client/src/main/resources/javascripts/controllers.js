'use strict'

function HomeCtrl($scope) {
  $scope.user = "";
  $scope.$on("event:authenticationSuccessful", function(user) {
    $scope.user = user;
  });
}

function UserCtrl($scope) {
}

function LoginCtrl($scope) {
  $scope.username = "";
  $scope.password = "";
  $scope.requestLogin = function() {
    $scope.$emit("event:authenticationRequest", $scope.username, $scope.password);
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
