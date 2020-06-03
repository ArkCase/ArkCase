'use strict';

angular.module('consultations').controller(
    'Consultations.DetailsController',
    [ '$scope', '$stateParams', '$translate', 'UtilService', 'ConfigService', 'Consultation.InfoService', 'MessageService', 'Helper.ObjectBrowserService', 'Mentions.Service', 'ObjectService',
        function($scope, $stateParams, $translate, Util, ConfigService, ConsultationInfoService, MessageService, HelperObjectBrowserService, MentionsService, ObjectService) {

            new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "consultations",
                componentId: "details",
                retrieveObjectInfo: ConsultationInfoService.getCaseInfo,
                validateObjectInfo: ConsultationInfoService.validateCaseInfo
            });

            // ---------------------   mention   ---------------------------------
            $scope.paramsSummernote = {
                emailAddresses: [],
                usersMentioned: []
            };

            $scope.saveDetailsSummary = function() {
                var consultationInfo = Util.omitNg($scope.objectInfo);
                ConsultationInfoService.saveCaseInfo(consultationInfo).then(function(consultationInfo) {
                    MessageService.info($translate.instant("consultations.comp.details.consultationSummary.informSaved"));
                    return consultationInfo;
                });
            };


            $scope.saveDetails = function() {
                var consultationInfo = Util.omitNg($scope.objectInfo);
                ConsultationInfoService.saveCaseInfo(consultationInfo).then(function(consultationInfo) {
                    MentionsService.sendEmailToMentionedUsers($scope.paramsSummernote.emailAddresses, $scope.paramsSummernote.usersMentioned, ObjectService.ObjectTypes.CONSULTATION, "DETAILS", consultationInfo.id, consultationInfo.details);
                    MessageService.info($translate.instant("consultations.comp.details.informSaved"));
                    return consultationInfo;
                });
            };
        } ]);