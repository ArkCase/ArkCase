'use strict';

angular.module('document-details').controller(
        'Document.TranscriptionController',
        [
                '$scope',
                'DocumentDetails.TranscriptionAppService',
                '$q',
                function($scope, TranscriptionAppService, $q) {

                    $scope.seconds = {};
                    $scope.minutes = {};
                    $scope.hours = {};
                    $scope.items = [];

                    TranscriptionAppService.getTranscribeObject().then(
                            function(res) {
                                $scope.transcribeDataModel = res.data;
                                //format time
                                angular.forEach($scope.transcribeDataModel.transcribeItems, function(v, k) {
                                    var itemHolder = $scope.getNewTranscribeItemHolder();
                                    itemHolder.item = v;
                                    itemHolder.seconds = v.startTime % 60 < 10 ? '0' + v.startTime % 60 : v.startTime % 60;
                                    itemHolder.minutes = Math.floor(v.startTime % 3600 / 60) < 10 ? '0'
                                            + Math.floor(v.startTime % 3600 / 60) : Math.floor(v.startTime % 3600 / 60);
                                    itemHolder.hours = Math.floor(v.startTime / 3600) < 10 ? '0' + Math.floor(v.startTime / 3600) : Math
                                            .floor(v.startTime / 3600);
                                    $scope.items.push(itemHolder);
                                    console.log("value " + v.startTime + "key= " + k.startTime);
                                });
                            }, function(err) {
                                console.log("error");
                            });

                    $scope.addTranscribeItem = function() {
                        var itemHolder = $scope.getNewTranscribeItemHolder();
                        $scope.items.push(itemHolder);
                    };

                    $scope.removeTranscribeItem = function(itemHolder) {
                        if (itemHolder != null) {
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