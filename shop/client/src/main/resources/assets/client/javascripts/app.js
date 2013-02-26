'use strict';

var mayocat = angular.module('mayocat', [
    'search',
    'thumbnail',
    'product',
    'category',
    'catalog',
    'configuration',
    'jqui'
]);

mayocat.config(['$routeProvider', function($routeProvider) {
  $routeProvider.
      when('/', {templateUrl: 'partials/home.html', controller: HomeCtrl}).
      when('/product/', {templateUrl: 'partials/catalog.html', controller: 'CatalogController'}).
      when('/category/', {templateUrl: 'partials/categories.html', controller: 'CatalogController'}).
      when('/product/:product', {templateUrl: 'partials/product.html', controller: 'ProductController'}).
      when('/category/:category', {templateUrl: 'partials/category.html', controller: 'CategoryController'}).
      when('/configuration/', {templateUrl: 'partials/configuration.html', controller: 'ConfigurationController'}).
      otherwise({redirectTo: '/'});
}]);

/**
 * A switch button similar to the ios toggle switch
 */
mayocat.directive('switchButton', function() {
    return {
        require: 'ngModel',
        restrict: 'E',
        template: '<div class="btn-group">' +
            '<button class="btn on" ng-click="on()"></button>' +
            '<button class="btn off" ng-click="off()"></button>' +
            '</div>',
        link: function($scope, element, attrs, controller) {
            $scope.on = function() {
                $(element).find(".btn.on").addClass("btn-primary");
                $(element).find(".btn.off").removeClass("btn-primary");
                controller.$setViewValue(true);
            }
            $scope.off = function() {
                $(element).find(".btn.off").addClass("btn-primary");
                $(element).find(".btn.on").removeClass("btn-primary");
                controller.$setViewValue(false);
            }
            controller.$render = function() {
                if (typeof controller.$viewValue !== 'undefined') {
                    $scope[controller.$viewValue ? "on" : "off"]();
                }
            };
        }
    }
});

/**
 * 'active-class' directive for <a> elements or <li> elements with a children <a>.
 *
 * Sets an active class when the current location path matches the path of the href attribute of the target <a>
 * (which is either the link element on which the active-class attribute has been set, or the first link element
 * found when descending nodes down a list element.
 *
 * Inspired by http://stackoverflow.com/a/12631074/1281372
 */
mayocat.directive('activeClass', ['$location', function(location) {
  return {
    restrict: ['A', 'LI'],
    link: function(scope, element, attrs, controller) {
      var clazz = attrs.activeClass,
          path = attrs.href || $(element).find("a[href]").attr('href'),
          otherHrefs = attrs.otherActiveHref || $(element).find("a[other-active-href]").attr('other-active-href'),
          allHrefs = [ path ];

      if (typeof otherHrefs != "undefined") {
        otherHrefs = otherHrefs.split(",");
        for (var i=0; i<otherHrefs.length; i++) {
          allHrefs.push(otherHrefs[i].trim());
        }
      }

      if (allHrefs.length > 0) {
        scope.location = location;
        scope.$watch('location.path()', function(newPath) {
         for (var i=0; i<allHrefs.length; i++) {
           var path = allHrefs[i].substring(1); //hack because path does bot return including hashbang
           if (newPath.indexOf(path) === 0) {
             element.addClass(clazz);
             return;
           }
         }
         element.removeClass(clazz);
        });
      }
    }
  };
}]);


/**
 * Image upload directive.
 */
mayocat.directive('imageUpload', ['$location', '$timeout', '$q', function factory($location, $timeout, $q) {
    return {
        restrict: "E",
        templateUrl: "partials/imageUpload.html",
        scope: {
            'requestedDropZone': '&dropZone',
            'requestedUploadUri': '&uploadUri',
            'onUpload': '&onUpload'
        },
        link:  function postLink($scope, element, attrs) {

            // Get the upload URI the directive customer requested. It is either provided as a function,
            // which we need to evaluate, or as a raw string.
            $scope.uploadUri = typeof $scope.requestedUploadUri === "function"
                             ? $scope.requestedUploadUri()
                             : $scope.requestedUploadUri;

            $scope.dropzone = typeof $scope.requestedDropZone === "string"
                            ? $($scope.requestedDropZone)
                            : $(element).find('.dropzone');



            $scope.files = [];

            $scope.getPreviewUri = function (file, index) {
                var deferred = $q.defer();
                loadImage(file, function (preview) {
                    deferred.resolve({
                        index:index,
                        preview:preview
                    });
                    $scope.$apply();
                }, {
                    maxWidth:100,
                    maxHeight:100,
                    canvas:false,
                    noRevoke:true
                });
                return deferred.promise;
            }

            $scope.remove = function(index) {
                $scope.files[index] = null;
            }

            $scope.fileUploadFailed = function(index) {
                $scope.$apply(function($scope){
                    $scope.files[index].failed = true;
                });
            }

            $scope.fileUploading = function(index, loaded, total) {
                $scope.$apply(function($scope){
                    $scope.files[index].progress = Math.round(loaded * 100 / total);
                });
            }

            $scope.fileUploaded = function(index) {
                $scope.$apply(function($scope){
                    $scope.files[index].uploaded = true;
                    if (typeof $scope.onUpload === "function") {
                        $scope.onUpload();
                    }
                });
            }

            $scope.submit = function() {
                for (var i=0; i<$scope.files.length; i++) {
                    if ($scope.files[i] !== null) {
                        $scope.files[i].progress = 0;
                        $(element).fileupload('send', {
                            files: $scope.files[i],
                            formData: {
                                "title" :  $scope.files[i].title,
                                "description" :  $scope.files[i].description
                            }
                        });
                    }
                }
            }

            $scope.hasFiles = function() {
                for (var i=0; i<$scope.files.length; i++) {
                    if ($scope.files[i] !== null) {
                        return true;
                    }
                }
                return false;
            }

            // Extend the directive element with the jQuery file upload plugin
            $(element).fileupload({
                dropZone: $scope.dropzone,
                url: $scope.uploadUri,
                add: function(e, data) {
                    $scope.$apply(function($scope) {
                        for (var i = 0; i < data.files.length; i++) {
                            // Usually there is just one
                            var index = $scope.files.push(data.files[i]) - 1;
                            $scope.files[index].index = index;
                            $scope.getPreviewUri($scope.files[index], index).then(function(result) {
                                $scope.files[result.index].previewUri = result.preview.src;
                                $scope.files[result.index].previewWidth = result.preview.width;
                                $scope.files[result.index].previewHeight = result.preview.height;
                            });
                        }
                    });
                },
                done: function(e, data) {
                    if (typeof data.files !== 'undefined' && typeof data.files[0].index !== 'undefined') {
                        $scope.fileUploaded(data.files[0].index);
                    }
                },
                fail: function(e, data) {
                    if (typeof data.files !== 'undefined' && typeof data.files[0].index !== 'undefined') {
                        $scope.fileUploadFailed(data.files[0].index);
                    }
                },
                progress: function(e, data) {
                    if (typeof data.files !== 'undefined' && typeof data.files[0].index !== 'undefined') {
                        $scope.fileUploading(data.files[0].index, data.loaded, data.total);
                    }
                }
            });
        }
    }
}]);

/**
 * Thumbnail editor directive
 */
mayocat.directive('thumbnailEditor', ['$rootScope', function factory($rootScope) {
    return {
        restrict:"E",
        scope:{
            'image':'&',
            'width':'&',
            'height':'&',
            'selection' : '&'
        },
        link:function postLink($scope, element, attrs) {
            $scope.$on('thumbnails:edit:ready', function (event, data) {
                var imageElement = $("<img />").load(
                    function () {
                        if ($scope.selection() === undefined) {

                            // If no initial selection was passed to the widget, we compute the largest box
                            // that can fit the desired thumbnail in.

                            var sizeRatio = $scope.width() / $scope.height(),
                                imageRatio = $(this).width() / $(this).height(),
                                width,
                                height;

                            // FIXME sometimes the image width and height are still 0, even though we are in the
                            // load callback.

                            width = sizeRatio > imageRatio ? $(this).height() * sizeRatio : $(this).width();
                            height = sizeRatio > imageRatio ? $(this).height() : $(this).width() * sizeRatio;

                            var i = 0;
                            var setSelection = function () {
                                if (typeof $scope.api !== "undefined") {
                                    $scope.api.setSelect([ 0, 0, width, height ]);
                                }
                                else {
                                    // The image is loaded before the Jcrop API has been initialized fully.
                                    // Wait 0.1 s
                                    i++;
                                    if (i < 10) {
                                        setTimeout(setSelection, 100);
                                    }
                                    // Give up after 10 tries
                                }
                            }
                            setSelection();
                        }
                    }
                ).attr("src", $scope.image());
                $(element).html($("<div/>").html(imageElement));

                $(imageElement).Jcrop({
                    boxWidth: 500,
                    boxHeight: 500,
                    setSelect: $scope.selection(),
                    aspectRatio: $scope.width() / $scope.height(),
                    onSelect: function(coordinates){
                        $rootScope.$broadcast('thumbnails:edit:selection', coordinates);
                    }
                }, function(){
                    $scope.api = this;
                });

            });
        }
    }
}]);

/**
 * Authentication/401 interception
 *
 * based on http://www.espeo.pl/2012/02/26/authentication-in-angularjs-application
 */
mayocat.config(function($httpProvider) {
  var interceptor = ['$rootScope','$q', function(scope, $q) {

    function success(response) {
      return response;
    }

    function error(response) {
      var status = response.status;
      if (status == 401 && response.config.url != '/api/1.0/login/') {
        var deferred = $q.defer();
        var req = {
          config: response.config,
          deferred: deferred
        }
        scope.requests401.push(req);
        scope.$broadcast('event:authenticationRequired');
        return deferred.promise;
      }
      // otherwise
      return $q.reject(response);
    }

    return function(promise) {
      return promise.then(success, error);
    }
  }];
  $httpProvider.responseInterceptors.push(interceptor);
});

mayocat.run(['$rootScope', '$http', function(scope, $http) {

  /**
   * Holds all the requests which failed due to 401 response.
   */
  scope.requests401 = [];

  /**
   * On 'event:authenticationSuccessful', resend all the 401 requests.
   */
  scope.$on('event:authenticationSuccessful', function() {
    var i, requests = scope.requests401;
    for (i = 0; i < requests.length; i++) {
      retry(requests[i]);
    }
    scope.requests401 = [];
    function retry(req) {
      $http(req.config).then(function(response) {
        req.deferred.resolve(response);
      });
    }
  });

  /**
   * On 'event:loginRequest' send credentials to the server.
   */
  scope.$on('event:authenticationRequest', function(event, username, password, remember) {
    var config = {
      headers: {'Content-Type':'application/x-www-form-urlencoded; charset=UTF-8'}
    };
    var data = {
      username: username,
      password: password,
      remember: remember
    };
    $http.post('/api/1.0/login/', $.param(data), config)
      .success(function(data, status) {
        if (status == 200) {
          ping();
        }
        else {
          scope.$broadcast('event:authenticationFailure');
        }
      })
      .error(function(data, status){
        scope.$broadcast('event:authenticationFailure');
      });
  });

  /**
   * On 'logoutRequest' invoke logout on the server and broadcast 'event:authenticationRequired'.
   */
  scope.$on('event:forgetAuthenticationRequest', function() {
    var config = {
      headers: {'Content-Type':'application/x-www-form-urlencoded; charset=UTF-8'}
    };
    $http.post('/api/1.0/logout/', "", config).success(function() {
      ping();
      scope.$broadcast('event:authenticationRequired');
    });
  });

  /**
   * Ping server to figure out if user is already logged in.
   */
  scope.ping = function() {
    $http.get('/api/1.0/tenant/').success(function(data) {
      scope.$broadcast('event:authenticationSuccessful', data);
    });
  }

}]);
