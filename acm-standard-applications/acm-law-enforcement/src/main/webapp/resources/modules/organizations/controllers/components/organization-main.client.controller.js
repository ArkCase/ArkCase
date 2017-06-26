'use strict';

angular.module('organizations').controller('Organizations.MainController', ['$scope', '$stateParams', '$translate', 'UtilService', 'ConfigService'
    , 'Organization.InfoService', 'ObjectService', 'Object.NoteService', 'Object.TaskService'
    , 'Object.AuditService', 'Object.CostService', 'Object.TimeService', 'dashboard', 'Dashboard.DashboardService', 'Acm.StoreService'
    , function ($scope, $stateParams, $translate, Util, ConfigService
        , PersonInfoService, ObjectService, ObjectNoteService, ObjectTaskService
        , ObjectAuditService, ObjectCostService, ObjectTimeService, dashboard, DashboardService, Store) {

        var promiseConfig = ConfigService.getModuleConfig("organizations").then(function (moduleConfig) {
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
            organizationModel: {
                titleTemplateUrl: 'modules/dashboard/templates/widget-blank-title.html'
            }
        };

        DashboardService.getConfig({moduleName: "ORGANIZATION"}, function (data) {
            $scope.dashboard.organizationModel = angular.fromJson(data.dashboardConfig);
            $scope.dashboard.organizationModel.titleTemplateUrl = 'modules/dashboard/templates/widget-blank-title.html';
            $scope.$emit("collapsed", data.collapsed);
        });
    }
]);