'use strict';

angular.module('cases').controller('Cases.NotesController',
        [ '$scope', '$stateParams', '$translate', 'ConfigService', 'ObjectService', 'Case.InfoService', 'Helper.ObjectBrowserService', function($scope, $stateParams, $translate, ConfigService, ObjectService, CaseInfoService, HelperObjectBrowserService) {
            var componentHelper = new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "cases",
                componentId: "notes",
                retrieveObjectInfo: CaseInfoService.getCaseInfo,
                validateObjectInfo: CaseInfoService.validateCaseInfo,
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
                noteTitle: $translate.instant("cases.comp.notes.title"),
                objectType: ObjectService.ObjectTypes.CASE_FILE,
                currentObjectId: $stateParams.id,
                noteType: "GENERAL",
                parentTitle: "",
                showAllNotes: true
            };

            var onObjectInfoRetrieved = function(objectInfo) {
                $scope.objectInfo = objectInfo;
                if ($scope.notesInit) {
                    $scope.notesInit.parentTitle = $scope.objectInfo.caseNumber;
                }
            };

            var onTranslateChangeSuccess = function(data) {
                if ($scope.notesInit) {
                    $scope.notesInit.noteTitle = $translate.instant("cases.comp.notes.title");
                }
            };

            ConfigService.getComponentConfig("cases", "notes").then(function(config) {
                $scope.notesInit = {
                    objectType: ObjectService.ObjectTypes.CASE_FILE,
                    currentObjectId: $stateParams.id,
                    noteType: "GENERAL",
                    showAllNotes: true
                };
                $scope.config = config;
                return config;
            });
        } ]);