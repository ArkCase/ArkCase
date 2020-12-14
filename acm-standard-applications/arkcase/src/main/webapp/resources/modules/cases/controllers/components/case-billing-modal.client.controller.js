'use strict';

angular.module('cases').controller('Cases.BillingModalController', ['$scope', '$modalInstance', 'params',
    function($scope, $modalInstance, params){

        $scope.objectDataModel = {};

        $scope.amount = "";

        $scope.objectDataModel.itemNumber = params.gridData.length+1;

        $scope.objectDataModel.itemType = "Adhoc";

        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };

        $scope.save = function(){
            $modalInstance.close({
                objectDataModel: $scope.objectDataModel
            });
        };

    }]);