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
                    $scope.emailAddresses = [];
                    $scope.usersMentioned = [];

                    // Obtains a list of all users in ArkCase
                    MentionsService.getUsers().then(function(users) {
                        $scope.people = [];
                        $scope.peopleEmails = [];
                        _.forEach(users, function(user) {
                            $scope.people.push(user.name);
                            $scope.peopleEmails.push(user.email_lcs);
                        });
                    });

                    $scope.options = {
                        focus: true,
                        dialogsInBody: true,
                        hint: {
                            mentions: $scope.people,
                            match: /\B@(\w*)$/,
                            search: function(keyword, callback) {
                                callback($.grep($scope.people, function(item) {
                                    return item.indexOf(keyword) == 0;
                                }));
                            },
                            content: function(item) {
                                var index = $scope.people.indexOf(item);
                                $scope.emailAddresses.push($scope.peopleEmails[index]);
                                $scope.usersMentioned.push('@' + item);
                                return '@' + item;
                            }
                        }
                    };
                    // -----------------------  end mention   ----------------------------

                    $scope.saveDetails = function() {
                        var timesheetInfo = Util.omitNg($scope.objectInfo);
                        TimeTrackingInfoService.saveTimesheetInfo(timesheetInfo, "Save").then(function(timesheetInfo) {
                            MentionsService.sendEmailToMentionedUsers($scope.emailAddresses, $scope.usersMentioned, ObjectService.ObjectTypes.TIMESHEET, "DETAILS", timesheetInfo.id, timesheetInfo.details);
                            MessageService.info($translate.instant("timeTracking.comp.details.informSaved"));
                            return timesheetInfo;
                        });
                    };

                } ]);