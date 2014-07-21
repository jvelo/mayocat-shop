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

            function sendUploadDoneEvent(data) {
                $rootScope.$emit("upload:done", data);
            }

            // Updates the uploadQueue for all the binded scopes.
            function sendUploadProgressEvent() {
                $rootScope.$emit("upload:progress", {
                    queue: uploadQueue
                });
            }

            // Processes and uploads files.
            function uploadFiles($form, entityUri, multiple, data, target, id) {
                var upload = {
                    entityUri: entityUri,
                    target: target,
                    id: id,
                    // In "multiple mode", we send all the files. In "mono mode", we send only the first one.
                    files: multiple ? data.files : [data.files[0]],
                    filesUploaded: 0
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
                    // Create a wrapper for the file, with its id, preview and progression. This is used to display the
                    // progression of the file across pages.
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

                sendUploadProgressEvent();
            }

            function progressall(data, entityUri) {
                var upload = filterUploadsByEntity(entityUri),
                    progress = Math.ceil(data.loaded / data.total * 100);

                upload.setProgress(progress);
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
                            arcFlag: +(degrees > 90)
                        };

                        break;
                    }
                }

                sendUploadProgressEvent();
            }

            // Notifies when a file (NOT an upload object) has been uploaded.
            function done(result) {
                var location = result.location,
                    status = result.status,
                    entityUri = result.entityUri,
                    upload = filterUploadsByEntity(entityUri);

                // If all the files of an upload object have been uploaded, remove the latter from the queue.
                if (++upload.filesUploaded >= upload.files.length) {
                    notificationService.notify($translate('upload.status.success'));

                    var index = uploadQueue.indexOf(upload),
                        uri = upload.entityUri,
                        id = upload.id;

                    if (index != -1) {
                        uploadQueue.splice(index, 1);

                        sendUploadDoneEvent({
                            entityUri: entityUri,
                            fileUri: location,
                            id: id
                        });
                    }
                }
            }

            return {
                uploadFiles: uploadFiles,
                progressall: progressall,
                progress: progress,
                done: done
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
                $scope.hasAddons = false;

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

                    // Display the container if there's a least one displayed drop zone.
                    $scope.$apply(function() {
                        angular.forEach($scope.dropZones, function(dropZone) {
                            $scope.visible = $scope.visible || !dropZone.noDropZone;
                        });
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
                    // Is it for an addon?
                    dropZoneScope.isAddon = dropZoneScope.target == 'addon';

                    // If the current drop zone is for an addon, activate the addon layout for the container.
                    if (dropZoneScope.isAddon) {
                        $scope.hasAddons = true;
                    }

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

                // Add the jQuery events only if a drop zone is displayed.
                if (!scope.dropZone.noDropZone) {
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
                }

                // Extend the form with the jQuery file upload plugin.
                $form.fileupload({
                    dropZone: scope.dropZone.noDropZone ? null : element, // Hide the drop zone if asked.
                    pasteZone: null, // For an unknown reason, this feature doesn't work with Mayocat Shop.
                    singleFileUploads: false,
                    url: scope.dropZone.uploadUri,

                    add: function(event, data) {
                        dropZonesCtrl.hideContainer(true);
                        uploadService.uploadFiles(
                            $form,
                            entityUri,
                            scope.dropZone.multiple,
                            data,
                            scope.dropZone.target,
                            scope.dropZone.id
                        );
                    },

                    progressall: function(event, data) {
                        uploadService.progressall(data, entityUri);
                    },

                    progress: function(event, data) {
                        uploadService.progress(data, entityUri);
                    },

                    done: function(event, data) {
                        var status = data.jqXHR.status,
                            location = data.jqXHR.getResponseHeader("Location");
                        uploadService.done({
                            entityUri: entityUri,
                            location: location,
                            status: status
                        });
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

                $scope.getButtonLabel = function() {
                    return $scope.label || 'upload.action.add';
                }
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
                templateUrl: '/common/partials/imageUpload.html?v=2',
                scope: {
                    'uploadUri': '@',
                    'multipleAttr': '@multiple',
                    'noDropZone': '=',
                    'dropTitle': '=',
                    'dropText': '=',
                    'target': '@',
                    'id': '=',
                    'label' : '@'
                },
                controller: controller,
                link: link
            };
        })

        /**
         * Simple directive to list the current uploads.
         */
        .directive('uploadList', ['$rootScope', '$location', function($rootScope, $location) {
            return {
                restrict: 'E',
                scope: {
                    id: '='
                },
                templateUrl: '/common/partials/uploadList.html',
                controller: function ($scope) {
                    $rootScope.$on("upload:progress", function (event, memo) {
                        var queue = memo.queue.filter(function (upload) {
                            return upload.entityUri == $location.path() && $scope.id == upload.id
                        });
                        $scope.uploadQueue = queue;
                    });

                    $rootScope.$on("upload:done", function (event, memo) {
                        $scope.uploadQueue = [];
                    });
                }
            };
        }]);

})();
