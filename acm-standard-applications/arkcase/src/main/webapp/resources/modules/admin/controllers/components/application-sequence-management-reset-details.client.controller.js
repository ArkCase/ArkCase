'use strict';

angular.module('admin').controller('Admin.SequenceManagementResetDetailsController', ['$scope', '$rootScope', '$modalInstance', 'params', 'Util.DateService', 'moment', 'Admin.SequenceManagementResetService', 'MessageService', 'Object.LookupService', '$q', 'Dialog.BootboxService', function ($scope, $rootScope, $modalInstance, params, UtilDateService, moment, SequenceManagementResetService, MessageService, ObjectLookupService, $q, DialogService) {

    $scope.config = angular.copy(params.config);
    var data = angular.copy(params.row);
    $scope.sequenceReset = {};
    var resetPeriod = ObjectLookupService.getResetRepeatPeriod();
    
    $q.all([resetPeriod]).then(function (data) {
        var resetPeriods = data[0];
        $scope.resetRepeatPeriods = resetPeriods;
    });

    if(data.newReset){
        $scope.sequenceReset.resetStartDate = "";
    }else{
        var date = data.resetDate ? moment(data.resetDate).format(UtilDateService.defaultDateLongTimeFormat) : "";
        $scope.sequenceReset.resetStartDate = new Date(date);    
    }
    $scope.sequenceReset.resetRepeatableFlag = data.resetRepeatableFlag;
    $scope.sequenceReset.resetRepeatablePeriod = data.resetRepeatablePeriod ? data.resetRepeatablePeriod : "";
    $scope.enableRepeatPeriod = $scope.sequenceReset.resetRepeatableFlag;
    $scope.viewMode = data.resetExecutedDate ? true : false;
    $scope.isEdit = params.isEdit;
    $scope.sequenceReset.resetRepeatPeriodOption = data.resetRepeatPeriodOption ? data.resetRepeatPeriodOption : "0";
    $scope.enableSave = params.isEdit ? false : true;
    $scope.sequenceReset.sequenceName = params.sequenceName;
    $scope.sequenceReset.sequencePartName = params.sequencePartName;

    $scope.showDaily = $scope.sequenceReset.resetRepeatPeriodOption;
    
    $scope.showRepeatResetPeriod = function() {
        $scope.showDaily = $scope.sequenceReset.resetRepeatPeriodOption;
    };
    
    $scope.showRepeatablePeriod = function(){
      $scope.enableRepeatPeriod = $scope.sequenceReset.resetRepeatableFlag;
    };
    
    $scope.dateSelected = function() {
        $scope.enableSave = $scope.sequenceReset.resetStartDate == "" ? true : false;
        var selectedDate = new Date($scope.sequenceReset.resetStartDate);
        var currentDate = new Date();
        if (moment(selectedDate).isSame(currentDate, 'day')) {
            $scope.sameDateWarning = true;
        }
        else {
            $scope.sameDateWarning = false;
        }
    };
    
    $scope.onClickSave = function(){

        $scope.sequenceReset.resetDate = $scope.sequenceReset.resetStartDate;
        $scope.sequenceReset.resetRepeatablePeriod = $scope.sequenceReset.resetRepeatPeriodOption == "0" ? $scope.sequenceReset.resetRepeatablePeriod : $scope.sequenceReset.resetRepeatPeriodOption;

        SequenceManagementResetService.saveSequenceReset($scope.sequenceReset).success(function () {
            MessageService.succsessAction();
            $rootScope.$broadcast('reset-sequence-added', $scope.sequenceReset);
            $modalInstance.dismiss();
        }).error(function () {
            MessageService.errorAction();
        })
    };
    
    $scope.onClickCancel = function() {
        $modalInstance.dismiss('Cancel');
    };

} ]);