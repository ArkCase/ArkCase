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
               item.resetDate = item.resetDate ? moment.utc(item.resetDate).local().format(UtilDateService.defaultDateLongTimeFormat) : "";
               item.resetExecutedDate = item.resetExecutedDate ? moment.utc(item.resetExecutedDate).local().format(UtilDateService.defaultDateLongTimeFormat) : "";
               resetList.push(item);
            });
            $scope.gridOptions.data = resetList;
        });
    };

    gridHelper.addButton($scope.config, "edit", null, "autoIncrementReset", "isButtonDisabled");
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

    $scope.autoIncrementReset = function(rowEntity) {
        var params = {};
        params.row = rowEntity;
        params.sequenceName = $scope.sequenceName;
        params.sequencePartName = $scope.sequencePart;
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
    }

    $scope.addNew = function(){
        var reset = {};
        reset.newReset = true;
        $scope.autoIncrementReset(reset);
    };

    $scope.deleteReset = function(rowEntity) {
        var sequenceReset = {};
        sequenceReset.resetDate = rowEntity.resetDate;
        sequenceReset.resetExecutedDate = rowEntity.resetExecutedDate;
        sequenceReset.resetExecutedFlag = rowEntity.resetExecutedFlag;
        sequenceReset.resetRepeatableFlag = rowEntity.resetRepeatableFlag;
        sequenceReset.resetRepeatablePeriod = rowEntity.resetRepeatablePeriod;
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