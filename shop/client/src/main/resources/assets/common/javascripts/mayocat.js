'use strict';

var mayocat = angular.module('mayocat', [
    'mayocat.authentication',
    'mayocat.addons',
    'mayocat.image',
    'mayocat.thumbnail',
    'mayocat.configuration',
    'mayocat.time'
]);

/**
 * Authentication/401 interception
 *
 * based on http://www.espeo.pl/2012/02/26/authentication-in-angularjs-application
 */
mayocat.config(function ($httpProvider) {

    var interceptor = ['$rootScope', '$q', function (scope, $q) {
        function success(response) {
            return response;
        }

        function error(response) {
            var status = response.status;
            if (status == 401 && response.config.url != '/api/login/') {
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

        return function (promise) {
            return promise.then(success, error);
        }
    }];

    $httpProvider.responseInterceptors.push(interceptor);
});


/**
 * Internal Server Error / 500 interception
 */
mayocat.config(function ($httpProvider) {
    var interceptor = ['$rootScope', '$q', function (scope, $q) {

        function success(response) {
            return response;
        }

        function error(response) {
            var status = response.status;
            if (status == 500) {
                scope.$broadcast('event:serverError');
            }
            return response;
        }

        return function (promise) {
            return promise.then(success, error);
        }
    }];
    $httpProvider.responseInterceptors.push(interceptor);
});



/**
 * A directive for bootstrap modals that will trigger the modal to show when a particular event is broadcast.
 */
mayocat.directive('modalTrigger', ['$rootScope', function ($rootScope) {
    return {
        restrict: "A",
        link: function ($scope, element, attrs) {
            var event = attrs.modalTrigger;
            $rootScope.$on(event, function () {
                $(element).modal("show");
            });
        }
    }
}]);

/**
 * A directive for bootstrap modals that will trigger the modal to be dismissed when a particular event is broadcast.
 */
mayocat.directive('modalDismiss', ['$rootScope', function ($rootScope) {
    return {
        restrict: "A",
        link: function ($scope, element, attrs) {
            var event = attrs.modalDismiss;
            $rootScope.$on(event, function () {
                $(element).modal("hide");
            });
        }
    }
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
        link: function postLink($scope, element, attrs) {

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
                        index: index,
                        preview: preview
                    });
                    $scope.$apply();
                }, {
                    maxWidth: 100,
                    maxHeight: 100,
                    canvas: false,
                    noRevoke: true
                });
                return deferred.promise;
            }

            $scope.remove = function (index) {
                $scope.files[index] = null;
            }

            $scope.fileUploadFailed = function (index) {
                $scope.$apply(function ($scope) {
                    $scope.files[index].failed = true;
                });
            }

            $scope.fileUploading = function (index, loaded, total) {
                $scope.$apply(function ($scope) {
                    $scope.files[index].progress = Math.round(loaded * 100 / total);
                });
            }

            $scope.fileUploaded = function (index) {
                $scope.$apply(function ($scope) {
                    if (typeof $scope.onUpload === "function") {
                        $scope.onUpload();
                    }
                    // Remove the file from list
                    $scope.files[index] = null;
                });
            }

            $scope.submit = function () {
                for (var i = 0; i < $scope.files.length; i++) {
                    if ($scope.files[i] !== null) {
                        $scope.files[i].progress = 0;
                        $(element).fileupload('send', {
                            files: $scope.files[i],
                            formData: {
                                "title": $scope.files[i].title,
                                "description": $scope.files[i].description
                            }
                        });
                    }
                }
            }

            $scope.hasFiles = function () {
                for (var i = 0; i < $scope.files.length; i++) {
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
                add: function (e, data) {
                    $scope.$apply(function ($scope) {
                        for (var i = 0; i < data.files.length; i++) {
                            // Usually there is just one
                            var index = $scope.files.push(data.files[i]) - 1;
                            $scope.files[index].index = index;
                            $scope.getPreviewUri($scope.files[index], index).then(function (result) {
                                $scope.files[result.index].previewUri = result.preview.src;
                                $scope.files[result.index].previewWidth = result.preview.width;
                                $scope.files[result.index].previewHeight = result.preview.height;
                            });
                        }
                    });
                },
                done: function (e, data) {
                    if (typeof data.files !== 'undefined' && typeof data.files[0].index !== 'undefined') {
                        $scope.fileUploaded(data.files[0].index);
                    }
                },
                fail: function (e, data) {
                    if (typeof data.files !== 'undefined' && typeof data.files[0].index !== 'undefined') {
                        $scope.fileUploadFailed(data.files[0].index);
                    }
                },
                progress: function (e, data) {
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
        restrict: "E",
        scope: {
            'image': '&',
            'width': '&',
            'height': '&',
            'selection': '&'
        },
        link: function postLink($scope, element, attrs) {
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
                onSelect: function (coordinates) {
                    $rootScope.$broadcast('thumbnails:edit:selection', coordinates);
                }
            }, function () {
                $scope.api = this;
            });

        }
    }
}]);

mayocat.controller('HomeCtrl', ['$scope', '$resource',
    function ($scope, $resource) {

    }]);

mayocat.controller('LoginController', ['$rootScope', '$scope',
    function ($rootScope, $scope) {

        $scope.username = "";
        $scope.password = "";
        $scope.remember = false;
        $scope.authenticationFailed = false;

        $scope.requestLogin = function () {
            $rootScope.$broadcast("event:authenticationRequest", $scope.username, $scope.password, $scope.remember);
        };
        $scope.$on("event:authenticationFailure", function () {
            $scope.authenticationFailed = true;
        });
        $scope.$on("event:authenticationSuccessful", function (event, data) {
            $scope.authenticationFailed = false;
        });
    }]);


mayocat.controller('AppController', ['$rootScope', '$scope', '$location', '$http', 'authenticationService',
    function ($rootScope, $scope, $location, $http, authenticationService) {


        /**
         * On logout request invoke logout on the server and broadcast 'event:authenticationRequired'.
         */
        $scope.$on('event:forgetAuthenticationRequest', function () {
            var config = {
                headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
            };
            $http.post('/api/logout/', "", config).success(function () {
                $scope.ping();
                $rootScope.$broadcast('event:authenticationRequired');
            });
        });

        /**
         * On 'event:loginRequest' send credentials to the server.
         */
        $scope.$on('event:authenticationRequest', function (event, username, password, remember) {
            var config = {
                headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
            };
            var data = {
                username: username,
                password: password,
                remember: remember
            };
            $http.post('/api/login/', $.param(data), config)
                .success(function (data, status) {
                    if (status == 200) {
                        $scope.ping();
                    }
                    else {
                        $scope.$broadcast('event:authenticationFailure');
                    }
                })
                .error(function (data, status) {
                    $scope.$broadcast('event:authenticationFailure');
                });
        });

        $scope.ping = function () {
            $http.get('/api/tenants/_current').success(function (data) {
                authenticationService.loginConfirmed(data);
            });
        }

        // Ensure authenticated
        $scope.ping();

        $scope.tenant = undefined;
        $scope.user = undefined;
        $scope.authenticated = undefined;

        $scope.logout = function () {
            $rootScope.$broadcast("event:forgetAuthenticationRequest");
        };

        $scope.$on("event:authenticationRequired", function () {
            $scope.authenticated = false;
        });

        $scope.$on("event:authenticationSuccessful", function (event, data) {
            $scope.authenticated = true;
            $scope.user = data.user;
            $scope.tenant = data.tenant;
        });

        $scope.setRoute = function (href) {
            $location.url(href);
        };

    }]);
