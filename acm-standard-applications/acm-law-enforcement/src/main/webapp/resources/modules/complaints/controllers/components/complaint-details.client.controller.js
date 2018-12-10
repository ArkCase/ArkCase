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
                    $scope.paramsSummernote = {
                        emailAddresses: [],
                        usersMentioned: []
                    };

                    $scope.saveDetails = function() {
                        var complaintInfo = Util.omitNg($scope.objectInfo);
                        ComplaintInfoService.saveComplaintInfo(complaintInfo).then(function(complaintInfo) {
                            MentionsService.sendEmailToMentionedUsers($scope.paramsSummernote.emailAddresses, $scope.paramsSummernote.usersMentioned, ObjectService.ObjectTypes.COMPLAINT, "DETAILS", complaintInfo.complaintId, complaintInfo.details);
                            MessageService.info($translate.instant("complaints.comp.details.informSaved"));
                            return complaintInfo;
                        });
                    };
                } ]);