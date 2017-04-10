'use strict';

angular.module('tasks').controller('Tasks.NotesController', ['$scope', '$stateParams', 'ConfigService', 'ObjectService', 'Helper.ObjectBrowserService'
    , 'PermissionsService', 'Task.InfoService'
    , function ($scope, $stateParams, ConfigService, ObjectService, HelperObjectBrowserService, PermissionsService, TaskInfoService) {

        var componentHelper = new HelperObjectBrowserService.Component(
            {
                scope : $scope,
                stateParams : $stateParams,
                moduleId : "tasks",
                componentId : "notes",
                retrieveObjectInfo : TaskInfoService.getTaskInfo,
                validateObjectInfo : TaskInfoService.validateTaskInfo,
                onConfigRetrieved : function(
                    componentConfig) {
                    return onConfigRetrieved(componentConfig);
                },
                onObjectInfoRetrieved : function(objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
            });

        var onConfigRetrieved = function(config) {

            $scope.config = config;

        };

        $scope.notesInit = {
            noteTitle: "Notes",
            objectType: ObjectService.ObjectTypes.TASK,
            currentObjectId: $stateParams.id,
            parentTitle: "",
            isReadOnly: false
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.notesInit.parentTitle = $scope.objectInfo.parentObjectName;
            PermissionsService.getActionPermission('editNote', objectInfo).then(function (result) {
                $scope.notesInit.isReadOnly = !result;
            });
        };
    }
]);