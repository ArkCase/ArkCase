'use strict';

angular.module('cases').controller('Cases.HoldReasonModalController', [ '$scope', '$translate', '$modalInstance', 'params', 'Object.LookupService', function($scope, $translate, $modalInstance, params, ObjectLookupService) {

    $scope.tollingFlag = params.tollingFlag;
    
    ObjectLookupService.getLookupByLookupName("holdStatus").then(function(data) {
        if($scope.tollingFlag){
            var tollingElement = _.find(data, {
                key: 'tolling'
             });
            _.remove(data,  function(element) {
                return element === tollingElement;
              });
        }
        $scope.statusOptions = data;
        $scope.status = data[0].key;
    });

    $scope.onClickOk = function() {
        var statusElement = _.find($scope.statusOptions, {
                key: $scope.status
             });
        if ($scope.status === 'tolling') {
            $modalInstance.close({
              holdReason:$scope.holdReason,
              status: $translate.instant(statusElement.value),
              isSelectedTolling: true});
        } else {
          $modalInstance.close({
              holdReason:$scope.holdReason,
              status: $translate.instant(statusElement.value),
              isSelectedTolling: false});
        }
    };

    $scope.onClickCancel = function() {
        $modalInstance.dismiss('cancel');
    };
} ]);