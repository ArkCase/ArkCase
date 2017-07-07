'use strict';

angular.module('dashboard.approvalRouting', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('approvalRouting', {
                    title: 'dashboard.widgets.approvalRouting.title',
                    description: 'dashboard.widgets.approvalRouting.description',
                    controller: 'Dashboard.ApprovalRoutingController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/approval-routing-widget.client.view.html',
                    commonName: 'approvalRouting'
                }
            );
    })
    .controller('Dashboard.ApprovalRoutingController', ['$scope', '$stateParams', 'Case.InfoService'
        , 'Complaint.InfoService', 'Helper.ObjectBrowserService'
        , function ($scope, $stateParams, CaseInfoService, ComplaintInfoService, HelperObjectBrowserService) {
            var modules = [
                {
                    name: "CASE_FILE",
                    configName: "cases",
                    getInfo: CaseInfoService.getCaseInfo,
                    validateInfo: CaseInfoService.validateCaseInfo
                }
                , {
                    name: "COMPLAINT",
                    configName: "complaints",
                    getInfo: ComplaintInfoService.getComplaintInfo,
                    validateInfo: ComplaintInfoService.validateComplaintInfo
                }
            ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

            new HelperObjectBrowserService.Component({
                scope: $scope
                , stateParams: $stateParams
                , moduleId: module.configName
                , componentId: "main"
                , retrieveObjectInfo: module.getInfo
                , validateObjectInfo: module.validateInfo
                , onObjectInfoRetrieved: function (objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
                , onConfigRetrieved: function (componentConfig) {
                    onConfigRetrieved(componentConfig);
                }
            });

            var onObjectInfoRetrieved = function (objectInfo) {
                $scope.gridOptions.data = objectInfo ? objectInfo : [];
                $scope.gridOptions.totalItems = $scope.gridOptions.data.length;
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "approvalRouting";
                });
                $scope.gridOptions.columnDefs = widgetInfo ? widgetInfo.columnDefs : [];
            };
        }
    ]);