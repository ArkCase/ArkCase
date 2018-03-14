'use strict';

angular.module('document-details').controller(
        'Document.TranscriptionController',
        [
                '$scope',
                'DocumentDetails.TranscriptionAppService',
                'UtilService',
                'MessageService',
                function($scope, TranscriptionAppService, Util, MessageService) {

                    $scope.items = [];

                    $scope.$on('document-data', function(event, ecmFile) {
                        var activeVersion = $scope.getEcmFileActiveVersion(ecmFile);
                        if (!Util.isEmpty(activeVersion)) {
                            TranscriptionAppService.getTranscribeObject(activeVersion.id).then(
                                    function(res) {
                                        $scope.transcribeDataModel = res.data;
                                        //format time
                                        angular.forEach($scope.transcribeDataModel.transcribeItems, function(v, k) {
                                            var itemHolder = $scope.getNewTranscribeItemHolder();
                                            itemHolder.item = v;
                                            itemHolder.seconds = v.startTime % 60 < 10 ? '0' + v.startTime % 60 : v.startTime % 60;
                                            itemHolder.minutes = Math.floor(v.startTime % 3600 / 60) < 10 ? '0'
                                                    + Math.floor(v.startTime % 3600 / 60) : Math.floor(v.startTime % 3600 / 60);
                                            itemHolder.hours = Math.floor(v.startTime / 3600) < 10 ? '0' + Math.floor(v.startTime / 3600)
                                                    : Math.floor(v.startTime / 3600);
                                            $scope.items.push(itemHolder);
                                        });
                                    }, function(err) {
                                        MessageService.error(err.data);
                                    });
                        }
                    });

                    $scope.getEcmFileActiveVersion = function(ecmFile) {
                        var activeVersion = null;
                        if (Util.isEmpty(ecmFile) || Util.isEmpty(ecmFile.activeVersionTag) || Util.isArrayEmpty(ecmFile.versions)) {
                            return null;
                        }

                        angular.forEach(ecmFile.versions, function(version, key) {
                            if (angular.equals(ecmFile.activeVersionTag, version.versionTag)) {
                                activeVersion = version;
                            }
                        });

                        return activeVersion;
                    };

                    $scope.addTranscribeItem = function() {
                        var itemHolder = $scope.getNewTranscribeItemHolder();
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

                    $scope.getNewTranscribeItem = function() {
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

                    $scope.getNewTranscribeItemHolder = function() {
                        return {
                            id : new Date().getTime(),
                            item : $scope.getNewTranscribeItem(),
                            seconds : '00',
                            minutes : '00',
                            hours : '00'
                        };
                    };

                } ]);