'use strict';

angular.module('consultations').controller('Consultations.NotesController',
    [ '$scope', '$stateParams', '$translate', 'ConfigService', 'ObjectService', 'Consultation.InfoService', 'Helper.ObjectBrowserService', function($scope, $stateParams, $translate, ConfigService, ObjectService, ConsultationInfoService, HelperObjectBrowserService) {

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope,
            stateParams: $stateParams,
            moduleId: "consultations",
            componentId: "notes",
            retrieveObjectInfo: ConsultationInfoService.getCaseInfo,
            validateObjectInfo: ConsultationInfoService.validateCaseInfo,
            onConfigRetrieved: function(componentConfig) {
                return onConfigRetrieved(componentConfig);
            },
            onObjectInfoRetrieved: function(objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            },
            onTranslateChangeSuccess: function(data) {
                onTranslateChangeSuccess(data);
            }
        });

        var onConfigRetrieved = function(config) {

            $scope.config = config;

        };

        $scope.notesInit = {
            noteTitle: $translate.instant("consultations.comp.notes.title"),
            objectType: ObjectService.ObjectTypes.CONSULTATION,
            currentObjectId: $stateParams.id,
            parentTitle: "",
            noteType: "GENERAL"
        };

        var onObjectInfoRetrieved = function(objectInfo) {
            $scope.objectInfo = objectInfo;
            if ($scope.notesInit) {
                $scope.notesInit.parentTitle = $scope.objectInfo.consultationNumber;
            }
        };

        var onTranslateChangeSuccess = function(data) {
            if ($scope.notesInit) {
                $scope.notesInit.noteTitle = $translate.instant("consultations.comp.notes.title");
            }
        };
    } ]);