'use strict';

angular.module('tasks').controller('Tasks.MainController', ['$scope', '$stateParams', '$translate'
    , 'Acm.StoreService', 'UtilService', 'ConfigService', 'ObjectService', 'Object.NoteService', 'Object.AuditService'
    , 'Object.SignatureService', 'Task.InfoService', 'Task.HistoryService', 'dashboard', 'Dashboard.DashboardService'
    , function ($scope, $stateParams, $translate
        , Store, Util, ConfigService, ObjectService, ObjectNoteService, ObjectAuditService
        , ObjectSignatureService, TaskInfoService, TaskHistoryService, dashboard, DashboardService) {

        ConfigService.getModuleConfig("tasks").then(function (moduleConfig) {
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
            taskModel: {
                titleTemplateUrl: 'modules/dashboard/templates/widget-blank-title.html'
            }
        };

        DashboardService.getConfig({moduleName: "TASK"}, function (data) {
            $scope.dashboard.taskModel = angular.fromJson(data.dashboardConfig);
            $scope.dashboard.taskModel.titleTemplateUrl = 'modules/dashboard/templates/widget-blank-title.html';
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