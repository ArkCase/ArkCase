'use strict';

angular.module('consultations').controller(
    'ConsultationsController',
    ['$scope', '$stateParams', '$state', '$translate', 'UtilService', 'ConfigService', 'Consultation.InfoService', 'ObjectService', 'Helper.ObjectBrowserService', 'Dashboard.DashboardService', 'Object.CalendarService', 'Admin.ObjectTitleConfigurationService',
        function ($scope, $stateParams, $state, $translate, Util, ConfigService, ConsultationInfoService, ObjectService, HelperObjectBrowserService, DashboardService, CalendarService, AdminObjectTitleConfigurationService) {

            $scope.isNodeDisabled = function(node) {
                return HelperObjectBrowserService.isNodeDisabled('consultations', $translate.instant(node));
            }

            CalendarService.getCalendarIntegration('CONSULTATION').then(function(calendarAdminConfigRes) {
                HelperObjectBrowserService.toggleNodeDisabled('consultations', 'Calendar', !calendarAdminConfigRes.data);
            });

            AdminObjectTitleConfigurationService.getObjectTitleConfiguration().then(function (configTitleData) {
                var disableSuggestedConsultations = !configTitleData.data.CASE_FILE.enableTitleField;
                HelperObjectBrowserService.toggleNodeDisabled('consultations', 'Suggested Consultations', disableSuggestedConsultations);
            });

            new HelperObjectBrowserService.Content({
                scope: $scope,
                state: $state,
                stateParams: $stateParams,
                moduleId: "consultations",
                resetObjectInfo: ConsultationInfoService.resetConsultationInfo,
                getObjectInfo: ConsultationInfoService.getConsultationInfo,
                updateObjectInfo: ConsultationInfoService.updateConsultationInfo,
                getObjectTypeFromInfo: function(objectInfo) {
                    return ObjectService.ObjectTypes.CONSULTATION;
                }
            });

        } ]);