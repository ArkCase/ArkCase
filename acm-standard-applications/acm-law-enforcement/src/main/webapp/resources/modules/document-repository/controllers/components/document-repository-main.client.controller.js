'use strict';

angular.module('document-repository').controller('DocumentRepository.MainController', ['$scope'
    , 'ConfigService', 'Dashboard.DashboardService', 'ObjectService'
    , function ($scope
        , ConfigService, DashboardService, ObjectService
    ) {

        ConfigService.getModuleConfig("document-repository").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            $scope.config = _.find(moduleConfig.components, {id: "main"});
            return moduleConfig;
        });

        DashboardService.localeUseTypical($scope);

        $scope.dashboard = {
            structure: '12',
            collapsible: false,
            maximizable: false,
            docRepoModel: {
                titleTemplateUrl: 'modules/dashboard/templates/module-dashboard-title.html'
            }
        };

        DashboardService.getConfig({moduleName: ObjectService.ObjectTypes.DOC_REPO}, function (data) {
            $scope.dashboard.docRepoModel = angular.fromJson(data.dashboardConfig);
            DashboardService.fixOldCode_removeLater("DOC_REPO", $scope.dashboard.docRepoModel);
            $scope.dashboard.docRepoModel.titleTemplateUrl = 'modules/dashboard/templates/module-dashboard-title.html';
            $scope.$emit("collapsed", data.collapsed);
        });
    }
]);