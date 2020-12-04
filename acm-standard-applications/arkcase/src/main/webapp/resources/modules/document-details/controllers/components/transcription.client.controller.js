'use strict';

angular.module('document-details').controller('Document.TranscriptionController',
        [ '$scope', 'DocumentDetails.TranscriptionAppService', 'UtilService', 'MessageService', 'moment', '$modal', 'Admin.TranscriptionManagementService', '$translate', function($scope, TranscriptionAppService, Util, MessageService, moment, $modal, TranscriptionManagementService, $translate) {

            $scope.items = [];

            var activeVersion = null;

            $scope.transcribeDataModel = null;

            $scope.readonlyInputs = false;
            $scope.isCompileLoading = false;
            $scope.isTranscribeLoading = false;
            $scope.isSaveLoading = false;
            $scope.isCancelLoading = false;
            $scope.isCompleteLoading = false;

            $scope.$on('document-data', function(event, ecmFile) {
                var isMediaFile = !Util.isEmpty(ecmFile) && (ecmFile.fileActiveVersionMimeType.indexOf("video") === 0 || ecmFile.fileActiveVersionMimeType.indexOf("audio") === 0);
                if (!isMediaFile) {
                    // terminate execution. It's not media file
                    return;
                }

                activeVersion = $scope.getEcmFileActiveVersion(ecmFile);

                TranscriptionManagementService.getTranscribeConfiguration().then(function(configResult) {
                    if (!Util.isEmpty(configResult) && !Util.isEmpty(configResult.data) && configResult.data['transcribe.enabled']) {
                        if (!Util.isEmpty(activeVersion)) {
                            TranscriptionAppService.getTranscription(activeVersion.id).then(function(transcribeResult) {
                                var transcribeRes = Util.omitNg(transcribeResult);
                                if (_.isEmpty(transcribeRes)) {
                                    // Something went wrong
                                    return;
                                }

                                if(transcribeResult.status == 'FAILED')
                                {
                                    TranscriptionManagementService.getTranscriptionFailureReason(transcribeResult.id)
                                        .then(function(response) {
                                            transcribeResult.failureReason = response.data.failureReason;
                                            $scope.$emit('transcribe-data-model', transcribeResult);
                                        });
                                }
                                else {
                                        $scope.transcribeConfidence = configResult.data['transcribe.confidence'];
                                        $scope.$emit('transcribe-data-model', transcribeResult);
                                }

                                $scope.transcribeDataModel = transcribeResult;
                                //disable editing on time inputs
                                if ($scope.transcribeDataModel.type === 'AUTOMATIC') {
                                    $scope.readonlyInputs = true;
                                }
                                //format time
                                angular.forEach($scope.transcribeDataModel.transcribeItems, function(v, k) {
                                    var itemHolder = getNewTranscribeItemHolder();
                                    itemHolder.item = v;
                                    var tempStartTime = moment.duration(v.startTime, 'seconds'); //get the startTime seconds
                                    itemHolder.startTime.seconds = moment().seconds(tempStartTime.seconds()).format('ss');
                                    itemHolder.startTime.minutes = moment().minutes(tempStartTime.minutes()).format('mm');
                                    itemHolder.startTime.hours = moment().hours(tempStartTime.hours()).format('HH');
                                    var tempEndTime = moment.duration(v.endTime, 'seconds'); //get the endTime seconds
                                    itemHolder.endTime.seconds = moment().seconds(tempEndTime.seconds()).format('ss');
                                    itemHolder.endTime.minutes = moment().minutes(tempEndTime.minutes()).format('mm');
                                    itemHolder.endTime.hours = moment().hours(tempEndTime.hours()).format('HH');
                                    $scope.items.push(itemHolder);
                                });
                            }, function(transcribeError) {
                                MessageService.error(transcribeError.data);
                            });
                        }
                    }
                }, function(configError) {
                    MessageService.error(configError.data);
                });
            });

            $scope.getEcmFileActiveVersion = function(ecmFile) {
                if (Util.isEmpty(ecmFile) || Util.isEmpty(ecmFile.activeVersionTag) || Util.isArrayEmpty(ecmFile.versions)) {
                    return null;
                }

                var activeVersion = _.find(ecmFile.versions, function(version) {
                    return ecmFile.activeVersionTag === version.versionTag;
                });

                return activeVersion;
            };

            $scope.addTranscribeItem = function() {
                var itemHolder = getNewTranscribeItemHolder();
                $scope.items.push(itemHolder);
            };

            $scope.removeTranscribeItem = function(itemHolder) {
                if (!Util.isEmpty(itemHolder)) {
                    var index = $scope.items.indexOf(itemHolder);
                    if (index > -1) {
                        $scope.items.splice(index, 1);
                    }
                }
            };

            $scope.diagram = function() {
                var modalInstance = $modal.open({
                    templateUrl: "modules/tasks/views/components/task-diagram-modal.client.view.html",
                    controller: 'Tasks.DiagramByProcessIdModalController',
                    windowClass: 'modal-width-80',
                    resolve: {
                        processId: function() {
                            return $scope.transcribeDataModel.processId;
                        },
                        showLoader: function() {
                            return true;
                        },
                        showError: function() {
                            return false;
                        }
                    }
                });
                modalInstance.result.then(function(result) {
                    if (result) {
                        // Do nothing
                    }
                });
            };

            var createManualTranscription = function(activeVersionId) {
                $scope.isSaveLoading = true;
                TranscriptionAppService.createTranscription($scope.transcribeDataModel).then(function(data) {
                    $scope.isSaveLoading = false;
                    $scope.$emit('transcribe-data-model', data);
                    $scope.transcribeDataModel = data;
                    MessageService.succsessAction();
                }, function(err) {
                    $scope.isSaveLoading = false;
                    MessageService.error(err.data);
                });

            };

            $scope.saveChanges = function() {
                if (!Util.isEmpty(activeVersion)) {
                    if (Util.isEmpty($scope.transcribeDataModel)) {
                        $scope.transcribeDataModel = getNewTranscribe();
                        $scope.transcribeDataModel.mediaEcmFileVersion = activeVersion;
                        $scope.transcribeDataModel.type = 'MANUAL';
                    }
                    $scope.transcribeDataModel.transcribeItems = getTranscribeItemsFromHolders();

                    if (Util.isEmpty($scope.transcribeDataModel.id) && $scope.transcribeDataModel.type === 'MANUAL') { //transcription for the first time
                        bootbox.dialog({
                            message: $translate.instant("documentDetails.comp.transcription.dialog.title"),
                            buttons: {
                                warning: {
                                    label: $translate.instant("documentDetails.comp.transcription.buttons.cancel"),
                                    className: "btn-default",
                                    callback: function() {
                                    }
                                },
                                processing: {
                                    label: $translate.instant("documentDetails.comp.transcription.dialog.processing"),
                                    className: "btn-warning",
                                    callback: function(result) {
                                        if (result) {
                                            $scope.transcribeDataModel.status = 'PROCESSING';
                                            createManualTranscription(activeVersion.id);
                                        }
                                    }
                                },
                                success: {
                                    label: $translate.instant("documentDetails.comp.transcription.dialog.completed"),
                                    className: "btn-success",
                                    callback: function(result) {
                                        if (result) {
                                            $scope.transcribeDataModel.status = 'COMPLETED';
                                            createManualTranscription(activeVersion.id);
                                        }
                                    }
                                }
                            }
                        });
                    } else if (!Util.isEmpty($scope.transcribeDataModel.id)) { //update transcription
                        $scope.isSaveLoading = true;
                        TranscriptionAppService.updateTranscription($scope.transcribeDataModel).then(function(data) {
                            $scope.isSaveLoading = false;
                            $scope.$emit('transcribe-data-model', data);
                            $scope.transcribeDataModel = data;
                            MessageService.succsessAction();
                        }, function(err) {
                            $scope.isSaveLoading = false;
                            MessageService.error(err.data);
                        });
                    }

                }

            };

            var confirmationDialog = function(title, callbackFunction) {
                bootbox.confirm({
                    message: title,
                    buttons: {
                        cancel: {
                            label: $translate.instant("documentDetails.comp.transcription.buttons.cancel")
                        },
                        confirm: {
                            label: $translate.instant("documentDetails.comp.transcription.buttons.ok")
                        }
                    },
                    callback: function(result) {
                        if (result) {
                            callbackFunction();
                        }
                    }
                })
            };

            $scope.complete = function() {
                var completeManualTranscription = function() {
                    if (!Util.isEmpty($scope.transcribeDataModel.id)) {
                        $scope.isCompleteLoading = true;
                        TranscriptionAppService.completeManualTranscription($scope.transcribeDataModel.id).then(function(data) {
                            $scope.isCompleteLoading = false;
                            $scope.$emit('transcribe-data-model', data);
                            $scope.transcribeDataModel = data;
                            MessageService.succsessAction();
                        }, function(err) {
                            $scope.isCompleteLoading = false;
                            MessageService.error(err.data);
                        });
                    }
                };
                confirmationDialog($translate.instant("documentDetails.comp.transcription.dialog.complete.title"), completeManualTranscription);
            };

            $scope.cancel = function() {
                var cancelManualTranscription = function() {
                    if (!Util.isEmpty($scope.transcribeDataModel.id)) {
                        $scope.isCancelLoading = true;
                        TranscriptionAppService.cancelManualTranscription($scope.transcribeDataModel.id).then(function(data) {
                            $scope.isCancelLoading = false;
                            $scope.$emit('transcribe-data-model', data);
                            $scope.transcribeDataModel = data;
                            MessageService.succsessAction();
                        }, function(err) {
                            $scope.isCancelLoading = false;
                            MessageService.error(err.data);
                        });
                    }
                };
                confirmationDialog($translate.instant("documentDetails.comp.transcription.dialog.cancel.title"), cancelManualTranscription);
            };

            $scope.transcribe = function() {
                var startAutomaticTranscription = function() {
                    if (!Util.isEmpty(activeVersion)) {
                        $scope.isTranscribeLoading = true;
                        TranscriptionAppService.startAutomaticTranscription(activeVersion.id).then(function(data) {
                            $scope.isTranscribeLoading = false;
                            $scope.$emit('transcribe-data-model', data);
                            $scope.transcribeDataModel = data;
                            MessageService.succsessAction();
                        }, function(err) {
                            $scope.isTranscribeLoading = false;
                            MessageService.error(err.data);
                        });
                    }
                };

                if (Util.isArrayEmpty($scope.items)) {
                    startAutomaticTranscription();
                } else {
                    confirmationDialog($translate.instant("documentDetails.comp.transcription.dialog.transcribe.title"), startAutomaticTranscription);
                }

            };

            $scope.compile = function() {
                var compileTranscription = function() {
                    if (!Util.isEmpty($scope.transcribeDataModel.id)) {
                        $scope.isCompileLoading = true;
                        TranscriptionAppService.compileTranscription($scope.transcribeDataModel.id).then(function(data) {
                            $scope.isCompileLoading = false;
                            MessageService.succsessAction();
                            $scope.transcribeDataModel.transcribeEcmFile = data;
                            $scope.$emit('transcribe-data-model', $scope.transcribeDataModel);
                        }, function(err) {
                            MessageService.error(err.data);
                            $scope.isCompileLoading = false;
                        });
                    }
                };
                if ($scope.transcribeDataModel.transcribeEcmFile === null) {
                    compileTranscription();
                } else {
                    confirmationDialog($translate.instant("documentDetails.comp.transcription.dialog.compile.title"), compileTranscription);
                }
            };

            var getTranscribeItemsFromHolders = function() {
                if (!Util.isArrayEmpty($scope.items)) {
                    var transcribeItems = [];
                    angular.forEach($scope.items, function(v, k) {
                        var item = getNewTranscribeItem();
                        var startTimeSeconds = Number(v.startTime.seconds);
                        var startTimeMinutes = Number(v.startTime.minutes);
                        var startTimeHours = Number(v.startTime.hours);
                        item.startTime = startTimeSeconds + startTimeMinutes * 60 + startTimeHours * 3600;
                        if (Util.isEmpty(v.endTime)) {
                            item.endTime = 0;
                        } else {
                            var endTimeSeconds = Number(v.endTime.seconds);
                            var endTimeMinutes = Number(v.endTime.minutes);
                            var endTimeHours = Number(v.endTime.hours);
                            item.endTime = endTimeSeconds + endTimeMinutes * 60 + endTimeHours * 3600;
                        }

                        item.text = v.item.text;
                        item.confidence = v.item.confidence;
                        item.corrected = v.item.corrected;

                        transcribeItems.push(item);
                    });

                    return transcribeItems;
                }

                return null;
            };

            var getNewTranscribe = function() {
                return {
                    id: null,
                    remoteId: null,
                    type: null,
                    language: null,
                    transcribeItems: null,
                    mediaEcmFileVersion: null,
                    status: null,
                    processId: null,
                    wordCount: 0,
                    confidence: 0,
                    creator: null,
                    created: null,
                    modifier: null,
                    modified: null,
                    className: 'com.armedia.acm.services.transcribe.model.Transcribe',
                    objectType: 'TRANSCRIBE'
                };
            };

            var getNewTranscribeItem = function() {
                return {
                    id: null,
                    transcribe: null,
                    startTime: 0,
                    endTime: 0,
                    confidence: 100,
                    corrected: false,
                    text: '',
                    creator: null,
                    created: null,
                    modifier: null,
                    modified: null,
                    className: 'com.armedia.acm.services.transcribe.model.TranscribeItem',
                    objectType: 'TRANSCRIBE_ITEM'
                };
            };

            var getNewTranscribeItemHolder = function() {
                return {
                    id: new Date().getTime(),
                    item: getNewTranscribeItem(),
                    startTime: {
                        seconds: '00',
                        minutes: '00',
                        hours: '00'
                    },
                    endTime: {
                        seconds: '00',
                        minutes: '00',
                        hours: '00'
                    }
                };
            };

        } ]);