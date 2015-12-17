'use strict';

angular.module('tasks').controller('Tasks.MainController', ['$scope', '$stateParams', '$translate', 'dashboard', 'Dashboard.DashboardService', 'StoreService'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.NoteService', 'Object.AuditService', 'Object.SignatureService', 'Task.InfoService', 'Task.HistoryService'
    , function ($scope, $stateParams, $translate, dashboard, DashboardService, Store
        , Util, ConfigService, ObjectService, ObjectNoteService, ObjectAuditService, ObjectSignatureService, TaskInfoService, TaskHistoryService) {

        var promiseConfig = ConfigService.getModuleConfig("tasks").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            $scope.config = _.find(moduleConfig.components, {id: "main"});

            //$scope.widgetsTask = [];
            //_.each(Util.goodMapValue($scope.config, "widgetsTask", []), function (widget) {
            //    if ("component" == Util.goodValue(widget.type)) {
            //        var item = {};
            //        var found = _.find(moduleConfig.components, {id: widget.id});
            //        //if (found && found.enabled) {
            //        if (found) {
            //            item.title = found.title;
            //            item.id = widget.id;
            //            $scope.widgetsTask.push(item);
            //        }
            //    }
            //});
            //
            //$scope.widgetsAdhoc = [];
            //_.each(Util.goodMapValue($scope.config, "widgetsAdhoc", []), function (widget) {
            //    if ("component" == Util.goodValue(widget.type)) {
            //        var item = {};
            //        var found = _.find(moduleConfig.components, {id: widget.id});
            //        //if (found && found.enabled) {
            //        if (found) {
            //            item.title = found.title;
            //            item.id = widget.id;
            //            $scope.widgetsAdhoc.push(item);
            //        }
            //    }
            //});
            //
            //if (ObjectService.ObjectTypes.TASK == $stateParams.type) {
            //    $scope.widgets = $scope.widgetsTask;
            //} else if (ObjectService.ObjectTypes.ADHOC_TASK == $stateParams.type) {
            //    $scope.widgets = $scope.widgetsAdhoc;
            //}

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

        DashboardService.getConfig({}, function (data) {
            var cacheDashboardConfig = new Store.CacheFifo(TaskInfoService.CacheNames.TASK_INFO);
            $scope.dashboard.taskModel = cacheDashboardConfig.get("dashboardConfig");

            if($scope.dashboard.taskModel) {
                //If cached, use that model
                $scope.dashboard.taskModel.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';
                $scope.dashboard.model.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';
            } else {
                //Else use dashboard config and filter.
                $scope.dashboard.model = angular.fromJson(data.dashboardConfig);
                $scope.dashboard.taskModel = Util.filterWidgets($scope.dashboard.model, $scope.allowedWidgets);
                $scope.dashboard.model.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';

                //Cache filtered dashboard model
                cacheDashboardConfig.put("dashboardConfig", $scope.dashboard.taskModel);
            }
        });

        $scope.$on('adfDashboardChanged', function (event, name, model) {
            //Save dashboard model only to cache
            var cacheDashboardConfig = new Store.CacheFifo(TaskInfoService.CacheNames.TASK_INFO);
            if(cacheDashboardConfig)
                cacheDashboardConfig.put("dashboardConfig", model);
        });

        var widgetFilter = function(model) {
            var taskModel = model;
            //iterate over rows
            for(var i = 0; i < taskModel.rows.length; i++) {
                //iterate over columns
                for(var j = 0; j < taskModel.rows[i].columns.length; j++) {
                    //iterate over column widgets
                    if(taskModel.rows[i].columns[j].widgets){
                        for(var k = taskModel.rows[i].columns[j].widgets.length; k > 0; k--) {
                            // var type = taskModel.rows[i].columns[j].widgets[k].type;
                            var type = taskModel.rows[i].columns[j].widgets[k-1].type;
                            if(!($scope.allowedWidgets.indexOf(type) > -1)) {
                                //remove widget from array
                                taskModel.rows[i].columns[j].widgets.splice(k-1, 1);
                            }
                        }
                    }
                }
            }
            return taskModel;
        };

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

        //$scope.$on('task-updated', function (e, data) {
        //    var z = 1;
        //});
        //
        //$scope.widgetData = {};
        //TaskInfoService.getTaskInfo($stateParams.id).then(function (taskInfo) {
        //    $scope.taskInfo = taskInfo;
        //
        //    $scope.widgetData["details"] = Util.goodMapValue($scope.taskInfo, "details");
        //    $scope.widgetData["reworkdetails"] = Util.goodMapValue($scope.taskInfo, "reworkInstructions");
        //    $scope.widgetData["docsreview"] = Util.goodMapValue($scope.taskInfo, "documentUnderReview.fileName");
        //
        //    TaskHistoryService.queryTaskHistory($scope.taskInfo).then(function (taskHistory) {
        //        $scope.widgetData["workflow"] = taskHistory.length;
        //    });
        //
        //    return taskInfo;
        //});
        //
        //$scope.widgetData["attachments"] = "documents data";
        //
        //ObjectNoteService.queryRejectComments(ObjectService.ObjectTypes.TASK, $stateParams.id)
        //    .then(function (notes) {
        //        $scope.widgetData["rejcomments"] = Util.goodValue(notes.length, 0);
        //    });
        //
        //ObjectNoteService.queryNotes(ObjectService.ObjectTypes.TASK, $stateParams.id)
        //    .then(function (notes) {
        //        $scope.widgetData["notes"] = Util.goodValue(notes.length, 0);
        //    });
        //
        //ObjectAuditService.queryAudit(ObjectService.ObjectTypes.TASK, $stateParams.id, 0, 10, "", "")
        //    .then(function (auditData) {
        //        $scope.widgetData["history"] = Util.goodValue(auditData.totalCount, 0);
        //    });
        //
        //ObjectSignatureService.findSignatures(ObjectService.ObjectTypes.TASK, $stateParams.id)
        //    .then(function (signatures) {
        //        $scope.widgetData["signatures"] = Util.goodValue(signatures.length, 0);
        //    });
    }
])
;