'use strict';

angular.module('tasks').controller('Tasks.RejectCommentsController', ['$scope', '$stateParams', '$translate',
    'ConfigService', 'ObjectService', "Helper.ObjectBrowserService", "Task.InfoService", "PermissionsService"
    , function ($scope, $stateParams, $translate, ConfigService, ObjectService, HelperObjectBrowserService, TaskInfoService, PermissionsService) {

        ConfigService.getComponentConfig("tasks", "rejcomments").then(function (config) {
            $scope.notesInit = {
                objectType: ObjectService.ObjectTypes.TASK,
                currentObjectId: $stateParams.id,
                noteType: "REJECT_COMMENT",
                noteTitle: $translate.instant("tasks.comp.rejcomments.title"),
                isReadOnly: false
            };
            $scope.config = config;
            return config;
        });

        var componentHelper = new HelperObjectBrowserService.Component(
            {
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "tasks",
                componentId: "rejcomments",
                retrieveObjectInfo: TaskInfoService.getTaskInfo,
                validateObjectInfo: TaskInfoService.validateTaskInfo,
                onConfigRetrieved: function (componentConfig) {
                    return onConfigRetrieved(componentConfig);
                },
                onObjectInfoRetrieved: function (objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
            });

        var onConfigRetrieved = function (config) {
            $scope.config = config;
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            PermissionsService.getActionPermission('editRejComment', objectInfo).then(function (result) {
                $scope.notesInit.isReadOnly = !result;
            });
        };
    }
]);
