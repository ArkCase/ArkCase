'use strict';

angular.module('cases').controller('Cases.ListInvoicesModalController', ['$scope', '$modalInstance', '$config', '$stateParams', 'params', 'Case.BillingService',
    function($scope, $modalInstance, $config, $stateParams, params, CaseBillingService){
        $scope.config = $config;
        $scope.gridDetailOptions = params.billingConfig;
        $scope.isGridShown = false;
        params.parentObjectId = $stateParams.id;
        params.parentObjectType = 'CASE_FILE';

        $scope.gridOptions = {
            enableColumnResizing: true,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            noUnselect: false,
            columnDefs: $scope.config.columnDefs,
            totalItems: 0,
            data: []
        };

        CaseBillingService.getBillingInvoices(params.parentObjectId, params.parentObjectType).then(function(data){
            $scope.items = data.data;
            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = $scope.items;
            $scope.gridOptions.totalItems = $scope.items.length;
        });

        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };

        $scope.toggle = function(){
            $scope.isGridShown = !$scope.isGridShown
        };

        $scope.viewInvoice = function(rowEntity){
            $scope.isGridShown = true;
            $scope.invoiceNumber = rowEntity.invoiceNumber;
            $scope.items = rowEntity.billingItems;
            $scope.gridDetailOptions = $scope.gridDetailOptions || {};
            $scope.gridDetailOptions.data = $scope.items;
        };

    }]);