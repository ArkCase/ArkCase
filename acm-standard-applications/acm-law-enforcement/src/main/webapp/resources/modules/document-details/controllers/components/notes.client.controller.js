'use strict';

angular.module('document-details').controller('Document.NotesController', ['$scope', '$stateParams', 'ConfigService', 'ObjectService'
    , function ($scope, $stateParams, ConfigService, ObjectService)
    {

        $scope.$on('document-data', getActiveVersionTag);
        $scope.activeVersion = "";

        function getActiveVersionTag(event, documentDetails)
        {
            if (documentDetails.activeVersionTag)
            {
                $scope.activeVersion = documentDetails.activeVersionTag;
            }
        }

        $scope.$watchCollection('activeVersion', function (newValue, oldValue)
        {
            ConfigService.getComponentConfig("document-details", "notes").then(function (config)
            {
                $scope.notesInit =
                {
                    objectType: ObjectService.ObjectTypes.FILE,
                    currentObjectId: $stateParams.id,
                    tag: $scope.activeVersion
                };
                $scope.config = config;
                return config;
            });

        })
    }
]);