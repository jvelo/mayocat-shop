/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
(function () {

    'use strict';

    angular.module('mayocat.upload', [
        'pascalprecht.translate'
    ])

        /**
         * Upload service
         */
        .factory('uploadService', [
                '$rootScope', '$location', '$translate', 'notificationService',
                function($rootScope, $location, $translate, notificationService) {

            var idCounter = 0,
                uploadQueue = [],
                bindedScopes = []; // Contains the scopes that require to be notified for each upload progress.

            // Finds an upload in the queue.
            function filterUploadsByEntity(uri) {
                return uploadQueue.filter(function(upload) {
                    return upload.entityUri == uri;
                })[0];
            }

            // Generates an image preview.
            function generatePreview(file) {
                loadImage(
                    file.raw,
                    function(preview) {
                        file.preview = {
                            src: preview.src,
                            width: preview.width,
                            height: preview.height
                        };
                    },
                    {
                        maxWidth: 100,
                        maxHeight: 100,
                        canvas: false,
                        noRevoke: true
                    }
                );
            }

            // Wraps and adds a new binded scope.
            function addBindedScope(event) {
                var scope = event.targetScope;

                bindedScopes.push({
                    entityUri: $location.path(),
                    raw: scope
                });

                // Remove the binded scope once it's destroyed.
                scope.$on('destroy', removeBindedScope);
            }

            // Updates the uploadQueue for all the binded scopes.
            function updateBindedScopes(reloadUploadedImages) {
                for (var i = 0, scope; scope = bindedScopes[i++];) {
                    // For each scope, update its upload queue.
                    scope.raw.$apply(function(rawScope) {
                        // An upload queue is affected to a binded scope only if the entity URIs are equivalent.
                        rawScope.uploadQueue = uploadQueue.filter(function(upload) {
                            return scope.entityUri == upload.entityUri;
                        });

                        // If necessary, reload the uploaded images.
                        reloadUploadedImages && rawScope.reloadImages();
                    });
                }
            }

            // Removes a binded scope.
            function removeBindedScope(event) {
                var index = bindedScopes.indexOf(event.targetScope);

                if (index != -1) {
                    bindedScopes.splice(index, 1);
                }
            }

            // Processes and uploads files.
            function uploadFiles($form, entityUri, multiple, data, target) {
                var upload = {
                    entityUri: entityUri,
                    // In "multiple mode", we send all the files. In "mono mode", we send only the first one.
                    files: multiple ? data.files : [data.files[0]]
                };

                // Add a progress bar to the notifications.
                var status = $translate('upload.status.progress', {filesNumber: upload.files.length});

                notificationService.notify(status, {
                    type: 'progress',
                    controls: function(setProgress) {
                        upload.setProgress = setProgress;
                    }
                });

                // Save the upload object to the queue.
                uploadQueue.push(upload);

                // Process each file.
                for (var i = 0; i < upload.files.length; i++) {
                    // Create a wrapper for the file, with its id, preview and progression. This will be used to display
                    // the progression of the file across pages.
                    var file = {
                        id: (idCounter++) +'-'+ upload.files[i].name,
                        progress: {
                            arcX: 22,
                            arcY: 0,
                            arcFlag: 0
                        },
                        raw: upload.files[i]
                    };

                    // Generate a preview for the file.
                    generatePreview(file);

                    // Replace the original file with the wrapper.
                    upload.files.splice(i, 1, file);

                    // Send the file with its associated data.
                    var formData = {
                        fileId: file.id,
                        filename: file.raw.name
                    };

                    if (typeof target !== 'undefined') {
                        formData.target = target;
                    }

                    $form.fileupload('send', {
                        files: file.raw,
                        formData: formData
                    });
                }

                updateBindedScopes();
            }

            function progressall(data, entityUri) {
                var upload = filterUploadsByEntity(entityUri),
                    progress = Math.ceil(data.loaded / data.total * 100);

                upload.setProgress(progress);

                // The upload is finished.
                if (progress >= 100) {
                    doneall(upload);
                }
            }

            function progress(data, entityUri) {
                var upload = filterUploadsByEntity(entityUri),
                    progress = Math.ceil(data.loaded / data.total * 100);

                for (var i = 0, file; file = upload.files[i++];) {
                    if (file.id == data.formData.fileId) {
                        // Math.min() avoids the circle to be fully complete (and so, invisible).
                        var degrees = 360 / 100 * Math.min(progress, 99.99) - 90;
                        
                        var radians = (degrees % 360) * Math.PI / 180;

                        // Define the points of the SVG progression.
                        file.progress = {
                            arcX: 22 + 22 * Math.cos(radians),
                            arcY: 22 + 22 * Math.sin(radians),
                            arcFlag: +(degrees > 180)
                        };

                        break;
                    }
                }

                updateBindedScopes();
            }

            // Notifies a finished upload and removes it from the queue.
            function doneall(upload) {
                // Once an upload of one or multiple images is finished, the server could return only some of them for
                // an unknown reason, so we must wait a bit with setTimeout() to be sure the server properly handled all
                // the uploaded images and returns them once the reloadImages() function of a binded scope is called.
                setTimeout(function() {
                    notificationService.notify($translate('upload.status.success'));

                    var index = uploadQueue.indexOf(upload);

                    if (index != -1) {
                        uploadQueue.splice(index, 1);
                    }

                    updateBindedScopes(true);
                }, 250);
            }

            // Register listeners.
            $rootScope.$on('upload:bindUploadQueue', addBindedScope);

            return {
                uploadFiles: uploadFiles,
                progressall: progressall,
                progress: progress
            };

        }])

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

        .directive('dropZone', ['$location', 'uploadService', function($location, uploadService) {

            function link(scope, element, attrs, dropZonesCtrl) {
                var $form = $(scope.dropZone.form),
                    entityUri = $location.path();

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

                    add: function(event, data) {
                        dropZonesCtrl.hideContainer(true);
                        uploadService.uploadFiles($form, entityUri, scope.dropZone.multiple, data, scope.dropZone.target);
                    },

                    progressall: function(event, data) {
                        uploadService.progressall(data, entityUri);
                    },

                    progress: function(event, data) {
                        uploadService.progress(data, entityUri);
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
        .directive('imageUpload', function() {

            function controller($scope) {
                // Convert multiple to a boolean.
                $scope.multiple = ($scope.multipleAttr === 'false') ? false : true;
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
                    'multipleAttr': '@multiple',
                    'dropText': '=',
                    'target': '@'
                },
                controller: controller,
                link: link
            };
        })

        /**
         * Simple directive to list the current uploads.
         */
        .directive('uploadList', function() {
            return {
                restrict: 'E',
                templateUrl: '/common/partials/uploadList.html'
            };
        });

})();
