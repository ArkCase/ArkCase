'use strict';

angular.module('admin').controller('Admin.PrivacyConfigController', ['$scope', '$translate', 'Admin.PrivacyConfigService', 'MessageService',
    function ($scope, $translate, AdminPrivacyConfigService, MessageService) {

        $scope.configDataModel = {};

        AdminPrivacyConfigService.getPrivacyConfig().then(function (response) {
            $scope.configDataModel = response.data;
        },function(err){
            MessageService.errorAction();
        });

        $scope.saveChanges = function(){
            AdminPrivacyConfigService.savePrivacyConfig($scope.configDataModel).then(function (res) {
                MessageService.succsessAction();
            }, function(err){
                MessageService.errorAction();
            });
        };

    }]);