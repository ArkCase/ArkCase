'use strict';

angular.module('complaints').controller('Complaints.NotesController', ['$scope', '$stateParams', 'ConfigService', 'ObjectService'
    , 'Complaint.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, ConfigService, ObjectService, ComplaintInfoService, HelperObjectBrowserService) {

        var componentHelper = new HelperObjectBrowserService.Component(
            {
                scope : $scope,
                stateParams : $stateParams,
                moduleId : "complaints",
                componentId : "notes",
                retrieveObjectInfo: ComplaintInfoService.getComplaintInfo,
                validateObjectInfo: ComplaintInfoService.validateComplaintInfo,
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
            $scope.parentObjectTitle = $scope.objectInfo.complaintNumber;

            $scope.notesInit = {
                noteTitle: "Notes",
                objectType: ObjectService.ObjectTypes.COMPLAINT,
                currentObjectId: $stateParams.id,
                parentTitle: $scope.parentObjectTitle,
                noteType: "GENERAL"
            };

        };
    }
]);