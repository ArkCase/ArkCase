'use strict';

angular.module('complaints').controller('Complaints.PeopleController', ['$scope', 'ObjectService', 'Complaint.InfoService'
    , function ($scope, ObjectService, ComplaintInfoService) {

        $scope.peopleInit = {
            moduleId: 'complaints',
            componentId: 'people',
            retrieveObjectInfo: ComplaintInfoService.getComplaintInfo,
            validateObjectInfo: ComplaintInfoService.validateComplaintInfo,
            saveObjectInfo: ComplaintInfoService.saveComplaintInfo,
            objectType: ObjectService.ObjectTypes.COMPLAINT,
            objectInfoId: 'complaintId'
        }
    }
]);