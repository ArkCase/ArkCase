'use strict';

angular.module('request-info').controller('RequestInfo.NotesController', [ '$scope', '$stateParams', 'ConfigService', 'ObjectService', function($scope, $stateParams, ConfigService, ObjectService) {

    ConfigService.getComponentConfig("request-info", "notes").then(function(config) {
        $scope.notesInit = {
            objectType: ObjectService.ObjectTypes.CASE_FILE,
            currentObjectId: $stateParams.id,
            noteType: 'GENERAL',
            showAllNotes: true
        };
        $scope.config = config;
        return config;
    });
} ]);