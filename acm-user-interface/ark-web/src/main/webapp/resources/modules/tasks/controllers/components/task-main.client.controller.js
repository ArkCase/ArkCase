'use strict';

angular.module('tasks').controller('Tasks.MainController', ['$scope', '$stateParams', '$translate'
    , 'StoreService', 'UtilService', 'ConfigService', 'ObjectService', 'Object.NoteService', 'Object.AuditService'
    , 'Object.SignatureService', 'Task.InfoService', 'Task.HistoryService', 'dashboard', 'Dashboard.DashboardService'
    , function ($scope, $stateParams, $translate
        , Store, Util, ConfigService, ObjectService, ObjectNoteService, ObjectAuditService
        , ObjectSignatureService, TaskInfoService, TaskHistoryService, dashboard, DashboardService) {

        $scope.$emit('main-component-started');

        var promiseConfig = ConfigService.getModuleConfig("tasks").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            $scope.config = _.find(moduleConfig.components, {id: "main"});

            $scope.allowedWidgets = ['details'];
            return moduleConfig;
        });

        _.forEach(dashboard.widgets, function (widget, widgetId) {
            widget.title = $translate.instant('dashboard.widgets.' + widgetId + '.title');
            widget.description = $translate.instant('dashboard.widgets.' + widgetId + '.description');
        });

        $scope.dashboard = {
            structure: '6-6',
            collapsible: false,
            maximizable: false,
            model: {
                titleTemplateUrl: 'modules/dashboard/views/dashboard-title.client.view.html'
            }
        };

        DashboardService.getConfig({moduleName: "TASK"}, function (data) {
                $scope.dashboard.taskModel = angular.fromJson(data.dashboardConfig);
                $scope.dashboard.model.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';
        });

        $scope.$on('adfDashboardChanged', function (event, name, model) {
            DashboardService.saveConfig({
                dashboardConfig: angular.toJson(model),
                moduleName: "TASK"
            });
        });

        //var widgetFilter = function(model) {
        //    var taskModel = model;
        //    //iterate over rows
        //    for(var i = 0; i < taskModel.rows.length; i++) {
        //        //iterate over columns
        //        for(var j = 0; j < taskModel.rows[i].columns.length; j++) {
        //            //iterate over column widgets
        //            if(taskModel.rows[i].columns[j].widgets){
        //                for(var k = taskModel.rows[i].columns[j].widgets.length; k > 0; k--) {
        //                    // var type = taskModel.rows[i].columns[j].widgets[k].type;
        //                    var type = taskModel.rows[i].columns[j].widgets[k-1].type;
        //                    if(!($scope.allowedWidgets.indexOf(type) > -1)) {
        //                        //remove widget from array
        //                        taskModel.rows[i].columns[j].widgets.splice(k-1, 1);
        //                    }
        //                }
        //            }
        //        }
        //    }
        //    return taskModel;
        //};

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