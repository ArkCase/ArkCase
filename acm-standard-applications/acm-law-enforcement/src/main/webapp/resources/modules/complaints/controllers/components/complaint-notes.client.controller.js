'use strict';

angular.module('complaints').controller('Complaints.NotesController',
        [ '$scope', '$stateParams', '$translate', 'ConfigService', 'ObjectService', 'Complaint.InfoService', 'Helper.ObjectBrowserService', function($scope, $stateParams, $translate, ConfigService, ObjectService, ComplaintInfoService, HelperObjectBrowserService) {

            var componentHelper = new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "complaints",
                componentId: "notes",
                retrieveObjectInfo: ComplaintInfoService.getComplaintInfo,
                validateObjectInfo: ComplaintInfoService.validateComplaintInfo,
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
                noteTitle: $translate.instant("complaints.comp.notes.title"),
                objectType: ObjectService.ObjectTypes.COMPLAINT,
                currentObjectId: $stateParams.id,
                parentTitle: "",
                noteType: "GENERAL"
            };

            var onObjectInfoRetrieved = function(objectInfo) {
                $scope.objectInfo = objectInfo;
                if ($scope.notesInit) {
                    $scope.notesInit.parentTitle = $scope.objectInfo.complaintNumber;
                }
            };

            var onTranslateChangeSuccess = function(data) {
                if ($scope.notesInit) {
                    $scope.notesInit.noteTitle = $translate.instant("complaints.comp.notes.title");
                }
            };
        } ]);