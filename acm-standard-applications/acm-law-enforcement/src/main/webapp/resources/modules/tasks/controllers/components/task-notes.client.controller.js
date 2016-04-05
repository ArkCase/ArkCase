'use strict';

angular.module('tasks').controller('Tasks.NotesController', ['$scope', '$stateParams', 'ConfigService', 'ObjectService'
    , function ($scope, $stateParams, ConfigService, ObjectService) {

        ConfigService.getComponentConfig("tasks", "notes").then(function (config) {
            $scope.notesInit = {
                objectType: ObjectService.ObjectTypes.TASK,
                currentObjectId: $stateParams.id
            };
            $scope.config = config;
            return config;
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
                onObjectInfoRetrieved : function(
                        objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
            });

        var onConfigRetrieved = function(config) {
        
            $scope.config = config;
        
        };

        var onObjectInfoRetrieved = function(objectInfo) {
        
            $scope.objectInfo = objectInfo;
            $scope.notesInit.notesEnabled = !objectInfo.completed;
        
        };

    }

]);