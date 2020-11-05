'use strict';

angular.module('admin').controller('Admin.DocumentUploadPolicyController',
        [ '$scope', 'Admin.DocumentUploadPolicyService', 'MessageService', 'UtilService', function($scope, DocumentUploadPolicyService, MessageService, Util) {


    $scope.documentUploadPolicyConfig = {
        "document.upload.policy.convertHtmlToPdf": false,
        "document.upload.policy.convertMsgToPdf": false,
        "document.upload.policy.convertEmlToPdf": false
    };

    DocumentUploadPolicyService.getDocumentUploadPolicyConfiguration().then(
        function(result) {
            $scope.documentUploadPolicyConfig = result.data;
        }
    );

    $scope.save = function() {
        DocumentUploadPolicyService.saveDocumentUploadPolicyConfiguration($scope.documentUploadPolicyConfig).then(
            function(result) {
                MessageService.succsessAction();
            },
            function(reason) {
                MessageService.errorAction();
            }
        );
    };

}]);