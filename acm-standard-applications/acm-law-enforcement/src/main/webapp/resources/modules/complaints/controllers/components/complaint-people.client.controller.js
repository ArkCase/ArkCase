'use strict';

angular.module('complaints').controller('Complaints.PeopleController', ['$scope', '$translate', 'ObjectService', 'Complaint.InfoService'
    , function ($scope, $translate, ObjectService, ComplaintInfoService) {

        $scope.peopleInit = {
            moduleId: 'complaints',
            componentId: 'people',
            retrieveObjectInfo: ComplaintInfoService.getComplaintInfo,
            validateObjectInfo: ComplaintInfoService.validateComplaintInfo,
            saveObjectInfo: ComplaintInfoService.saveComplaintInfo,
            objectType: ObjectService.ObjectTypes.COMPLAINT,
            objectInfoId: 'complaintId',
            peopleTitle: $translate.instant("complaints.comp.people.title")
        }
    }
]);