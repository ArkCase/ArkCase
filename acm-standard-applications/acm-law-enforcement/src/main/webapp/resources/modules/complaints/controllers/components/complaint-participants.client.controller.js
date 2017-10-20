'use strict';

angular.module('complaints').controller('Complaints.ParticipantsController', ['$scope', '$translate', 'Complaint.InfoService', 'ObjectService'
    , function ($scope, $translate, ComplaintInfoService, ObjectService) {

        $scope.participantsInit = {
            moduleId: 'complaints',
            componentId: 'participants',
            showReplaceChildrenParticipants: true,
            retrieveObjectInfo: ComplaintInfoService.getComplaintInfo,
            validateObjectInfo: ComplaintInfoService.validateComplaintInfo,
            saveObjectInfo: ComplaintInfoService.saveComplaintInfo,
            objectType: ObjectService.ObjectTypes.COMPLAINT,
            participantsTitle: $translate.instant("complaints.comp.participants.title")
        }
    }
]);
