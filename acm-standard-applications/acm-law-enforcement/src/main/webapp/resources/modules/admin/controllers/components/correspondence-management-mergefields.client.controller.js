'use strict';

angular.module('admin').controller('Admin.CMMergeFieldsController',
        [ '$scope', '$rootScope', '$modal', 'Admin.CMMergeFieldsService', 'Helper.UiGridService', 'MessageService', 'LookupService', 'Acm.StoreService', 'Object.LookupService', function($scope, $rootScope, $modal, correspondenceMergeFieldsService, HelperUiGridService, messageService, LookupService, Store, ObjectLookupService) {

            var gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });
            var promiseUsers = gridHelper.getUsers();
            $scope.objectTypes = [];
            $scope.mergingType = {};
            $scope.configVersions = {};

            //get config and init grid settings
            $scope.config.$promise.then(function(config) {
                var configVersions = _.find(config.components, {
                    id: 'correspondenceManagementMergeFieldsVersions'
                });
                var config = _.find(config.components, {
                    id: 'correspondenceManagementMergeFields'
                });
                var promiseCorrespondenceObjectTypes = ObjectLookupService.getCorrespondenceObjectTypes();
                promiseCorrespondenceObjectTypes.then(function(correspondenceObject) {
                    $scope.correspondenceObjectTypes = correspondenceObject;

                    promiseUsers.then(function(data) {
                        gridHelper.setUserNameFilterToConfig(promiseUsers, config);
                        gridHelper.setColumnDefs(config);
                        gridHelper.setBasicOptions(config);
                        gridHelper.disableGridScrolling(config);
                        $scope.config = config;
                        $scope.objectTypes = $scope.correspondenceObjectTypes;
                        $scope.mergingType = $scope.objectTypes[0].key;

                        gridHelper.setUserNameFilterToConfig(promiseUsers, configVersions);
                        $scope.configVersions = configVersions;

                        ReloadGrid();
                    });
                });
            });

            $scope.changeType = function() {
                ReloadGrid();
            }

            $scope.save = function() {
                correspondenceMergeFieldsService.saveMergeFieldsData($scope.gridOptions.data).then(function() {
                    messageService.succsessAction();
                    ReloadGrid();
                }, function() {
                    messageService.errorAction();
                });
            }

            $scope.$on('reloadMergeFieldGrid', function() {
                ReloadGrid();
            });

            $scope.showVersion = function() {
                var modalScope = $scope.$new();
                modalScope.config = $scope.configVersions;
                var mergeFieldVersionsPromise = correspondenceMergeFieldsService.retrieveMergeFieldsVersionsByType($scope.mergingType);

                mergeFieldVersionsPromise.then(function(mergeFieldVersionsData) {
                    var modalInstance = $modal.open({
                        scope: modalScope,
                        animation: true,
                        templateUrl: 'modules/admin/views/components/correspondence-management-mergefield-versions.modal.client.view.html',
                        controller: function($scope, $modalInstance) {

                            $scope.gridOptions = {
                                enableColumnResizing: true,
                                enableRowSelection: true,
                                pinSelectionCheckbox: false,
                                enableColumnMenus: false,
                                enableRowHeaderSelection: false,
                                multiSelect: false,
                                noUnselect: false,
                                columnDefs: $scope.config.columnDefs,
                                paginationPageSizes: $scope.config.paginationPageSizes,
                                paginationPageSize: $scope.config.paginationPageSize,
                                data: mergeFieldVersionsData.data,
                                onRegisterApi: function(gridApi) {
                                    $scope.modalGridApi = gridApi;
                                }
                            };

                            $scope.onClickLoadVersion = function() {
                                var selectedRow = $scope.modalGridApi.selection.getSelectedRows();
                                if (selectedRow.length == 1) {
                                    correspondenceMergeFieldsService.setActiveMergingVersion(selectedRow[0]).then(function() {
                                        messageService.succsessAction();
                                        $rootScope.$broadcast('reloadMergeFieldGrid');
                                        $modalInstance.close();
                                    }, function() {
                                        messageService.errorAction();
                                    });
                                }
                            };
                            $scope.onClickCancel = function() {
                                $modalInstance.dismiss('cancel');
                            };
                        },
                        size: 'md',
                        backdrop: 'static'
                    });
                });

            }

            function ReloadGrid() {
                var mergeFieldsPromise = correspondenceMergeFieldsService.retrieveActiveMergeFieldsByType($scope.mergingType);
                mergeFieldsPromise.then(function(mergeFields) {
                    $scope.gridOptions.data = mergeFields.data;
                });
            }

            $scope.addNewMergeField = function() {
                var modalInstance = $modal.open({
                    scope: $scope,
                    animation: true,
                    templateUrl: 'modules/admin/views/components/correspondence-management-add-edit-merge-field.modal.client.view.html',
                    controller: [ '$scope', '$modalInstance', '$translate', function($scope, $modalInstance, $translate) {
                        $scope.onClickCancel = function() {
                            $modalInstance.dismiss('Cancel');
                        };

                        $scope.addMergeField = function () {
                            var mergeField = {
                                "fieldId" : $scope.fieldId,
                                "fieldValue" : $scope.fieldValue,
                                "fieldDescription" : $scope.fieldDescription,
                                "fieldObjectType" : $scope.fieldObjectType
                            };

                            var mergeFieldArray = [];
                            mergeFieldArray.push(mergeField);

                            correspondenceMergeFieldsService.saveMergeFieldsData(mergeFieldArray).then(function() {
                                messageService.succsessAction();
                                ReloadGrid();
                                $modalInstance.close();
                            }, function() {
                                messageService.errorAction();
                            });
                        }
                    } ],
                    size: 'md',
                    backdrop: 'static'
                });
            };

        } ]);