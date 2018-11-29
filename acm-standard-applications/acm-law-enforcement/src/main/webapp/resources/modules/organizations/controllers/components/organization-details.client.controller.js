'use strict';

angular.module('organizations').controller(
        'Organizations.DetailsController',
        [ '$scope', '$stateParams', '$translate', 'UtilService', 'ConfigService', 'Organization.InfoService', 'MessageService', 'Helper.ObjectBrowserService', 'ObjectService', 'PermissionsService', '$timeout', 'Mentions.Service',
                function($scope, $stateParams, $translate, Util, ConfigService, OrganizationInfoService, MessageService, HelperObjectBrowserService, ObjectService, PermissionsService, $timeout, MentionsService) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "organizations",
                        componentId: "details",
                        retrieveObjectInfo: OrganizationInfoService.getOrganizationInfo,
                        validateObjectInfo: OrganizationInfoService.validateOrganizationInfo
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

                    $scope.init = function() {
                        PermissionsService.getActionPermission('editOrganization', $scope.objectInfo, {
                            objectType: ObjectService.ObjectTypes.ORGANIZATION
                        }).then(function(result) {
                            if (!result) {
                                $timeout(function() {
                                    $scope.editor.summernote('disable');
                                }, 0);
                            }
                        });
                    };

                    $scope.saveDetails = function() {
                        var organizationInfo = Util.omitNg($scope.objectInfo);
                        OrganizationInfoService.saveOrganizationInfo(organizationInfo).then(function(organizationInfo) {
                            MentionsService.sendEmailToMentionedUsers($scope.emailAddresses, $scope.usersMentioned, ObjectService.ObjectTypes.ORGANIZATION, "DETAILS", organizationInfo.organizationId, organizationInfo.details);
                            MessageService.info($translate.instant("organizations.comp.details.informSaved"));
                            return organizationInfo;
                        });
                    };
                } ]);