'use strict';

angular.module('dashboard.approvers', [ 'adf.provider' ]).config(function(dashboardProvider) {
    dashboardProvider.widget('approvers', {
        title: 'preference.overviewWidgets.approvers.title',
        description: 'dashboard.widgets.approvers.description',
        controller: 'Dashboard.ApproversController',
        controllerAs: 'approvers',
        reload: true,
        templateUrl: 'modules/dashboard/views/components/approvers-widget.client.view.html',
        commonName: 'approvers'
    });
}).controller(
        'Dashboard.ApproversController',
        [ '$scope', 'config', '$state', '$translate', 'UtilService', 'Dashboard.DashboardService', 'CostTracking.InfoService', 'TimeTracking.InfoService', 'Helper.ObjectBrowserService', '$stateParams', 'Helper.UiGridService', 'ObjectService', 'LookupService',
                function($scope, config, $state, $translate, Util, DashboardService, CostTrackingInfoService, TimeTrackingInfoService, HelperObjectBrowserService, $stateParams, HelperUiGridService, ObjectService, LookupService) {

                    var modules = [ {
                        name: "COSTSHEET",
                        configName: "cost-tracking",
                        getInfo: CostTrackingInfoService.getCostsheetInfo,
                        validateInfo: CostTrackingInfoService.validateCostsheet
                    }, {
                        name: "TIMESHEET",
                        configName: "time-tracking",
                        getInfo: TimeTrackingInfoService.getTimesheetInfo,
                        validateInfo: TimeTrackingInfoService.validateTimesheet
                    } ];

                    var module = _.find(modules, function(module) {
                        return module.name == $stateParams.type;
                    });

                    $scope.gridOptions = {
                        enableColumnResizing: true,
                        columnDefs: []
                    };

                    var gridHelper = new HelperUiGridService.Grid({
                        scope: $scope
                    });
                    var promiseUsers = gridHelper.getUsers();

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: module.configName,
                        componentId: "main",
                        retrieveObjectInfo: module.getInfo,
                        validateObjectInfo: module.validateInfo,
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        },
                        onConfigRetrieved: function(componentConfig) {
                            onConfigRetrieved(componentConfig);
                        }
                    });

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;

                        LookupService.getApprovers(objectInfo).then(function(approvers) {
                            if (!Util.isEmpty(approvers)) {
                                $scope.gridOptions.data = approvers;
                                $scope.gridOptions.noData = false;
                            } else {
                                $scope.gridOptions.data = [];
                                $scope.gridOptions.noData = true;
                                $scope.noDataMessage = $translate.instant('dashboard.widgets.approvers.noDataMessage');
                            }
                        });
                    };

                    var onConfigRetrieved = function(componentConfig) {
                        var widgetInfo = _.find(componentConfig.widgets, function(widget) {
                            return widget.id === "tasks";
                        });
                        gridHelper.setColumnDefs(widgetInfo);
                    };

                } ]);
