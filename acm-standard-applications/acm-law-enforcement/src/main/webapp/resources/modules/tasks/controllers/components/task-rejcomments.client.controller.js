'use strict';

angular.module('tasks').controller('Tasks.RejectCommentsController', ['$scope', '$stateParams', '$translate',
    'ConfigService', 'ObjectService'
    , function ($scope, $stateParams, $translate, ConfigService, ObjectService) {

        ConfigService.getComponentConfig("tasks", "rejcomments").then(function (config) {
            $scope.notesInit = {
                objectType: ObjectService.ObjectTypes.TASK,
                currentObjectId: $stateParams.id,
                noteType: "REJECT_COMMENT",
                noteTitle: $translate.instant("tasks.comp.rejcomments.title")
            };
            $scope.config = config;
            return config;
        });
    }
]);
