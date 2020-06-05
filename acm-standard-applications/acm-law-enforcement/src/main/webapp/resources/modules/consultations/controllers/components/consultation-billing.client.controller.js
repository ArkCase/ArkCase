'use strict';

angular.module('consultations').controller('Consultations.BillingController', ['$scope', '$modal', 'ConfigService', '$stateParams', 'Consultation.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Consultation.BillingService', 'MessageService', 'UtilService',
    function($scope, $modal, ConfigService, $stateParams, ConsultationInfoService, HelperUiGridService, HelperObjectBrowserService, ConsultationBillingService, MessageService, Util){
        new HelperObjectBrowserService.Component({
            scope: $scope,
            stateParams: $stateParams,
            moduleId: "consultations",
            componentId: "billing",
            retrieveObjectInfo: ConsultationInfoService.getConsultationInfo,
            validateObjectInfo: ConsultationInfoService.validateConsultationInfo,
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
            params.objectType = 'CONSULTATION';
            ConsultationBillingService.getBillingItems(params.objectId, params.objectType).then(function (data) {
                $scope.items = data.data;
                $scope.gridOptions = $scope.gridOptions || {};
                $scope.gridOptions.data = $scope.items;
                $scope.gridOptions.totalItems = $scope.items.length;
            });
        }

        var onObjectInfoRetrieved = function(objectInfo) {
            $scope.objectInfo = objectInfo;
        };

        ConfigService.getModuleConfig("consultations").then(function(moduleConfig) {
            $scope.listInvoiceConfig = _.find(moduleConfig.components, {
                id: "listInvoices"
            });
        });

        var invoiceData = {
            parentObjectId : $stateParams.id,
            parentObjectType : 'CONSULTATION'
        };

        $scope.emailInvoice = function() {
            ConsultationBillingService.sendBillingInvoiceByEmail(invoiceData).then(function() {
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
            params.objectType = 'CONSULTATION';
            $modal.open({
                templateUrl: 'modules/consultations/views/components/consultation-list-invoices-modal.client.view.html',
                controller: 'Consultations.ListInvoicesModalController',
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

        $scope.isInvoiceGenerated =  false;
        ConsultationBillingService.getBillingInvoices($stateParams.id, 'CONSULTATION').then(function(data){
            if(!Util.isArrayEmpty(data.data)) {
                $scope.isInvoiceGenerated = true;
            }
            else {
                $scope.isInvoiceGenerated = false;
            }
        });

        $scope.$bus.subscribe('invoice-generated', function() {
            $scope.isInvoiceGenerated =  true;
        });

        $scope.generateInvoice = function() {
            ConsultationBillingService.createBillingInvoice(invoiceData).then(function() {
                $scope.$bus.publish('invoice-generated', invoiceData);
                MessageService.succsessAction();
            }, function() {
                MessageService.errorAction();
            });
        };

        $scope.addBillingItem = function() {
            var params = {};
            params.gridData = $scope.gridOptions.data;
            var modalInstance = $modal.open({
                templateUrl: 'modules/consultations/views/components/consultation-billing-modal.client.view.html',
                controller: 'Consultations.BillingModalController',
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
            itemData.parentObjectType = 'CONSULTATION';
            ConsultationBillingService.addBillingItem(itemData);
        }

    }]);
