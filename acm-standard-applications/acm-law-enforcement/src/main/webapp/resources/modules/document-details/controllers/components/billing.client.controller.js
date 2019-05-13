'use strict';

angular.module('cases').controller('BillingItemController', ['$scope', '$modal', '$stateParams', 'Case.InfoService', 'Complaint.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Case.BillingService', 'Complaint.BillingService', 'MessageService', 'Complaint.InfoService',
    function($scope, $modal, $stateParams, CaseInfoService, ComplaintInfoService, HelperUiGridService, HelperObjectBrowserService, CaseBillingService, ComplaintBillingService, MessageService){

        $scope.parentObjectType = $stateParams.containerType;
        function setCaseOrComplaintConfiguration(parentObjectType) {
            if (parentObjectType === 'CASE_FILE') {
                $scope.templateUrl = 'modules/cases/views/components/case-billing-modal.client.view.html';
                $scope.controller = 'Cases.BillingModalController';
                $scope.moduleId = 'cases';
                $scope.billingService = CaseBillingService;
                $scope.infoService = CaseInfoService.getCaseInfo;
                $scope.validateInfo = ComplaintInfoService.validateCaseInfo;
            }
            else if (parentObjectType === 'COMPLAINT') {
                $scope.templateUrl = 'modules/complaints/views/components/complaint-billing-modal.client.view.html';
                $scope.controller = 'Complaints.BillingModalController';
                $scope.moduleId = 'complaints';
                $scope.billingService = ComplaintBillingService;
                $scope.infoService = ComplaintInfoService.getComplaintInfo;
                $scope.validateInfo = ComplaintInfoService.validateComplaintInfo;
            }
        }
        setCaseOrComplaintConfiguration($scope.parentObjectType);

        new HelperObjectBrowserService.Component({
            scope: $scope,
            stateParams: $stateParams,
            moduleId: $scope.moduleId,
            componentId: "billing",
            retrieveObjectInfo: $scope.infoService,
            validateObjectInfo: $scope.validateInfo,
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
            params.objectId = $stateParams.containerId;
            params.objectType = $scope.parentObjectType;
            $scope.billingService.getBillingItems(params.objectId,  params.objectType).then(function(data){
                $scope.items = data.data;
                $scope.gridOptions = $scope.gridOptions || {};
                $scope.gridOptions.data = $scope.items;
                $scope.gridOptions.totalItems = $scope.items.length;
            });
        }

        var onObjectInfoRetrieved = function(objectInfo) {
            $scope.objectInfo = objectInfo;
        };

        var invoiceData = {
            parentObjectId : $stateParams.containerId,
            parentObjectType : $scope.parentObjectType
        };

        $scope.emailInvoice = function(){
            $scope.billingService.sendBillingInvoiceByEmail(invoiceData).then(function() {
                MessageService.succsessAction();
            }, function() {
                MessageService.errorAction();
            });
        };

        $scope.generateInvoice = function(){
            $scope.billingService.createBillingInvoice(invoiceData).then(function() {
                MessageService.succsessAction();
            }, function() {
                MessageService.errorAction();
            });
        };

        $scope.addBillingItem = function(){
            var params = {};
            params.gridData = $scope.gridOptions.data;
            var modalInstance = $modal.open({
                templateUrl: $scope.templateUrl,
                controller: $scope.controller,
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
            itemData.parentObjectId = $stateParams.containerId;
            itemData.parentObjectType = $scope.parentObjectType;
            $scope.billingService.addBillingItem(itemData);
        }


    }]);