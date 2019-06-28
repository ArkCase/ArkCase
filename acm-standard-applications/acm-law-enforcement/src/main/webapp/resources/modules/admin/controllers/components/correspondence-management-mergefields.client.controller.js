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

            $scope.gridOptions = {
                enableRowSelection: true,
                enableFiltering: false,
                enableRowHeaderSelection: true,
                enableFullRowSelection: true,
                data: [],
                onRegisterApi: function(gridApi) {
                    $scope.gridApi = gridApi;
                    gridApi.selection.on.rowSelectionChanged($scope, function(row) {
                        $scope.selectedRows = gridApi.selection.getSelectedRows();
                    });

                    gridApi.selection.on.rowSelectionChangedBatch($scope, function(rows) {
                        $scope.selectedRows = gridApi.selection.getSelectedRows();
                    });
                }
            };

            //get config and init grid settings
            $scope.config.$promise.then(function(config) {
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

                        reloadGrid();
                    });
                });
            });

            $scope.changeType = function() {
                reloadGrid();
            };

            $scope.deleteMergeFields = function() {
                angular.forEach($scope.selectedRows, function(row, index) {
                    correspondenceMergeFieldsService.deleteMergeFields(row.fieldId).then(function() {
                        reloadGrid();
                        messageService.succsessAction();
                    }, function() {
                        messageService.errorAction();
                    });
                });
            };

            $scope.save = function() {
                correspondenceMergeFieldsService.saveMergeFieldsData($scope.gridOptions.data).then(function() {
                    messageService.succsessAction();
                    reloadGrid();
                }, function() {
                    messageService.errorAction();
                });
            };

            $scope.$on('reloadMergeFieldGrid', function() {
                reloadGrid();
            });

            function reloadGrid() {
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

                            correspondenceMergeFieldsService.addMergeField(mergeField).then(function() {
                                messageService.succsessAction();
                                reloadGrid();
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