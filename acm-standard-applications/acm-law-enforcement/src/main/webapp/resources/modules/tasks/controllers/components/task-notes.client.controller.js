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

    }

]);