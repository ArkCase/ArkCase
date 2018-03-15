'use strict';

angular.module('document-details').controller(
        'Document.TranscriptionController',
        [ '$scope', 'DocumentDetails.TranscriptionAppService', 'UtilService', 'MessageService', 'moment',
                function($scope, TranscriptionAppService, Util, MessageService, moment) {

                    $scope.items = [];

                    $scope.$on('document-data', function(event, ecmFile) {
                        var activeVersion = $scope.getEcmFileActiveVersion(ecmFile);
                        if (!Util.isEmpty(activeVersion)) {
                            TranscriptionAppService.getTranscribeObject(activeVersion.id).then(function(res) {
                                $scope.$emit('transcribe-data-model', res.data);
                                $scope.transcribeDataModel = res.data;
                                //format time
                                angular.forEach($scope.transcribeDataModel.transcribeItems, function(v, k) {
                                    var itemHolder = getNewTranscribeItemHolder();
                                    itemHolder.item = v;
                                    var tempTime = moment.duration(v.startTime, 'seconds'); //get the seconds
                                    itemHolder.seconds = moment().seconds(tempTime.seconds()).format('ss');
                                    itemHolder.minutes = moment().minutes(tempTime.minutes()).format('mm');
                                    itemHolder.hours = moment().hours(tempTime.hours()).format('HH');
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