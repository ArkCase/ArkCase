'use strict';

angular.module('complaints').controller('Complaints.CalendarController', ['$scope', '$stateParams', 'Complaint.InfoService', 'Helper.ObjectBrowserService', 'ObjectService'
    , function ($scope, $stateParams, ComplaintInfoService, HelperObjectBrowserService, ObjectService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "calendar"
            , retrieveObjectInfo: ComplaintInfoService.getComplaintInfo
            , validateObjectInfo: ComplaintInfoService.validateComplaintInfo
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var onObjectInfoRetrieved = function(objectInfo) {
            $scope.objectInfoRetrieved = true;
            $scope.objectType = ObjectService.ObjectTypes.COMPLAINT;
            $scope.objectId = objectInfo.id;
        };
    }
]);