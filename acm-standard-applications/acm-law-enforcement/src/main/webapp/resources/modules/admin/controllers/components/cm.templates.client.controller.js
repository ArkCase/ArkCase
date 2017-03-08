'use strict';

angular.module('admin').controller('Admin.CMTemplatesController', ['$scope', '$modal', 'Admin.CMTemplatesService',
    'Helper.UiGridService', 'MessageService', 'LookupService', 'Acm.StoreService',
    function ($scope, $modal, correspondenceService, HelperUiGridService, messageService, LookupService, Store) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();
        $scope.selectedRows = [];
        $scope.correspondenceManagementTemplateVersions = undefined;
        
        $scope.gridOptions = {
            enableRowSelection: true,
            enableFiltering: false,
            enableRowHeaderSelection: true,
            enableFullRowSelection: true,
            data: [],
            onRegisterApi: function(gridApi) {
                $scope.gridApi = gridApi;
                gridApi.selection.on.rowSelectionChanged($scope, function (row) {
                    $scope.selectedRows = gridApi.selection.getSelectedRows();
                });
 
                gridApi.selection.on.rowSelectionChangedBatch($scope, function (rows) {
                    $scope.selectedRows = gridApi.selection.getSelectedRows();
                });
           }
        };

        //get config and init grid settings
        $scope.config.$promise.then(function (config) {
            $scope.correspondenceManagementTemplateVersions = _.find(config.components, {id: 'correspondenceManagementTemplateVersions'});
            var config = _.find(config.components, {id: 'correspondenceManagementTemplates'});
            $scope.gridOptions.columnDefs = config.columnDefs;
            gridHelper.setUserNameFilter(promiseUsers);
            $scope.config = config;
            ReloadGrid();
        });
        

        $scope.addTemplate = function () {
            var modalScope = $scope.$new();
            modalScope.config = $scope.config;
            var modalInstance = $modal.open({
                scope: modalScope,
                animation: true,
                templateUrl: 'modules/admin/views/components/add-edit-template.modal.client.view.html',
                controller: ['$scope', '$modalInstance', 'Admin.CMTemplatesService', function ($scope, $modalInstance, correspondenceService) {

                    $scope.objectTypes = $scope.config.objectTypes;
                    $scope.selectedFiles = [];
                    $scope.template = {};
                    if ($scope.selectedRows.length > 0) {
                        $scope.template.objectType = $scope.selectedRows[0].objectType;
                        $scope.template.label = $scope.selectedRows[0].label;
                    }

                    $scope.upload = function upload(files) {
                        $scope.selectedFiles = files;
                    };
                    $scope.onClickOk = function (files) {
                        correspondenceService.uploadTemplateWithTimestamp(files).then(
                            function (result) {
                                correspondenceService.getTemplateData($scope.selectedRows.length > 0 ? $scope.selectedRows[0].templateId : $scope.selectedRows.length, result.data[0].name).then(function (template) {
                                    $scope.template.templateId = template.data.templateId;
                                    $scope.template.templateVersion = template.data.templateVersion;
                                    $scope.template.templateVersionActive = template.data.templateVersionActive;
                                    $scope.template.documentType = template.data.documentType;
                                    $scope.template.templateFilename = template.data.templateFilename;
                                    $scope.template.dateFormatString = template.data.dateFormatString;
                                    $scope.template.numberFormatString = template.data.numberFormatString;
                                    //we will activate it by default
                                    $scope.template.activated = true;
                                    
                                    var template = $scope.template;
                                    correspondenceService.saveTemplateData(template).then(function () {
                                        clearCachedForms(template);
                                        messageService.succsessAction();
                                        ReloadGrid();
                                        $modalInstance.close();
                                    }, function () {
                                        messageService.errorAction();
                                    });
                                });
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

        };
        
        $scope.versionTemplate = function () {
                var templateVersionsPromise = correspondenceService.getTemplateVersionData($scope.selectedRows[0].templateId);
                var colDefs = $scope.correspondenceManagementTemplateVersions.columnDefs;

                templateVersionsPromise.then(function (templateVersionData) {
                    var modalInstance = $modal.open({
                        animation: true,
                        templateUrl: 'modules/admin/views/components/cm.template-versions.modal.client.view.html',
                        controller: function ($scope, $modalInstance) {

                            angular.forEach(templateVersionData.data, function (row, index) {
                                row.downloadFileName = correspondenceService.downloadByFilename(row.templateFilename);
                            });
                            
                            $scope.gridOptions = {
                                    enableColumnResizing: true,
                                    enableRowSelection: true,
                                    columnDefs: colDefs,
                                    data: templateVersionData.data
                            };
                            $scope.onClickOk = function () {
                                $modalInstance.dismiss('cancel');
                            };
                        },
                        size: 'lg'
                    });
                });

        }
        
        $scope.deleteTemplate = function () {

                angular.forEach($scope.selectedRows, function (row, index) {
                    correspondenceService.deleteTemplate(row.templateId).then(function () {
                        clearCachedForms(row);
                        ReloadGrid();
                        messageService.succsessAction();
                    }, function () {
                        messageService.errorAction();
                    });
                });
            
        };

        $scope.activate = function (rowEntity) {
            var template = angular.copy(rowEntity);
            template.activated = !rowEntity.activated;
            correspondenceService.saveTemplateData(template).then(function () {
                clearCachedForms(template);
                messageService.succsessAction();
                ReloadGrid();
            }, function () {
                messageService.errorAction();
            });
        };

        function clearCachedForms(template) {
            var cacheConfigMap = new Store.SessionData(LookupService.SessionCacheNames.CONFIG_MAP);
            var configMap = cacheConfigMap.get();
            if (template.objectType == 'CASE_FILE') {
                delete configMap['caseCorrespondenceForms'];
            } else if (template.objectType == 'COMPLAINT') {
                delete configMap['complaintCorrespondenceForms'];
            }
            cacheConfigMap.set(configMap);
        }

        function ReloadGrid() {
            var templatesPromise = correspondenceService.retrieveActiveVersionTemplatesList();
            templatesPromise.then(function (templates) {
                angular.forEach(templates.data, function (row, index) {
                    row.downloadFileName = correspondenceService.downloadByFilename(row.templateFilename);
                });
                $scope.gridOptions.data = templates.data;
                $scope.selectedRows = [];
            });
        }

    }]);
