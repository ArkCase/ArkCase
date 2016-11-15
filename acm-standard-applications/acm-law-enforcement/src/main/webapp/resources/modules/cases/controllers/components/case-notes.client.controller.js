'use strict';

angular.module('cases').controller('Cases.NotesController', ['$scope', '$stateParams', 'ConfigService', 'ObjectService'
    , function ($scope, $stateParams, ConfigService, ObjectService) {

        ConfigService.getComponentConfig("cases", "notes").then(function (config) {
            $scope.notesInit = {
                objectType: ObjectService.ObjectTypes.CASE_FILE,
                currentObjectId: $stateParams.id,
                noteType: "GENERAL"
            };
            $scope.config = config;
            return config;
        });
    }
]);