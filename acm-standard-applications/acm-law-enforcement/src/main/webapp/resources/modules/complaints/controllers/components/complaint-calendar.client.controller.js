'use strict';

angular.module('complaints').controller('Complaints.CalendarController', ['$scope', '$stateParams', 'Complaint.InfoService'
    , 'Helper.ObjectBrowserService', 'ObjectService', 'Admin.CalendarConfigurationService'
    , function ($scope, $stateParams, ComplaintInfoService, HelperObjectBrowserService, ObjectService, CalendarConfigurationService) {

        $scope.objectInfoRetrieved = false;

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
            CalendarConfigurationService.getCurrentCalendarConfiguration().then(function (calendarAdminConfigRes) {
                $scope.objectType = ObjectService.ObjectTypes.COMPLAINT;
                $scope.objectId = objectInfo.complaintId;
                $scope.eventSources = [];
                if(calendarAdminConfigRes.data.configurationsByType['COMPLAINT'].integrationEnabled)
                {
                    $scope.objectInfoRetrieved = true;
                } else {
                    MessageService.info('Calendar Integration Configuration Not Enabled');
                    $scope.objectInfoRetrieved = false;
                }
            });
        };
    }
]);