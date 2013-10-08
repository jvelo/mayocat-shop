'use strict';

var mayocat = angular.module('mayocat', [
    'mayocat.authentication',
    'mayocat.addons',
    'mayocat.image',
    'mayocat.thumbnail',
    'mayocat.configuration',
    'mayocat.time',
    'mayocat.entities',
    'mayocat.locales'
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
 * Simple list picker directive to manage a list of elements
 *
 * TODO: handle ng-disabled
 */
mayocat.directive('listPicker', ['$parse', function($parse){
    return {
        restrict: 'E',
        require: 'ngModel',
        transclude: 'element',
        replace: true,
        template: '<div><div><ul class="pickerElements"><li ng-repeat="element in model">' +
            '<button class="btn btn-mini" ng-click="remove(element)">{{getDisplayElement(element)}} &times;</span></button>' +
            '</li></ul></div><div class="clearfix"></div>' +
            '<span ng-transclude></span>' +
            '<input type="submit" class="btn" value="{{\'global.actions.add\' | translate}}" ng-click="add()"></div>',

        link: function (scope, element, attr, ngModel) {
            scope.$watch(function () {
                return ngModel.$modelValue;
            }, function (modelValue) {
                scope.model = modelValue;
            });
            scope.$watch("model", function (value) {
                ngModel.$setViewValue(value);
            });
        },
        controller: function ($scope, $element, $attrs) {
            $scope.add = function () {
                $scope.new = $element.find("select,input").attr("value");
                if ($scope.model.indexOf($scope.new) < 0) {
                    $scope.model.push($scope.new);
                }
                $scope.new = "";
            }
            $scope.remove = function (currency) {
                $scope.model.splice($scope.model.indexOf(currency), 1);
            }
            if (typeof $attrs.display !== 'undefined') {
                var passed = $parse($attrs.display);
            }
            $scope.getDisplayElement = function (element) {
                $scope.elementToDisplay = element;
                return passed ? passed($scope): element;
            }
        }
    };
}]);

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
        templateUrl: "/common/partials/imageUpload.html",
        scope: {
            'headerText': "@",
            'dragAndDrop': '&',
            'multiple': '&',
            'requestedDropZone': '&dropZone',
            'requestedUploadUri': '&uploadUri',
            'onUpload': '&onUpload'
        },
        controller: function($scope, $element, $attrs) {
            // Initialize default value for attributes

            // Get the upload URI the directive customer requested. It is either provided as a function,
            // which we need to evaluate, or as a raw string.
            $scope.uploadUri = typeof $scope.requestedUploadUri === "function"
                ? $scope.requestedUploadUri()
                : $scope.requestedUploadUri;

            // DND default value
            if (typeof $scope.dragAndDrop === "function") {
                $scope.dragAndDrop = $scope.dragAndDrop();
                if (typeof $scope.dragAndDrop === "undefined") {
                    $scope.dragAndDrop = true;
                }
            }

            // Multiple default value
            if (typeof $scope.multiple === "function") {
                $scope.multiple = $scope.multiple();
                if (typeof $scope.multiple === "undefined") {
                    $scope.multiple = true;
                }
            }

            // Custom dropzone option
            $scope.dropzone = typeof $scope.requestedDropZone === "string"
                ? $($scope.requestedDropZone)
                : $element.find('.dropzone');

            if ($scope.dropzone && typeof $scope.dropzone.length !== "undefined") {
                // The file upload widget expects an element, not a jQuery-style array
                $scope.dropzone = $scope.dropzone[0];
            }

        },
        compile: function compile(tElement, tAttrs, transclude) {
            return {
                post: function postLink($scope, element, attrs) {

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
                                $scope.onUpload($scope.files[index]);
                            }
                            // Remove the file from list
                            $scope.files[index] = null;
                        });
                    }

                    $scope.submit = function () {
                        for (var i = 0; i < $scope.files.length; i++) {
                            if ($scope.files[i] !== null) {
                                $scope.files[i].progress = 0;
                                var data = {};
                                if (typeof $scope.files[i].title !== 'undefined') {
                                    data["title"] = $scope.files[i].title;
                                }
                                if (typeof $scope.files[i].description !== 'undefined') {
                                    data["description"] = $scope.files[i].description;
                                }
                                $(element).fileupload('send', {
                                    files: $scope.files[i],
                                    formData: data
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
                        dropZone: $scope.dropzone || undefined,
                        url: $scope.uploadUri,
                        add: function (e, data) {
                            $scope.$apply(function ($scope) {
                                for (var i = 0; i < data.files.length; i++) {
                                    if (!$scope.multiple) {
                                        // If we are in "mono-upload" mode, we always put the image as first one of
                                        // the array
                                        $scope.files[0] = data.files[i];
                                        index = 0;
                                    }
                                    else {
                                        // In "multi-upload" mode, we addpend the file to the array
                                        var index = $scope.files.push(data.files[i]) - 1;
                                    }
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

/**
 * WYSIWYG-augmented textarea directive with CKEditor.
 *
 * See http://stackoverflow.com/questions/11997246/bind-ckeditor-value-to-model-text-in-angularjs-and-rails
 * and http://stackoverflow.com/questions/15483579/angularjs-ckeditor-directive-sometimes-fails-to-load-data-from-a-service
 */
mayocat.directive('ckEditor', function () {
    return {
        require: '?ngModel',
        link: function (scope, elm, attr, ngModel) {
            var ckOptions = {
                language: localStorage.locale || Mayocat.defaultLocale,
                toolbarGroups: [
                    { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
                    { name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align' ] },
                    { name: 'links' },
                    { name: 'styles' }
                ],
                removePlugins: 'elementspath',
                height: '290px',
                width: '99%'
            };

            var textarea = elm[0],
                ck = CKEDITOR.replace(textarea, ckOptions);

            if (!ngModel) return;

            // loaded didn't seem to work, but instanceReady did
            // I added this because sometimes $render would call setData before the ckeditor was ready
            ck.on('instanceReady', function () {
                ck.setData(ngModel.$viewValue);
            });

            // Make sure that if the model changes, the values is passed backed to the ckeditor
            // Example: value comes from an AJAX request, and that creates a race condition vs. ckeditor initialization
            scope.$watch(ngModel, function(){
                ck.setData(ngModel.$viewValue);
            });


            ck.on('pasteState', function () {
                scope.$apply(function () {
                    ngModel.$setViewValue(ck.getData());
                });
            });

            ngModel.$render = function (value) {
                ck.setData(ngModel.$viewValue);
            };

            scope.$on('entity:initialized', function(event, entity){
                CKEDITOR.config.mayocat_entityUri = entity.uri;
            });

            // Create a new ckEditor with a new locale when this last one is changed
            scope.$on('ui:localeChanged', function (event, locale) {
                var data = ck.getData();

                ckOptions.language = locale;
                ckOptions.on = {
                    instanceReady: function () {
                        this.setData(data);
                    }
                };

                ck.destroy();
                ck = CKEDITOR.replace(textarea, ckOptions);
            });

        }
    };
});

/**
 * Scroll directive.
 *
 * - On touch devices : we use iScroll4 to handle scroll ; until native scrolling is good enough and mainstream
 * - On desktop devices : the goal is to have custom scroll bars inspired  by OSX but with our own style
 */
mayocat.directive('scroll', [function () {
    return {
        restrict: ['A', 'E'],
        replace: true,
        transclude: true,
        template: "<div class='scroll-wrapper'><div class='content' ng-transclude></div></div>",
        link: function (scope, element, attr) {

            var createOrUpdateScroll = function () {
                if ('ontouchstart' in window || navigator.msMaxTouchPoints) {
                    // On touch devices, scroll is handled by iScroll4

                    if (!element.data("iscroll4")) {
                        element[0].addEventListener('touchmove', function (e) {
                            // Prevent default behavior
                            e.preventDefault();
                        }, false);
                        new iScroll(element[0], {vScrollbar: false});
                        element.data("iscroll4", "true");
                    }
                }
                else {
                    // On non-touch devices, we emulate OSX Lion style scroll bars using the jScrollPane jQuery plugin.

                    var api = $(element).data('jsp');
                    if (api) {
                        api.reinitialise();
                    }
                    else {
                        $(element).jScrollPane({
                            // magic trick to not have horizontal scrollbars
                            // http://stackoverflow.com/questions/4404944/how-do-i-disable-horizontal-scrollbar-in-jscrollpane-jquery
                            contentWidth: '0px'
                        });
                    }
                }
            }

            var observe = function () {
                var observationTarget = element.find(".content").length ? element.find(".content")[0] : element[0];
                var observer = new MutationObserver(function (mutations) {
                    observer.disconnect();
                    createOrUpdateScroll();
                    observe();
                });
                observer.observe(observationTarget, { attributes: true, childList: true, characterData: true, subtree: true });
            }

            observe();

            $(window).resize(function () {
                // force update the scroll when the window is resized
                createOrUpdateScroll();
            });

            scope.$on('$routeChangeSuccess', function (event, current) {
                // force update the scroll when the route changes
                createOrUpdateScroll();
            });
        }
    };
}]);

/**
 * A switch button similar to the ios toggle switch
 */
mayocat.directive('switchButton', function () {
    return {
        require: 'ngModel',
        restrict: 'E',
        template: '<div class="btn-group">' +
            '<button class="btn on" ng-click="on()"></button>' +
            '<button class="btn off" ng-click="off()"></button>' +
            '</div>',
        link: function ($scope, element, attrs, controller) {
            $scope.on = function () {
                $(element).find(".btn.on").addClass("btn-primary");
                $(element).find(".btn.off").removeClass("btn-primary");
                controller.$setViewValue(true);
            }
            $scope.off = function () {
                $(element).find(".btn.off").addClass("btn-primary");
                $(element).find(".btn.on").removeClass("btn-primary");
                controller.$setViewValue(false);
            }
            controller.$render = function () {
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
mayocat.directive('activeClass', ['$location', function (location) {
    return {
        restrict: ['A', 'LI'],
        link: function (scope, element, attrs, controller) {
            var clazz = attrs.activeClass,
                path = attrs.href || $(element).find("a[href]").attr('href'),
                otherHrefs = attrs.otherActiveHref || $(element).find("a[other-active-href]").attr('other-active-href'),
                allHrefs = [ path ];

            if (typeof otherHrefs != "undefined") {
                otherHrefs = otherHrefs.split(",");
                for (var i = 0; i < otherHrefs.length; i++) {
                    allHrefs.push(otherHrefs[i].trim());
                }
            }

            if (allHrefs.length > 0) {
                scope.location = location;
                scope.$watch('location.path()', function (newPath) {
                    for (var i = 0; i < allHrefs.length; i++) {
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
 * Directive that displays validation messages (errors, ok, etc.) only when the user leaves a field (blur)
 *
 * Inspired by http://stackoverflow.com/questions/15798594/angularjs-forms-validate-fields-after-user-has-left-field
 */
mayocat.directive('validateOnBlur', function () {
    return {
        restrict: 'AC',
        link: function (scope, element, attrs) {
            var inputs = $(element).find('input, select, textarea');

            inputs.on('blur', function () {
                $(this).addClass('has-visited');
                $(this).siblings(".validation").addClass('has-visited');
            });

            element.on('submit', function () {
                inputs.addClass('has-visited');
                inputs.siblings(".validation").addClass('has-visited');
            });
        }
    };
});

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


mayocat.controller('AppController', ['$rootScope', '$scope', '$location', '$http', '$translate', 'authenticationService',
    'configurationService',
    function ($rootScope, $scope, $location, $http, $translate, authenticationService, configurationService) {


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
            $http.get('/api/tenant')
                .success(function (data, status, headers, config) {
                    if (status === 200) {
                        $scope.tenant = data;
                    }
                    else if (status === 404) {
                        $scope.tenant = undefined;
                    }

                })
                .error(function (data, status, headers, config) {
                    $scope.$parent.$broadcast('event:serverError');
                });
            $http.get('/api/me')
                .success(function (data, status, headers, config) {
                    if (status === 200) {
                        authenticationService.loginConfirmed(data);
                    }
                    else if (status === 404) {
                        $scope.tenant = undefined;
                    }

                })
                .error(function (data, status, headers, config) {
                    $scope.$parent.$broadcast('event:serverError');
                });
        }

        // Ensure authenticated
        $scope.ping();

        // By default pretend there is a tenant.
        // If there is not, it will be set to  "undefined". See ping() callback.
        $scope.tenant = {};

        configurationService.get('site', function (site) {
            $scope.site = site;
        });

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

        $rootScope.uiLocale = localStorage.locale || Mayocat.defaultLocale;

        $scope.changeLocale = function (locale) {
            $rootScope.$broadcast('ui:localeChanged', locale);

            localStorage.locale = locale;
            $rootScope.uiLocale = locale;
            $translate.uses(locale);
        };

    }]);
