'use strict';

angular.module('complaints').controller('Complaints.MainController', ['$scope', '$stateParams'
    , 'ConfigService', 'Complaint.InfoService', 'Dashboard.DashboardService'
    , function ($scope, $stateParams
        , ConfigService, ComplaintInfoService, DashboardService
    ) {
        ConfigService.getModuleConfig("complaints").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            $scope.config = _.find(moduleConfig.components, {id: "main"});
            return moduleConfig;
        });

        DashboardService.localeUseTypical($scope);

        $scope.dashboard = {
            structure: '12',
            collapsible: false,
            maximizable: false,
            complaintModel: {
                titleTemplateUrl: 'modules/dashboard/templates/widget-blank-title.html'
            }
        };

        DashboardService.getConfig({moduleName: "COMPLAINT"}, function (data) {
            $scope.dashboard.complaintModel = angular.fromJson(data.dashboardConfig);
            DashboardService.fixOldCode_removeLater($scope.dashboard.complaintModel);
            $scope.dashboard.complaintModel.titleTemplateUrl = 'modules/dashboard/templates/widget-blank-title.html';
            $scope.$emit("collapsed", data.collapsed);
        });
    }
]);