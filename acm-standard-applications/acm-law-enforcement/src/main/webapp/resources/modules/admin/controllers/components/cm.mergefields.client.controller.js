'use strict';

angular.module('admin').controller('Admin.CMMergeFieldsController', ['$scope', '$modal', 'Admin.CMMergeFieldsService',
    'Helper.UiGridService', 'MessageService', 'LookupService', 'Acm.StoreService',
    function ($scope, $modal, correspondenceMergeFieldsService, HelperUiGridService, messageService, LookupService, Store) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();
        $scope.selectedRows = {};
        $scope.correspondenceManagementTemplateVersions = undefined;

        //get config and init grid settings
        $scope.config.$promise.then(function (config) {
            var config = _.find(config.components, {id: 'correspondenceManagementMergeFields'});
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilter(promiseUsers);

            $scope.config = config;
            ReloadGrid();
        });
        
        function ReloadGrid() {
            var mergeFieldsPromise = correspondenceMergeFieldsService.retrieveMergeFieldsList();
            mergeFieldsPromise.then(function (mergeFields) {
                $scope.gridOptions.data = mergeFields.data;
            });
        }

    }]);
