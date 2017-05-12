'use strict';

angular.module('document-repository').controller('DocumentRepository.MainController', ['$scope', '$stateParams', '$translate'
    , 'Acm.StoreService', 'UtilService', 'ConfigService', 'dashboard', 'Dashboard.DashboardService', 'ObjectService'
    , function ($scope, $stateParams, $translate, Store, Util, ConfigService, dashboard, DashboardService
        , ObjectService) {

        ConfigService.getModuleConfig("document-repository").then(function (moduleConfig) {
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
            docRepoModel: {
                titleTemplateUrl: 'modules/dashboard/views/module-dashboard-title.client.view.html'
            }
        };

        DashboardService.getConfig({moduleName: ObjectService.ObjectTypes.DOC_REPO}, function (data) {
            $scope.dashboard.docRepoModel = angular.fromJson(data.dashboardConfig);
            $scope.dashboard.docRepoModel.titleTemplateUrl = 'modules/dashboard/views/module-dashboard-title.client.view.html';
            $scope.$emit("collapsed", data.collapsed);
        });
    }
]);