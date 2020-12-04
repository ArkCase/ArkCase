'use strict';

angular.module('complaints').controller(
        'ComplaintsController',
        [ '$scope', '$state', '$stateParams', 'UtilService', 'ConfigService', 'Complaint.InfoService', 'ObjectService', 'Helper.ObjectBrowserService', 'Object.CalendarService', '$translate',
                function($scope, $state, $stateParams, Util, ConfigService, ComplaintInfoService, ObjectService, HelperObjectBrowserService, CalendarService, $translate) {

                    $scope.isNodeDisabled = function(node) {
                        return HelperObjectBrowserService.isNodeDisabled('complaints', $translate.instant(node));
                    }

                    CalendarService.getCalendarIntegration('COMPLAINT').then(function(calendarAdminConfigRes) {
                        HelperObjectBrowserService.toggleNodeDisabled('complaints', 'Calendar', !calendarAdminConfigRes.data);
                    });

                    new HelperObjectBrowserService.Content({
                        scope: $scope,
                        state: $state,
                        stateParams: $stateParams,
                        moduleId: "complaints",
                        resetObjectInfo: ComplaintInfoService.resetComplaintInfo,
                        getObjectInfo: ComplaintInfoService.getComplaintInfo,
                        updateObjectInfo: ComplaintInfoService.updateComplaintInfo,
                        getObjectIdFromInfo: function(complaintInfo) {
                            return Util.goodMapValue(complaintInfo, "complaintId");
                        },
                        getObjectTypeFromInfo: function(objectInfo) {
                            return ObjectService.ObjectTypes.COMPLAINT;
                        }
                    });
                } ]);