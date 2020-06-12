'use strict';

angular.module('admin').controller('Admin.WorkflowsConfigDeactivatedModalController',
    ['$scope', '$modalInstance', 'Helper.UiGridService', 'Admin.WorkflowsConfigService', 'ConfigService', 'MessageService', '$translate', function ($scope, $modalInstance, HelperUiGridService, WorkflowsConfigService, ConfigService, MessageService, $translate) {

        $scope.gridOptions = {
            enableColumnResizing: true,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            noUnselect: false,
            totalItems: 0,
            data: []
        };

        var gridHelper = new HelperUiGridService.Grid({
            scope: $scope
        });

        var promiseUsers = gridHelper.getUsers();

        ConfigService.getComponentConfig("admin", "workflowsDeactivatedHistoryDialogConfig").then(function (componentConfig) {
            $scope.workflowsDeactivatedHistoryDialogConfig = componentConfig;
            onConfigRetrieved(componentConfig);
            return componentConfig;
        });


        var onConfigRetrieved = function (config) {
            $scope.config = config;
            //first the filter is set, and after that everything else,
            //so that the data loads with the new filter applied
            gridHelper.setUserNameFilterToConfig(promiseUsers).then(function (updatedConfig) {
                $scope.config = updatedConfig;
                if ($scope.gridApi != undefined) {
                    $scope.gridApi.core.refresh();
                }
                gridHelper.setColumnDefs(updatedConfig);
                gridHelper.setBasicOptions(updatedConfig);
                gridHelper.disableGridScrolling(updatedConfig);
            });

            reloadGrid()
        };

        function reloadGrid() {
            WorkflowsConfigService.retrieveDeactivatedWorkflows().then(function (deactivatedWorkflows) {
                $scope.deactivatedWorkflows = deactivatedWorkflows.data;
                $scope.gridOptions = $scope.gridOptions || {};
                $scope.gridOptions.data = $scope.deactivatedWorkflows;
            });
        }

        $scope.activateWorkflow = function (rowEntity) {
            var template = angular.copy(rowEntity);
            gridHelper.deleteRow(rowEntity);
            WorkflowsConfigService.activate(template.key, template.version).then(function () {
                MessageService.info($translate.instant('admin.workflows.config.activate.success'));
            }, function () {
                MessageService.error($translate.instant('admin.workflows.config.activate.error'));
            });
        };

        $scope.onClickClose = function () {
            $modalInstance.close();
        };
    }]);
