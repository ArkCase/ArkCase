'use strict';

angular.module('cases').controller('Cases.BillingController', ['$scope', '$modal', 'ConfigService', '$stateParams', 'Case.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Case.BillingService', 'MessageService',
    function($scope, $modal, ConfigService, $stateParams, CaseInfoService, HelperUiGridService, HelperObjectBrowserService, CaseBillingService, MessageService){
        new HelperObjectBrowserService.Component({
            scope: $scope,
            stateParams: $stateParams,
            moduleId: "cases",
            componentId: "billing",
            retrieveObjectInfo: CaseInfoService.getCaseInfo,
            validateObjectInfo: CaseInfoService.validateCaseInfo,
            onConfigRetrieved: function(componentConfig) {
                return onConfigRetrieved(componentConfig);
            },
            onObjectInfoRetrieved: function(objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({
            scope: $scope
        });

        var onConfigRetrieved = function(config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            $scope.config = config;
            $scope.gridOptions = {
                showColumnFooter: $scope.config.showColumnFooter,
                enableColumnResizing: true,
                enableRowSelection: true,
                enableRowHeaderSelection: false,
                multiSelect: false,
                noUnselect: false,
                columnDefs: $scope.config.columnDefs,
                paginationPageSizes: $scope.config.paginationPageSizes,
                paginationPageSize: $scope.config.paginationPageSize,
                totalItems: 0,
                data: []
            };
            retrieveGridData();
        };

        function retrieveGridData(){
            var params = {};
            params.objectId = $stateParams.id;
            params.objectType = 'CASE_FILE';
            CaseBillingService.getBillingItems(params.objectId,  params.objectType).then(function(data){
                $scope.items = data.data;
                $scope.gridOptions = $scope.gridOptions || {};
                $scope.gridOptions.data = $scope.items;
                $scope.gridOptions.totalItems = $scope.items.length;
            });
        }

        var onObjectInfoRetrieved = function(objectInfo) {
            $scope.objectInfo = objectInfo;
        };

        ConfigService.getModuleConfig("cases").then(function(moduleConfig) {
            $scope.listInvoiceConfig = _.find(moduleConfig.components, {
                id: "listInvoices"
            });
        });

        var invoiceData = {
            parentObjectId : $stateParams.id,
            parentObjectType : 'CASE_FILE',
            objectSubtype: 'Request'
        };

        $scope.emailInvoice = function() {
            CaseBillingService.sendBillingInvoiceByEmail(invoiceData).then(function() {
                MessageService.succsessAction();
            }, function() {
                MessageService.errorAction();
            });
        };

        $scope.listInvoices = function() {
            var params = {};
            params.billingConfig = $scope.config;
            params.gridData = $scope.gridOptions.data;
            params.objectId = $stateParams.id;
            params.objectType = 'CASE_FILE';
            $modal.open({
                templateUrl: 'modules/cases/views/components/case-list-invoices-modal.client.view.html',
                controller: 'Cases.ListInvoicesModalController',
                size: 'lg',
                backdrop: 'static',
                resolve: {
                    $config: function() {
                        return $scope.listInvoiceConfig;
                    },
                    params: function() {
                        return params;
                    }
                }
            });
        };
        
        $scope.generateInvoice = function() {
            CaseBillingService.createBillingInvoice(invoiceData).then(function() {
                MessageService.succsessAction();    
            }, function() {
                MessageService.errorAction();
            });
        };

        $scope.addBillingItem = function() {
            var params = {};
            params.gridData = $scope.gridOptions.data;
            var modalInstance = $modal.open({
                templateUrl: 'modules/cases/views/components/case-billing-modal.client.view.html',
                controller: 'Cases.BillingModalController',
                size: 'lg',
                backdrop: 'static',
                resolve: {
                    params: function() {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function (data) {
                $scope.entry = data.objectDataModel
                $scope.gridOptions.data.push($scope.entry);
                saveItem();
            });
        };

        function saveItem(){
            var itemData = $scope.entry;
            itemData.parentObjectId = $stateParams.id;
            itemData.parentObjectType = 'CASE_FILE';
            CaseBillingService.addBillingItem(itemData);
        }

    }]);