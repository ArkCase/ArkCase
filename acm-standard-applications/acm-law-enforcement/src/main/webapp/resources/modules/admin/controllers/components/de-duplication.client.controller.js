'use strict';

angular.module('admin').controller('Admin.DeDuplicationController',
    ['$scope', 'Admin.DeDuplicationConfigurationService', 'UtilService', 'MessageService', function ($scope, DeDuplicationConfigurationService, Util, MessageService) {

        $scope.deDuplication = {
            enableDeDuplication : false
        };

        DeDuplicationConfigurationService.getDeDuplicationConfiguration().then(function (response) {
            if (!Util.isEmpty(response.data)) {
                $scope.duplication = response.data;
                $scope.deDuplication.enableDeDuplication = $scope.duplication['enableDeDuplication'];
            }
        });

        $scope.applyChanges = function () {
            DeDuplicationConfigurationService.saveDeDuplicationConfiguration($scope.deDuplication).then(function (response) {
                MessageService.succsessAction();
            }, function (reason) {
            })
        };
    }]);
