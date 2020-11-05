'use strict';

angular.module('admin').controller('Admin.ResetConfigurationController',
    [ '$scope', '$translate', 'MessageService', 'Admin.ResetConfigurationService', function($scope, $translate, MessageService, ResetConfigurationService) {

        $scope.resetConfigurationConfirm = function deleteUserConfirm() {
            bootbox.confirm({
                message: $translate.instant("admin.application.resetConfiguration.modal.body"),
                buttons: {
                    confirm:{
                        label: $translate.instant("admin.application.resetConfiguration.modal.btnConfirm.title"),
                        className: "btn btn-danger"
                    },
                    cancel: {
                        label: $translate.instant("admin.application.resetConfiguration.modal.btnCancel.title")
                    }
                },
                callback: function(result){
                    if (result) {
                        resetConfiguration();
                    }
                }
            })
        };

        function resetConfiguration () {
            ResetConfigurationService.resetConfiguration().then(function() {
                MessageService.succsessAction();
            }, function() {
                MessageService.errorAction();
            });
        };

    }]);