'use strict';

angular.module('complaints').controller(
        'Complaints.DetailsController',
        [ '$scope', '$stateParams', '$translate', 'UtilService', 'ConfigService', 'Complaint.InfoService', 'MessageService', 'Helper.ObjectBrowserService', 'Mentions.Service', 'ObjectService',
                function($scope, $stateParams, $translate, Util, ConfigService, ComplaintInfoService, MessageService, HelperObjectBrowserService, MentionsService, ObjectService) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "complaints",
                        componentId: "details",
                        retrieveObjectInfo: ComplaintInfoService.getComplaintInfo,
                        validateObjectInfo: ComplaintInfoService.validateComplaintInfo
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
                        var complaintInfo = Util.omitNg($scope.objectInfo);
                        ComplaintInfoService.saveComplaintInfo(complaintInfo).then(function(complaintInfo) {
                            MentionsService.sendEmailToMentionedUsers($scope.emailAddresses, $scope.usersMentioned, ObjectService.ObjectTypes.COMPLAINT, "DETAILS", complaintInfo.complaintId, complaintInfo.details);
                            MessageService.info($translate.instant("complaints.comp.details.informSaved"));
                            return complaintInfo;
                        });
                    };
                } ]);