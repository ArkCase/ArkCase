'use strict';

angular.module('consultations').controller('Consultations.BillingModalController', ['$scope', '$modalInstance', 'params',
    function($scope, $modalInstance, params){

        $scope.objectDataModel = {};

        $scope.amount = "";

        $scope.objectDataModel.itemNumber = params.gridData.length+1;

        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };

        $scope.save = function(){
            $modalInstance.close({
                objectDataModel: $scope.objectDataModel
            });
        };

    }]);