'use strict';

angular.module('dashboard.expenses', [ 'adf.provider' ]).config(function(dashboardProvider) {
    dashboardProvider.widget('expenses', {
        title: 'preference.overviewWidgets.expenses.title',
        description: 'dashboard.widgets.expenses.description',
        controller: 'Dashboard.ExpensesController',
        controllerAs: 'expenses',
        reload: true,
        templateUrl: 'modules/dashboard/views/components/expenses-widget.client.view.html',
        commonName: 'expenses'
    });
}).controller(
        'Dashboard.ExpensesController',
        [ '$scope', '$stateParams', '$state', '$translate', 'UtilService', 'Dashboard.DashboardService', 'CostTracking.InfoService', 'Helper.ObjectBrowserService', 'ObjectService', 'Helper.UiGridService', 'moment',
                function($scope, $stateParams, $state, $translate, Util, DashboardService, CostTrackingInfoService, HelperObjectBrowserService, ObjectService, HelperUiGridService, moment) {

                    var vm = this;

                    var modules = [ {
                        name: ObjectService.ObjectTypes.COSTSHEET,
                        configName: "cost-tracking",
                        getInfo: CostTrackingInfoService.getCostsheetInfo,
                        validateInfo: CostTrackingInfoService.validateCostsheet
                    } ]; // todo: needs to be changed to use ModulesServices.getModulesServiceStructure(); after merge
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
                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.data = objectInfo.costs;
                        _.forEach($scope.data, function(data) {
                            data.date = moment(new Date(data.date)).format($translate.instant('common.defaultDateFormat'));
                        });
                        gridHelper.setWidgetsGridData($scope.data);
                    };
                    var onConfigRetrieved = function(componentConfig) {
                        var widgetInfo = _.find(componentConfig.widgets, function(widget) {
                            return widget.id === "expenses";
                        });
                        gridHelper.setColumnDefs(widgetInfo);
                    };

                    var componentHelper = new HelperObjectBrowserService.Component({
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

                } ]);
