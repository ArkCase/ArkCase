'use strict';

angular.module('admin').controller('Admin.SequenceManagementResetController', [ '$scope', '$rootScope', '$modal', '$modalInstance', 'params', 'Helper.UiGridService', 'Admin.SequenceManagementResetService', 'MessageService', 'Util.DateService', function($scope, $rootScope, $modal, $modalInstance, params, HelperUiGridService, SequenceManagementResetService, MessageService, UtilDateService) {

    var gridHelper = new HelperUiGridService.Grid({
        scope: $scope
    });

    $scope.config = angular.copy(params.config);

    $scope.sequenceName = params.sequenceName;
    $scope.sequencePart = params.sequencePartName;

    $rootScope.$on('reset-sequence-added', function() {
        reloadGrid();
    });

    function reloadGrid() {
        SequenceManagementResetService.getSequenceReset(params.sequenceName, params.sequencePartName).then(function(response){
            var resetList = [];
            _.forEach(response.data, function (item) {
               item.resetDate = item.resetDate ? item.resetDate : "";
               item.resetExecutedDate = item.resetExecutedDate ? item.resetExecutedDate : "";
               if(item.resetRepeatablePeriod == -1){
                   item.resetRepeatablePeriod = 'WEEKLY';
               }else if(item.resetRepeatablePeriod == -2){
                   item.resetRepeatablePeriod = 'MONTHLY';
               }else if(item.resetRepeatablePeriod == -3){
                   item.resetRepeatablePeriod = 'YEARLY';
               }
               resetList.push(item);
            });
            $scope.gridOptions.data = resetList;
        });
    };

    gridHelper.addButton($scope.config, "edit", null, "editRow", "isButtonDisabled");
    gridHelper.addButton($scope.config, "delete", null, "deleteReset", "isButtonDisabled");
    gridHelper.addButton($scope.config, "view", "fa fa-eye", "autoIncrementReset", "isViewDisabled");
    gridHelper.setColumnDefs($scope.config);
    gridHelper.setBasicOptions($scope.config);
    gridHelper.disableGridScrolling($scope.config);
    reloadGrid();

    $scope.gridOptions = {
        enableColumnResizing: true,
        enableRowSelection: true,
        enableRowHeaderSelection: false,
        multiSelect: false,
        noUnselect: false,
        columnDefs: $scope.config.columnDefs,
        paginationPageSizes: $scope.config.paginationPageSizes,
        paginationPageSize: $scope.config.paginationPageSize,
        data: []
    };


    $scope.isButtonDisabled = function(rowEntity){
        return rowEntity.resetExecutedDate ? true : false;
    }

    $scope.isViewDisabled = function(rowEntity){
        return rowEntity.resetExecutedDate ? false : true;
    }

    $scope.autoIncrementReset = function(rowEntity, isEdit) {
        var params = {};
        params.row = rowEntity;
        params.sequenceName = $scope.sequenceName;
        params.sequencePartName = $scope.sequencePart;
        params.isEdit = isEdit;
        var modalInstance = $modal.open({
            animation: true,
            templateUrl: 'modules/admin/views/components/application-sequence-management-reset-details.modal.client.view.html',
            controller: 'Admin.SequenceManagementResetDetailsController',
            size: 'md',
            backdrop: 'static',
            resolve: {
                params: function() {
                    return params;
                }
            }
        });
        return modalInstance.result;
    };
    
    $scope.editRow = function(rowEntity) {
        if(rowEntity.resetRepeatablePeriod == 'YEARLY'){
            rowEntity.resetRepeatPeriodOption = "-3";
        }else if(rowEntity.resetRepeatablePeriod == 'MONTHLY'){
            rowEntity.resetRepeatPeriodOption = "-2";
        }else if(rowEntity.resetRepeatablePeriod == 'WEEKLY'){
            rowEntity.resetRepeatPeriodOption = "-1";
        }else if(rowEntity.resetRepeatablePeriod == 'DAILY'){
            rowEntity.resetRepeatPeriodOption = "0";
        }
        $scope.autoIncrementReset(rowEntity,true);
    };

    $scope.addNew = function(){
        var reset = {};
        reset.newReset = true;
        $scope.autoIncrementReset(reset, false);
    };

    $scope.deleteReset = function(rowEntity) {
        var sequenceReset = {};
        var resetDate = rowEntity.resetDate;
        sequenceReset.resetDate = resetDate;
        if (rowEntity.resetExecutedDate) {
            var executeDate = rowEntity.resetExecutedDate;
            sequenceReset.resetExecutedDate = executeDate;
        }
        sequenceReset.resetExecutedFlag = rowEntity.resetExecutedFlag;
        sequenceReset.resetRepeatableFlag = rowEntity.resetRepeatableFlag;
        if(rowEntity.resetRepeatablePeriod == 'YEARLY'){
            sequenceReset.resetRepeatablePeriod = -3;
        }else if(rowEntity.resetRepeatablePeriod == 'MONTHLY'){
            sequenceReset.resetRepeatablePeriod = -2;
        }else if(rowEntity.resetRepeatablePeriod == 'WEEKLY'){
            sequenceReset.resetRepeatablePeriod = -1;
        }else{
            sequenceReset.resetRepeatablePeriod = rowEntity.resetRepeatablePeriod;
        }
        sequenceReset.sequenceName = rowEntity.sequenceName;
        sequenceReset.sequencePartName = rowEntity.sequencePartName;

        SequenceManagementResetService.deleteSequenceReset(sequenceReset).then(function () {
            reloadGrid();
            MessageService.succsessAction();
        });
    };

    $scope.onClickCancel = function() {
        $modalInstance.dismiss('Cancel');
    };

} ]);