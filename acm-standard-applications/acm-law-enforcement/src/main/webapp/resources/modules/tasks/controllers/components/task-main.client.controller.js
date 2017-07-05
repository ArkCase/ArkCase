'use strict';

angular.module('tasks').controller('Tasks.MainController', ['$scope', 'Acm.StoreService', 'UtilService'
    , 'ConfigService', 'Object.AuditService', 'Dashboard.DashboardService'
    , function ($scope, Store, Util
        , ConfigService, ObjectAuditService, DashboardService
    ) {

        ConfigService.getModuleConfig("tasks").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            $scope.config = _.find(moduleConfig.components, {id: "main"});
            return moduleConfig;
        });

        DashboardService.localeUseTypical($scope);

        $scope.dashboard = {
            structure: '12',
            collapsible: false,
            maximizable: false,
            taskModel: {
                titleTemplateUrl: 'modules/dashboard/templates/module-dashboard-title.html'
            }
        };

        DashboardService.getConfig({moduleName: "TASK"}, function (data) {
            $scope.dashboard.taskModel = angular.fromJson(data.dashboardConfig);
            DashboardService.fixOldCode_removeLater("TASK", $scope.dashboard.taskModel);
            $scope.dashboard.taskModel.titleTemplateUrl = 'modules/dashboard/templates/module-dashboard-title.html';
            $scope.$emit("collapsed", data.collapsed);
        });

        $scope.shallInclude = function (component) {
            if (component.enabled) {
                var componentsStore = new Store.Variable("TaskComponentsStore");
                var componentsToShow = Util.goodValue(componentsStore.get(), []);
                for (var i = 0; i < componentsToShow.length; i++) {
                    if (componentsToShow[i] == component.id) {
                        return true;
                    }
                }
            }
            return false;
        };

    }
])
;