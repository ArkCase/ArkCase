'use strict';

angular.module('document-details').controller('Document.NotesController', ['$scope', '$stateParams', 'ConfigService', 'ObjectService'
    , function ($scope, $stateParams, ConfigService, ObjectService) {

        ConfigService.getComponentConfig("document-details", "notes").then(function (config) {
            $scope.notesInit = {
                objectType: ObjectService.ObjectTypes.FILE,
                currentObjectId: $stateParams.id
            };
            $scope.config = config;
            return config;
        });
    }
]);

