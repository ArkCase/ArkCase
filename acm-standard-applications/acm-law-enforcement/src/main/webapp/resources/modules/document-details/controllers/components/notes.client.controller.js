'use strict';

angular.module('document-details').controller('Document.NotesController', ['$scope', '$stateParams', 'ConfigService', 'ObjectService'
    , function ($scope, $stateParams, ConfigService, ObjectService) {

        $scope.$on('document-data', updateVersionHistory);
        $scope.versions = [];

        function updateVersionHistory(event,documentDetails){
            if(documentDetails.versions && documentDetails.versions.length){
                $scope.versions = documentDetails.versions;
            }
        }

        $scope.$watchCollection('versions', function(newValue, oldValue){
            if(newValue && newValue.length){
				ConfigService.getComponentConfig("document-details", "notes").then(function (config) {
            	   $scope.notesInit = {
                        objectType: ObjectService.ObjectTypes.FILE,
                        currentObjectId: $stateParams.id,
                        tag: newValue.length
                    };
            	   $scope.config = config;
            	   return config;
        	    });
            }

        });
    }
]);