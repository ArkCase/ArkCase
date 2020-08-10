'use strict';

angular.module('cases').controller(
        'CasesController',
    ['$scope', '$stateParams', '$state', '$translate', 'UtilService', 'ConfigService', 'Case.InfoService', 'ObjectService', 'Helper.ObjectBrowserService', 'Dashboard.DashboardService', 'Object.CalendarService', 'Admin.ObjectTitleConfigurationService',
        function ($scope, $stateParams, $state, $translate, Util, ConfigService, CaseInfoService, ObjectService, HelperObjectBrowserService, DashboardService, CalendarService, AdminObjectTitleConfigurationService) {

                    $scope.isNodeDisabled = function(node) {
                        return HelperObjectBrowserService.isNodeDisabled('cases', $translate.instant(node));
                    }

                    CalendarService.getCalendarIntegration('CASE_FILE').then(function(calendarAdminConfigRes) {
                        HelperObjectBrowserService.toggleNodeDisabled('cases', 'Calendar', !calendarAdminConfigRes.data);
                    });

            AdminObjectTitleConfigurationService.getObjectTitleConfiguration().then(function (configTitleData) {
                var disableSuggestedCases = !configTitleData.data.CASE_FILE.enableTitleField;
                HelperObjectBrowserService.toggleNodeDisabled('cases', 'Suggested Requests', disableSuggestedCases);
            });

            $scope.currentRoute = $state.current.name;
                    $scope.$on("$stateChangeStart", function(event, nextUrl, currentUrl) {
                        $scope.currentRoute = nextUrl.name;
                    });

                    new HelperObjectBrowserService.Content({
                        scope: $scope,
                        state: $state,
                        stateParams: $stateParams,
                        moduleId: "cases",
                        resetObjectInfo: CaseInfoService.resetCaseInfo,
                        getObjectInfo: CaseInfoService.getCaseInfo,
                        updateObjectInfo: CaseInfoService.updateCaseInfo,
                        getObjectTypeFromInfo: function(objectInfo) {
                            return ObjectService.ObjectTypes.CASE_FILE;
                        }
                    });

                } ]);