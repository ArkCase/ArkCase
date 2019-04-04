'use strict';

angular.module('admin').controller('Admin.AWSTranscriptionManagementController', [ '$scope', 'Admin.AWSTranscriptionManagementService', 'ConfigService', 'MessageService', function($scope, AWSTranscriptionManagementService, ConfigService, MessageService) {

    $scope.isLoading = false;
    $scope.AWSConfigDataModel = {};
    $scope.awsTranscribeConfiguration = {};
    $scope.credentialsConfiguration = {};

    AWSTranscriptionManagementService.getAWSTranscribeConfiguration().then(function(res) {
        $scope.AWSConfigDataModel = res.data;
        $scope.awsTranscribeConfiguration = res.data.awsTranscribeConfiguration;
        $scope.credentialsConfiguration = res.data.credentialsConfiguration;
    }, function(err) {
        MessageService.error(err.data);
    });

    $scope.saveChanges = function() {
        $scope.isLoading = true;
        AWSTranscriptionManagementService.saveAWSTranscribeConfiguration($scope.AWSConfigDataModel).then(function(res) {
            $scope.isLoading = false;
            MessageService.succsessAction();
        }, function(err) {
            $scope.isLoading = false;
            MessageService.errorAction();
        });
    };

} ]);