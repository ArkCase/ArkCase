'use strict';

angular.module('cases').controller('Cases.NotesController', ['$scope', '$stateParams', 'ConfigService', 'ObjectService'
    , 'Case.InfoService'
    , function ($scope, $stateParams, ConfigService, ObjectService, CaseInfoService) {

        ConfigService.getComponentConfig("cases", "notes").then(function (config) {
            CaseInfoService.getCaseInfo($stateParams.id).then(function (data) {
                $scope.parentTitleFromCase = data.caseNumber;

                $scope.notesInit = {
                    objectType: ObjectService.ObjectTypes.CASE_FILE,
                    currentObjectId: $stateParams.id,
                    parentTitle: $scope.parentTitleFromCase,
                    noteType: "GENERAL"
                };
                $scope.config = config;
                return config;
            });

        });
    }
]);