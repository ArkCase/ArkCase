'use strict';

angular.module('complaints').controller(
        'Complaints.CalendarController',
        [ '$scope', '$stateParams', 'Complaint.InfoService', 'Helper.ObjectBrowserService', 'ObjectService', 'Admin.CalendarConfigurationService', 'MessageService', 'Object.CalendarService',
                function($scope, $stateParams, ComplaintInfoService, HelperObjectBrowserService, ObjectService, CalendarConfigurationService, MessageService, CalendarService) {
                    $scope.objectInfoRetrieved = false;

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "cases",
                        componentId: "calendar",
                        retrieveObjectInfo: ComplaintInfoService.getComplaintInfo,
                        validateObjectInfo: ComplaintInfoService.validateComplaintInfo,
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectType = ObjectService.ObjectTypes.COMPLAINT;
                        $scope.objectId = objectInfo.complaintId;
                        $scope.eventSources = [];
                        CalendarService.isCalendarConfigurationEnabled(ObjectService.ObjectTypes.COMPLAINT).then(function(data) {
                            $scope.objectInfoRetrieved = data;
                        });
                    };
                } ]);