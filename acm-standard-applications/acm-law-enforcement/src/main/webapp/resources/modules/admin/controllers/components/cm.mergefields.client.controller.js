'use strict';

angular.module('admin').controller('Admin.CMMergeFieldsController', ['$scope', '$modal', 'Admin.CMMergeFieldsService',
    'Helper.UiGridService', 'MessageService', 'LookupService', 'Acm.StoreService',
    function ($scope, $modal, correspondenceMergeFieldsService, HelperUiGridService, messageService, LookupService, Store) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();
        $scope.objectTypes = {};
        $scope.mergingType = {};
        $scope.correspondenceManagementMergeFieldsVersions = undefined;

        //get config and init grid settings
        $scope.config.$promise.then(function (config) {
            $scope.correspondenceManagementMergeFieldsVersions = _.find(config.components, {id: 'correspondenceManagementMergeFieldsVersions'});
            var config = _.find(config.components, {id: 'correspondenceManagementMergeFields'});
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilter(promiseUsers);

            $scope.config = config;
            $scope.objectTypes = $scope.config.objectTypes;
            $scope.mergingType = $scope.objectTypes[0].id;
            ReloadGrid();
        });
        
        $scope.changeType = function() {
            ReloadGrid();
        }
        
        $scope.save = function() {
            correspondenceMergeFieldsService.saveMergeFieldsData($scope.gridOptions.data).then(function () {
                messageService.succsessAction();
                ReloadGrid();
            }, function () {
                messageService.errorAction();
            });
        }
        
        $scope.showVersion = function () {
            var mergeFieldVersionsPromise = correspondenceMergeFieldsService.retrieveMergeFieldsVersionsByType($scope.mergingType);
            var colDefs = $scope.correspondenceManagementMergeFieldsVersions.columnDefs;

            mergeFieldVersionsPromise.then(function (mergeFieldVersionsData) {
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/admin/views/components/cm.mergefield-versions.modal.client.view.html',
                    controller: function ($scope, $modalInstance) {

                        $scope.gridOptions = {
                                enableColumnResizing: true,
                                enableRowSelection: true,
                                pinSelectionCheckbox: false,
                                enableColumnMenus: false,
                                enableRowHeaderSelection: false,
                                multiSelect: false,
                                noUnselect: false,
                                columnDefs: colDefs,
                                data: mergeFieldVersionsData.data
                        };
                        
                        $scope.gridOptions.onRegisterApi = function(gridApi) {
                            $scope.modalGridApi = gridApi;
                        };
                        
                        $scope.onClickLoadVersion = function () {
                            var selectedRow = $scope.modalGridApi.selection.getSelectedRows();
                            if (selectedRow.length == 1) {
                                correspondenceMergeFieldsService.setActiveMergingVersion(selectedRow[0]).then(function () {
                                    messageService.succsessAction();
                                    $modalInstance.close();
                                }, function () {
                                    messageService.errorAction();
                                });
                            }
                        };
                        $scope.onClickCancel = function () {
                            $modalInstance.dismiss('cancel');
                        };
                    },
                    size: 'lg'
                });
            });

        }
        
        function ReloadGrid() {
            var mergeFieldsPromise = correspondenceMergeFieldsService.retrieveActiveMergeFieldsByType($scope.mergingType);
            mergeFieldsPromise.then(function (mergeFields) {
                $scope.gridOptions.data = mergeFields.data;
            });
        }

    }]);
