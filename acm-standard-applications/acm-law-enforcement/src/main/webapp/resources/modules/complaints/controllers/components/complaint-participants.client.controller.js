'use strict';

angular.module('complaints').controller('Complaints.ParticipantsController', ['$scope', 'Complaint.InfoService', 'ObjectService'
    , function ($scope, ComplaintInfoService, ObjectService) {

        $scope.participantsInit = {
            moduleId: 'complaints',
            componentId: 'participants',
            retrieveObjectInfo: ComplaintInfoService.getComplaintInfo,
            validateObjectInfo: ComplaintInfoService.validateComplaintInfo,
            saveObjectInfo: ComplaintInfoService.saveComplaintInfo,
            objectType: ObjectService.ObjectTypes.COMPLAINT
        }
    }
]);
