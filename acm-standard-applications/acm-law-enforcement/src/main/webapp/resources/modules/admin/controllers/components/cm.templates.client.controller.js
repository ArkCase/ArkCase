'use strict';

angular.module('admin').controller('Admin.CMTemplatesController', ['$scope', '$modal', 'Admin.CMTemplatesService', 'Helper.UiGridService',
    function ($scope, $modal, correspondenceService, HelperUiGridService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        $scope.upload = function upload(files) {
            $scope.selectedFiles = files;
            correspondenceService.uploadTemplate(files).then(
                function (result) {
                    ReloadGrid();
                }
            );
        };

        //get config and init grid settings
        $scope.config.$promise.then(function (config) {
            var config = _.find(config.components, {id: 'correspondenceManagementTemplates'});
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);

            gridHelper.addButton(config, "edit");
            gridHelper.addButton(config, "delete");
            $scope.config = config;
            ReloadGrid();
        });

        $scope.editRow = function (rowEntity) {
            showModal(rowEntity);
        };

        $scope.deleteRow = function (rowEntity) {
            console.log(rowEntity);
            console.log('delete template.');
            correspondenceService.deleteTemplate(rowEntity.fileName).then(function () {
                ReloadGrid();
            });
        };

        function AddFullPath(data) {
            angular.forEach(data, function (row, index) {
                row.fullPath = correspondenceService.fullDownloadPath(row.path);
            });
        }

        function showModal(template) {

            var modalScope = $scope.$new();
            modalScope.template = template || {};
            modalScope.config = $scope.config;

            var modalInstance = $modal.open({
                scope: modalScope,
                animation: true,
                templateUrl: 'modules/admin/views/components/add-edit-template.modal.client.view.html',
                controller: function ($scope, $modalInstance) {
                    $scope.objectTypes = $scope.config.queryObjectTypes;

                    //TODO: get data for template if existing and set query fields
                    console.log($scope.template);

                    $scope.onClickOk = function () {
                        var template = {};
                        template.objectType = $scope.selectedObjectType;
                        template.query = $scope.query;
                        template.fieldValues = $scope.fieldValues;
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
                        params.selectedObjectType = $scope.selectedObjectType;

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
                            if (selected) {
                                $scope.query = selected.query;
                            }
                        });
                    };
                },
                size: 'md',
                backdrop: 'static'
            });

            modalInstance.result.then(function (template) {
                console.log('saving template data');
                correspondenceService.saveTemplateData(template);
            });
        }

        function ReloadGrid() {
            var templatesPromise = correspondenceService.retrieveTemplatesList();
            templatesPromise.then(function (templates) {
                AddFullPath(templates.data);
                $scope.gridOptions.data = templates.data;
            });
        }
    }
]);
