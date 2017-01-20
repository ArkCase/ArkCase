'use strict';

angular.module('complaints').controller('Complaints.NotesController', ['$scope', '$stateParams', 'ConfigService', 'ObjectService'
    , 'Complaint.InfoService'
    , function ($scope, $stateParams, ConfigService, ObjectService, ComplaintInfoService) {

        ConfigService.getComponentConfig("complaints", "notes").then(function (config) {
            ComplaintInfoService.getComplaintInfo($stateParams.id).then(function (data) {
                $scope.parentTitleFromComplaint = data.complaintNumber;

                $scope.notesInit = {
                    objectType: ObjectService.ObjectTypes.COMPLAINT,
                    currentObjectId: $stateParams.id,
                    parentTitle: $scope.parentTitleFromComplaint,
                    noteType: "GENERAL"
                };

            });

            $scope.config = config;
            return config;
        });
    }
]);