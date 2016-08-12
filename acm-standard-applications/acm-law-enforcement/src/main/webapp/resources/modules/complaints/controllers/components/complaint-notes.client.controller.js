'use strict';

angular.module('complaints').controller('Complaints.NotesController', ['$scope', '$stateParams', 'ConfigService', 'ObjectService'
    , function ($scope, $stateParams, ConfigService, ObjectService) {

        ConfigService.getComponentConfig("complaints", "notes").then(function (config) {
            $scope.notesInit = {
                objectType: ObjectService.ObjectTypes.COMPLAINT,
                currentObjectId: $stateParams.id
            };
            $scope.config = config;
            return config;
        });
    }
]);