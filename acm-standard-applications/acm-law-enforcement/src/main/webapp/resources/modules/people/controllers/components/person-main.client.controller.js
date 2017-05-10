'use strict';

angular.module('people').controller('People.MainController', ['$scope', '$stateParams', '$translate', 'UtilService', 'ConfigService'
    , 'Person.InfoService', 'ObjectService', 'Object.NoteService', 'Object.TaskService'
    , 'Object.AuditService', 'Object.CostService', 'Object.TimeService', 'dashboard', 'Dashboard.DashboardService', 'Acm.StoreService'
    , function ($scope, $stateParams, $translate, Util, ConfigService
        , PersonInfoService, ObjectService, ObjectNoteService, ObjectTaskService
        , ObjectAuditService, ObjectCostService, ObjectTimeService, dashboard, DashboardService, Store) {

        var promiseConfig = ConfigService.getModuleConfig("people").then(function (moduleConfig) {
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
            personModel: {
                titleTemplateUrl: 'modules/dashboard/views/module-dashboard-title.client.view.html'
            }
        };

        DashboardService.getConfig({moduleName: "PERSON"}, function (data) {
            $scope.dashboard.personModel = angular.fromJson(data.dashboardConfig);
            $scope.dashboard.personModel.titleTemplateUrl = 'modules/dashboard/views/module-dashboard-title.client.view.html';
            $scope.$emit("collapsed", data.collapsed);
        });
    }
]);