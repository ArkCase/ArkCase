'use strict';

angular.module('cases').controller('Cases.MainController', ['$scope', '$stateParams', '$translate', 'UtilService', 'ConfigService'
    , 'Case.InfoService', 'ObjectService', 'Object.NoteService', 'Object.TaskService'
    , 'Object.AuditService', 'Object.CostService', 'Object.TimeService', 'dashboard', 'Dashboard.DashboardService', 'Acm.StoreService'
    , function ($scope, $stateParams, $translate, Util, ConfigService
        , CaseInfoService, ObjectService, ObjectNoteService, ObjectTaskService
        , ObjectAuditService, ObjectCostService, ObjectTimeService, dashboard, DashboardService, Store) {

        var promiseConfig = ConfigService.getModuleConfig("cases").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            $scope.config = _.find(moduleConfig.components, {id: "main"});
            return moduleConfig;
        });


        _.forEach(dashboard.widgets, function (widget, widgetId) {
            widget.title = $translate.instant('dashboard.widgets.' + widgetId + '.title');
            widget.description = $translate.instant('dashboard.widgets.' + widgetId + '.description');
        });

        $scope.dashboard = {
            structure: '12',
            collapsible: false,
            maximizable: false,
            caseModel: {
                titleTemplateUrl: 'modules/dashboard/views/module-dashboard-title.client.view.html'
            }
        };

        DashboardService.getConfig({moduleName: "CASE"}, function (data) {
            $scope.dashboard.caseModel = angular.fromJson(data.dashboardConfig);
            $scope.dashboard.caseModel.titleTemplateUrl = 'modules/dashboard/views/module-dashboard-title.client.view.html';
            $scope.$emit("collapsed", data.collapsed);
        });
    }
]);