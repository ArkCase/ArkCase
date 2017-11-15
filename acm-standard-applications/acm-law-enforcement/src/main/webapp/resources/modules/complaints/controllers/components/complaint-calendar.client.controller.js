'use strict';

angular.module('complaints').controller('Complaints.CalendarController', ['$scope', '$stateParams', 'Complaint.InfoService'
    , 'Helper.ObjectBrowserService', 'ObjectService', 'Object.CalendarService'
    , function ($scope, $stateParams, ComplaintInfoService, HelperObjectBrowserService, ObjectService, CalendarService) {

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
            CalendarService.getCalendarIntegration('COMPLAINT').then(function (calendarAdminConfigRes) {
                $scope.objectType = ObjectService.ObjectTypes.COMPLAINT;
                $scope.objectId = objectInfo.complaintId;
                if(calendarAdminConfigRes.data === true){
                    $scope.objectInfoRetrieved = true;
                }else{
                    MessageService.info('Calendar Integration Configuration Not Enabled');
                    $scope.objectInfoRetrieved = false;

                }
            });

        };
    }
]);