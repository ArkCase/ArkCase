'use strict';

angular.module('admin').controller('Admin.SequenceManagementResetDetailsController', [ '$scope', '$rootScope', '$modalInstance', 'params', 'Util.DateService', 'moment', 'Admin.SequenceManagementResetService', 'MessageService',  function($scope, $rootScope, $modalInstance, params, UtilDateService, moment, SequenceManagementResetService, MessageService) {

    $scope.config = angular.copy(params.config);
    $scope.data = angular.copy(params.row);

    if($scope.data.newReset){
        $scope.resetStartDate = "";
    }else{
        var date = $scope.data.resetDate ? moment($scope.data.resetDate).format(UtilDateService.defaultDateTimeFormat) : "";
        $scope.resetStartDate = new Date(date);    
    }
    
    $scope.resetRepeatableFlag = $scope.data.resetRepeatableFlag;
    $scope.resetRepeatablePeriod = $scope.data.resetRepeatablePeriod;
    
    $scope.viewMode = $scope.data.resetExecutedDate ? true : false;

    $scope.onClickSave = function(){
        var sequenceReset = {};
        sequenceReset.resetDate = UtilDateService.dateToIsoDateTime($scope.resetStartDate);
        sequenceReset.resetRepeatableFlag = $scope.resetRepeatableFlag;
        sequenceReset.resetRepeatablePeriod = $scope.resetRepeatablePeriod;
        sequenceReset.sequenceName = params.sequenceName;
        sequenceReset.sequencePartName = params.sequencePartName;

        SequenceManagementResetService.saveSequenceReset(sequenceReset).success(function () {
            MessageService.succsessAction();
            $rootScope.$broadcast('reset-sequence-added', sequenceReset);
            $modalInstance.dismiss();
        }).error(function () {
            MessageService.errorAction();
        })
    };
    
    $scope.onClickCancel = function() {
        $modalInstance.dismiss('Cancel');
    };

} ]);