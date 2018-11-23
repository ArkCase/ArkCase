'use strict';

angular.module('complaints').controller('Complaints.DetailsController',
        [ '$scope', '$stateParams', '$translate', 'UtilService', 'ConfigService', 'Complaint.InfoService', 'MessageService', 'Helper.ObjectBrowserService', 'Mentions.Service', function($scope, $stateParams, $translate, Util, ConfigService, ComplaintInfoService, MessageService, HelperObjectBrowserService, MentionsService) {

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
            MentionsService.getUsers().then(function (users) {

            });

            $scope.getMentionedUsers = function (item) {
                $scope.emailAddresses.push(item.email_lcs);
                $scope.usersMentioned.push('@' + item.name);
                return '@' + item.name;
            };
            // -----------------------  end mention   ----------------------------


            $scope.options = {
                focus: true,
                dialogsInBody: true,
                hint: {
                    mentions: ['jayden', 'sam', 'alvin', 'david'],
                    match: /\B@(\w*)$/,
                    search: function (keyword, callback) {
                        callback($.grep(this.mentions, function (item) {
                            return item.indexOf(keyword) == 0;
                        }));
                    },
                    content: function (item) {
                        return '@' + item;
                    }
                }
            };

            $('#summernote').summernote({
                placeholder: 'Hello bootstrap 4',
                tabsize: 2,
                height: 100
            });

            $scope.saveDetails = function() {
                var complaintInfo = Util.omitNg($scope.objectInfo);
                ComplaintInfoService.saveComplaintInfo(complaintInfo).then(function(complaintInfo) {
                    MessageService.info($translate.instant("complaints.comp.details.informSaved"));
                    return complaintInfo;
                });
            };
        } ]);