'use strict';

angular.module('time-tracking').controller(
        'TimeTracking.DetailsController',
        [ '$scope', '$stateParams', '$translate', 'UtilService', 'ConfigService', 'TimeTracking.InfoService', 'MessageService', 'Helper.ObjectBrowserService', 'ObjectService', 'Mentions.Service',
                function($scope, $stateParams, $translate, Util, ConfigService, TimeTrackingInfoService, MessageService, HelperObjectBrowserService, ObjectService, MentionsService) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "time-tracking",
                        componentId: "details",
                        retrieveObjectInfo: TimeTrackingInfoService.getTimesheetInfo,
                        validateObjectInfo: TimeTrackingInfoService.validateTimesheet
                    });

                    // ---------------------   mention   ---------------------------------
                    $scope.paramsSummernote = {
                        emailAddresses: [],
                        usersMentioned: []
                    };

                    $scope.saveDetails = function() {
                        var timesheetInfo = Util.omitNg($scope.objectInfo);
                        TimeTrackingInfoService.saveTimesheetInfo(timesheetInfo, "Save").then(function(timesheetInfo) {
                            MentionsService.sendEmailToMentionedUsers($scope.paramsSummernote.emailAddresses, $scope.paramsSummernote.usersMentioned, ObjectService.ObjectTypes.TIMESHEET, "DETAILS", timesheetInfo.id, timesheetInfo.details);
                            MessageService.info($translate.instant("timeTracking.comp.details.informSaved"));
                            return timesheetInfo;
                        });
                    };

                } ]);