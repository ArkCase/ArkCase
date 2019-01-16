'use strict';

angular.module('admin').controller('Admin.SequenceManagementResetDetailsController', [ '$scope', '$rootScope', '$modalInstance', 'params', 'Util.DateService', 'moment', 'Admin.SequenceManagementResetService', 'MessageService', 'Object.LookupService', '$q', function($scope, $rootScope, $modalInstance, params, UtilDateService, moment, SequenceManagementResetService, MessageService, ObjectLookupService, $q) {

    $scope.config = angular.copy(params.config);
    $scope.data = angular.copy(params.row);
    
    var resetPeriod = ObjectLookupService.getResetRepeatPeriod();
    
    $q.all([resetPeriod]).then(function (data) {
        var resetPeriods = data[0];
        $scope.resetRepeatPeriods = resetPeriods;
    

    if($scope.data.newReset){
        $scope.resetStartDate = "";
    }else{
        var date = $scope.data.resetDate ? moment($scope.data.resetDate).format(UtilDateService.defaultDateLongTimeFormat) : "";
        $scope.resetStartDate = new Date(date);    
    }
    
    $scope.resetRepeatableFlag = $scope.data.resetRepeatableFlag;
    $scope.resetRepeatablePeriod = $scope.data.resetRepeatablePeriod;
    $scope.enableRepeatPeriod = $scope.resetRepeatableFlag;
    $scope.viewMode = $scope.data.resetExecutedDate ? true : false;
    $scope.isEdit = params.isEdit;
    
    $scope.enableSave = true;
    
    });
    
    $scope.showRepeatResetPeriod = function() {
        $scope.showDaily = $scope.resetRepeatPeriod;
    };
    
    $scope.showRepeatablePeriod = function(){
      $scope.enableRepeatPeriod = $scope.resetRepeatableFlag;
    };
    
    $scope.dateSelected = function() {
      $scope.enableSave = $scope.resetStartDate == "" ? true : false;  
    };
    
    $scope.onClickSave = function(){
        var sequenceReset = {};
        sequenceReset.resetDate = UtilDateService.dateToIsoDateTime($scope.resetStartDate);
        sequenceReset.resetRepeatableFlag = $scope.resetRepeatableFlag;
        sequenceReset.resetRepeatablePeriod = $scope.resetRepeatablePeriod ? $scope.resetRepeatablePeriod : $scope.showDaily;
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