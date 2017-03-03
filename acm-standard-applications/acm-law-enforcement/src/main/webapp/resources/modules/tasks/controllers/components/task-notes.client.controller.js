'use strict';

angular.module('tasks').controller('Tasks.NotesController', ['$scope', '$stateParams', 'ConfigService', 'ObjectService', 'Helper.ObjectBrowserService'
    , 'PermissionsService', 'Task.InfoService'
    , function ($scope, $stateParams, ConfigService, ObjectService, HelperObjectBrowserService, PermissionsService, TaskInfoService) {

        ConfigService.getComponentConfig("tasks", "notes").then(function (config) {
            TaskInfoService.getTaskInfo($stateParams.id).then(function (data) {
                $scope.parentTitleFromTask = data.parentObjectName;

                $scope.notesInit = {
                    objectType: ObjectService.ObjectTypes.TASK,
                    currentObjectId: $stateParams.id,
                    parentTitle: $scope.parentTitleFromTask,
                    isReadOnly: false
                };
                $scope.config = config;
                return config;
            });

        });

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

        var onObjectInfoRetrieved = function(objectInfo) {

            $scope.objectInfo = objectInfo;
            PermissionsService.getActionPermission('editNote', objectInfo).then(function(result) {

                $scope.notesInit.isReadOnly = !result;

            });

        };
    }

]);