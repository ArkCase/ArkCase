'use strict';

angular.module('cases').controller('Cases.NotesController', ['$scope', '$stateParams', 'ConfigService', 'ObjectService'
    , 'Case.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, ConfigService, ObjectService, CaseInfoService, HelperObjectBrowserService) {

        var componentHelper = new HelperObjectBrowserService.Component(
            {
                scope : $scope,
                stateParams : $stateParams,
                moduleId : "cases",
                componentId : "notes",
                retrieveObjectInfo: CaseInfoService.getCaseInfo,
                validateObjectInfo: CaseInfoService.validateCaseInfo,
                onConfigRetrieved : function(
                    componentConfig) {
                    return onConfigRetrieved(componentConfig);
                },
                onObjectInfoRetrieved : function(objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
            });

        var onConfigRetrieved = function(config) {

            $scope.config = config;

        };

        var onObjectInfoRetrieved = function(objectInfo) {

            $scope.objectInfo = objectInfo;
            $scope.parentObjectTitle = $scope.objectInfo.caseNumber;

            $scope.notesInit = {
                noteTitle: "Notes",
                objectType: ObjectService.ObjectTypes.CASE_FILE,
                currentObjectId: $stateParams.id,
                parentTitle: $scope.parentObjectTitle,
                noteType: "GENERAL"
            };

        };

    }
]);