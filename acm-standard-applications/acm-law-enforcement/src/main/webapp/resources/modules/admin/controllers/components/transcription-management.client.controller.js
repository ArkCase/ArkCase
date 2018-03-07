'use strict';

angular.module('admin').controller(
        'Admin.TranscriptionManagementController',
        [ '$scope', 'Admin.TranscriptionManagementService', 'ConfigService', 'MessageService',
                function($scope, TranscriptionManagementService, ConfigService, MessageService) {

                    $scope.isLoading = false;
                    $scope.transcribeConfigDataModel = {};
                    $scope.transcriptionVersion = "";

                    TranscriptionManagementService.getTranscribeConfiguration().then(function(res) {
                        $scope.transcribeConfigDataModel = res.data;
                    });

                    $scope.saveChanges = function() {
                        $scope.isLoading = true;
                        $scope.transcribeConfigDataModel.newTranscriptionForNewVersion = $scope.transcriptionVersion;
                        $scope.transcribeConfigDataModel.copyTranscriptionForNewVersion = !$scope.transcriptionVersion;
                        TranscriptionManagementService.saveTranscribeConfiguration($scope.transcribeConfigDataModel).then(function(res) {
                            $scope.isLoading = false;
                            MessageService.succsessAction();
                        }, function(err) {
                            $scope.isLoading = false;
                            MessageService.errorAction();
                        });
                    };

                } ]);