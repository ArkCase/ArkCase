'use strict';

angular.module('admin').controller('Admin.WorkflowsConfigController', ['$scope', 'Admin.WorkflowsConfigService', '$modal', '$translate', 'MessageService',
    function ($scope, workflowsConfigService, $modal, $translate, messageService) {
        $scope.uploadingInProgress = false;
        $scope.loadingProgress = 0;
        $scope.selectedBPMNFile = null;
        $scope.workflowsHistoryDialogConfig = undefined;

        $scope.gridOptions = {
            enableColumnResizing: true,
            enableRowSelection: true,
            enableColumnMenus: false,
            enableRowHeaderSelection: false,
            multiSelect: false,
            noUnselect: false,
            data: []
        };

        //get config and init grid settings
        $scope.config.$promise.then(function (config) {
            $scope.workflowsHistoryDialogConfig = _.find(config.components, {id: 'workflowsHistoryDialogConfig'});

            var componentConfig = _.find(config.components, {id: 'workflowsConfig'});
            var columnDefs = componentConfig.columnDefs;

            columnDefs.push(getActionsColumn());

            $scope.gridOptions.columnDefs = columnDefs;

            reloadGrid();
        });

        $scope.replaceFile = function (file, entity) {
            //TODO add logic for replace file, now only uploads the file as new.
            if (file) {
                $scope.uploadDefinition(file);
            }
        };

        function getActionsColumn() {
            return {
                "name": "active",
                "displayName": "admin.workflows.config.grid.active",
                "width": 300,
                "enableSorting": false,
                "visible": true,
                "headerCellFilter": "translate",
                "cellTemplate": "<a target='_blank' href='proxy/arkcase/api/latest/plugin/admin/workflowconfiguration/workflows/{{row.entity.key}}/versions/{{row.entity.version}}/file'><i class='fa fa-download text-active'>{{'admin.workflows.config.links.download' | translate}}</i></a>"
                + " | " + "<a ng-disabled='uploadingInProgress' ngf-select='grid.appScope.replaceFile($file,row.entity.version)' href=''><i class='fa fa-upload text-active'>{{'admin.workflows.config.links.replaceFile' | translate}}</i></a>"
                + " | " + "<a ng-click='grid.appScope.showHistory(row.entity)' href='' <i class='fa fa-retweet text-active'>{{'admin.workflows.config.links.versionHistory' | translate}}</i></a>"
            }
        }

        function reloadGrid() {
            var workflowsPromise = workflowsConfigService.retrieveWorkflows();
            workflowsPromise.then(function (templates) {
                $scope.gridOptions.data = templates.data;
            });
        }

        $scope.uploadDefinition = function (file) {
            if (file) {
                $scope.uploadingInProgress = true;
                workflowsConfigService.uploadDefinition(file).then(
                    function (result) {
                        reloadGrid();
                        $scope.uploadingInProgress = false;
                        messageService.info($translate.instant('admin.workflows.config.upload.success'));
                    }, function () {
                        $scope.uploadingInProgress = false;
                        messageService.error($translate.instant('admin.workflows.config.upload.error'));
                    }
                );
            }
        };


        //dialog for edit or create new role
        $scope.showHistory = function (entity) {
            var historyPromise = workflowsConfigService.retrieveHistory(entity.key, entity.version);
            var colDefs = $scope.workflowsHistoryDialogConfig.columnDefs;
            colDefs.unshift({
                "name": "active",
                "displayName": "admin.workflows.config.grid.active",
                "visible": true,
                "enableSorting": false,
                width: 100,
                cellTemplate: "<input type='radio' name='activeBPMN' ng-model='grid.appScope.activeBPMN.version' ng-value='{{row.entity.version}}' ng-change='grid.appScope.changeActive()'/>",
                "headerCellFilter": "translate"
            });
            historyPromise.then(function (payload) {
                if (!payload.data || payload.data.length < 1) {
                    messageService.error($translate.instant('admin.workflows.config.noHistory'));
                    return;
                }
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/admin/views/components/workflows.config.show-history.dialog.view.html',
                    controller: function ($scope, $modalInstance) {
                        console.log($scope);
                        //initial values
                        $scope.activeBPMN = undefined;
                        $scope.initialActiveBPMNVersion = undefined;
                        $scope.activateBtnDisabled = true;


                        $scope.changeActive = function () {
                            if ($scope.activeBPMN && $scope.activeBPMN.version != $scope.initialActiveBPMNVersion) {
                                $scope.activateBtnDisabled = false;
                            } else {
                                $scope.activateBtnDisabled = true;
                            }
                        };

                        angular.forEach(payload.data, function (row) {
                            if (row.active) {
                                $scope.activeBPMN = row;
                                $scope.initialActiveBPMNVersion = row.version;
                            }
                        });

                        $scope.gridOptions = {
                            enableColumnResizing: true,
                            enableRowSelection: true,
                            pinSelectionCheckbox: true,
                            enableColumnMenus: false,
                            enableRowHeaderSelection: false,
                            multiSelect: false,
                            noUnselect: false,
                            columnDefs: colDefs,
                            data: payload.data
                        };

                        $scope.activate = function () {
                            $modalInstance.close($scope.activeBPMN);
                        };
                        $scope.cancel = function () {
                            $modalInstance.dismiss('cancel');
                        };
                    },
                    size: 'lg'
                });

                //handle the result
                modalInstance.result.then(function (result) {
                    //button activate
                    workflowsConfigService.activate(result.key, result.version).then(function (payload) {
                        //success
                        messageService.info($translate.instant('admin.workflows.config.activate.success'));
                        reloadGrid();
                    }, function (payload) {
                        //error
                        messageService.error($translate.instant('admin.workflows.config.activate.error'));
                    });
                }, function (result) {
                    //button cancel, nothing to do.
                });

            });
        }
    }
]);