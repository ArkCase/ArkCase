'use strict';

angular.module('admin').controller('Admin.TranscriptionManagementController', [ '$scope', 'Admin.TranscriptionManagementService', 'ConfigService', 'MessageService', function($scope, TranscriptionManagementService, ConfigService, MessageService) {

    $scope.isLoading = false;
    $scope.transcribeConfigDataModel = {};
    $scope.transcriptionForNewVersion = {};

    TranscriptionManagementService.getTranscribeConfiguration().then(function(res) {
        $scope.transcribeConfigDataModel = res.data;
        $scope.transcriptionForNewVersion.value = res.data.newTranscriptionForNewVersion;
    }, function(err) {
        MessageService.error(err.data);
    });

    $scope.saveChanges = function() {
        $scope.isLoading = true;
        $scope.transcribeConfigDataModel.newTranscriptionForNewVersion = $scope.transcriptionForNewVersion.value;
        $scope.transcribeConfigDataModel.copyTranscriptionForNewVersion = !$scope.transcribeConfigDataModel.newTranscriptionForNewVersion;
        TranscriptionManagementService.saveTranscribeConfiguration($scope.transcribeConfigDataModel).then(function(res) {
            $scope.isLoading = false;
            MessageService.succsessAction();
        }, function(err) {
            $scope.isLoading = false;
            MessageService.errorAction();
        });
    };

    $scope.resetMode = function () {
        if(!$scope.transcribeConfigDataModel.enabled)
        {
            $scope.transcribeConfigDataModel.automaticEnabled = false;
        }
    }

} ]);