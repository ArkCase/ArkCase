'use strict';

angular.module('admin').controller('Admin.WorkflowsConfigController', ['$scope', 'Admin.WorkflowsConfigService', '$modal', '$translate', 'MessageService', '$window', 'Helper.UiGridService',
    function ($scope, workflowsConfigService, $modal, $translate, messageService, $window, HelperUiGridService) {
        $scope.uploadingInProgress = false;
        $scope.loadingProgress = 0;
        $scope.selectedBPMNFile = null;
        $scope.workflowsHistoryDialogConfig = undefined;
        
        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();
        
        var onConfigRetrieved = function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);            
            gridHelper.setUserNameFilter(promiseUsers);            
        };

        //get config and init grid settings
        $scope.config.$promise.then(function (config) {
            $scope.workflowsHistoryDialogConfig = _.find(config.components, {id: 'workflowsHistoryDialogConfig'});

            var componentConfig = _.find(config.components, {id: 'workflowsConfig'});
            var columnDefs = componentConfig.columnDefs;

            columnDefs.push(getActionsColumn());

            $scope.gridOptions.columnDefs = columnDefs;

            onConfigRetrieved(componentConfig);
            
            reloadGrid();
        });

        $scope.openProcessModeler = function()
        {
            var goToUrl = $translate.instant('admin.workflows.config.btn.createNewUrl');
            $window.open(goToUrl);
        }

        $scope.replaceFile = function (file, entity) {
            //TODO add logic for replace file, now only uploads the file as new.
            if (file) {
                $scope.uploadDefinition(file);
            }
        };

        $scope.diagram = function (entity) {
            var modalInstance = $modal.open({
                templateUrl: "modules/admin/views/components/workflows.config.diagram-modal.client.view.html",
                controller: 'Admin.WorkflowsConfigDiagramController',
                windowClass: 'modal-width-80',
                resolve:{
                    deploymentId: function(){
                        return entity.deploymentId;
                    },
                    key: function(){
                        return entity.key;
                    },
                    version: function(){
                        return entity.version;
                    },
                    showLoader: function() {
                        return true;
                    },
                    showError: function() {
                        return false;
                    }
                }
            });
            modalInstance.result.then(function (result) {
                if (result) {
                    // Do nothing
                }
            });
        };

        function getActionsColumn() {
            return {
                "name": "active",
                "displayName": "admin.workflows.config.grid.active",
                "width": 100,
                "enableSorting": false,
                "visible": true,
                "headerCellFilter": "translate",
                "cellTemplate": "<a target='_blank'" +
                " href='api/latest/plugin/admin/workflowconfiguration/workflows/{{row.entity.key}}/versions/{{row.entity.version}}/file' class='inline animated btn btn-default btn-xs'><i class='fa fa-download'></i></a>"
                + "<a ng-disabled='uploadingInProgress' ng-click='grid.appScope.uploadReplaceBpmn()' href='' class='inline animated btn btn-default btn-xs'><i" +
                " class='fa fa-upload'></i></a>"
                + "<a ng-click='grid.appScope.showHistory(row.entity)' href='' class='inline animated btn btn-default btn-xs'><i class='fa fa-retweet'></i></a>"
                + "<a ng-click='grid.appScope.diagram(row.entity)' href='' class='inline animated btn btn-default btn-xs'><i class='fa fa-sitemap'></i></a>"
            }
        }

        function reloadGrid() {
            var workflowsPromise = workflowsConfigService.retrieveWorkflows();
            workflowsPromise.then(function (templates) {
                $scope.gridOptions.data = templates.data;
            });
        }

        $scope.uploadDefinition = function (file, description) {
            if (file) {
                $scope.uploadingInProgress = true;
                workflowsConfigService.uploadDefinition(file, description).then(
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
        
        //dialog for upload/replace BPMN
        $scope.uploadReplaceBpmn = function () {
            var modalScope = $scope.$new();
            modalScope.config = $scope.config;
            var modalInstance = $modal.open({
                scope: modalScope,
                animation: true,
                templateUrl: 'modules/admin/views/components/workflows.config.upload-replace.modal.client.view.html',
                controller: ['$scope', '$modalInstance', function ($scope, $modalInstance) {
                    
                    $scope.selectedFiles = [];
                    
                    $scope.upload = function upload(files) {
                        $scope.selectedFiles = files;
                    };
                   
                    $scope.onClickOk = function (files) {
                    	 $modalInstance.close(
                                 {
                                     selectedFiles:$scope.selectedFiles,
                                     description : $scope.bpmn.description
                                 }
                             );
                    };
                    $scope.onClickCancel = function () {
                        $modalInstance.dismiss('cancel');
                    };

                }],
                size: 'md',
                backdrop: 'static'
            });
            
            modalInstance.result.then(function (data) {
            	$scope.uploadDefinition(data.selectedFiles, data.description);            	
            });        
        }


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
                cellTemplate: "<input type='radio' name='activeBPMN' ng-model='grid.appScope.activeBPMN' ng-value='{{row.entity}}' ng-change='grid.appScope.changeActive()'/>",
                "headerCellFilter": "translate"
            });
            historyPromise.then(function (payload) {
                if (!payload.data || payload.data.length < 1) {
                    messageService.error($translate.instant('admin.workflows.config.noHistory'));
                    return;
                }
                var params = {};
                params.config = $scope.workflowsHistoryDialogConfig;
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/admin/views/components/workflows.config.show-history.dialog.view.html',
                    controller: ['$scope', '$modalInstance','Helper.UiGridService', 'params', function ($scope, $modalInstance, HelperUiGridService, params) {
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
                        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
                        var promiseUsers = gridHelper.getUsers();
                        
                        var onConfigRetrieved = function (config) {
                            gridHelper.setColumnDefs(config);
                            gridHelper.setBasicOptions(config);
                            gridHelper.disableGridScrolling(config);            
                            gridHelper.setUserNameFilter(promiseUsers);            
                        };
                        
                        onConfigRetrieved(params.config);                                                
                        $scope.gridOptions.data= payload.data;

                        $scope.activate = function () {
                            $modalInstance.close($scope.activeBPMN);
                        };
                        $scope.cancel = function () {
                            $modalInstance.dismiss('cancel');
                        };
                    }],
                    size: 'lg',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
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