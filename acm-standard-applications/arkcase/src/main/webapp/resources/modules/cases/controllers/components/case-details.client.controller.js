'use strict';

angular.module('cases').controller(
        'Cases.DetailsController',
        [ '$scope', '$stateParams', '$translate', 'UtilService', 'ConfigService', 'Case.InfoService', 'MessageService', 'Helper.ObjectBrowserService', 'Mentions.Service', 'ObjectService',
                function($scope, $stateParams, $translate, Util, ConfigService, CaseInfoService, MessageService, HelperObjectBrowserService, MentionsService, ObjectService) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "cases",
                        componentId: "details",
                        retrieveObjectInfo: CaseInfoService.getCaseInfo,
                        validateObjectInfo: CaseInfoService.validateCaseInfo
                    });

                    // ---------------------   mention   ---------------------------------
                    $scope.paramsSummernote = {
                        emailAddresses: [],
                        usersMentioned: []
                    };

                    $scope.saveDetailsSummary = function() {
                        var caseInfo = Util.omitNg($scope.objectInfo);
                        CaseInfoService.saveCaseInfo(caseInfo).then(function(caseInfo) {
                            MessageService.info($translate.instant("cases.comp.details.caseSummary.informSaved"));
                            return caseInfo;
                        });
                    };


                    $scope.saveDetails = function() {
                        var caseInfo = Util.omitNg($scope.objectInfo);
                        CaseInfoService.saveCaseInfo(caseInfo).then(function(caseInfo) {
                            MentionsService.sendEmailToMentionedUsers($scope.paramsSummernote.emailAddresses, $scope.paramsSummernote.usersMentioned, ObjectService.ObjectTypes.CASE_FILE, "DETAILS", caseInfo.id, caseInfo.details);
                            MessageService.info($translate.instant("cases.comp.details.informSaved"));
                            return caseInfo;
                        });
                    };
                } ]);