/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
(function () {

    'use strict';

    angular.module('mayocat.upload', [])

        /**
         * Upload service
         */
        .factory('uploadService', function() {

            var filesQueue = [];

            // Processes and uploads files.
            function uploadFiles($form, multiple, data, target) {
                // In "multiple mode", we send all the files. In "mono mode", we send only the first one.
                var files = multiple ? data.files : [data.files[0]];
                filesQueue.push.apply(filesQueue, files);

                for (var i = 0, file; file = files[i++];) {
                    var data = {};

                    if (typeof target !== 'undefined') {
                        data.target = target;
                    }

                    data.filename = file.name;

                    $form.fileupload('send', {
                        files: file,
                        formData: data
                    });
                }
            }

            function progressall(event, data) {
                console.log('Total progress: '+ Math.round(data.loaded / data.total * 100) +'%');
            }

            function progress(event, data) {
                console.log('Progress for "'+ data.formData.filename +'": '+ Math.round(data.loaded / data.total * 100) +'%');
            }

            function done(event, data) {
                console.log('Done', event, data);
            }

            function fail(event, data) {
                console.log('Fail', event, data);
            }

            return {
                uploadFiles: uploadFiles,
                progressall: progressall,
                progress: progress,
                done: done,
                fail: fail
            };

        })

        /**
         * Drop zone directives.
         */
        .directive('dropZonesContainer', ['$rootScope', function($rootScope) {

            function controller($scope) {
                var leaveTimeouts = [];

                $scope.visible = false; // The drop zones are hidden by default.
                $scope.dropZones = [];

                /**
                 * Show/Hide process for the container: Webkit is bugged (https://bugs.webkit.org/show_bug.cgi?id=66547)
                 * so we must handle the dragenter/dragleave events in a crappy way. When the cursor leaves the drop
                 * zones container (or one of its children) a timeout is registered to hide the container. However, if
                 * the cursor stays in the container, the dragenter event will cancel the previously registered timeout.
                 */

                // Shows the container and cancels the timeouts used to hide the container.
                this.showContainer = function() {
                    // Cancel the dragleave timeouts. This timeout is required since the dragenter event is triggered
                    // before the dragleave event.
                    setTimeout(function() {
                        leaveTimeouts.forEach(function(timeout) {
                            clearTimeout(timeout);
                        });

                        leaveTimeouts = [];
                    }, 0);
                    
                    // Display the container if there's a least one drop zone.
                    $scope.$apply(function() {
                        $scope.visible = !!$scope.dropZones.length;
                    });
                };

                // Hides the drop zones with a delay.
                this.hideContainer = function(force) {
                    var timeout = setTimeout(function() {
                        $scope.$apply(function() {
                            $scope.visible = false;
                        });
                    }, 0);

                    // Save a reference to the timeout if no force close has been asked.
                    if (force !== true) {
                        leaveTimeouts.push(timeout);
                    }
                };

                // Adds a new drop zone.
                function addDropZone(dropZoneScope) {
                    // Remove the drop zone when the associated scope is destroyed.
                    dropZoneScope.$on('$destroy', function() {
                        var index = $scope.dropZones.indexOf(dropZoneScope);
                        $scope.dropZones.splice(index, 1);
                    });

                    // Register the drop zone.
                    $scope.dropZones.push(dropZoneScope);
                }

                // Register listeners.
                $rootScope.$on('upload:addDropZone', function(event) {
                    addDropZone(event.targetScope);
                });
            }

            function link(scope, element, attrs, dropZonesCtrl) {
                $(document.body).on('dragenter', dropZonesCtrl.showContainer);
                $(element).on('dragleave', dropZonesCtrl.hideContainer);
            }

            return {
                restrict: 'E',
                scope: {},
                controller: controller,
                link: link,
                templateUrl: '/common/partials/dropZone.html'
            };

        }])

        .directive('dropZone', ['uploadService', function(uploadService) {

            function link(scope, element, attrs, dropZonesCtrl) {
                var $form = $(scope.dropZone.form);

                scope.highlighted = false;

                // Highlight the element on hover.
                $(element)
                    .on('dragenter', function() {
                        // See the explanations in the "dropZoneContainer" directive.
                        dropZonesCtrl.showContainer();

                        scope.$apply(function() {
                            scope.highlighted = true;
                        });
                    })
                    .on('dragleave', function() {
                        scope.$apply(function() {
                            scope.highlighted = false;
                        });
                    });

                // Extend the form with the jQuery file upload plugin.
                $form.fileupload({
                    dropZone: element,
                    pasteZone: null, // For an unknown reason, this feature doesn't work with Mayocat Shop.
                    singleFileUploads: false,
                    url: scope.dropZone.uploadUri,

                    progressall: uploadService.progressall,
                    progress: uploadService.progress,
                    done: uploadService.done,
                    fail: uploadService.fail,

                    add: function(event, data) {
                        dropZonesCtrl.hideContainer(true);
                        uploadService.uploadFiles($form, scope.dropZone.multiple, data, scope.dropZone.target);
                    }
                });
            }

            return {
                require: '^dropZonesContainer',
                restrict: 'A',
                link: link
            };

        }])

        /**
         * Image upload directive.
         */
        .directive('imageUpload', function factory() {

            function controller($scope) {
                // Convert multiple to a boolean.
                $scope.multiple = ($scope.multiple === 'false') ? false : true;
            }

            function link(scope, element) {
                scope.form = element;

                // If necessary, add the "multiple" attribute to the input.
                if (scope.multiple) {
                    $(element).find('input[type=file]').attr('multiple', '');
                }

                // Add a new drop zone for this instance.
                scope.$emit('upload:addDropZone');
            }

            return {
                restrict: 'E',
                templateUrl: '/common/partials/imageUpload.html',
                scope: {
                    'uploadUri': '@',
                    'multiple': '@',
                    'dropText': '=',
                    'target': '@'
                },
                controller: controller,
                link: link
            };
        });

})();
