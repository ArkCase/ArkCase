'use strict';

angular.module('admin').controller('Admin.CMTemplatesController', ['$scope', '$modal', 'Admin.CMTemplatesService',
    'Helper.UiGridService', 'MessageService', 'LookupService', 'Acm.StoreService',
    function ($scope, $modal, correspondenceService, HelperUiGridService, messageService, LookupService, Store) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();


        //get config and init grid settings
        $scope.config.$promise.then(function (config) {
            var config = _.find(config.components, {id: 'correspondenceManagementTemplates'});
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilter(promiseUsers);

            gridHelper.addButton(config, "edit");
            gridHelper.addButton(config, "delete");
            $scope.config = config;
            ReloadGrid();
        });

        $scope.editRow = function (rowEntity) {
            showModal(rowEntity, true);
        };

        $scope.addTemplate = function () {
            showModal(null, false);
        };

        $scope.activate = function (rowEntity) {


            var template = angular.copy(rowEntity);
            template.activated = !rowEntity.activated;
            delete template.fileName;
            correspondenceService.saveTemplateData(template).then(function () {
                clearCachedForms(template);
                messageService.succsessAction();
                ReloadGrid();
            }, function () {
                messageService.errorAction();
            });
        };

        $scope.deleteRow = function (rowEntity) {
            correspondenceService.deleteTemplate(rowEntity.templateFilename).then(function () {
                clearCachedForms(rowEntity);
                ReloadGrid();
                messageService.succsessAction();
            }, function () {
                messageService.errorAction();
            });
        };

        function showModal(row, isEdit) {

            var template = angular.copy(row);

            var modalScope = $scope.$new();
            modalScope.template = template;
            modalScope.config = $scope.config;
            modalScope.isEdit = isEdit;

            var modalInstance = $modal.open({
                scope: modalScope,
                animation: true,
                templateUrl: 'modules/admin/views/components/add-edit-template.modal.client.view.html',
                controller: ['$scope', '$modalInstance', 'Admin.CMTemplatesService', function ($scope, $modalInstance, correspondenceService) {

                    $scope.objectTypes = $scope.config.queryObjectTypes;
                    $scope.query = {};
                    $scope.selectedFiles = [];

                    if ($scope.isEdit) {
                        $scope.query.beanId = $scope.template.correspondenceQueryBeanId;
                        $scope.query.fieldNames = Object.keys($scope.template.templateSubstitutionVariables);
                        $scope.fieldValues = Object.values($scope.template.templateSubstitutionVariables);
                    }
                    else {
                        $scope.template = {};
                    }


                    $scope.upload = function upload(files) {
                        $scope.selectedFiles = files;
                        correspondenceService.uploadTemplate(files).then(
                            function (result) {
                                correspondenceService.getTemplateData($scope.selectedFiles[0].name).then(function (template) {
                                    $scope.template = template.data;
                                    $scope.query.fieldNames = Object.keys($scope.template.templateSubstitutionVariables);
                                    $scope.fieldValues = Object.values($scope.template.templateSubstitutionVariables);
                                });
                            }
                        );
                    };
                    $scope.onClickOk = function () {
                        var template = $scope.template;
                        template.query = $scope.query;
                        template.query.fieldValues = $scope.fieldValues;
                        $modalInstance.close({template: template});
                    };
                    $scope.onClickCancel = function () {
                        $modalInstance.dismiss('cancel');
                    };

                    $scope.clearQueryAndFieldNames = function () {
                        $scope.query = null;
                        $scope.fieldValues = [];
                    };
                    $scope.pickQuerySelect = function () {

                        var params = {};

                        params.config = $scope.config.dialogQuerySelect;
                        params.selectedObjectType = $scope.template.queryType;

                        var modalInstance = $modal.open({
                            templateUrl: "modules/admin/views/components/query-select-picker-modal.client.view.html",
                            controller: ['$scope', '$modalInstance', 'Helper.UiGridService', 'Admin.CMTemplatesService', 'params'
                                , function ($scope, $modalInstance, HelperUiGridService, correspondenceService, params) {
                                    $scope.modalInstance = $modalInstance;
                                    $scope.config = params.config;
                                    $scope.selectedObjectType = params.selectedObjectType;


                                    var gridHelper = new HelperUiGridService.Grid({scope: $scope});
                                    gridHelper.setColumnDefs($scope.config);
                                    gridHelper.setBasicOptions($scope.config);
                                    gridHelper.disableGridScrolling($scope.config);
                                    $scope.gridOptions.enableRowSelection = true;
                                    ReloadData();

                                    $scope.countRows = 0;
                                    $scope.gridOptions.onRegisterApi = function (gridApi) {
                                        $scope.gridApi = gridApi;

                                        gridApi.selection.on.rowSelectionChanged($scope, function (row) {

                                            var selectedRows = $scope.gridApi.selection.getSelectedRows();
                                            $scope.selected = selectedRows[0];
                                            $scope.countRows = selectedRows.length;
                                        });
                                        gridApi.selection.on.rowSelectionChangedBatch($scope, function (row) {
                                            $scope.countRows = $scope.gridApi.selection.getSelectedRows().length;
                                        });
                                    };

                                    $scope.onClickOk = function () {
                                        $modalInstance.close({query: $scope.selected});
                                    };
                                    $scope.onClickCancel = function () {
                                        $modalInstance.dismiss('cancel');
                                    };

                                    function ReloadData() {
                                        var querySelectPromise = correspondenceService.retrieveQuerySelectList($scope.selectedObjectType);
                                        querySelectPromise.then(function (queries) {
                                            $scope.gridOptions.data = queries.data;
                                        });
                                    }

                                }],
                            animation: true,
                            size: 'lg',
                            backdrop: 'static',
                            resolve: {
                                params: function () {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function (selected) {
                            $scope.clearQueryAndFieldNames();
                            $scope.query = selected.query;
                        });
                    };
                }],
                size: 'md',
                backdrop: 'static'
            });

            modalInstance.result.then(function (data) {
                var template = {};
                template.documentType = data.template.documentType;
                template.templateFilename = data.template.templateFilename;
                template.dateFormatString = data.template.dateFormatString;
                template.numberFormatString = data.template.numberFormatString;
                template.displayName = data.template.displayName;
                template.activated = data.template.activated;
                template.queryType = data.template.queryType;
                template.correspondenceQueryBeanId = data.template.query.beanId;

                var tempFields = {};
                var n = data.template.query.fieldNames.length;
                var fieldNames = data.template.query.fieldNames;
                var fieldValues = data.template.query.fieldValues;
                for (var i = 0; i < n; i++) {
                    tempFields[fieldNames[i]] = "";
                    if (fieldValues[i]) {
                        tempFields[fieldNames[i]] = fieldValues[i];
                    }
                }
                template.templateSubstitutionVariables = tempFields;

                correspondenceService.saveTemplateData(template).then(function () {
                    clearCachedForms(template);
                    messageService.succsessAction();
                    ReloadGrid();
                }, function () {
                    messageService.errorAction();
                });
            });

        }

        function clearCachedForms(template) {
            var cacheConfigMap = new Store.SessionData(LookupService.SessionCacheNames.CONFIG_MAP);
            var configMap = cacheConfigMap.get();
            if (template.queryType == 'CASE_FILE') {
                delete configMap['caseCorrespondenceForms'];
            } else if (template.queryType == 'COMPLAINT') {
                delete configMap['complaintCorrespondenceForms'];
            }
            cacheConfigMap.set(configMap);
        }

        function ReloadGrid() {
            var templatesPromise = correspondenceService.retrieveTemplatesList();
            templatesPromise.then(function (templates) {
                angular.forEach(templates.data, function (row, index) {
                    row.downloadFileName = correspondenceService.downloadByFilename(row.templateFilename);
                });
                $scope.gridOptions.data = templates.data;
            });
        }

    }]);
