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
                        validateObjectInfo: OrganizationInfoService.validateOrganizationInfo,
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                        $scope.permissions = {
                            actionName: 'editOrganization',
                            objectProperties: objectInfo,
                            opts: {
                                objectType: ObjectService.ObjectTypes.ORGANIZATION
                            }
                        };
                    };

                    // ---------------------   mention   ---------------------------------
                    $scope.paramsSummernote = {
                        emailAddresses: [],
                        usersMentioned: [],
                    };

                    $scope.saveDetails = function() {
                        var organizationInfo = Util.omitNg($scope.objectInfo);
                        OrganizationInfoService.saveOrganizationInfo(organizationInfo).then(function(organizationInfo) {
                            MentionsService.sendEmailToMentionedUsers($scope.paramsSummernote.emailAddresses, $scope.paramsSummernote.usersMentioned, ObjectService.ObjectTypes.ORGANIZATION, "DETAILS", organizationInfo.organizationId, organizationInfo.details);
                            MessageService.info($translate.instant("organizations.comp.details.informSaved"));
                            return organizationInfo;
                        });
                    };
                } ]);