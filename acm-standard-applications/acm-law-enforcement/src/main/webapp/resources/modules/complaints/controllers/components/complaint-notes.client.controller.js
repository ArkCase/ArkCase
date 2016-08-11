'use strict';

angular.module('complaints').controller('Complaints.NotesController', ['$scope', '$stateParams', 'ConfigService', 'ObjectService'
    , function ($scope, $stateParams, ConfigService, ObjectService) {

        ConfigService.getComponentConfig("complaints", "notes").then(function (config) {
            $scope.notesInit = {
                objectType: ObjectService.ObjectTypes.COMPLAINT,
                currentObjectId: $stateParams.id
            };
            config.enableSorting = true; //temp fix before find out why the flag is not set by the config service
            $scope.config = config;
            return config;
        });
    }
]);