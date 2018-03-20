'use strict';

angular.module('document-details').controller(
        'Document.TranscriptionController',
        [ '$scope', 'DocumentDetails.TranscriptionAppService', 'UtilService', 'MessageService', 'moment', '$modal', 'Admin.TranscriptionManagementService', '$q',
                function($scope, TranscriptionAppService, Util, MessageService, moment, $modal, TranscriptionManagementService, $q) {

                    $scope.items = [];

                    $scope.$on('document-data', function(event, ecmFile) {
                        var isMediaFile = ecmFile.fileActiveVersionMimeType.indexOf("video") === 0 || ecmFile.fileActiveVersionMimeType.indexOf("audio") === 0 ? true : false;
                        if (!isMediaFile){
                            // terminate execution. It's not media file
                            return;
                        }

                        TranscriptionManagementService.getTranscribeConfiguration().then(function (configResult){
                            if (!Util.isEmpty(configResult) && !Util.isEmpty(configResult.data) && configResult.data.enabled){
                                var activeVersion = $scope.getEcmFileActiveVersion(ecmFile);
                                if (!Util.isEmpty(activeVersion)) {
                                    TranscriptionAppService.getTranscribeObject(activeVersion.id).then(function (transcribeResult) {
                                        if (Util.isEmpty(transcribeResult)) {
                                            // Something went wrong
                                            return;
                                        }

                                        $scope.transcribeConfidence = configResult.data.confidence;

                                        $scope.$emit('transcribe-data-model', transcribeResult.data);
                                        $scope.transcribeDataModel = transcribeResult.data;
                                        //format time
                                        angular.forEach($scope.transcribeDataModel.transcribeItems, function (v, k) {
                                            var itemHolder = getNewTranscribeItemHolder();
                                            itemHolder.item = v;
                                            var tempTime = moment.duration(v.startTime, 'seconds'); //get the seconds
                                            itemHolder.seconds = moment().seconds(tempTime.seconds()).format('ss');
                                            itemHolder.minutes = moment().minutes(tempTime.minutes()).format('mm');
                                            itemHolder.hours = moment().hours(tempTime.hours()).format('HH');
                                            $scope.items.push(itemHolder);
                                        });
                                    }, function (transcribeError) {
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
                            templateUrl : "modules/tasks/views/components/task-diagram-modal.client.view.html",
                            controller : 'Tasks.DiagramByProcessIdModalController',
                            windowClass : 'modal-width-80',
                            resolve : {
                                processId : function() {
                                    return $scope.transcribeDataModel.processId;
                                },
                                showLoader : function() {
                                    return true;
                                },
                                showError : function() {
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

                    var getNewTranscribeItem = function() {
                        return {
                            id : null,
                            transcribe : null,
                            startTime : 0,
                            endTime : 0,
                            confidence : 100,
                            corrected : false,
                            text : '',
                            creator : null,
                            created : null,
                            modifier : null,
                            modified : null,
                            className : 'com.armedia.acm.services.transcribe.model.TranscribeItem',
                            objectType : 'TRANSCRIBE_ITEM'
                        };
                    };

                    var getNewTranscribeItemHolder = function() {
                        return {
                            id : new Date().getTime(),
                            item : getNewTranscribeItem(),
                            seconds : '00',
                            minutes : '00',
                            hours : '00'
                        };
                    };

                } ]);