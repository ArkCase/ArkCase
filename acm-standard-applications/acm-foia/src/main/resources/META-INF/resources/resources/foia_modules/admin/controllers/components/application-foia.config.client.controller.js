'use strict';

angular.module('admin').controller('Admin.FoiaConfigController', ['$scope', '$translate', 'Admin.FoiaConfigService', 'MessageService',
    function($scope, $translate, AdminFoiaConfigService, MessageService){

        $scope.configDataModel = {};

        AdminFoiaConfigService.getFoiaConfig().then(function(response){
            $scope.configDataModel = response.data;
        },function(err){
            MessageService.errorAction();
        });

        $scope.saveChanges = function(){
            AdminFoiaConfigService.saveFoiaConfig($scope.configDataModel).then(function(res){
                MessageService.succsessAction();
            }, function(err){
                MessageService.errorAction();
            });
        };

    }]);