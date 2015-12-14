'use strict';

angular.module('tasks').controller('Tasks.MainController', ['$scope', '$stateParams', 'StoreService'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.NoteService', 'Object.AuditService', 'Object.SignatureService', 'Task.InfoService', 'Task.HistoryService'
    , function ($scope, $stateParams, Store
        , Util, ConfigService, ObjectService, ObjectNoteService, ObjectAuditService, ObjectSignatureService, TaskInfoService, TaskHistoryService) {

        var promiseConfig = ConfigService.getModuleConfig("tasks").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            $scope.config = _.find(moduleConfig.components, {id: "main"});

            $scope.widgetsTask = [];
            _.each(Util.goodMapValue($scope.config, "widgetsTask", []), function (widget) {
                if ("component" == Util.goodValue(widget.type)) {
                    var item = {};
                    var found = _.find(moduleConfig.components, {id: widget.id});
                    //if (found && found.enabled) {
                    if (found) {
                        item.title = found.title;
                        item.id = widget.id;
                        $scope.widgetsTask.push(item);
                    }
                }
            });

            $scope.widgetsAdhoc = [];
            _.each(Util.goodMapValue($scope.config, "widgetsAdhoc", []), function (widget) {
                if ("component" == Util.goodValue(widget.type)) {
                    var item = {};
                    var found = _.find(moduleConfig.components, {id: widget.id});
                    //if (found && found.enabled) {
                    if (found) {
                        item.title = found.title;
                        item.id = widget.id;
                        $scope.widgetsAdhoc.push(item);
                    }
                }
            });

            if (ObjectService.ObjectTypes.TASK == $stateParams.type) {
                $scope.widgets = $scope.widgetsTask;
            } else if (ObjectService.ObjectTypes.ADHOC_TASK == $stateParams.type) {
                $scope.widgets = $scope.widgetsAdhoc;
            }

            return moduleConfig;
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

        $scope.$on('task-updated', function (e, data) {
            var z = 1;
        });

        $scope.widgetData = {};
        TaskInfoService.getTaskInfo($stateParams.id).then(function (taskInfo) {
            $scope.taskInfo = taskInfo;

            $scope.widgetData["details"] = Util.goodMapValue($scope.taskInfo, "details");
            $scope.widgetData["reworkdetails"] = Util.goodMapValue($scope.taskInfo, "reworkInstructions");
            $scope.widgetData["docsreview"] = Util.goodMapValue($scope.taskInfo, "documentUnderReview.fileName");

            TaskHistoryService.queryTaskHistory($scope.taskInfo).then(function (taskHistory) {
                $scope.widgetData["workflow"] = taskHistory.length;
            });

            return taskInfo;
        });

        $scope.widgetData["attachments"] = "documents data";

        ObjectNoteService.queryRejectComments(ObjectService.ObjectTypes.TASK, $stateParams.id)
            .then(function (notes) {
                $scope.widgetData["rejcomments"] = Util.goodValue(notes.length, 0);
            });

        ObjectNoteService.queryNotes(ObjectService.ObjectTypes.TASK, $stateParams.id)
            .then(function (notes) {
                $scope.widgetData["notes"] = Util.goodValue(notes.length, 0);
            });

        ObjectAuditService.queryAudit(ObjectService.ObjectTypes.TASK, $stateParams.id, 0, 10, "", "")
            .then(function (auditData) {
                $scope.widgetData["history"] = Util.goodValue(auditData.totalCount, 0);
            });

        ObjectSignatureService.findSignatures(ObjectService.ObjectTypes.TASK, $stateParams.id)
            .then(function (signatures) {
                $scope.widgetData["signatures"] = Util.goodValue(signatures.length, 0);
            });
    }
])
;