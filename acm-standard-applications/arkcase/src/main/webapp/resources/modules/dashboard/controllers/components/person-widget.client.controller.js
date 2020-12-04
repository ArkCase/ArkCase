'use strict';

angular.module('dashboard.person', [ 'adf.provider' ]).config(function(dashboardProvider) {
    dashboardProvider.widget('person', {
        title: 'preference.overviewWidgets.person.title',
        description: 'dashboard.widgets.person.description',
        controller: 'Dashboard.PersonController',
        reload: true,
        templateUrl: 'modules/dashboard/views/components/person.client.view.html',
        commonName: 'person'
    });
}).controller(
        'Dashboard.PersonController',
        [ '$scope', '$stateParams', '$translate', 'CostTracking.InfoService', 'TimeTracking.InfoService', 'Helper.ObjectBrowserService', 'Helper.UiGridService',
                function($scope, $stateParams, $translate, CostTrackingInfoService, TimeTrackingInfoService, HelperObjectBrowserService, HelperUiGridService) {

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

                    var gridHelper = new HelperUiGridService.Grid({
                        scope: $scope
                    });
                    var promiseUsers = gridHelper.getUsers();

                    $scope.gridOptions = {
                        enableColumnResizing: true,
                        columnDefs: []
                    };

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
                        $scope.costsheetInfo = objectInfo;
                        gridHelper.setWidgetsGridData([ objectInfo.user ]);
                    };

                    var onConfigRetrieved = function(componentConfig) {
                        var widgetInfo = _.find(componentConfig.widgets, function(widget) {
                            return widget.id === "person";
                        });

                        gridHelper.setColumnDefs(widgetInfo);
                        gridHelper.setUserNameFilterToConfig(promiseUsers, widgetInfo);
                    };
                } ]);